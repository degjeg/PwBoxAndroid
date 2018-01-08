package com.pw.box.core.cmds;


import com.common.bean.BaseRes;
import com.pw.box.bean.protobuf.RetrievePasswordReq;
import com.pw.box.utils.Aes256;
import com.squareup.wire.Message;

import okio.ByteString;

/**
 */
public class ResetPasswordTask extends Task<BaseRes> {
    String account;
    String answer;
    byte[] rawKey;
    String password;

    public ResetPasswordTask(String account, String answer, byte[] rawKey,
                             String password) {
        super(false);
        this.account = account;
        this.answer = answer;
        this.rawKey = rawKey;
        this.password = password;
        cmd = CmdIds.RETRIEVE_PASSWORD;
    }

    @Override
    protected Message prepareData() throws Exception {
        RetrievePasswordReq.Builder builder = new RetrievePasswordReq.Builder();

        builder.account(account);
        builder.answer(ByteString.of(Aes256.encrypt(account.getBytes(), answer, Aes256.FILL_TYPE_PROTECT)));
        builder.password(ByteString.of(Aes256.encrypt(account.getBytes(), password, Aes256.FILL_TYPE_PW)));
        builder.raw_key_by_password(ByteString.of(Aes256.encrypt(rawKey, password, Aes256.FILL_TYPE_RAW_KEY)));

        return builder.build();
    }
}
