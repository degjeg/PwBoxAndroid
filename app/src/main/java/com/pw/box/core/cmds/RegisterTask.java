package com.pw.box.core.cmds;


import com.common.bean.BaseRes;
import com.pw.box.bean.protobuf.RegisterReq;
import com.pw.box.core.Cm;
import com.pw.box.utils.Aes256;
import com.squareup.wire.Message;

import okio.ByteString;

/**
 * 注册任务
 * Created by danger on 16/10/26.
 */

public class RegisterTask extends Task<BaseRes> {
    String account;
    String password;

    public RegisterTask(String account, String password) {
        super(false);
        cmd = CmdIds.REGISTER;
        this.account = account;
        this.password = password;

    }

    @Override
    public String getAccount() {
        return account;
    }

    @Override
    protected Message prepareData() throws Exception {
        // Thread.sleep(3000);
        RegisterReq.Builder builder = new RegisterReq.Builder();
        builder.account(account);

        byte[] rawKey = new byte[32];
        Cm.get().random.nextBytes(rawKey);

        // 1.登录使用的密码
        builder.pw_loggin(ByteString.of(Aes256.encrypt(account.getBytes(), password, Aes256.FILL_TYPE_PW)));
        // 2.加使用的密码
        builder.pw_encrypt(ByteString.of(Aes256.encrypt(rawKey, password, Aes256.FILL_TYPE_RAW_KEY)));

        builder.device_info(Cm.get().deviceInfo);
        return builder.build();
    }
}
