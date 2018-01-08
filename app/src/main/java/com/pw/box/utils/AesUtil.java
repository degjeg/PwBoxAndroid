//package com.pw.box.utils;
//
//import com.pw.box.R;
//import com.pw.box.core.C;
//
//import java.security.InvalidParameterException;
//import java.util.Random;
//
//import javax.crypto.Cipher;
//import javax.crypto.spec.SecretKeySpec;
//
///**
// * Created by danger on 16/8/19.
// */
//public class AesUtil {
//
//
//
//    // 加密
//    public static byte[] encrypt(byte[] sSrc, byte[] sKey) throws Exception {
//        if (sKey == null) {
//            System.out.print("Key为空null");
//            throw new InvalidParameterException();
//        }
//        // 判断Key是否为16位
//        // if (sKey.length() != 16) {
//        //     System.out.print("Key长度不是16位");
//        //     return null;
//        // }
//
//        byte[] raw = sKey; // sKey.getBytes("utf-8");
//        SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");//"算法/模式/补码方式"
//        cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
//        byte[] encrypted = cipher.doFinal(sSrc/*.getBytes("utf-8")*/);
//
//        return encrypted;//此处使用BASE64做转码功能，同时能起到2次加密的作用。
//    }
//
//    // 解密
//    public static byte[] decrypt(byte[] encrypted, byte[] sKey) throws Exception {
//        try {
//            // 判断Key是否正确
//            if (sKey == null) {
//                System.out.print("Key为空null");
//                throw new InvalidParameterException();
//            }
//            // 判断Key是否为16位
//            // if (sKey.length() != 16) {
//            //     System.out.print("Key长度不是16位");
//            //     return null;
//            // }
//            byte[] raw = sKey; // sKey.getBytes("utf-8");
//            SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec);
//            try {
//                byte[] original = cipher.doFinal(encrypted);
//                return original;
//            } catch (Exception e) {
//                System.out.println(e.toString());
//                throw new InvalidParameterException();
//            }
//        } catch (Exception ex) {
//            System.out.println(ex.toString());
//            throw new InvalidParameterException();
//        }
//    }
//
//
//    public static void main(String[] arg) {
//        // L.init("core/log4j.properties");
//
//        Random random = new Random(System.currentTimeMillis());
//
//        long startTime = System.currentTimeMillis();
//
//        int size = 10;
//
//        long start = System.currentTimeMillis();
//
//        for (int i = 0; i < 50; i++) {
//            try {
//                String k = random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat();
//                String k2 = random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat() + "" + random.nextFloat();
//
//                byte key[] = new byte[16];
//                random.nextBytes(key);
//
//                if (k2.length() > 16) {
//                    k2 = k2.substring(0, 16);
//                } else if (k2.length() > 16) {
//                    k2 = k2.substring(0, 16);
//                }
//                k = k.substring(0, 64);
//
//                byte[] encryped = encrypt(k.getBytes(), key);
//                byte[] decryped = decrypt(encryped, key);
//
//                if (!k.equals(new String(decryped))) {
//                    if(L.E) L.get().e("测试失败");
//                    return;
//                }
//                if(L.D) L.get().d(k.length() + "->" + encryped.length + " " + k);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//        if(L.D) L.get().d("used:" + (System.currentTimeMillis() - start));
//
//    }
//}