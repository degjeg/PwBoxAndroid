package com.pw.box.utils;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

/**
 * Android剪切板工具类
 * Created by danger on 16/9/18.
 */
public class ClipBoardUtil {

    /**
     * 实现文本复制功能
     * add by wangqianzhou
     *
     * @param content
     */
    public static void copy(Context context, String content) {

        try {
            // 得到剪贴板管理器
            ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);

            ClipData cd = ClipData.newPlainText("label", content);
            cmb.setPrimaryClip(cd);
            // cmb.setText(content.trim());
        } catch (Exception ignore) {
            // e.printStackTrace();
        }
    }

    /**
     * 实现粘贴功能
     * add by wangqianzhou
     *
     * @param context
     * @return
     */
    public static String paste(Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        if (cmb == null) return null;

        ClipData cd = cmb.getPrimaryClip();
        if (cd == null) return null;

        ClipData.Item item = cd.getItemAt(0);
        if (item.getText() == null) return null;

        return item.getText().toString();
        // return cmb.getText().toString().trim();
    }
}
