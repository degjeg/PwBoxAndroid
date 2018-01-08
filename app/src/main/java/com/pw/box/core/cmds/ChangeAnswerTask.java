package com.pw.box.core.cmds;

import android.text.TextUtils;

import com.common.bean.BaseRes;
import com.pw.box.bean.protobuf.SetProtect;
import com.pw.box.cache.Cache;
import com.pw.box.cache.User;
import com.pw.box.utils.Aes256;
import com.squareup.wire.Message;

import okio.ByteString;

/**
 */
public class ChangeAnswerTask extends Task<BaseRes> {
    String answerOld;
    String question;
    String answer;

    public ChangeAnswerTask(String answerOld, String question, String answer) {
        this.answerOld = answerOld;
        this.question = question;
        this.answer = answer;

        cmd = CmdIds.SET_ANSWER;
    }

    @Override
    protected Message prepareData() throws Exception {
        User user = Cache.get().getUser();
        String account = user.getAccount();

        SetProtect.Builder builder = new SetProtect.Builder();
        if (!TextUtils.isEmpty(answerOld)) {
            builder.old_answer(ByteString.of(Aes256.encrypt(account.getBytes(), answerOld, Aes256.FILL_TYPE_PROTECT)));
        }

        builder.new_answer(ByteString.of(Aes256.encrypt(account.getBytes(), answer, Aes256.FILL_TYPE_PROTECT)));
        builder.new_question(question);

        builder.raw_key_by_answer(ByteString.of(Aes256.encrypt(user.getRawKey(), answer, Aes256.FILL_TYPE_RAW_KEY)));

        return builder.build();
    }
}
