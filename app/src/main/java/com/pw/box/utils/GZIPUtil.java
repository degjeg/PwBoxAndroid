package com.pw.box.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * gzip工具类
 * Created by danger on 16/8/7.
 */
public class GZIPUtil {

    public static final byte[] emptyByteArray = new byte[0];

    // 压缩
    public static byte[] gZIPCompress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return emptyByteArray;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(out);
        gzip.write(data);
        gzip.close();
        return out.toByteArray();
    }

    // 解压缩
    public static byte[] gZIPUncompress(byte[] data) throws IOException {
        if (data == null || data.length == 0) {
            return emptyByteArray;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(data);
        GZIPInputStream gunzip = new GZIPInputStream(in);
        byte[] buffer = new byte[256];
        int n;
        while ((n = gunzip.read(buffer)) >= 0) {
            out.write(buffer, 0, n);
        }
        return out.toByteArray();
    }

}
