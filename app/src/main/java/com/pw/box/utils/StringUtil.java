package com.pw.box.utils;

import com.pw.box.core.C;
import com.pw.box.ui.widgets.PasswordLengthChecker;

/**
 * 字符串工具类
 * Created by danger on 16/9/12.
 */
public class StringUtil {

    public static final int PW_ISVALID = 0;
    public static final int PW_TOO_LEN_INVALID = 1;
    public static final int PW_TOO_SIMPLE_NO_HANZI = 2;
    public static final int PW_TOO_SIMPLE_SAME = 3; // 6个相同的字符

    public static int checkPasswordTooSimple(String password) {

        int lenPw = new PasswordLengthChecker().getLength(password);
        if (lenPw < C.min_password_len || lenPw > C.max_password_len) {
            // 长度不合法
            return PW_TOO_LEN_INVALID;
        }

        /* boolean findHanZi = false;
         for (int i = 0; i < password.length(); i++) {
            if ((password.charAt(i) & 0xff00) != 0) {
                findHanZi = true;
                break;

            }
        }
        if (!findHanZi) {
            //  errorMsg.append("为提高安全性,密码至少包含一个汉字");
            return PW_TOO_SIMPLE_NO_HANZI;
        } */

        int sameCount = 0;
        int upCount = 0;
        int downCount = 0;
        char c = password.charAt(0);
        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == c) {
                sameCount++;
                if (sameCount >= 6) {
                    // errorMsg.append("密码过于简单");
                    return PW_TOO_SIMPLE_SAME;
                }
            } else {
                sameCount = 1;
            }

            if (password.charAt(i) == (c + 1)) {
                upCount++;
                if (upCount >= 6) {
                    // errorMsg.append("密码过于简单");
                    return PW_TOO_SIMPLE_SAME;
                }
            } else {
                upCount = 1;
            }

            if (password.charAt(i) == (c - 1)) {
                downCount++;
                if (downCount >= 6) {
                    // errorMsg.append("密码过于简单");
                    return PW_TOO_SIMPLE_SAME;
                }
            } else {
                downCount = 1;
            }
            c = password.charAt(i);
        }


        return 0;
    }

    public static boolean isNumber(String string) {
        for (Character c : string.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }
}
