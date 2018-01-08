package com.pw.box.utils;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;

/**
 * 计算MD5 的工具类
 * Created by danger on 16/9/23.
 */

public class Md5 {

    /**
     * Used to build output as Hex
     */
    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd',
            'e', 'f'};

    private Md5() {
        throw new AssertionError();
    }

    /**
     * encode By MD5
     *
     * @param str
     * @return String
     */
    public static String md5(String str) {
        return md5(str.getBytes());
    }

    public static String md5(byte[] str) {
        if (str == null) {
            return null;
        }
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str);
            return new String(encodeHex(messageDigest.digest()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static byte[] md532Bytes(String str) {
        try {
            byte[] d = str.getBytes();

            byte[] md532 = new byte[32];

            int len = d.length * 2 / 3;

            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.update(str.getBytes(), 0, len);
            System.arraycopy(messageDigest.digest(), 0, md532, 0, 16);

            messageDigest.update(str.getBytes(), d.length - len - 1, len);
            System.arraycopy(messageDigest.digest(), 0, md532, 16, 16);

            return md532;
        } catch (Exception e) {
            e.printStackTrace();
            // throw new RuntimeException(e);
        }
        return null;
    }

    /**
     * Converts an array of bytes into an array of characters representing the hexadecimal values of each byte in order.
     * The returned array will be double the length of the passed array, as it takes two characters to represent any
     * given byte.
     *
     * @param data a byte[] to convert to Hex characters
     * @return A char[] containing hexadecimal characters
     */
    protected static char[] encodeHex(final byte[] data) {
        final int l = data.length;
        final char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS_LOWER[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS_LOWER[0x0F & data[i]];
        }
        return out;
    }

    public static String md5(File file) {
        String value = null;
        FileInputStream in = null; // new FileInputStream(file);
        try {
            in = new FileInputStream(file);
            MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            md5.update(byteBuffer);
            BigInteger bi = new BigInteger(1, md5.digest());

            value = bi.toString(16);
            if (value.length() < 32) {
                StringBuilder b = new StringBuilder();
                for (int i = 0; i < 32 - value.length(); i++) {
                    b.append("0");
                }
                value = b.append(value).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (null != in) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return value;
    }

}
