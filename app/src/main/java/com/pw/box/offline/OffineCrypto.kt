package com.pw.box.offline

import android.content.Context
import com.pw.box.core.db.Db
import com.pw.box.core.db.OfflineItemDao
import com.pw.box.core.db.bean.OfflineItem
import com.pw.box.tool.Base64
import com.pw.box.tool.StrongHash
import com.pw.box.utils.Aes256
import java.security.SecureRandom
import java.util.*
import kotlin.experimental.xor

/**
 * Created by Administrator on 2018/1/4/004.
 */

var offlineAccount: String = ""
var offlinePassword: String = ""


private fun addSalt(item: OfflineItem, maxSalt2Len: Int = 100) {
    val salt1Len = 20 + (Math.abs(SecureRandom().nextInt()) % 100)

    val randSalt2Len = 20 + (Math.abs(SecureRandom().nextInt()) % (maxSalt2Len + 1))
    val salt2Len = arrayOf(maxSalt2Len, randSalt2Len, salt1Len + item.content.size * 2 / 3).min()!!
    val offset = 2

    val len = item.content.size + salt1Len + offset // 2的意义是1字节加密次数，1字节saltLen
    val newContent = ByteArray(len)

    val random = SecureRandom()

    if (salt2Len > 0) {
        item.salt = ByteArray(salt2Len) // 用来亦或到尾部
        random.nextBytes(item.salt)
    }

    val salt1 = ByteArray(salt1Len) // 用来添加到头部


    random.nextBytes(salt1)

    newContent[0] = salt1Len.toByte()
    newContent[1] = salt2Len.toByte()

    // 填入salt1
    System.arraycopy(salt1, 0, newContent, offset, salt1Len)

    // 填入内容
    System.arraycopy(item.content, 0, newContent, offset + salt1Len, item.content.size)

    if (salt2Len > 0) {
        // 异或尾部的salt
        for (i in 0 until salt2Len) {
            val p = i + newContent.size - salt2Len
            newContent[p] = item.salt[i] xor newContent[p]
        }
    }


    item.content = newContent
}

private fun removeSalt(item: OfflineItem): ByteArray {
    return try {
        val salt1Len = item.content[0]
        val salt2Len = item.content[1]

        if (salt2Len > 0) {
            // 异或尾部的salt
            for (i in 0 until salt2Len) {
                val p = i + item.content.size - salt2Len
                item.content[p] = item.salt[i] xor item.content[p]
            }
        }

        val outDataLen = item.content.size - 2 - salt1Len
        val outData = ByteArray(outDataLen)
        System.arraycopy(item.content, 2 + salt1Len, outData, 0, outDataLen)
        outData
    } catch (e: Exception) {
        byteArrayOf()
    }
}

fun encrypt(hashUtil: StrongHash, account: String, content: String, password: String): OfflineItem {

    val offlineItem = OfflineItem()
    offlineItem.content = content.toByteArray(Charsets.UTF_8)
    addSalt(offlineItem)

    val strongAesKey = hashUtil.strongHash(password, account.toByteArray(Charsets.UTF_8))
    // Log.e("TAG", "strongAesKey:" + Base64.encode(strongAesKey))
    offlineItem.content = Aes256.encryptStrong(offlineItem.content, strongAesKey)

    return offlineItem
}

fun decrypt(hashUtil: StrongHash, account: String, offlineItem: OfflineItem, password: String): ByteArray {
    val strongAesKey = hashUtil.strongHash(password, account.toByteArray(Charsets.UTF_8))


    offlineItem.content = Aes256.decryptStrong(offlineItem.content, strongAesKey)

    return removeSalt(offlineItem)
}

fun copyItem(offlineItem: OfflineItem): OfflineItem {
    val copyOfItem = OfflineItem()
    copyOfItem.id = offlineItem.id
    copyOfItem.account = offlineItem.account
    copyOfItem.desc = offlineItem.desc
    copyOfItem.content = offlineItem.content
    copyOfItem.salt = offlineItem.salt
    copyOfItem.havePassword = offlineItem.havePassword

    return copyOfItem
}

/**根据账号密码获取离线登录的key
 */
fun genLoginKey(context: Context, account: String, password: String): String {
    val hashUtil = StrongHash(context)
    val passwordHash = hashUtil.strongHash(password + hashUtil.getSaltString(password), account.toByteArray(Charsets.UTF_8))

    val offlineItem = OfflineItem()
    offlineItem.content = account.toByteArray(Charsets.UTF_8)
    addSalt(offlineItem, 0)
    return Base64.encode(Aes256.encryptStrong(offlineItem.content, passwordHash))
}

fun verifyLoginKey(context: Context, account: String, password: String, k: String): Boolean {
    return try {
        val hashUtil = StrongHash(context)
        val passwordHash = hashUtil.strongHash(password + hashUtil.getSaltString(password), account.toByteArray(Charsets.UTF_8))

        val encrytptedByAes = Base64.decode(k)
        val decrytptedByAes = Aes256.decryptStrong(encrytptedByAes, passwordHash)

        val offlineItem = OfflineItem()
        offlineItem.content = decrytptedByAes
        val oriContent = removeSalt(offlineItem)

        Arrays.equals(oriContent, account.toByteArray(Charsets.UTF_8))
    } catch (e: Exception) {
        false
    }

}


fun loadOffLineDataList(): MutableList<OfflineItem> {
    return Db.getSession().offlineItemDao.queryBuilder().where(
            OfflineItemDao.Properties.Account.eq(offlineAccount)
    ).build().list()
}