package com.pw.box.offline

import android.content.Context
import android.content.DialogInterface
import android.os.AsyncTask
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AlertDialog
import android.widget.Toast
import com.pw.box.R
import com.pw.box.core.db.Db
import com.pw.box.core.db.bean.OfflineItem
import com.pw.box.tool.Base64
import com.pw.box.tool.StrongHash
import com.pw.box.tool.sha512
import com.pw.box.ui.dialog.InputDialog
import com.pw.box.ui.dialog.ProgressDialog
import com.pw.box.utils.PrefUtil
import java.util.*
import java.util.concurrent.atomic.AtomicBoolean

/**
 * 修改密码任务
 * Created by danger on 2018/1/5.
 */

private data class RawContent(
        val id: Long,
        val content: String,
        val desc: String
)

open class ChangePasswordThreadTask(
        private val context: Context,

        private val newPassword: String,
        private val taskFinishHandler: () -> Unit
) : AsyncTask<Void, Int, Int>() {

    private lateinit var oriList: List<OfflineItem>
    private val outList = mutableListOf<OfflineItem>()
    private val rawContentList = mutableListOf<RawContent>()

    private var needInputedCount = 0
    private var inputedCount = 0
    private val isInputDialogShow = AtomicBoolean(false)
    private val lock = Object()

    private var repeatCount = 0
    private var progressDialog: ProgressDialog? = null

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

        oriList = Db.getSession().offlineItemDao.loadAll()

        // step1.需要二次难密码的排在前面,先引导用户输入二次检验密码
        Collections.sort(oriList) { t1, t2 ->
            if (t1.havePassword == t2.havePassword) {
                return@sort (t1.id - t2.id).toInt()
            }
            return@sort if (t1.havePassword) -1 else 1
        }

        needInputedCount = oriList.count { it.havePassword }

        // step2.对所有数据进行解密前重新加密
        oriList.forEachIndexed { index, offlineItem ->
            var decrypted: ByteArray? = null

            do {
                var password = ""

                publishProgress(index)

                // step2.1 获取二次验证密码
                if (offlineItem.havePassword) {
                    password = getPassword(index, offlineItem)
                }

                if (isCancelled) {
                    return 0
                }
                try {
                    // step2.2 解密数据
                    val passwordMix = Base64.encode(sha512(offlinePassword) + sha512(password))
                    decrypted = decrypt(hashUtil, offlineAccount, copyItem(offlineItem), passwordMix)

                    rawContentList.add(RawContent(
                            offlineItem.id,
                            String(decrypted, Charsets.UTF_8),
                            offlineItem.desc
                    ))

                    // step2.3 重新加密
                    val passwordMix2 = Base64.encode(sha512(newPassword) + sha512(password))
                    val outItem = encrypt(hashUtil, offlineAccount, String(decrypted, Charsets.UTF_8), passwordMix2)


                    outItem.id = offlineItem.id
                    outItem.havePassword = offlineItem.havePassword
                    outItem.desc = offlineItem.desc
                    outItem.account = offlineItem.account

                    outList.add(outItem)
                    if (offlineItem.havePassword) {
                        inputedCount++
                    }
                } catch (e: Exception) {
                    publishProgress(-2)
                }
            } while (decrypted == null)
        }


        publishProgress(-1)
        val offlineLoginKey = genLoginKey(context, offlineAccount, newPassword)
        val pwPrefKey = "offline_key_" + offlineAccount
        removeRepeated()

        // step3. update数据库中的所有记录，如果中途中断会造成严重后果！,关键步骤，并且不可以进行耗时较多的操作
        if (isCancelled) {
            return 0
        }
        outList.forEach {
            Db.getSession().offlineItemDao.update(it)
        }
        offlinePassword = newPassword
        PrefUtil.setString(context, pwPrefKey, offlineLoginKey)

        Handler(Looper.getMainLooper()).post {
            val builder = AlertDialog.Builder(context)
            builder.setTitle(R.string.tip)
            builder.setMessage(
                    if (repeatCount == 0) {
                        context.getString(R.string.message_change_password_success)
                    } else {
                        context.getString(R.string.message_change_password_success_rm_repeat, repeatCount)
                    })
            builder.setPositiveButton(R.string.i_see, null)
            builder.show()
            taskFinishHandler()
        }

        return 0
    }

    private fun removeRepeated() {

        val grouped = rawContentList.groupBy {
            it.content + "|1|2|3|4|5|6|7|" + it.desc // 需要防止"12"+"3" 和 "1"+"23" 相等的情况
        }

        grouped.values.forEach {
            if (it.size > 1) {
                for (i in 1 until it.size) {
                    // requires api 24
                    // outList.removeIf({ it1 -> it1.id == it[i].id }

                    repeatCount++
                    Db.getSession().offlineItemDao.deleteByKey(it[i].id)
                    outList.removeAll { it1 -> it1.id == it[i].id }
                }
            }
        }
    }

    private fun getPassword(index: Int, it: OfflineItem): String {
        // 需要用户输入二次验证密码
        var inputPassword = ""
        Handler(Looper.getMainLooper()).post {
            val dlg = InputDialog(context)
            dlg.setHint(context.getString(R.string.please_input_second_password))
            dlg.setcancelTips(R.string.press_again_to_exit_edit)
            dlg.setCancelable(false)
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
            dlg.setListener(object : InputDialog.OnTextInputedListener {
                override fun onTextInputed(pw: String) {
                    inputPassword = pw
                    synchronized(lock) {
                        isInputDialogShow.set(false)
                        lock.notify()
                    }
                }
            })

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

    private fun waitForInput() {
        while (isInputDialogShow.get()) {
            synchronized(lock) {
                lock.wait(100000)
            }
        }
    }

}