package com.pw.box.offline

import android.databinding.DataBindingUtil
import android.os.Bundle
import android.os.SystemClock
import android.support.v7.app.AlertDialog
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.pw.box.R
import com.pw.box.core.db.Db
import com.pw.box.databinding.OfflineEncryptFragmentBinding
import com.pw.box.tool.Base64
import com.pw.box.tool.DoubleTabHelper
import com.pw.box.tool.StrongHash
import com.pw.box.tool.sha512
import com.pw.box.ui.base.BaseFragment
import com.pw.box.ui.base.ContainerActivity
import com.pw.box.ui.fragments.accounts.LoginFragment
import com.pw.box.ui.fragments.data.PasswordGenerateDialog

/**
 * 添加数据数据界面
 * Created by danger on 2017/12/31.
 */
class AddOfflineDataFragment : BaseFragment() {
    private var hashUtil: StrongHash? = null

    private var binding: OfflineEncryptFragmentBinding? = null

    private var dataId: Long? = null
    private var havePassword: Boolean = false

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.offline_encrypt_fragment, null, false)

        val binding = this.binding ?: return null


        if (offlineAccount.isEmpty() || offlinePassword.isEmpty()) {
            ContainerActivity.goClearTask(context, LoginFragment::class.java, null)
        }
        initView(binding)
        setDataIfNeeded()
        return binding.root
    }

    override fun onBackPressed(): Boolean {
        DoubleTabHelper.pressAgainToDoSth(context, R.string.press_again_to_exit_edit) {
            finish()
        }

        return true
    }

    private fun setDataIfNeeded() {

        val b = binding ?: return
        b.titleBar.setLeftButtonText(R.string.add_item)
        dataId = arguments?.getLong(ViewOfflineDataFragment.EXTRA_ID) ?: return

        b.titleBar.setLeftButtonText(R.string.edit_item)

        b.etContent.setText(arguments.getString(ViewOfflineDataFragment.EXTRA_CONTENT))
        b.etTips.setText(arguments.getString(ViewOfflineDataFragment.EXTRA_TITLE))
        havePassword = arguments.getBoolean(ViewOfflineDataFragment.EXTRA_HAVE_PW, havePassword)

        b.btnEncrypt.setText(
                if (dataId != null) R.string.save
                else R.string.add
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        hashUtil = null
    }

    private fun initView(b: OfflineEncryptFragmentBinding) {
        // 防止编辑完需要验证登录密码start
        val watcher = object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                // no need to do anything
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // no need to do anything
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                ViewOfflineDataFragment.lastResumeTime = SystemClock.elapsedRealtime()
            }
        }
        // 防止编辑完需要验证登录密码end

        b.etContent.addTextChangedListener(watcher)
        b.etTips.addTextChangedListener(watcher)
        b.etPassword.addTextChangedListener(watcher)

        b.btnGen.setOnClickListener {
            val dlg = PasswordGenerateDialog()
            // dlg.setOri(binding?.etContent?.text.toString())
            // dlg.setListener { s ->
            //     binding?.etContent?.setText(s)
            // }
            dlg.show(fragmentManager, null)
        }
        b.btnEncrypt.setOnClickListener {


            val binding = this@AddOfflineDataFragment.binding ?: return@setOnClickListener

            val tips = binding.etTips.text.toString().trim()
            val content = binding.etContent.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            if (tips.isEmpty()) {
                toast(getString(R.string.tips_description_is_empty))
                return@setOnClickListener
            }
            if (content.isEmpty() /*|| password.length < 6*/) {
                toast(getString(R.string.tips_content_is_empty))
                return@setOnClickListener
            }

            if (havePassword && password.isEmpty()) {
                val dlg = AlertDialog.Builder(context)
                dlg.setTitle(R.string.tip)
                dlg.setMessage(R.string.sure_to_use_empty_password)
                dlg.setNegativeButton(R.string.cancel, null)
                dlg.setPositiveButton(R.string.sure, { dlg, _ ->
                    dlg.dismiss()
                    doAddItem(password, content, tips)
                })
                dlg.show()
            } else {
                doAddItem(password, content, tips)
            }
        }
    }

    private fun doAddItem(password: String, content: String, tips: String) {
        showProgressDialog(true)
        Thread {
            hashUtil = hashUtil ?: StrongHash(context)
            val hashUtil = hashUtil ?: return@Thread

            // val rawContent = content.toByteArray(Charsets.UTF_8)

            val passwordMix = Base64.encode(sha512(offlinePassword) + sha512(password))
            val offlineItem = encrypt(hashUtil, offlineAccount, content, passwordMix)

            offlineItem.id = dataId
            offlineItem.desc = tips
            offlineItem.havePassword = !password.isEmpty()
            offlineItem.account = offlineAccount

            Db.getSession().offlineItemDao.insertOrReplace(offlineItem)
            // val decrypted = decrypt(hashUtil, offlineItem, password)

            // Log.e("TAG", "encrypteddata:" + Base64.encode(offlineItem.content))

            this@AddOfflineDataFragment.binding?.btnGen?.post {
                dismissDialog()
                // if (Arrays.equals(decrypted, rawContent)) {
                //     toast("解密成功")
                // }
                if (dataId != null) {
                    toast(R.string.data_saved)
                } else {
                    toast(R.string.data_added)
                }

                finish()
            }

        }.start()
    }

//    private fun test(b: OfflineEncryptFragmentBinding) {
//        offlineAccount = b.etContent.text.toString().trim()
//        Thread {
//
//
//            for (i in 40..44) {
//                val start = SystemClock.elapsedRealtime()
//                val toEnc = String(ByteArray(i, { it.toString().elementAt(0).toByte() }))
//                val encrypted = encUtil!!.encryptToString(toEnc)
//                val end = SystemClock.elapsedRealtime()
//
//                Log.e("eeee", String.format("[%d]%s -> %s used %d", i, toEnc, encrypted, end - start))
//            }
//        }.start()
//    }
//
//    fun bCryptTest() {
//        for (i in 0..10) {
//            Log.e("eeee BCrypttest", BCrypt.hashpw("1234", BCRYPT_SALT))
//            Log.e("eeee BCrypttest", BCrypt.hashpw("12345", BCRYPT_SALT))
//            Log.e("eeee BCrypttest", BCrypt.hashpw("12346", BCRYPT_SALT))
//            Log.e("eeee BCrypttest", BCrypt.hashpw("123466", BCRYPT_SALT))
//        }
//    }
}