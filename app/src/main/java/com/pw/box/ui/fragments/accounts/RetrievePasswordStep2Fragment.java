package com.pw.box.ui.fragments.accounts;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.common.bean.BaseRes;
import com.pw.box.R;
import com.pw.box.core.C;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.cmds.ResetPasswordTask;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.widgets.PasswordLengthChecker;
import com.pw.box.utils.StringUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

/**
 * * 找回密码第二步 填写新密码
 * 注：找回密码分2步，第一步填写答案
 * 第二步填写新密码
 * Created by danger on 16/8/28.
 */
public class RetrievePasswordStep2Fragment extends BaseFragment {
    // public static final String EXTRA_QUESTION = "question";
    public static final String EXTRA_ACCOUNT = "account";
    public static final String EXTRA_ANSWER = "answer";
    public static final String EXTRA_RAW_KEY = "raw_key";

    PasswordLengthChecker lengthChecker = new PasswordLengthChecker();


    String account;
    String answer;
    byte[] rawKey;

    ViewHolder holder;
    TextWatcher passowrdWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            String password = holder.etPassword.getText().toString();

            int len = lengthChecker.getLength(password);

            if (password.trim().length() != password.length()) {
                holder.etPassword.setHelperText(getString(R.string.error_password_space));
            } else if (password.isEmpty()) {
                holder.etPassword.setHelperText(getString(R.string.error_password_empty));
            } else if (lengthChecker.getLength(password) < 8 || len > holder.etPassword.getMaxCharacters()) {
                holder.etPassword.setHelperText(getString(R.string.error_passsword_len));
            } else {
                holder.etPassword.setHelperText("");
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle args = getArguments();
        if (args == null) {
            return;
        }

        account = args.getString(EXTRA_ACCOUNT);
        answer = args.getString(EXTRA_ANSWER);
        rawKey = args.getByteArray(EXTRA_RAW_KEY);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new ViewHolder(inflater, container);

        // -for test ☟
        // holder.etPassword.setText("大家好1234");
        // holder.etPassword1.setText("大家好1234");
        // -for test ☝︎

        return holder.rootView;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn:
                onClickSubmit();
                break;
        }
    }

    @Override
    public boolean onBackPressed() {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext());
        dlgBuilder.setTitle(R.string.tip);
        dlgBuilder.setMessage(R.string.message_abandon_retrieve_password);

        dlgBuilder.setNegativeButton(R.string.misoperation, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dlgBuilder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                // ContainerActivity.goClearTask(activity, LoginFragment.class, null);
                setResult(Activity.RESULT_OK);
                finish();
                // popTo(LoginFragment.class);
            }
        });
        dlgBuilder.setCancelable(false);
        dlgBuilder.create().show();
        return true;
    }

    private void onClickSubmit() {
        final String password = holder.etPassword.getText().toString().trim();
        String password1 = holder.etPassword1.getText().toString().trim();


        /*int tooSimple = StringUtil.checkPasswordTooSimple(password);

        if (tooSimple != StringUtil.PW_ISVALID) {
            toast("原密码不正确");
            return;
        }*/
        final int tooSimple = StringUtil.checkPasswordTooSimple(password1);

        if (!TextUtils.equals(password, password1)) {
            toast(R.string.error_password_not_same);
        } else {
            if (tooSimple != 0) {
                switch (tooSimple) {
                    case StringUtil.PW_TOO_LEN_INVALID:
                        toast(getString(R.string.error_pw_len_error, C.min_password_len, C.max_password_len));
                        break;
                    case StringUtil.PW_TOO_SIMPLE_NO_HANZI:
                        toast(R.string.error_password_must_contain_hani);
                        break;
                    default:
                        toast(R.string.error_password_is_too_simple);
                }
                return;
            }
        }

        showProgressDialog(R.string.please_wait_moment);
        N.NetHandler<BaseRes> cb = new N.NetHandler<BaseRes>() {
            @Override
            public void onSuccess(BaseRes retPack) {
                /*if(retPack.g)*/
                dismissDialog();

                if (retPack.retCode == ErrorCodes.SUCCESS) {
                    toast(R.string.success);

                    SpannableStringBuilder builder = new SpannableStringBuilder();
                    String msg = getString(R.string.message_reset_password_success, password);
                    builder.append(msg);
                    int start = msg.indexOf("\n");
                    int end = msg.indexOf("\n", start + 2);
                    builder.setSpan(new ForegroundColorSpan(0xffff0000), start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                    AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext());
                    dlgBuilder.setTitle(R.string.tip);
                    dlgBuilder.setMessage(builder);

                    dlgBuilder.setPositiveButton(R.string.go_login, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setResult(Activity.RESULT_OK);
                            finish();
                        }
                    });
                    dlgBuilder.setCancelable(false);
                    dlgBuilder.create().show();
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

        new ResetPasswordTask(account, answer, rawKey, password)
                .setCallBack(cb).execute();
        // Cm.get().resetPassword(account, answer, rawKey, password, );
    }

    private class ViewHolder {
        View rootView;

        MaterialEditText etPassword;
        MaterialEditText etPassword1;

        public ViewHolder(LayoutInflater inflater, ViewGroup container) {
            rootView = inflater.inflate(R.layout.fragment_retrieve_password2, container, false);

            etPassword = rootView.findViewById(R.id.et_password);
            etPassword1 = rootView.findViewById(R.id.et_password1);

            etPassword.setLengthChecker(lengthChecker);
            etPassword1.setLengthChecker(lengthChecker);
            etPassword.addTextChangedListener(passowrdWatcher);
            rootView.findViewById(R.id.btn).setOnClickListener(RetrievePasswordStep2Fragment.this);
        }
    }
}
