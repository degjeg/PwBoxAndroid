package com.pw.box.cache;

import android.text.TextUtils;

/**
 * 记录用户数据的类，
 * Created by danger on 16/9/3.
 */
public class User {
    String account; // 账号
    // String password; // 用户输入的原文密码

    /**
     * 加密数据使用的原始密钥,由本地随机生成
     */
    byte[] rawKey;

    boolean havePretection = false;
    String question;

    byte[] rawKeyByAnswer;
    String answer;


    byte[] pwFilledLogin;
    byte[] pwFilledRawKey;

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public byte[] getRawKey() {
        return rawKey;
    }

    public void setRawKey(byte[] rawKey) {
        this.rawKey = rawKey;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public boolean haveProtect() {
        return !TextUtils.isEmpty(question) || havePretection;
    }

    public boolean isHavePretection() {
        return havePretection;
    }

    public void setHavePretection(boolean havePretection) {
        this.havePretection = havePretection;
    }

    public byte[] getRawKeyByAnswer() {
        return rawKeyByAnswer;
    }

    public void setRawKeyByAnswer(byte[] rawKeyByAnswer) {
        this.rawKeyByAnswer = rawKeyByAnswer;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public byte[] getPwFilledLogin() {
        return pwFilledLogin;
    }

    public void setPwFilledLogin(byte[] pwFilledLogin) {
        this.pwFilledLogin = pwFilledLogin;
    }

    public byte[] getPwFilledRawKey() {
        return pwFilledRawKey;
    }

    public void setPwFilledRawKey(byte[] pwFilledRawKey) {
        this.pwFilledRawKey = pwFilledRawKey;
    }
}
