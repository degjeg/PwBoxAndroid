package com.pw.box.ui.fragments.accounts;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.common.bean.BaseRes;
import com.pw.box.R;
import com.pw.box.cache.Cache;
import com.pw.box.core.C;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.cmds.ChangePasswordTask;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.widgets.PasswordLengthChecker;
import com.pw.box.utils.StringUtil;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashSet;


/**
 * 修改密码界面
 * Created by danger on 16/8/28.
 */
public class ChangePasswordFragment extends BaseFragment implements N.NetHandler<BaseRes> {

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

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new ViewHolder(inflater, container);

        // -for test ☟
        // holder.etOldPassword.setText("中国1234");
        // holder.etPassword.setText("新密码1234");
        // holder.etPassword1.setText("新密码1234");
        // -for test ☝︎

        holder.rootView.findViewById(R.id.btn_register).setOnClickListener(this);
        return holder.rootView;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.btn_register:
                onClickSure();
                break;
        }
    }

    @Override
    public void onSuccess(BaseRes retPack) {
        String password = holder.etPassword.getText().toString().trim();

        dismissDialog();
        if (retPack.retCode == ErrorCodes.SUCCESS) {
            toast(R.string.success);
            // TODO save to local
            finish();
            if (Cache.get().getPatternUtil().havePattern()) {
                Cache.get().getPatternUtil().setLockPattern(
                        Cache.get().getPatternUtil().getPatternString(),
                        Cache.get().getPatternUtil().getPatternCount()
                );
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

    private void onClickSure() {
        String oldPassword = holder.etOldPassword.getText().toString().trim();
        String password = holder.etPassword.getText().toString().trim();
        String password1 = holder.etPassword1.getText().toString().trim();

        int oldPasswordValid = StringUtil.checkPasswordTooSimple(oldPassword);


        // 1.原密码输入不合法
        if (oldPasswordValid != StringUtil.PW_ISVALID) {
            toast(R.string.error_invalid_ori_password);
            return;
        }

        // 2.新密码输入不合法
        int passwordValid = StringUtil.checkPasswordTooSimple(password);
        if (passwordValid != StringUtil.PW_ISVALID) {
            switch (passwordValid) {
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

        // 2.2次输入新密码不一样
        if (!TextUtils.equals(password, password1)) {
            toast(R.string.error_password_not_same);
            return;
        }

        showProgressDialog(false);
        new ChangePasswordTask(oldPassword, password).setCallBack(this).execute();
        // Cm.get().changePassword(oldPassword, password, this);
    }

    private class ViewHolder {
        View rootView;

        MaterialEditText etOldPassword;
        MaterialEditText etPassword;
        MaterialEditText etPassword1;

        public ViewHolder(LayoutInflater inflater, ViewGroup container) {
            rootView = inflater.inflate(R.layout.fragment_change_password, container, false);

            etOldPassword = rootView.findViewById(R.id.et_old_password);
            etPassword = rootView.findViewById(R.id.et_password);
            etPassword1 = rootView.findViewById(R.id.et_password1);

            etOldPassword.setLengthChecker(lengthChecker);
            etPassword.setLengthChecker(lengthChecker);
            etPassword1.setLengthChecker(lengthChecker);

            etPassword.addTextChangedListener(passowrdWatcher);
        }
    }
}
