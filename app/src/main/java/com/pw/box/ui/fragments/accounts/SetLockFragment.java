package com.pw.box.ui.fragments.accounts;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.pw.box.R;
import com.pw.box.bean.protobuf.cli.SaveKey;
import com.pw.box.cache.Cache;
import com.pw.box.tool.DoubleTabHelper;
import com.pw.box.tool.UnLock;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.home.HomeFragment;
import com.pw.box.ui.widgets.PatternLockView;
import com.pw.box.ui.widgets.TitleBar;
import com.pw.box.utils.L;

import java.util.List;


/**
 * 图案解锁/设置图案解锁界面
 * Created by danger on 16/8/28.
 */
public class SetLockFragment extends BaseFragment implements
        PatternLockView.OnPatternListener {


    public static final String TAG = "SetLockFragment";
    public static final String EXTRA_TYPE = "type";
    public static final int EXTRA_TYPE_SET = 1;
    public static final int EXTRA_TYPE_UNLOCK = 2;

    ViewHolder holder;
    int type = -1;

    int errorCount = 0;

    int STEP1 = 1;
    int STEP2 = 2;
    int STEP3 = 3;

    int step = STEP1;
    private String patternString = null;

    public static SaveKey parseSaveKey(byte[] data) {
        try {
            if (data == null || data.length < 4 || data.length > 1024) {
                return null;
            }
            SaveKey saveKey = SaveKey.ADAPTER.decode(data);
            if (!TextUtils.isEmpty(saveKey.account)
                    && saveKey.account.length() >= com.pw.box.core.C.min_account_len
                    && saveKey.key_md5 != null
                    && saveKey.pw1 != null
                    && saveKey.get_line_count >= 1 && saveKey.get_line_count < 10) {
                return saveKey;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        holder = new ViewHolder(inflater.inflate(R.layout.fragment_set_lock, container, false));
        holder.btn.setOnClickListener(this);

        holder.patternLockView.setPatternListener(this);

        Bundle args = getArguments();
        type = args.containsKey(EXTRA_TYPE) ? args.getInt(EXTRA_TYPE) : -1;

        setToStep1();
        return holder.rootView;
    }

    void setToStep1() {
        step = STEP1;

        switch (type) {
            case EXTRA_TYPE_SET:
                holder.titleBar.setLeftButtonText(getString(R.string.verify_the_original_password));
                if (!Cache.get().getPatternUtil().havePattern()) { // 还没有设置，直接进入step2
                    setToStep2();
                } else {
                    toast(R.string.please_verify_ori_pattern);
                    holder.patternLockView.resetPattern();
                    holder.btn.setVisibility(View.GONE);
                }
                break;

            case EXTRA_TYPE_UNLOCK:
                if (!Cache.get().getPatternUtil().havePattern()) {
                    goToLogin();
                    return;
                }

                holder.titleBar.setLeftButtonText(R.string.pattern_unlock);
                holder.patternLockView.resetPattern();
                holder.btn.setVisibility(View.GONE);
                break;
        }
    }

    void setToStep2() {
        step = STEP2;
        patternString = null;
        switch (type) {
            case EXTRA_TYPE_SET:
                holder.titleBar.setLeftButtonText(getString(R.string.draw_new_pattern));
                holder.patternLockView.resetPattern();
                holder.btn.setVisibility(View.VISIBLE);
                holder.btn.setText(R.string.next_step);
                // holder.btn.setEnabled(false);
                break;
        }
    }

    void setToStep3() {
        step = STEP3;

        switch (type) {
            case EXTRA_TYPE_SET:
                holder.titleBar.setLeftButtonText(getString(R.string.redraw_pattern));
                holder.patternLockView.resetPattern();
                holder.btn.setVisibility(View.VISIBLE);
                holder.btn.setText(R.string.sure);
                // holder.btn.setEnabled(false);
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn:
            case R.id.btn_right:
                if (type == EXTRA_TYPE_SET) {
                    if (null == patternString) {
                        if (holder.patternLockView.getPointCount() < 6) {
                            showTipDialog(getString(R.string.pattern_points_too_little));
                            return;
                        }
                        patternString = holder.patternLockView.toString(holder.patternLockView.getPatterns());

                        setToStep3();
                    } else {
                        String patternString2 = holder.patternLockView.toString(holder.patternLockView.getPatterns());
                        if (!TextUtils.equals(patternString2, patternString)) {
                            patternString = null;
                            toast(R.string.pattern_not_match2);
                            setToStep2();
                        } else {
                            if (Cache.get().getPatternUtil().setLockPattern(patternString2, holder.patternLockView.getPatterns().size())) {
                                toast(R.string.pattern_is_set);
                                UnLock.unlockOk(); // 更新上次解锁时间
                                // 保存成功
                                finish();
                            }
                            /*if (setLockPattern(patternString2, holder.patternLockView.getPatterns().size())) {
                                toast("解锁图案已更新");
                                // 保存成功
                                finish();

                            }*/
                        }
                    }
                }
                holder.patternLockView.resetPattern();
                break;
        }
    }

    @Override
    public void onPatternCleared() {

    }

    @Override
    public void onPatternDetected(List<List<PatternLockView.Cell>> p) {

        // holder.btn.setEnabled(true);

        if (L.E) L.get().e(TAG, "Pattern:" + holder.patternLockView.toString(p));

        String patString = holder.patternLockView.toString(p);

        /// 如果是设置密码保护的话
        if (type == EXTRA_TYPE_SET) {
            /*if (holder.patternLockView.getPointCount(p) >= 6) {*/
            if (step == STEP1) {
                if (p.size() == Cache.get().getPatternUtil().getPatternCount()) {
                    if (Cache.get().getPatternUtil().match(patString)/*TextUtils.equals(Md5.md5(patString), saveKey.getKeyMd5())*/) { // 验证成功
                        setToStep2();
                        toast(R.string.pattern_verified);
                    } else { // 密码错误
                        holder.patternLockView.resetPattern();
                        toast(R.string.pattern_error);
                    }
                }
            } else if (p.size() == 1 && step == STEP2) {
                showDrawMoreLineDialog();
            }
            /*}*/
        }

        if (type == EXTRA_TYPE_UNLOCK) {
            if (Cache.get().getPatternUtil().havePattern()) {
                int unLockResult = Cache.get().getPatternUtil().unLock(patString);

                if (unLockResult == 0) { // 验证成功
                    UnLock.unlockOk();
                    goToHome(patString);
                } else if (unLockResult == -2) {
                    goToLogin();
                } else if (p.size() >= Cache.get().getPatternUtil().getPatternCount()) { // 密码错误
                    // saveKey.count
                    if (++errorCount >= 5) {
                        Cache.get().getPatternUtil().clearLockPattern();
                        goToLogin();
                    }
                    holder.patternLockView.resetPattern();
                    toast(R.string.pattern_error);
                }
            }
        }
    }

    private void showDrawMoreLineDialog() {
        AlertDialog.Builder dlgBuilder = new AlertDialog.Builder(getContext());
        dlgBuilder.setMessage(R.string.error_dlg_multi_pattern_lines);
        dlgBuilder.setPositiveButton(R.string.i_see, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dlgBuilder.setCancelable(false);
        dlgBuilder.create().show();
    }

    private void goToHome(String patString) {
        if (Cache.get().getLoginStatus() == Cache.LOGIN_STATUS_LOGGIN) { // 已经登录
            finish();
        } else {
            ContainerActivity.goClearTask(activity, HomeFragment.class, null);
        }


        /*
        User user = new User();
        user.setAccount(saveKey.getAccount());

        byte[] md532 = Md5.md532Bytes(patString);

        try {
            Cm.get().autoLogin();

            user.setPwFilledLogin(Aes256.decrypt(saveKey.getPw1().toByteArray(), md532));
            user.setPwFilledRawKey(Aes256.decrypt(saveKey.getPw2().toByteArray(), md532));

            Cm.get().login(user.getAccount(), user.getPwFilledLogin(), user.getPwFilledRawKey(), null);
            // new LoginTask().



        } catch (Exception e) {
            // e.printStackTrace();
            goToLogin();
        }*/
    }

    private void goToLogin() {
        ContainerActivity.goClearTask(activity, LoginFragment.class, null);
    }

    @Override
    public boolean onBackPressed() {
        if (type == EXTRA_TYPE_UNLOCK) {
            DoubleTabHelper.INSTANCE.pressAgainToExit(getContext());
            return true;
        } else {
            return super.onBackPressed();
        }
    }

    private class ViewHolder {
        View rootView;
        TitleBar titleBar;
        TextView btn;
        PatternLockView patternLockView;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            this.titleBar = rootView.findViewById(R.id.title_view);
            // btn = (TextView) rootView.findViewById(R.id.btn);
            btn = titleBar.mButtonRight;
            patternLockView = rootView.findViewById(R.id.plv);
        }
    }
}
