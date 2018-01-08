package com.pw.box.ui.widgets;

import com.rengwuxian.materialedittext.validation.METLengthChecker;

import java.io.UnsupportedEncodingException;

/**
 * 专门用于check密码长度和强度的工具类
 * Created by danger on 16/8/30.
 */
public class PasswordLengthChecker extends METLengthChecker {
    @Override
    public int getLength(CharSequence text) {
        if (text == null) {
            return 0;
        }
        try {
            return text.toString().trim().getBytes("utf-8").length;
        } catch (UnsupportedEncodingException e) {
            // e.printStackTrace();
        }
        return text.length();
    }
}
