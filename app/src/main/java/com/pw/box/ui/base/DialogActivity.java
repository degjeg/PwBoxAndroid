package com.pw.box.ui.base;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.pw.box.R;

import java.io.Serializable;

// import com.pw.box.cache.Counter;

/**
 * 对话框风格的activity
 * 提示用户是否升级app
 */
public class DialogActivity extends AppCompatActivity implements DialogInterface {

    public static final String EXTRA_MSG = "msg";
    public static final String EXTRA_ADAPTER = "adapter";
    public TextView tvTitle;
    public TextView btn1, btn2;
    public TextView tvMessage;


    public static Intent newIntent(Context c, Serializable msg, Serializable adapter) {
        Intent intent = new Intent(c, DialogActivity.class);
        intent.putExtra(DialogActivity.EXTRA_MSG, msg);
        intent.putExtra(DialogActivity.EXTRA_ADAPTER, adapter);

        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();

        setContentView(R.layout.dialog_activity);
        tvTitle = findViewById(R.id.tv_title);
        tvMessage = findViewById(R.id.tv_message);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);

        Object object = intent.getSerializableExtra(EXTRA_MSG);
        DialogActivityAdapter adapter = (DialogActivityAdapter) intent.getSerializableExtra(EXTRA_ADAPTER);
        adapter.initWithDialogActivity(this, object);
    }


    @Override
    protected void onResume() {
        super.onResume();

        // Counter.get().onResume(this);
    }

    @Override
    protected void onPause() {
        super.onPause();

        // Counter.get().onPause();
    }

    public void setTitle(CharSequence title) {
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.VISIBLE);
            tvTitle.setText(title);
        } else {
            tvTitle.setVisibility(View.GONE);
        }
    }

    public void setMessage(String message) {
        if (!TextUtils.isEmpty(message)) {
            tvMessage.setVisibility(View.VISIBLE);
            tvMessage.setText(message);
        } else {
            tvMessage.setVisibility(View.GONE);
        }
    }


    public void setPositiveButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        if (!TextUtils.isEmpty(text)) {
            btn2.setVisibility(View.VISIBLE);
            btn2.setText(text);
            btn2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(DialogActivity.this, DialogInterface.BUTTON_POSITIVE);
                    } else {
                        dismiss();
                    }
                }
            });
        } else {
            btn2.setVisibility(View.GONE);
        }
    }


    public void setPositiveButton(@StringRes int textId, final DialogInterface.OnClickListener listener) {
        String txt = textId > 0 ? getString(textId) : null;
        setPositiveButton(txt, listener);
    }

    public void setNegativeButton(CharSequence text, final DialogInterface.OnClickListener listener) {
        if (!TextUtils.isEmpty(text)) {
            btn1.setVisibility(View.VISIBLE);
            btn1.setText(text);
            btn1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onClick(DialogActivity.this, DialogInterface.BUTTON_POSITIVE);
                    } else {
                        dismiss();
                    }
                }
            });
        } else {
            btn1.setVisibility(View.GONE);
        }
    }

    public void setNegativeButton(@StringRes int textId, final DialogInterface.OnClickListener listener) {
        String txt = textId > 0 ? getString(textId) : null;
        setNegativeButton(txt, listener);
    }

    @Override
    public void cancel() {
        finish();
    }

    @Override
    public void dismiss() {
        finish();
    }

    public void toast(String s) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
    }


    public static class DialogActivityAdapter implements Serializable {
        public void initWithDialogActivity(DialogActivity activity, Object info) {

        }
    }
}
