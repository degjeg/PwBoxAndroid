package com.pw.box.offline

import android.content.Context
import android.os.Environment
import android.os.Environment.MEDIA_MOUNTED
import com.pw.box.App.context
import com.pw.box.R
import com.pw.box.core.db.bean.OfflineItem
import com.pw.box.tool.Base64
import com.pw.box.utils.FileUtils
import com.pw.box.utils.Md5
import com.pw.box.utils.PrefUtil
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

/**
 * 为降低包大小，并且只有这里用json，暂时手工拼json
 * Created by danger on 2018/1/7.
 */

// const val EXPORT_ROOT_DIR = "Android/data/com.pw.box"
private const val EXPORT_ROOT_DIR = "password"
private const val DATE_FORMAT = "MMddHHmmss"
private const val FILE_EXT = "password"

const val NODE_NAME_PASSWORD = "password"
const val NODE_NAME_DATA = "data"

fun export(context: Context, list: List<OfflineItem>): File {
    if (Environment.getExternalStorageState() != MEDIA_MOUNTED) {
        throw Exception(context.getString(R.string.error_sdcard_not_mounted))
    }

    val dir = File(Environment.getExternalStorageDirectory(), EXPORT_ROOT_DIR)

    if (!dir.exists()) {
        if (!dir.mkdirs()) {
            throw    Exception(context.getString(R.string.unable_to_create_file))
        }
    }

    val fmt = SimpleDateFormat(DATE_FORMAT, Locale.getDefault())
    val tmpFile = File(dir, "${fmt.format(Date())}.$FILE_EXT.tmp")

    val pwKey = "offline_key_" + offlineAccount
    val requiredLoginKey = PrefUtil.getString(context, pwKey, "")

    val jsonRoot = JSONObject()
    val jsonDataList = JSONArray()
    jsonRoot.put(NODE_NAME_PASSWORD, requiredLoginKey)
    jsonRoot.put(NODE_NAME_DATA, jsonDataList)

    list.forEach {
        val jsonItem = JSONObject()
        jsonItem.put("title", it.desc)
        jsonItem.put("content", Base64.encode(it.content))
        jsonItem.put("salt", Base64.encode(it.salt))
        jsonItem.put("have_password", it.havePassword)

        jsonDataList.put(jsonItem)
    }

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(tmpFile)
        fos.write(jsonRoot.toString().toByteArray(Charsets.UTF_8))
        fos.close()
    } catch (e: Exception) {
        throw   Exception(context.getString(R.string.write_file_fail))
    } finally {
        fos?.close()
    }

    val file = File(dir, "${fmt.format(Date())}.$FILE_EXT")
    if (tmpFile.renameTo(file)) {
        // 删除完全重复的文件
        dir.listFiles().forEach {
            if (it.name != file.name && Md5.md5(it) == Md5.md5(file)) {
                it.delete()
            }
        }
        return file
    }
    throw Exception(context.getString(R.string.write_file_fail))
}


fun parseFile(file: String, account: String, password: String): List<OfflineItem> {
    try {

        val jsonContent: String = FileUtils.readFile(file, Charsets.UTF_8.displayName())?.toString() ?: ""
        val jsonRoot = JSONObject(jsonContent)

        val requiredLoginKey = jsonRoot.getString("password")
        val verified = verifyLoginKey(context, account, password, requiredLoginKey)

        if (!verified) {
            throw Exception(context.getString(R.string.import_data_fail_account_or_password))
        }

        val outList = mutableListOf<OfflineItem>()
        val jsonDataList = jsonRoot.getJSONArray(NODE_NAME_DATA)
        for (i in 0 until jsonDataList.length()) {
            val jsonItem: JSONObject = jsonDataList.get(i) as JSONObject
            val item = OfflineItem()

            outList.add(item)

            item.account = account
            item.content = Base64.decode(jsonItem.getString("content"))
            item.salt = Base64.decode(jsonItem.getString("salt"))
            item.desc = jsonItem.getString("title")
            item.havePassword = jsonItem.getBoolean("have_password")
        }
        return outList


    } catch (e: JSONException) {
        throw Exception(context.getString(R.string.import_data_fail_invalid_file))
    }

}