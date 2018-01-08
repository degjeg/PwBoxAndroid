package com.pw.box.core.cmds;


import com.pw.box.bean.protobuf.GetProtecReq;
import com.pw.box.bean.protobuf.GetProtectRes;
import com.squareup.wire.Message;

/**
 * 获取密码保护问题的工具类
 * Created by danger on 16/10/27.
 */

public class GetQuestionTask extends Task<GetProtectRes> {
    String account;

    public GetQuestionTask(String account) {
        needLogin = false;
        cmd = CmdIds.GET_QUESTION;
        this.account = account;
    }


    @Override
    protected Message prepareData() throws Exception {
        GetProtecReq.Builder builder = new GetProtecReq.Builder();

        builder.account(account);
        return builder.build();
    }
}
