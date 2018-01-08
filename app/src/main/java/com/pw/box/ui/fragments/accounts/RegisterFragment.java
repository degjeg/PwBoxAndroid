package com.pw.box.ui.fragments.accounts;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;

import com.common.bean.BaseRes;
import com.pw.box.BuildConfig;
import com.pw.box.R;
import com.pw.box.core.C;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.cmds.RegisterTask;
import com.pw.box.tool.StatTool;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.widgets.PasswordLengthChecker;
import com.pw.box.utils.StringUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashSet;


/**
 * 注册界面
 * Created by danger on 16/8/28.
 */
public class RegisterFragment extends BaseFragment implements N.NetHandler<BaseRes> {

    static HashSet<String> unusableSet = new HashSet<>();
    PasswordLengthChecker lengthChecker = new PasswordLengthChecker();
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
                holder.etPassword.setHelperText(getString(R.string.error_password_len));
            } else {
                holder.etPassword.setHelperText("");
            }
        }
    };
    View.OnFocusChangeListener pw2FocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View view, boolean b) {
            if (b) {
                scrollToBottom();
            }
        }
    };

    private void scrollToBottom() {
        holder.scrollingView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (holder == null) {
                    return;
                }
                holder.scrollingView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        }, 300);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new ViewHolder(inflater, container);

        // -for test ☟
        if (BuildConfig.DEBUG) {
            holder.etAccount.setText("测试1");
            holder.etPassword.setText("测试1234");
            holder.etPassword1.setText("测试1234");
        }
        // -for test ☝︎

        holder.rootView.findViewById(R.id.btn_register).setOnClickListener(this);
        holder.etAccount.requestFocus();
        holder.etPassword1.setOnFocusChangeListener(pw2FocusChangeListener);
        holder.etPassword1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scrollToBottom();
            }
        });
        return holder.rootView;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_register:
                onClickRegister();
                break;
        }
    }

    @Override
    public void onSuccess(BaseRes retPack) {
        String account = holder.etAccount.getText().toString().trim();
        dismissDialog();
        if (retPack.retCode == ErrorCodes.SUCCESS) {
            StatTool.trackEvent(StatTool.EVENT_REGISTER, retPack.retCode);
            toast(R.string.register_success);


            String password = holder.etPassword.getText().toString().trim();

            Intent bundle = new Intent();
            bundle.putExtra("account", account);
            bundle.putExtra("password", password);
            setResult(Activity.RESULT_OK, bundle);
            finish();
        } else {
            if (retPack.retCode == ErrorCodes.ACCOUNT_EXIST) {
                unusableSet.add(account);
            }
            StatTool.trackEvent(StatTool.EVENT_REGISTER, retPack.retCode);
            toast(ErrorCodes.getErrorDescription(retPack.retCode));
        }
    }

    @Override
    public void onFail(int localErrorCode, Exception e) {
        dismissDialog();
        toast(ErrorCodes.getErrorDescription(localErrorCode));
        StatTool.trackEvent(StatTool.EVENT_REGISTER, localErrorCode);
    }

    private void onClickRegister() {
        final String account = holder.etAccount.getText().toString().trim();
        final String password = holder.etPassword.getText().toString().trim();
        final String password1 = holder.etPassword1.getText().toString().trim();

        if (unusableSet.contains(account)) {
            toast(R.string.error_account_already_exist);
            return;
        }

        if (account.length() < C.min_account_len) {
            toast(getString(R.string.error_account_too_short, C.min_account_len));
        } /*else if (account.contains("@")) {
            toast(R.string.error_account_unsupport_email);
        } else if (StringUtil.isNumber(account)) {
            toast(R.string.error_account_unsupport_phone);
        } */ else if (!TextUtils.equals(password, password1)) {
            toast(R.string.error_password_not_same);
        } else {
            int tooSimple = StringUtil.checkPasswordTooSimple(password);
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


            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle(R.string.tip);
            builder.setMessage(R.string.message_dlg_register);
            builder.setNegativeButton(R.string.i_see, null);
            builder.setPositiveButton(R.string.register, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                    showProgressDialog(R.string.message_registering);
                    new RegisterTask(account, password).setCallBack(RegisterFragment.this).execute();
                }
            });

            Dialog dialog = builder.create();

            dialog.show();

            // Cm.get().register(account, password, this);
        }
    }



   /* boolean checkPasswordTooSimple(String password) {
        boolean findHanZi = false;
        for (int i = 0; i < password.length(); i++) {
            if ((password.charAt(i) & 0x80) != 0) {
                findHanZi = true;
                break;

            }
        }
        if (!findHanZi) {
            toast("为提高安全性,密码至少包含一个汉字");
            return true;
        }

        int sameCount = 0;
        int upCount = 0;
        int downCount = 0;
        char c = password.charAt(0);
        for (int i = 1; i < password.length(); i++) {
            if (password.charAt(i) == c) {
                sameCount++;
                if (sameCount >= 6) {
                    toast("密码过于简单");
                    return true;
                }
            } else {
                sameCount = 1;
            }

            if (password.charAt(i) == (c + 1)) {
                upCount++;
                if (upCount >= 6) {
                    toast("密码过于简单");
                    return true;
                }
            } else {
                upCount = 1;
            }

            if (password.charAt(i) == (c - 1)) {
                downCount++;
                if (downCount >= 6) {
                    toast("密码过于简单");
                    return true;
                }
            } else {
                downCount = 1;
            }
            c = password.charAt(i);
        }


        return false;
    }*/

    private class ViewHolder {
        View rootView;

        ScrollView scrollingView;
        MaterialEditText etAccount;
        MaterialEditText etPassword;
        MaterialEditText etPassword1;

        public ViewHolder(LayoutInflater inflater, ViewGroup container) {
            rootView = inflater.inflate(R.layout.fragment_register, container, false);


            scrollingView = rootView.findViewById(R.id.scroll_view);

            etAccount = rootView.findViewById(R.id.et_account);
            etPassword = rootView.findViewById(R.id.et_password);
            etPassword1 = rootView.findViewById(R.id.et_password1);

            etPassword.setLengthChecker(lengthChecker);
            etPassword1.setLengthChecker(lengthChecker);
            etPassword.addTextChangedListener(passowrdWatcher);
        }
    }
}
