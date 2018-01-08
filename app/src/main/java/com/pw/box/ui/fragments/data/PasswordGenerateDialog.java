package com.pw.box.ui.fragments.data;

import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.pw.box.R;
import com.pw.box.ads.Ad;
import com.pw.box.utils.ClipBoardUtil;

import java.util.Random;

/**
 * 生成密码的对话械
 * Created by danger on 2016/11/12.
 */
public class PasswordGenerateDialog extends DialogFragment implements View.OnClickListener, CompoundButton.OnCheckedChangeListener, SeekBar.OnSeekBarChangeListener {

    SwitchCompat swLower;
    SwitchCompat swUpper;
    SwitchCompat swNumber;
    SwitchCompat swSymbol;
    TextView tvPw;
    AppCompatSeekBar seekBar;
    TextView btnSure;
    PasswordGeneratedListener listener;
    String ori;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View v = inflater.inflate(R.layout.dialog_password_generator, container);

        swLower = v.findViewById(R.id.sw_lower);
        swUpper = v.findViewById(R.id.sw_upper);
        swNumber = v.findViewById(R.id.sw_number);
        swSymbol = v.findViewById(R.id.sw_m);
        tvPw = v.findViewById(R.id.tv_pw);
        seekBar = v.findViewById(R.id.seek);
        btnSure = v.findViewById(R.id.btn_sure);

        seekBar.setProgress(6);

        if (listener == null) {
            genPw();
            btnSure.setText(R.string.copy);
        } else if (!initOri())
            genPw();

        // seekbar
        seekBar.setOnSeekBarChangeListener(this);
        swLower.setOnCheckedChangeListener(this);
        swUpper.setOnCheckedChangeListener(this);
        swNumber.setOnCheckedChangeListener(this);
        swSymbol.setOnCheckedChangeListener(this);
        tvPw.setOnClickListener(this);
        v.findViewById(R.id.btn_regen).setOnClickListener(this);
        btnSure.setOnClickListener(this);

        Ad.showBanner(getActivity(), (ViewGroup) v.findViewById(R.id.ad_container));
        return v;
    }

    private boolean initOri() {
        if (ori == null || ori.length() <= 0) {
            return false;
        }
        tvPw.setText(ori);

        seekBar.setProgress(ori.length());

        for (int i = 0; i < ori.length(); i++) {
            char c = ori.charAt(i);
            if (c >= 'a' && c <= 'z') {
                swLower.setChecked(true);
            } else if (c >= 'A' && c <= 'Z') {
                swUpper.setChecked(true);
            } else if (c >= '0' && c <= '9') {
                swNumber.setChecked(true);
            } else {
                swSymbol.setChecked(true);
            }
        }

        return true;
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_regen:
                genPw();
                break;
            case R.id.btn_sure:
                if (listener != null) {
                    listener.onPasswordGenerated(tvPw.getText().toString());
                } else {

                    String text = tvPw.getText().toString();
                    ClipBoardUtil.copy(getContext(), text);

                    // ClipData myClip;
                    // ClipboardManager myClipboard = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                    // myClip = ClipData.newPlainText("text", text);
                    // myClipboard.setPrimaryClip(myClip);
                    Toast.makeText(getContext(), R.string.password_is_copyed, Toast.LENGTH_SHORT).show();
                }
                dismiss();
                break;
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        // Toast.makeText(getContext(), "" + b, Toast.LENGTH_SHORT).show();
        genPw();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
        generatPw();
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void setListener(PasswordGeneratedListener listener) {
        this.listener = listener;
    }

    public void setOri(String ori) {
        this.ori = ori;
    }

    private void genPw() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                generatPw();
            }
        });
    }

    private void generatPw() {
        boolean isLower = swLower.isChecked();
        boolean isUpper = swUpper.isChecked();
        boolean isNumber = swNumber.isChecked();
        boolean isSymbol = swSymbol.isChecked();

        StringBuilder b = new StringBuilder();
        if (isLower) {
            b.append("abcdefghijklmnopqrstuvwxyz");
        }
        if (isUpper) {
            b.append("ABCDEFGHIJKLMNOPQRSTUVWXYZ");
        }

        if (isNumber) {
            b.append("01234567890");
        }
        if (isSymbol) {
            b.append("`~!@#$%^&*()_-+=-[]{}\\|:;'\",./<>?");
        }
        if (b.length() == 0) {
            return;
        }
        int len = seekBar.getProgress();

        StringBuilder b1 = new StringBuilder();
        Random ra = new Random(SystemClock.elapsedRealtime());
        for (int i = 0; i < len; i++) {
            b1.append(b.charAt(ra.nextInt(b.length())));
        }
        tvPw.setText(b1);
        if (listener == null) {
            btnSure.setText(getString(R.string.copy_n, len));
        } else {
            btnSure.setText(getString(R.string.sure_n, len));
        }
    }

    public interface PasswordGeneratedListener {
        void onPasswordGenerated(String s);
    }
}
