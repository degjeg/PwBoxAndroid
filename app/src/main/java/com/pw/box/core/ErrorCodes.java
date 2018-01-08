package com.pw.box.core;

import android.content.res.Resources;

import com.pw.box.App;
import com.pw.box.R;

/**
 * 全局错误码，
 * Created by danger on 16/9/10.
 */
public class ErrorCodes {
    // local errcodes ☟
    public static final int NO_NET = -10000; // 网络未连接
    public static final int TRY_AGAIN = -10001; // 网络未连接
    public static final int NOT_LOGIN = -10002;
    public static final int TIMEOUT = -10003;

    public static final int PREPAREDATA = -10004;
    public static final int NOT_CONNECTED = -10005;
    public static final int CONNECT_FAIL = -10006;
    // local errcodes ☝︎


    public static final int SUCCESS = 0; // 无错误

    public static final int UNKNOWN_ERROR = 1;
    public static final int DATABASE_ERROR = 2;
    public static final int DATA_IS_NEW = 3;

    // 账号相关错误码
    public static final int ACCOUNT_EXIST = 10; // 注册时账号已经存在
    public static final int ACCOUNT_NOT_EXIST = 11; // 登录时账号不存在
    public static final int ACCOUNT_NOT_SUPPORT_EMAIL = 12; //
    public static final int ACCOUNT_NOT_SUPPORT_PHONE = 13; //
    public static final int ACCOUNT_LEN_ERROR = 14; //
    public static final int ACCOUNT_IS_FORBIDDEN = 15; // 账号被禁用
    public static final int DEVICE_IS_FORBIDDEN = 16; // 设备被禁用

    // 密码相关
    public static final int PASSOWRD_LEN_ERROR = 50; // 注册时密码长度不合法
    public static final int PASSWORD_ERROR = 51; // 登录时密码错误,修改密码时原密码错误
    public static final int ANSWER_ERROR = 52; // 修改密码保护答案时原答案错误


    public static String getErrorDescription(int errorCode) {
        String desc;
        Resources res = App.getContext().getResources();
        switch (errorCode) {
            // local errcodes ☟
            case NO_NET: // = -10000; // 网络未连接
                return res.getString(R.string.error_no_net);
            case TRY_AGAIN: //  = -10001; // 网络未连接
            case NOT_CONNECTED:
                return res.getString(R.string.error_please_try_later);
            case NOT_LOGIN: //  = -10002;
                return res.getString(R.string.error_please_login_first);
            case TIMEOUT: //  = -10002;
                return res.getString(R.string.timeout);
            // local errcodes ☝︎

            case SUCCESS:
                return res.getString(R.string.error_no_error);

            case UNKNOWN_ERROR:
                return res.getString(R.string.error_unknown_error);

            case ACCOUNT_NOT_EXIST:
                return res.getString(R.string.error_account_not_exist);

            case ACCOUNT_NOT_SUPPORT_EMAIL:
                return res.getString(R.string.error_account_unsupport_email);

            case ACCOUNT_NOT_SUPPORT_PHONE:
                return res.getString(R.string.error_account_unsupport_phone);

            case ACCOUNT_IS_FORBIDDEN:
                return res.getString(R.string.error_account_is_forbidden);

            case DEVICE_IS_FORBIDDEN:
                return res.getString(R.string.error_device_is_forbidden);

            case ACCOUNT_EXIST:
                return res.getString(R.string.error_account_already_exist);

            case ACCOUNT_LEN_ERROR:
                return res.getString(R.string.error_account_len);

            case PASSOWRD_LEN_ERROR:
                return res.getString(R.string.error_password_len);

            case PASSWORD_ERROR:
                return res.getString(R.string.error_password_error);

            case ANSWER_ERROR:
                return res.getString(R.string.error_answer_error);

            default:
                return res.getString(R.string.error_unknown_error_code, errorCode);

        }
    }
}
