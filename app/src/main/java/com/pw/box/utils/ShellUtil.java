package com.pw.box.utils;

import java.io.File;
import java.io.IOException;

/**
 * android 执行命令的工具类
 * Created by danger on 2016/11/19.
 */
public class ShellUtil {

    public static void grantFilePerm(File file) {
        grantFilePerm(file.getAbsolutePath());
    }

    public static void grantFilePerm(String file) {
        synchronized (file) {
            String command = "chmod 777 " + file;
            Runtime runtime = Runtime.getRuntime();
            try {
                runtime.exec(command);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }
}
