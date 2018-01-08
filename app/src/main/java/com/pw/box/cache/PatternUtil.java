package com.pw.box.cache;

import android.text.TextUtils;

import com.pw.box.bean.protobuf.cli.SaveKey;
import com.pw.box.core.Cm;
import com.pw.box.core.cmds.LoginTask;
import com.pw.box.utils.Aes256;
import com.pw.box.utils.FileUtils;
import com.pw.box.utils.L;
import com.pw.box.utils.Md5;

import java.io.IOException;

import okio.ByteString;

/**
 * 记录图案解锁数据的工具类
 * Created by danger on 16/10/30.
 */

public class PatternUtil {

    SaveKey saveKey;
    private String patternString;

    public void init() {
        byte[] savedKeyRaw = FileUtils.readFile(Constants.keyFile);
        saveKey = parseSaveKey(savedKeyRaw);
    }


    public boolean havePattern() {
        return saveKey != null;
    }

    public int getPatternCount() {
        return saveKey == null ? 0 : saveKey.get_line_count;
    }

    private SaveKey parseSaveKey(byte[] data) {
        try {
            if (data == null || data.length < 4 || data.length > 1024) {
                return null;
            }
            SaveKey saveKey = SaveKey.ADAPTER.decode(data);
            if (!TextUtils.isEmpty(saveKey.account)
                    && saveKey.account.length() >= com.pw.box.core.C.min_account_len
                    && saveKey.key_md5 != null && saveKey.key_md5.length() > 0
                    && saveKey.pw1 != null
                    && saveKey.get_line_count >= 1 && saveKey.get_line_count < 10) {
                return saveKey;
            }
        } catch (IOException e) {
            // e.printStackTrace();
        }
        return null;
    }

    public int unLock(String patString) {
        if (!TextUtils.equals(Md5.md5(patString), saveKey.key_md5)) {
            return -1;
        }

        User user = new User();

        user.setAccount(saveKey.account);

        byte[] md532 = Md5.md532Bytes(patString);

        try {
            // 该用户已经正常登录,只要验证通过即可
            if (TextUtils.equals(saveKey.account, Cm.get().getAccount())
                    && Cache.get().getLoginStatus() == Cache.LOGIN_STATUS_LOGGIN) {
                return 0;
            }

            // Cm.get().autoLogin();
            Cm.get().connect(user.getAccount());

            user.setPwFilledLogin(Aes256.decrypt(saveKey.pw1.toByteArray(), md532));
            user.setPwFilledRawKey(Aes256.decrypt(saveKey.pw2.toByteArray(), md532));

            Cache.get().setUser(user);

            // Cm.get().login(user.getAccount(), user.getPwFilledLogin(), user.getPwFilledRawKey(), null);
            LoginTask loginTask = new LoginTask(user.getAccount(), user.getPwFilledLogin(), user.getPwFilledRawKey(), null);

            loginTask.execute();
            this.patternString = patString;
            // new LoginTask().

            // ContainerActivity ac = (ContainerActivity) getActivity();
            // ac.clear();
            // ac.showFragment(HomeFragment.class, null);

        } catch (Exception e) {
            // e.printStackTrace();
            // goToLogin();
            if (L.E) L.get().e("LockPattern", "unlock", e);
            return -2;
        }
        return 0;
    }

    // 密码等数据已经更新,需要重新保存
    public boolean updateInfo() {
        if (havePattern()) {
            return setLockPattern(patternString, getPatternCount());
        }
        return false;
    }

    public boolean setLockPattern(String patternString, int cnt) {
        SaveKey.Builder builder = new SaveKey.Builder();
        User user = Cache.get().getUser();
        if (user == null || user.getAccount() == null) {
            return true;
        }

        builder.account(user.getAccount());
        builder.get_line_count(cnt);

        try {
            if (cnt > 0) {
                byte[] md532Key = Md5.md532Bytes(patternString);

                builder.key_md5(Md5.md5(patternString));
                builder.pw1(ByteString.of(Aes256.encrypt(user.getPwFilledLogin(), md532Key)));
                builder.pw2(ByteString.of(Aes256.encrypt(user.getPwFilledRawKey(), md532Key)));
            }
            if (0 == FileUtils.writeFile(Constants.keyFile, builder.build().encode())) {
                this.saveKey = builder.build();
                this.patternString = patternString;
                return true;
            }
        } catch (Exception e) {
            // e.printStackTrace();
            // toast("系统错误,请重新登录后重试");
        }
        return false;
    }

    public void clearLockPattern() {
        Constants.keyFile.delete();
        patternString = null;
        saveKey = null;
    }

    public boolean match(String patString) {
        return TextUtils.equals(patString, patternString);
    }

    public String getPatternString() {
        return patternString;
    }
}
