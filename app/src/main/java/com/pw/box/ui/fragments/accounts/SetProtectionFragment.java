package com.pw.box.ui.fragments.accounts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.common.bean.BaseRes;
import com.pw.box.R;
import com.pw.box.bean.protobuf.GetProtectRes;
import com.pw.box.cache.Cache;
import com.pw.box.cache.User;
import com.pw.box.core.C;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.cmds.ChangeAnswerTask;
import com.pw.box.core.cmds.GetQuestionTask;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.home.HomeFragment;
import com.pw.box.ui.widgets.PasswordLengthChecker;
import com.pw.box.utils.StringUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * 设置密码保护界面
 * Created by danger on 16/8/28.
 */
public class SetProtectionFragment extends BaseFragment {


    ViewHolder holder;

    private String question;
    private String answer;
    private N.NetHandler<GetProtectRes> getQuestionHandler = new N.NetHandler<GetProtectRes>() {
        @Override
        public void onSuccess(GetProtectRes retPack) {
            dismissDialog();
            if (!TextUtils.isEmpty(retPack.question) && retPack.question.length() >= 3) {
                // 成功得到了密码保护问题
                Cache.get().getUser().setQuestion(retPack.question);
                if (holder != null) {
                    setOldQuestion(Cache.get().getUser().getQuestion());
                    // holder.etOldAnswer.setHint(Cache.get().getUser().getQuestion());
                    // holder.etOldAnswer.setFloatingLabelText(getString(R.string.hint_answer_question));

                    holder.btn.setText(R.string.submit);
                }
            } else { // 没有设置过保护
                if (holder != null) {
                    holder.etQuestion.setVisibility(View.GONE);
                }
            }
        }

        @Override
        public void onFail(int localErrorCode, Exception e) {
            dismissDialog();
            toast(ErrorCodes.getErrorDescription(localErrorCode));

            if (holder != null) {
                holder.btn.setText(R.string.retry);
            }
        }

    };
    private N.NetHandler<BaseRes> changeAnswerHandler = new N.NetHandler<BaseRes>() {
        @Override
        public void onSuccess(BaseRes retPack) {
            dismissDialog();
            if (retPack.retCode == ErrorCodes.SUCCESS) {
                toast(R.string.protection_is_set);

                // save data to local ☟
                User user = Cache.get().getUser();
                user.setHavePretection(true);
                user.setQuestion(question);
                user.setAnswer(answer);
                // save data to local ☝︎

                finish();
            } else { // 没有设置过保护
                toast(ErrorCodes.getErrorDescription(retPack.retCode));
            }
        }


        @Override
        public void onFail(int localErrorCode, Exception e) {
            dismissDialog();
            toast(ErrorCodes.getErrorDescription(localErrorCode));
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle arg = getArguments();
        // if (arg == null) {
        //     // popupFragment();
        // }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_set_protection, container, false);
        holder = new ViewHolder(v);

        if (!Cache.get().getUser().isHavePretection()) { // 已经有密保,需要先验证
            holder.etOldAnswer.setVisibility(View.GONE);
            holder.btn.setText(R.string.set_protection);
        } else {
            holder.btn.setText(R.string.modify_protection);

            // 暂无密保,需要先验证
            if (!TextUtils.isEmpty(Cache.get().getUser().getQuestion())) {
                setOldQuestion(Cache.get().getUser().getQuestion());
                // holder.etQuestion.setHint(Cache.get().getUser().getQuestion());
                // holder.etQuestion.setFloatingLabelText(getString(R.string.hint_answer_question));
            } else {
                showProgressDialog(R.string.hint_getting_protection);
                new GetQuestionTask(Cache.get().getUser().getAccount())
                        .setCallBack(getQuestionHandler)
                        .execute();

                // Cm.get().getQuestion(Cache.get().getUser().getAccount(), getQuestionHandler);
            }
        }


        // -for test ☟
        // holder.etAnswer.setText("答案1234");
        // -for test ☝︎

        return holder.rootView;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn:
                onClickSure();
                break;
        }
    }

    private void onClickSure() {
        User user = Cache.get().getUser();
        if (user.haveProtect() && TextUtils.isEmpty(user.getQuestion())) {
            // 还没有获取到问题
            showProgressDialog(R.string.hint_getting_protection);

            new GetQuestionTask(Cache.get().getUser().getAccount())
                    .setCallBack(getQuestionHandler)
                    .execute();
            // Cm.get().getQuestion(Cache.get().getUser().getAccount(), getQuestionHandler);
        } else {
            // 已获取到问题
            String answerOld = null; // holder.etOldAnswer.getText().toString().trim();
            question = holder.etQuestion.getText().toString().trim();
            answer = holder.etAnswer.getText().toString().trim();

            if (TextUtils.isEmpty(question) || question.length() < C.min_account_len) {
                toast(R.string.question_too_short);
                return;
            }
            if (user.isHavePretection()) {
                answerOld = holder.etOldAnswer.getText().toString().trim();
                int isValid = StringUtil.checkPasswordTooSimple(answerOld);
                if (isValid != StringUtil.PW_ISVALID) {
                    toast(R.string.tip_answer_error);
                    return;
                }
            }

            int tooSimple = StringUtil.checkPasswordTooSimple(answer);
            if (tooSimple != 0) {
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
            showProgressDialog(false);
            new ChangeAnswerTask(answerOld, question, answer)
                    .setCallBack(changeAnswerHandler)
                    .execute();
            // Cm.get().changeAnswer(answerOld, question, answer, changeAnswerHandler);
        }
    }

    private void setOldQuestion(String question) {
        if (!question.endsWith("?")) {
            question += "?";
        }

        holder.etOldAnswer.setFloatingLabelText(question);
        holder.etOldAnswer.setFloatingLabelAlwaysShown(true);
    }

    private void goToHomePage() {
        ContainerActivity activity = (ContainerActivity) getActivity();
        ContainerActivity.goClearTask(activity, HomeFragment.class, null);
    }

    @Override
    public boolean onBackPressed() {
        if (activity == null) {
            return false;
        }

        // Counter.get().setRunning();
        ContainerActivity.goClearTask(activity, HomeFragment.class, null);

        return true;
    }

    private class ViewHolder {
        View rootView;

        MaterialEditText etOldAnswer;
        MaterialEditText etQuestion;
        MaterialEditText etAnswer;


        TextView btn;

        public ViewHolder(View v) {
            rootView = v;

            etQuestion = rootView.findViewById(R.id.et_question);
            etOldAnswer = rootView.findViewById(R.id.et_old_answer);
            etAnswer = rootView.findViewById(R.id.et_answer);
            btn = rootView.findViewById(R.id.btn);

            etOldAnswer.setLengthChecker(new PasswordLengthChecker());
            etAnswer.setLengthChecker(new PasswordLengthChecker());

            btn.setOnClickListener(SetProtectionFragment.this);
        }
    }
}
