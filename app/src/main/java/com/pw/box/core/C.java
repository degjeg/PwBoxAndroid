package com.pw.box.core;

import com.pw.box.App;
import com.pw.box.R;

/**
 * 全局常量
 * Created by danger on 16/8/19.
 */
public class C {

    public static final int PING = 1;
    public static final int REG_CMD = 2;
    public static final int REG_PROXY = 3; // 通知接入服务器接入

    public static final int MAX_CLIENT_PACK_LEN = 65534;

    // 数据量达到这个值就会自动尝试gip压缩
    public static final int COMPRESS_TRIGGER = 300;

    public static final int min_question_len = App.getContext().getResources().getInteger(R.integer.min_question_len);
    public static final int max_question_len = App.getContext().getResources().getInteger(R.integer.max_question_len);
    public static final int min_account_len = App.getContext().getResources().getInteger(R.integer.min_account_len);
    public static final int max_account_len = App.getContext().getResources().getInteger(R.integer.max_account_len);
    public static final int min_password_len = App.getContext().getResources().getInteger(R.integer.min_password_len);
    public static final int max_password_len = App.getContext().getResources().getInteger(R.integer.max_password_len);
    public static final int password_len = App.getContext().getResources().getInteger(R.integer.password_len);

}
