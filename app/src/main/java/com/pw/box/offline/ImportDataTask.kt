package com.pw.box.offline

import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.pw.box.BuildConfig
import com.pw.box.R
import com.pw.box.core.C
import com.pw.box.core.db.Db
import com.pw.box.core.db.bean.OfflineItem
import com.pw.box.tool.Base64
import com.pw.box.tool.StrongHash
import com.pw.box.tool.sha512
import com.pw.box.ui.dialog.InputDialog
import com.pw.box.ui.dialog.ProgressDialog
import com.pw.box.utils.L
import java.io.File
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 修改密码任务
 * Created by danger on 2018/1/5.
 */
private const val TAG = "ImportDataTask"

open class ImportDataTask(
        private val context: Context,
        private val file: File,
        // private val oldAccount: String,
        // private val oldPassword: String,
        private val taskFinishHandler: () -> Unit
) : AsyncTask<Void, Int, Int>() {

    private var listInApp: MutableList<OfflineItem> = mutableListOf()
    private lateinit var oriList: List<OfflineItem>

    private var needInputedCount = 0
    private var inputedCount = 0
    private val isInputDialogShow = AtomicBoolean(false)
    private val lock = Object()

    private var progressDialog: ProgressDialog? = null

    private var ignoreCount = 0
    private var oldAccount = ""
    private var oldPassword = ""
    override fun onPreExecute() {
        super.onPreExecute()
        progressDialog = ProgressDialog(context)
        progressDialog!!.setCancelable(false)
        progressDialog!!.show()
    }

    override fun onProgressUpdate(vararg values: Int?) {
        val i = values[0]!!
        when (i) {
            -1 -> progressDialog?.setMessage(R.string.do_not_interrupt_changepw_task)
            -2 -> Toast.makeText(context, R.string.error_password_error, Toast.LENGTH_SHORT).show()
            else -> progressDialog?.setMessage(context.getString(R.string.export_progress,
                    i + 1,
                    oriList.size,
                    oriList[i].desc
            ))
        }
    }

    override fun onPostExecute(result: Int?) {
        progressDialog?.dismiss()
        progressDialog = null
    }


    fun cancel() {
        cancel(true)
        progressDialog?.dismiss()
        progressDialog = null
    }

    override fun doInBackground(vararg params: Void?): Int {

        val hashUtil = StrongHash(context)

        // TODO  oriList = Db.getSession().offlineItemDao.loadAll()

        do {
            if (isCancelled) return 0
            oldAccount = getAccount()
            if (isCancelled) return 0
            oldPassword = getPassword()
            if (isCancelled) return 0
            try {
                oriList = parseFile(file.absolutePath, oldAccount, oldPassword)
                break
            } catch (e: Exception) {
                Handler(Looper.getMainLooper()).post {
                    val dlg = AlertDialog.Builder(context)
                    dlg.setTitle(R.string.tip)
                    dlg.setCancelable(false)
                    dlg.setMessage(e.message)
                    dlg.setNegativeButton(R.string.cancel, { dialog, _ ->
                        dialog.dismiss()
                        isInputDialogShow.set(false)
                        cancel()
                    })
                    dlg.setPositiveButton(R.string.retry, { dialog, _ ->
                        dialog.dismiss()
                        isInputDialogShow.set(false)
                        synchronized(lock) { lock.notify() }
                    })
                    dlg.show()
                }

                isInputDialogShow.set(true)
                waitForInput()
            }
        } while (true)

        if (isCancelled) return 0
        listInApp.addAll(loadOffLineDataList())

        // step1.需要二次难密码的排在前面,先引导用户输入二次检验密码
        Collections.sort(oriList) { t1, t2 ->
            if (t1.havePassword == t2.havePassword) {
                return@sort 0
            }
            return@sort if (t1.havePassword) -1 else 1
        }

        needInputedCount = oriList.count { it.havePassword }

        // step2.对所有数据进行解密前重新加密
        oriList.forEachIndexed { index, offlineItem ->
            var decrypted: ByteArray? = null
            var needPassword = false
            do {
                var password = ""

                publishProgress(index)

                if (L.D) L.get().e(TAG, "import $index/${oriList.size}")
                if (isCancelled) {
                    return 0
                }

                var outItem: OfflineItem = offlineItem

                if (oldAccount != offlineAccount || oldPassword != offlinePassword) {
                    try {
                        // step2.1 获取二次验证密码
                        if (offlineItem.havePassword) {
                            password = getPassword(index, offlineItem)
                        }
                        // step2.2 解密数据
                        val passwordMix = Base64.encode(sha512(oldPassword) + sha512(password))
                        decrypted = decrypt(hashUtil, oldAccount, copyItem(offlineItem), passwordMix)

                        // step2.3 重新加密
                        val passwordMix2 = Base64.encode(sha512(offlinePassword) + sha512(password))
                        outItem = encrypt(hashUtil, offlineAccount, String(decrypted, Charsets.UTF_8), passwordMix2)

                        // outItem.id = offlineItem.id
                        outItem.havePassword = offlineItem.havePassword
                        outItem.desc = offlineItem.desc
                        outItem.account = offlineAccount

                        if (L.D) L.get().e(TAG, "insert" + outItem)
                        insertOrReplace(outItem)
                        if (offlineItem.havePassword) {
                            inputedCount++
                        }
                    } catch (e: Exception) {
                        if (!outItem.havePassword) {
                            cancel()
                            return 0
                        }
                        needPassword = true
                    }
                } else {
                    insertOrReplace(outItem)
                }
            } while (needPassword)
        }


        publishProgress(-1)

        // step3. update数据库中的所有记录，如果中途中断会造成严重后果！,关键步骤，并且不可以进行耗时较多的操作
        if (isCancelled) {
            return 0
        }
        Handler(Looper.getMainLooper()).post {
            val dlg = android.support.v7.app.AlertDialog.Builder(context)
            dlg.setTitle(R.string.tip)
            dlg.setMessage(if (ignoreCount == 0) {
                context.getString(R.string.import_data_success, oriList.size)
            } else {
                context.getString(R.string.import_data_success_with_ignore, oriList.size - ignoreCount, ignoreCount)

            })
            dlg.setPositiveButton(R.string.i_see, null)
            dlg.show()

            taskFinishHandler()
        }

        return 0
    }

    private fun insertOrReplace(outItem: OfflineItem) {
        if (listInApp.none { Arrays.equals(outItem.content, it.content) }) {
            Db.getSession().offlineItemDao.insert(outItem)
            listInApp.add(outItem)
        } else {
            ignoreCount++
        }
    }

    private fun showInputDialog(dialogIniter: (dlg: InputDialog) -> Unit): String {
        // 需要用户输入二次验证密码
        var inputPassword = ""
        Handler(Looper.getMainLooper()).post {
            val dlg = InputDialog(context)
            // dlg.setHint(context.getString(R.string.please_input_second_password))
            // dlg.setcancelTips(R.string.press_again_to_cancel)
            dlg.setcancelTips(R.string.press_again_to_cancel)
            dlg.setCancelable(false)
            /*dlg.checker = { pw ->
                if (pw.toByteArray(Charsets.UTF_8).isEmpty()) {
                    Toast.makeText(context, R.string.error_password_len, Toast.LENGTH_SHORT).show()
                    false
                } else {
                    true
                }
            }
            dlg.setTitle(context.getString(
                    R.string.tips_changepw_input_pw,
                    index + 1,
                    oriList.size,
                    needInputedCount - inputedCount,
                    it.desc

            ))*/
            dlg.setListener(object : InputDialog.OnTextInputedListener {
                override fun onTextInputed(pw: String) {
                    inputPassword = pw
                    synchronized(lock) {
                        isInputDialogShow.set(false)
                        lock.notify()
                    }
                }
            })

            dialogIniter(dlg)
            dlg.show()
            dlg.setCancelable(false)
            dlg.setOnCancelListener(DialogInterface.OnCancelListener {
                isInputDialogShow.set(false)
                cancel()
                Thread.currentThread().interrupt()
            })

        }
        isInputDialogShow.set(true)
        waitForInput()
        return inputPassword
    }

    private fun getAccount(): String {
        return showInputDialog { dlg ->
            dlg.setHint(context.getString(R.string.hint_input_account_of_import))

            if (BuildConfig.DEBUG) {
                dlg.setValue("测试6")
            }
            dlg.checker = { pw ->
                if (pw.toByteArray(Charsets.UTF_8).isEmpty()) {
                    Toast.makeText(context, R.string.error_account_len, Toast.LENGTH_SHORT).show()
                    false
                } else {
                    true
                }
            }
        }
    }

    private fun getPassword(): String {
        return showInputDialog { dlg ->
            dlg.setHint(context.getString(R.string.hint_input_password))
            if (BuildConfig.DEBUG) {
                dlg.setValue("测试1234")
            }
            dlg.checker = { pw ->
                if (pw.toByteArray(Charsets.UTF_8).size < C.min_password_len) {
                    Toast.makeText(context, R.string.error_password_len, Toast.LENGTH_SHORT).show()
                    false
                } else {
                    true
                }
            }
        }
    }

    private fun getPassword(index: Int, it: OfflineItem): String {
        return showInputDialog { dlg ->
            dlg.setHint(context.getString(R.string.please_input_second_password))
            dlg.setcancelTips(R.string.press_again_to_cancel)
            dlg.checker = { pw ->
                if (pw.toByteArray(Charsets.UTF_8).isEmpty()) {
                    Toast.makeText(context, R.string.error_password_len, Toast.LENGTH_SHORT).show()
                    false
                } else {
                    true
                }
            }
            dlg.setTitle(context.getString(
                    R.string.tips_changepw_input_pw,
                    index + 1,
                    oriList.size,
                    needInputedCount - inputedCount,
                    it.desc

            ))
        }
    }

    private fun waitForInput() {
        while (isInputDialogShow.get()) {
            synchronized(lock) {
                lock.wait(1000)
            }
        }
    }

}