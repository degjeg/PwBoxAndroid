package com.pw.box.ui.fragments.accounts;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pw.box.R;
import com.pw.box.bean.protobuf.GetProtectRes;
import com.pw.box.bean.protobuf.RetrievePassRes;
import com.pw.box.core.C;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.cmds.GetQuestionTask;
import com.pw.box.core.cmds.VerifyAnswerTask;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.widgets.PasswordLengthChecker;
import com.pw.box.utils.Aes256;
import com.pw.box.utils.StringUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

/**
 * 找回密码第一步，填写密码保护问题的答案
 * 注：找回密码分2步，第一步填写答案
 * 第二步填写新密码
 * Created by danger on 16/8/28.
 */
public class RetrievePasswordStep1Fragment extends BaseFragment {
    // public static final String EXTRA_QUESTION = "question";
    public static final java.lang.String EXTRA_ACCOUNT = "account";
    private static HashMap<String, String> noProtectAccounts = new HashMap<>();

    ViewHolder holder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new ViewHolder(inflater, container);

        Bundle arg = getArguments();
        String account = arg.getString(EXTRA_ACCOUNT);
        holder.etAccount.setText(account);

        // -for test ☟
        // holder.etAnswer.setText("答案1234");
        // -for test ☝︎

        if (!TextUtils.isEmpty(account) && account.length() >= 3) {
            if (!noProtectAccounts.containsKey(account)) {
                retrieveStep1(account, null);
            } else if (noProtectAccounts.get(account).length() < 3) { // 无密码保护
                toast(R.string.message_account_not_set_protect);
                // toast("R.string.message_account_not_set_protect");
                finishDelayed(2000);

            } else {
                // 已有正确的密码保护
                setQuestion(noProtectAccounts.get(account));
            }
        }
        return holder.rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        holder = null;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn:
                onClickRetrievePassword();
                break;
        }
    }

    private void setQuestion(String question) {
        if (!question.endsWith("?")) {
            question += "?";
        }

        holder.etAnswer.setFloatingLabelText(question);
        holder.etAnswer.setFloatingLabelAlwaysShown(true);
    }

    private void onClickRetrievePassword() {
        final String account = holder.etAccount.getText().toString().trim();
        final String answer = holder.etAnswer.getText().toString().trim();

        if (account.length() < C.min_account_len) {
            toast(R.string.message_error_account);
            return;
        }

        if (noProtectAccounts.containsKey(account)) { // 该账号已经请求过
            String question = noProtectAccounts.get(account);
            if (TextUtils.isEmpty(question) && question.length() <= 3) {
                toast(R.string.message_account_not_set_protect);
                return;
            }

            setQuestion(question);
            int tooSimple = StringUtil.checkPasswordTooSimple(answer);
            if (tooSimple != StringUtil.PW_ISVALID) {
                switch (tooSimple) {
                    case StringUtil.PW_TOO_LEN_INVALID:
                        toast(getString(R.string.error_answer_len_error, C.min_password_len, C.max_password_len));
                        break;
                    case StringUtil.PW_TOO_SIMPLE_NO_HANZI:
                        toast(R.string.error_answer_must_contains_a_full_c);
                        break;
                    default:
                        toast(R.string.answer_is_too_simple);
                }
                return;
            }
            retrieveStep2(account, answer);
        } else {
            retrieveStep1(account, answer);
        }
    }

    private void retrieveStep1(final String account, final String answer) {
        showProgressDialog(false);
        N.NetHandler<GetProtectRes> cb = new N.NetHandler<GetProtectRes>() {
            @Override
            public void onSuccess(GetProtectRes retPack) {
                dismissDialog();
                if (holder == null) {
                    return;
                }
                if (retPack.question != null /*&& retPack.getQuestion().length() < 3*/) {
                    // M.GetProtecReq req = (M.GetProtecReq) reqPack;
                    noProtectAccounts.put(account, retPack.question);
                    if (holder != null) {
                        if (retPack.question.length() >= 3) {
                            setQuestion(retPack.question);

                            if (!TextUtils.isEmpty(answer)) {
                                retrieveStep2(account, answer);
                            }
                        } else {
                            toast(R.string.message_account_not_set_protect);
                            finish();
                        }
                    }

                } else {
                    onFail(ErrorCodes.UNKNOWN_ERROR, null);
                }
            }

            @Override
            public void onFail(int localErrorCode, Exception e) {
                dismissDialog();
                toast(ErrorCodes.getErrorDescription(localErrorCode));
            }
        };

        new GetQuestionTask(account).setCallBack(cb).execute();
        // Cm.get().getQuestion(account,
    }

    private void retrieveStep2(final String account, final String answer) {
        showProgressDialog(false);
        N.NetHandler<RetrievePassRes> cb = new N.NetHandler<RetrievePassRes>() {
            @Override
            public void onSuccess(RetrievePassRes retPack) {
                dismissDialog();
                if (holder == null) {
                    return;
                }
                if (retPack.retCode == ErrorCodes.SUCCESS) {
                    toast(R.string.anwer_verify_passed);
                    // retPack.getRawKeyByAnswer();
                    try {
                        Bundle bundle = new Bundle();
                        bundle.putString(RetrievePasswordStep2Fragment.EXTRA_ACCOUNT, account);
                        bundle.putString(RetrievePasswordStep2Fragment.EXTRA_ANSWER, answer);
                        byte[] key = Aes256.fillKey(answer, Aes256.FILL_TYPE_RAW_KEY);
                        byte[] k = Aes256.decrypt(retPack.raw_key_by_answer.toByteArray(), key);

                        bundle.putByteArray(RetrievePasswordStep2Fragment.EXTRA_RAW_KEY, k);
                        // showFragment(RetrievePasswordStep2Fragment.class, bundle);

                        finish();
                        startActivityForResult(ContainerActivity.getIntent(activity, RetrievePasswordStep2Fragment.class, bundle), 1);
                    } /*catch (UnsupportedEncodingException e) {
                        toast(R.string.failed);
                    } */ catch (Exception e) {
                        e.printStackTrace();
                        toast(R.string.failed);
                    }

                } else {
                    toast(ErrorCodes.getErrorDescription(retPack.retCode));
                }
            }

            @Override
            public void onFail(int localErrorCode, Exception e) {
                dismissDialog();
                toast(ErrorCodes.getErrorDescription(localErrorCode));
            }
        };

        new VerifyAnswerTask(account, answer).setCallBack(cb).execute();
        // Cm.get().verifyAnswer(account, answer, );
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 1) {
                finish();
            }
        }
    }

    private class ViewHolder {
        View rootView;


        MaterialEditText etAccount;
        // MaterialEditText etQuestion;
        MaterialEditText etAnswer;

        public ViewHolder(LayoutInflater inflater, ViewGroup container) {
            rootView = inflater.inflate(R.layout.fragment_retrieve_password, container, false);

            etAccount = rootView.findViewById(R.id.et_account);
            // etQuestion = (MaterialEditText) rootView.findViewById(R.id.et_question);
            etAnswer = rootView.findViewById(R.id.et_answer);

            etAnswer.setLengthChecker(new PasswordLengthChecker());
            rootView.findViewById(R.id.btn).setOnClickListener(RetrievePasswordStep1Fragment.this);
        }
    }
}
