package com.pw.box.utils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Pbkdf加密工具类
 * Created by danger on 2017/12/31.
 */

public class Pbkdf2Util {
    public static final String PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA1";
    public static final int iterationCount = 1000;
    public static final int KEY_LEN = 3024;

    public static byte[] encrypt(char[] password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
        PBEKeySpec spec = new PBEKeySpec(password, salt,
                iterationCount, KEY_LEN);
        SecretKeyFactory skf = SecretKeyFactory

                .getInstance(PBKDF2_ALGORITHM);

        return skf.generateSecret(spec).getEncoded();
    }

}
