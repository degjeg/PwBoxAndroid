package com.pw.box.core;

import com.pw.box.utils.RsaUtils;

import java.security.interfaces.RSAPublicKey;

/**
 * 使用jni实现的底层加密解密工具类
 * Created by danger on 16/9/10.
 */
public class K {

    public static final byte[] K1;
    public static final byte[] K2;
    public static final String R1;
    public static final String R2;

    static {
        System.loadLibrary("core");
        K1 = k1();
        K2 = k2();

        R1 = new String(r1());
        R2 = new String(r2());
    }

    public native static void i(Object o);

    public native static byte[] k1();

    public native static byte[] k2();

    public native static byte[] r1();

    public native static byte[] r2();

    public static RSAPublicKey r() {
        String m = R1;
        String public_exponent = R2;

        return RsaUtils.getPublicKey(m, public_exponent);
    }





}
