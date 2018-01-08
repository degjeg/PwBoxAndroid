package com.pw.box.core.cmds;


import com.pw.box.bean.protobuf.RetrievePassRes;
import com.pw.box.bean.protobuf.RetrievePasswordReq;
import com.pw.box.utils.Aes256;
import com.squareup.wire.Message;

import okio.ByteString;

/**
 */
public class VerifyAnswerTask extends Task<RetrievePassRes> {
    String account;
    String answer;

    public VerifyAnswerTask(String account, String answer) {
        this.account = account;
        this.answer = answer;
        needLogin = false;
        cmd = CmdIds.VERIFY_ANSWER;
    }

    @Override
    protected Message prepareData() throws Exception {
        RetrievePasswordReq.Builder builder = new RetrievePasswordReq.Builder();
        // builder.setAccount(account);
        builder.account(account);
        builder.answer(ByteString.of(Aes256.encrypt(account.getBytes(), answer, Aes256.FILL_TYPE_PROTECT)));

        return builder.build();
    }
}
