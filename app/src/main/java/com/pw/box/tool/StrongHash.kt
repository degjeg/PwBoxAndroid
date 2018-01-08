package com.pw.box.tool

import android.content.Context
import android.os.SystemClock
import android.util.Log
import com.pw.box.utils.Pbkdf2Util
import de.greenrobot.dao.DbUtils
import java.util.*


/**
 * 离线加密的工具类
 * Created by danger on 2017/12/31.
 */
const val BCRYPT_COUNT = 4

// "\$2a\$09\$85A5TU7iuEQlfY3UVqg7iu"        // 9
// "\$2a\$12\$c1FGBJb.vuT1yAw9/xH35u"   // 12
const val BCRYPT_SALT = "\$2a\$09\$85A5TU7iuEQlfY3UVqg7iu"


class StrongHash(context: Context) {
    private val builtInSalt = mutableListOf<ByteArray>()

    init {
        val rawSalts = DbUtils.readAsset(context, "enc.png")
        (0 until rawSalts.size step 255).mapTo(builtInSalt) {
            rawSalts.copyOfRange(it, it + 255)
        }
    }


    fun strongHash(password: ByteArray, dynamicSalt: ByteArray): ByteArray {
        val out = kotlin.arrayOfNulls<String>(password.size)

        val sizeOfStep = (BCRYPT_COUNT - 1 + password.size) / BCRYPT_COUNT
        val threads = arrayOfNulls<Thread>(BCRYPT_COUNT)
        for (i in 0 until BCRYPT_COUNT step 1) {
            threads[i] = Thread {
                val endIndex = Math.min((i + 1) * sizeOfStep, password.size - 1)
                val passwordPart = password.sliceArray(IntRange(i * sizeOfStep, endIndex))

                val saltIndex = Math.abs(Arrays.hashCode(passwordPart)) % builtInSalt.size
                val valueWithSalt: ByteArray = builtInSalt[saltIndex] + passwordPart
                // val b = byte.toInt() - Byte.MIN_VALUE
                // valueWithSalt[b % valueWithSalt.size] = byte


                val bCryptSalt: String = BCRYPT_SALT // BCrypt.gensalt(9/*Math.min(Math.max(b, 4), 12)*/)

                val start = SystemClock.elapsedRealtime()
                out[i] = BCrypt.hashpw(Base64.encode(valueWithSalt), bCryptSalt)
                val end = SystemClock.elapsedRealtime()
                Log.e("eeee", String.format("[--->%d]len %d, used %d", i, valueWithSalt.size, end - start))
            }

            threads[i]?.start()
        }
        threads.forEach {
            it?.join(100000)
        }
        val str: String = out.reduce { acc, s -> acc + s }!! // { // Arrays.toString(out)
        val start = SystemClock.elapsedRealtime()
        var encrypted = Pbkdf2Util.encrypt(str.toCharArray(), builtInSalt[Math.abs(str.hashCode()) % builtInSalt.size])

        encrypted = sha512(encrypted)

        val accountWithSalt = builtInSalt[Math.abs(Arrays.hashCode(dynamicSalt)) % builtInSalt.size] + dynamicSalt
        val accountSha = sha512(accountWithSalt)

        encrypted = sha512(encrypted + accountSha)

        val end = SystemClock.elapsedRealtime()
        Log.e("eeee", String.format("[--->>>]used %d", end - start))
        return encrypted
    }


    fun strongHash(password: String, salt: ByteArray) = strongHash(password.toByteArray(), salt)
    fun strongHashToString(password: ByteArray, salt: ByteArray): String = Base64.encode(strongHash(password, salt))
    fun strongHashToString(password: String, salt: ByteArray): String = Base64.encode(strongHash(password, salt))


    fun getSaltString(s: String): String = Base64.encode(builtInSalt[Math.abs(s.hashCode()) % builtInSalt.size])
}


