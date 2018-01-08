package com.pw.box.core.cmds;


import com.common.bean.BaseRes;
import com.pw.box.bean.protobuf.ChangePasswordReq;
import com.pw.box.cache.Cache;
import com.pw.box.cache.User;
import com.pw.box.utils.Aes256;
import com.squareup.wire.Message;

import okio.ByteString;

/**
 * 修改密码
 */
public class ChangePasswordTask extends Task<BaseRes> {
    String old;
    String newPassword;

    public ChangePasswordTask(String old, String newPassword) {
        super(true);
        this.old = old;
        this.newPassword = newPassword;

        cmd = CmdIds.MODIFY_PASSWORD;
    }

    @Override
    protected Message prepareData() throws Exception {
        ChangePasswordReq.Builder builder = new ChangePasswordReq.Builder();

        User user = Cache.get().getUser();
        String account = user.getAccount();

        builder.old_password(ByteString.of(Aes256.encrypt(account.getBytes(), old, Aes256.FILL_TYPE_PW)));
        builder.new_password(ByteString.of(Aes256.encrypt(account.getBytes(), newPassword, Aes256.FILL_TYPE_PW)));

        builder.raw_key_by_password(ByteString.of(Aes256.encrypt(user.getRawKey(), newPassword, Aes256.FILL_TYPE_RAW_KEY)));

        return builder.build();
    }
}
