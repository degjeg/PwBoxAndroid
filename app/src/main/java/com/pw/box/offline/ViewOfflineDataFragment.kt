package com.pw.box.offline

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pw.box.BuildConfig
import com.pw.box.R
import com.pw.box.core.db.Db
import com.pw.box.core.db.bean.OfflineItem
import com.pw.box.databinding.OfflineDecryptFragmentBinding
import com.pw.box.tool.Base64
import com.pw.box.tool.StrongHash
import com.pw.box.tool.sha512
import com.pw.box.ui.base.BaseFragment
import com.pw.box.ui.base.ContainerActivity
import com.pw.box.ui.dialog.InputDialog
import com.pw.box.utils.ClipBoardUtil

/**
 * 用于解密数据的界面
 * Created by danger on 2017/12/31.
 */
class ViewOfflineDataFragment : BaseFragment() {

    companion object {
        var lastResumeTime = SystemClock.elapsedRealtime()
        val needVerifyTime = if (BuildConfig.DEBUG) 10000 else 40000

        val EXTRA_ID = "id"
        val EXTRA_TITLE = "title"
        val EXTRA_CONTENT = "content"
        val EXTRA_HAVE_PW = "have_pw"
    }

    lateinit var data: OfflineItem
    private var binding: OfflineDecryptFragmentBinding? = null

    var oriString: String? = null
    var decryptedOK = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.offline_decrypt_fragment, null, false)

        val binding = this.binding ?: return null
        initView(binding)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        if (SystemClock.elapsedRealtime() - lastResumeTime < needVerifyTime) {
            lastResumeTime = SystemClock.elapsedRealtime()
        }
        data = copyItem(Db.getSession().offlineItemDao.load(arguments.getLong(EXTRA_ID)))

        val binding = this@ViewOfflineDataFragment.binding ?: return

        binding.tvTitle.text = data.desc
        binding.tvContent.text = Base64.encode(data.content)

        decryptedOK = false
        oriString = null
        binding.titleBar.setRightButtonVisible(View.GONE)
        binding.btnCopy.setText(R.string.show_decrypted_data)
        binding.btnCopy.setOnClickListener {
            if (!decryptedOK) { // 查看原文
                if (data.havePassword) {
                    showInputPasswordDialog()
                } else {
                    if (SystemClock.elapsedRealtime() - lastResumeTime > needVerifyTime) {
                        // 验证登录密码
                        toast(R.string.auth_expired)
                        val inputDlg = InputDialog(context)
                        inputDlg.setHint(getString(R.string.hint_input_password))
                        inputDlg.checker = { pw ->
                            if (offlinePassword != pw) {
                                toast(R.string.error_password_error)
                            }
                            offlinePassword == pw
                        }
                        inputDlg.setListener(object : InputDialog.OnTextInputedListener {
                            override fun onTextInputed(s: String) {
                                lastResumeTime = SystemClock.elapsedRealtime()
                                decrypt(null)
                            }
                        })

                        inputDlg.show()

                    } else {
                        decrypt(null)
                    }
                }
            } else {
                // 拷贝数据
                ClipBoardUtil.copy(context, oriString)
                toast(R.string.password_is_copyed)
            }
        }
    }

    private fun showInputPasswordDialog() {

        val dlg = InputDialog(context)

        dlg.setHint(getString(R.string.please_input_second_password))
        dlg.show()
        dlg.setListener(object : InputDialog.OnTextInputedListener {
            override fun onTextInputed(pw: String) {
                decrypt(pw)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }

    fun decrypt(pw: String?) {
        showProgressDialog(false)
        Thread {
            val passwordMix = Base64.encode(sha512(offlinePassword) + sha512(pw ?: ""))
            var decrypted: ByteArray? = null
            try {

                decrypted = decrypt(StrongHash(context), offlineAccount, data, passwordMix)
            } catch (e: Exception) {

            } finally {
                Handler(Looper.getMainLooper()).post {
                    dismissDialog()
                    if (decrypted == null) {
                        // 解密失败，
                        toast(R.string.error_password_error)
                        // showInputPasswordDialog()
                    } else {
                        lastResumeTime = SystemClock.elapsedRealtime()
                        decryptedOK = true
                        binding?.btnCopy?.setText(R.string.copy)
                        binding?.titleBar?.setRightButtonVisible(View.VISIBLE)

                        oriString = String(decrypted, Charsets.UTF_8)
                        binding?.tvContent?.text = oriString

                    }
                }
            }
        }.start()

    }

    private fun initView(binding: OfflineDecryptFragmentBinding) {
        binding.titleBar.setRightButtonClickListener {
            // if (!decryptedOK) {
            //     toast(R.string.please_show_ori)
            //     return@setRightButtonClickListener
            // }
            val args = Bundle()
            args.putLong(EXTRA_ID, data.id)
            args.putString(EXTRA_CONTENT, oriString)
            args.putString(EXTRA_TITLE, data.desc)
            args.putBoolean(EXTRA_HAVE_PW, data.havePassword ?: false)
            ContainerActivity.go(context, AddOfflineDataFragment::class.java, args)
        }
    }
}