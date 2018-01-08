package com.pw.box.tool

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException


/**
 * 可计算sha256 和sha512的工具类
 * Created by danger on 2018/1/1.
 */


/**
 * 字符串 SHA 加密
 *
 * @param strText
 * @return
 */
private fun sha(strText: ByteArray, strType: String): ByteArray {
    // 返回值
    var strResult = ""

    // 是否是有效字符串
    try {
        // SHA 加密开始
        // 创建加密对象 并傳入加密類型
        val messageDigest = MessageDigest
                .getInstance(strType)
        // 传入要加密的字符串
        messageDigest.update(strText)
        // 得到 byte 類型结果
        return messageDigest.digest()

        /*return byteBuffer

        // 將 byte 轉換爲 string
        val strHexString = StringBuffer()
        // 遍歷 byte buffer
        for (i in byteBuffer.indices) {
            val hex = Integer.toHexString(0xff and byteBuffer[i])
            if (hex.length == 1) {
                strHexString.append('0')
            }
            strHexString.append(hex)
        }
        // 得到返回結果
        strResult = strHexString.toString()*/
    } catch (e: NoSuchAlgorithmException) {
        e.printStackTrace()
    }

    return byteArrayOf()
}

fun sha256(strText: ByteArray) = sha(strText, "SHA-256")
fun sha256(strText: String) = sha256(strText.toByteArray(Charsets.UTF_8))


fun sha512(strText: ByteArray) = sha(strText, "SHA-512")
fun sha512(strText: String) = sha512(strText.toByteArray(Charsets.UTF_8))