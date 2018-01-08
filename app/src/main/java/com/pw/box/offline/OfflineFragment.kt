package com.pw.box.offline

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.databinding.DataBindingUtil
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import com.pw.box.BuildConfig
import com.pw.box.R
import com.pw.box.core.C
import com.pw.box.core.db.Db
import com.pw.box.core.db.bean.OfflineItem
import com.pw.box.databinding.ListFooterCountBinding
import com.pw.box.databinding.OfflineFragmentBinding
import com.pw.box.databinding.OfflineItemBinding
import com.pw.box.tool.DoubleTabHelper
import com.pw.box.ui.base.BaseFragment
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew
import com.pw.box.ui.base.ContainerActivity
import com.pw.box.ui.dialog.InputDialog
import com.pw.box.utils.DensitiUtil
import com.pw.box.utils.FileUtils
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 提供离线加密解密功能
 * Created by danger on 2017/12/31.
 */
private const val REQUEST_PERMISSION_EXPORT = 1
private const val REQUEST_PERMISSION_IMPORT = 2
private const val REQUEST_SELECT_FILE = 1

class OfflineFragment : BaseFragment(), BaseRecyclerViewAdapterNew.OnItemClickListener<OfflineItem>, BaseRecyclerViewAdapterNew.OnItemLongClickListener<OfflineItem> {


    private val isLoading = AtomicBoolean(false)
    private var binding: OfflineFragmentBinding? = null
    private var adapter: BaseRecyclerViewAdapterNew<OfflineItem>? = null

    private var fileToImport: String? = null
    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.offline_fragment, null, false)

        val binding = this.binding ?: return null
        initView(binding)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        adapter = null
        binding = null
    }

    override fun onResume() {
        super.onResume()
        reloadData()
    }

    private fun reloadData() {
        Thread {
            if (isLoading.get()) return@Thread
            isLoading.set(true)
            try {
                val mu = loadOffLineDataList()
                Handler(Looper.getMainLooper()).post {
                    adapter?.data = mu
                    adapter?.setShowHeaderAndFooter(mu.size > 10)
                    binding?.emptyView?.visibility = if ((adapter?.itemCount ?: 0) > 0) View.GONE else View.VISIBLE
                }
            } catch (e: Exception) {

            } finally {
                isLoading.set(false)
            }
        }.start()
    }

    override fun onBackPressed(): Boolean {
        DoubleTabHelper.pressAgainToExit(context)
        return true
    }

    private fun initView(binding: OfflineFragmentBinding) {
        binding.btnMore.setOnClickListener { v ->
            showDropdownMenu(v)
        }

        adapter = object : BaseRecyclerViewAdapterNew<OfflineItem>(context, R.layout.offline_item) {
            override fun bindData(vh: Vh<OfflineItem>, t: OfflineItem, pos: Int) {
                super.bindData(vh, t, pos)
                val binding: OfflineItemBinding = vh.getBinding()
                binding.tvTitle.text = t.desc
                binding.ivLock.visibility = if (t.havePassword) View.VISIBLE else View.GONE
            }

            override fun bindFooter(holder: Vh<OfflineItem>, position: Int, footerPosition: Int) {
                super.bindFooter(holder, position, footerPosition)
                val binding: ListFooterCountBinding = holder.getBinding()
                binding.tvCount.text = getString(R.string.data_count, adapter?.itemCount ?: 0)
            }
        }

        adapter!!.addFooter(R.layout.list_footer_count)
        adapter!!.setOnItemClickListener(this)
        binding.emptyView.setOnClickListener {
            onClickAddData()
        }
        adapter!!.setOnItemLongClickListener(this)
        binding.recyclerView.layoutManager = LinearLayoutManager(context)
        binding.recyclerView.adapter = adapter


//        binding.viewpager.adapter = object : FragmentPagerAdapter(fragmentManager) {
//            override fun getItem(position: Int): Fragment {
//                return Fragment.instantiate(context,
//                        when (position) {
//                            0 -> EncryptFragment::class.java
//                            else -> DecryptFragment::class.java
//                        }.name, null)
//            }
//
//            override fun getCount() = 2
//
//            override fun getPageTitle(position: Int): CharSequence {
//                return getString(when (position) {
//                    0 -> R.string.encrypt
//                    else -> R.string.decrypt
//                })
//            }
//        }

    }


    private fun showDropdownMenu(v: View?) {
        val view = v ?: return
        // 一个自定义的布局，作为显示的内容
        val contentView = LayoutInflater.from(context).inflate(R.layout.pop_menu_offline, null)
        // 设置按钮的点击事件

        val popupWindow = PopupWindow(contentView,
                DensitiUtil.dp2px(context, 240f), ViewGroup.LayoutParams.WRAP_CONTENT, true)

        val onItemClickListener = View.OnClickListener { v ->
            popupWindow.dismiss()
            when (v.id) {
                R.id.item_add -> onClickAddData()
                R.id.item_changepw -> changePw()
                R.id.item_import -> importCheckPermission()
                R.id.item_export -> exportData()
            }
        }


        popupWindow.isTouchable = true

        popupWindow.setTouchInterceptor { _, _ ->
            false
            // 这里如果返回true的话，touch事件将被拦截
            // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
        }

        // 如果不设置PopupWindow的背景，无论是点击外部区域还是Back键都无法dismiss弹框
        // 我觉得这里是API的一个bug
        popupWindow.setBackgroundDrawable(ColorDrawable(0))

        // 设置好参数之后再show
        popupWindow.showAsDropDown(view)

        contentView.findViewById<View>(R.id.item_add).setOnClickListener(onItemClickListener)
        contentView.findViewById<View>(R.id.item_changepw).setOnClickListener(onItemClickListener)
        contentView.findViewById<View>(R.id.item_export).setOnClickListener(onItemClickListener)
        contentView.findViewById<View>(R.id.item_import).setOnClickListener(onItemClickListener)
    }


    private fun onClickAddData() {
        ContainerActivity.go(activity, AddOfflineDataFragment::class.java, null)
    }

    private fun changePw() {
        val timeSeconds: Int = (adapter?.itemCount ?: 0) * 5
        if (timeSeconds == 0) { // 没有数据
            changePasswordStep1()
            return
        }

        // 弹出确认提示框
        val confirmDlg = AlertDialog.Builder(context)
        confirmDlg.setTitle(R.string.tip)

        val tipsMessage = getString(R.string.sure_to_change_offline_password,
                adapter!!.itemCount,
                if (timeSeconds <= 60) getString(R.string.time_in_seconds, timeSeconds)
                else getString(R.string.time_in_minutes, (59 + timeSeconds) / 60)
        )

        confirmDlg.setMessage(tipsMessage)
        confirmDlg.setNegativeButton(R.string.cancel, null)
        confirmDlg.setPositiveButton(R.string.sure, { dialog, _ ->
            dialog.dismiss()
            changePasswordStep1()
        })

        confirmDlg.show()
    }

    // 1.验证原密码
    private fun changePasswordStep1() {
        val oriDlg = InputDialog(context)

        oriDlg.setHint(getString(R.string.hint_ori_password))

        if (BuildConfig.DEBUG) {
            oriDlg.setValue("测试1234")
        }
        oriDlg.setListener(object : InputDialog.OnTextInputedListener {
            override fun onTextInputed(pw: String) {
                if (pw != offlinePassword) {
                    oriDlg.dismiss()
                    toast(R.string.error_password_error)
                    return
                }

                changePasswordStep2(null)
            }
        })
        oriDlg.show()
    }

    // 1.输入新密码
    private fun changePasswordStep2(newPw: String?) {
        val newPwDlg = InputDialog(context)


        newPwDlg.setHint(getString(
                if (newPw == null) R.string.hint_new_password
                else R.string.hint_new_password_again))

        if (BuildConfig.DEBUG) {
            newPwDlg.setValue("测试12345")
        }
        newPwDlg.setCancelable(false)
        newPwDlg.checker = checker@ { pw ->
            if (newPw != null && newPw != pw) {
                toast(R.string.error_password_not_same)
                return@checker false
            }
            if (pw.toByteArray(Charsets.UTF_8).size < C.min_password_len) {
                toast(R.string.error_password_len)
                false
            } else {
                true
            }
        }
        newPwDlg.setListener(object : InputDialog.OnTextInputedListener {
            override fun onTextInputed(pw: String) {

                if (newPw == null) {
                    newPwDlg.dismiss()
                    changePasswordStep2(pw)
                    return
                }

                newPwDlg.dismiss()
                val task = ChangePasswordThreadTask(context, pw) {
                    reloadData()
                }
                task.execute()
                // Thread(ChangePasswordThread(context, adapter?.data!!)).start()
            }
        })
        newPwDlg.show()
    }


    private fun requestSdcardPermission(code: Int): Boolean {
        if (Build.VERSION.SDK_INT >= 23) {
            if (PackageManager.PERMISSION_GRANTED == activity.checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                return true
            }
            requestPermissions(arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE), code)
            return false
        } else {
            return true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (!grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
            // 授权失败
            return
        }
        if (requestCode == REQUEST_PERMISSION_EXPORT) {
            doExport()
        } else {
            importSelectFile()
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode != Activity.RESULT_OK) {
            return
        }

        if (requestCode == REQUEST_SELECT_FILE) {
            val uri: Uri = data?.getData() ?: return//得到uri，后面就是将uri转化成file的过程。
            fileToImport = FileUtils.getPath(context, uri)
            doImport(fileToImport!!)
        }
    }

    private fun exportData() {
        try {
            if (adapter?.itemCount == 0) {
                toast(R.string.message_export_empty)
                return
            }

            if (requestSdcardPermission(REQUEST_PERMISSION_EXPORT)) {
            }
            doExport()
        } catch (e: Exception) {
            toast(e.message)
        }
    }

    private fun doExport() {
        val file = export(context, adapter!!.dataList)
        if (file.exists()) {

            val dlg = AlertDialog.Builder(context)
            dlg.setMessage(getString(R.string.export_success, file.absolutePath))
            dlg.setPositiveButton(R.string.i_see, null)
            dlg.show()
        }
    }


    /**
     * import step1
     */
    private fun importCheckPermission() {
        if (requestSdcardPermission(REQUEST_PERMISSION_IMPORT))
            importSelectFile()
    }

    /**
     * import step2
     */
    private fun importSelectFile() {
        val intent = Intent(Intent.ACTION_GET_CONTENT);
        intent.type = "*/*";//设置类型，我这里是任意类型，任意后缀的可以这样写。
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        startActivityForResult(intent, 1);
    }

    /**
     * import step3
     */
    private fun doImport(file: String) {
        ImportDataTask(context, File(file)) {
            reloadData()
        }.execute()
    }

    override fun onItemLongClick(t: OfflineItem, pos: Int) {
        val dlg = AlertDialog.Builder(context)
        dlg.setTitle(R.string.tip)
        dlg.setMessage(R.string.sure_to_delete_data)
        dlg.setNegativeButton(R.string.cancel, null)
        dlg.setPositiveButton(R.string.sure, { dlg, _ ->
            dlg.dismiss()
            Db.getSession().offlineItemDao.delete(t)
            reloadData()
        })
        dlg.show()
    }

    override fun onItemClick(t: OfflineItem, pos: Int) {
        val args = Bundle()
        args.putLong(ViewOfflineDataFragment.EXTRA_ID, t.id)
        ContainerActivity.go(activity, ViewOfflineDataFragment::class.java, args)
    }
}