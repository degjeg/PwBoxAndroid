package com.pw.box.utils;

import com.pw.box.core.C;

import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.security.Security;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * 支持256bit(32B)的Aes工具类
 * Created by danger on 16/9/7.
 */
public class Aes256 {

    public static final String KEY_ALGORITHM = "AES";
    public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS7Padding";


    public static final int FILL_TYPE_PW = 1;
    public static final int FILL_TYPE_PROTECT = 2;
    public static final int FILL_TYPE_RAW_KEY = 3;


    public static byte[] fillKey(String key, int fillType) throws UnsupportedEncodingException {
        byte[] keyFilled = new byte[C.password_len];
        byte[] rawKey = key.getBytes("utf-8");
        int len = Math.min(C.max_password_len, rawKey.length);
        System.arraycopy(rawKey, 0, keyFilled, 0, len);

        for (int i = len; i < keyFilled.length; i++) {
            keyFilled[i] = (byte) (i ^ fillType);
        }
        return keyFilled;
    }

    public static byte[] encrypt(byte[] ori, String key, int fillType) throws Exception {


        return encrypt(ori, fillKey(key, fillType));
    }

    public static byte[] initkey() throws Exception {
        //实例化密钥生成器
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM, "BC");
        kg.init(256);
        kg.init(128);
        SecretKey secretKey = kg.generateKey();
        return secretKey.getEncoded();
    }

    public static byte[] initRootKey() throws Exception {
        return new byte[]{0x08, 0x08, 0x04, 0x0b, 0x02, 0x0f, 0x0b, 0x0c,
                0x01, 0x03, 0x09, 0x07, 0x0c, 0x03, 0x07, 0x0a, 0x04, 0x0f,
                0x06, 0x0f, 0x0e, 0x09, 0x05, 0x01, 0x0a, 0x0a, 0x01, 0x09,
                0x06, 0x07, 0x09, 0x0d};
    }

    public static Key toKey(byte[] key) throws Exception {
        SecretKey secretKey = new SecretKeySpec(key, KEY_ALGORITHM);
        return secretKey;
    }


    public static byte[] encrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);
        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");

        cipher.init(Cipher.ENCRYPT_MODE, k);

        return cipher.doFinal(data);
    }

    public static byte[] decrypt(byte[] data, byte[] key) throws Exception {
        Key k = toKey(key);

        Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
        Cipher cipher = Cipher.getInstance(CIPHER_ALGORITHM, "BC");

        cipher.init(Cipher.DECRYPT_MODE, k);

        return cipher.doFinal(data);
    }


    public static byte[] encryptStrong(byte[] data, byte[] key) throws Exception {

        byte[] encrypted = data;
        for (int i = 0; i < key.length; i += 32) {
            int len = Math.min(key.length - i, 32);
            byte[] k = new byte[len];
            System.arraycopy(key, i, k, 0, len);
            encrypted = encrypt(encrypted, k);
        }
        return encrypted;
    }

    public static byte[] decryptStrong(byte[] data, byte[] key) throws Exception {
        byte[] decrypted = data;
        for (int i = ((key.length + 31) / 32 - 1) * 32; i >= 0; i -= 32) {
            int len = Math.min(key.length - i, 32);
            byte[] k = new byte[len];
            System.arraycopy(key, i, k, 0, len);
            decrypted = decrypt(decrypted, k);
        }
        return decrypted;
    }

    public static byte[] encrypt(byte[] data, List<byte[]> key) throws Exception {
        for (int i = 0; i < key.size(); i++) {
            data = encrypt(data, key.get(i));
        }
        return data;
    }

    public static byte[] decrypt(byte[] data, List<byte[]> key) throws Exception {
        for (int i = key.size() - 1; i >= 0; i--) {
            data = decrypt(data, key.get(i));
        }
        return data;
    }

    public static void main(String[] args) throws UnsupportedEncodingException {
        String str = "芸sweet";
        //打印原文
        System.out.println("原文：" + str);


        for (int i = 0; i <= 2; i++) {
            try {
                //密钥
                byte[] key = new byte[16 + 8 * i];

                //生成随机密钥
                // key = Aes256.initkey();
                Random random = new Random(System.currentTimeMillis());
                random.nextBytes(key);

                //打印密钥
                System.out.println("密钥：" + key.length + "," + Arrays.toString(key));

                //加密
                byte[] data = Aes256.encrypt(str.getBytes(), key);

                //打印密文
                System.out.println("加密后：" + data.length + "," + Arrays.toString(data));

                //解密密文
                data = Aes256.decrypt(data, key);
                //打印原文
                System.out.println("解密后：" + new String(data));
                System.out.println();

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
