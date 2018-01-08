package com.pw.box.ui.fragments.setting;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ScrollView;

import com.common.bean.BaseRes;
import com.pw.box.R;
import com.pw.box.bean.protobuf.FeedBackRequest;
import com.pw.box.cache.Cache;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.Net;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.widgets.TitleBar;
import com.squareup.wire.Message;


/**
 * 用户反馈界面
 * Created by danger on 16/8/28.
 */
public class FeedbackFragment extends BaseFragment {

    ViewHolder holder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new ViewHolder(inflater, container);

        holder.titleBar.setRightButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submit();
            }
        });

        return holder.rootView;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);

        if (v == holder.til1) {
            holder.scrollView.fullScroll(ScrollView.FOCUS_UP);
        } else if (v == holder.til2) {
            holder.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }
    }

    void submit() {
        String content = holder.tvContent.getText().toString().trim();
        String contact = holder.tvContact.getText().toString().trim();

        if (content.length() < 6) {
            toast(R.string.content_too_short);
            return;
        }
        showProgressDialog(R.string.submiting);

        FeedBackRequest.Builder builder = new FeedBackRequest.Builder();
        builder.uname(Cache.get().getUser().getAccount());

        builder.contact(contact);
        builder.content(content);

        builder.androidVer(Build.VERSION.RELEASE + "-" + Build.VERSION.CODENAME);

        PackageManager pm = getContext().getPackageManager();
        try {
            builder.appVer(pm.getPackageInfo(getContext().getPackageName(), 0).versionName);
        } catch (PackageManager.NameNotFoundException e) {
            // e.printStackTrace();  should not happen
        }
        builder.phoneModel(Build.BRAND + "-" + Build.MODEL + "-" + Build.PRODUCT);

        new Net(CmdIds.FEEDBACK, builder.build(), new Net.NetHandler<BaseRes>() {
            @Override
            public void onSuccess(int cmd, Message req, BaseRes response) {
                dismissDialog();
                if (response.retCode == ErrorCodes.SUCCESS) {
                    dismissDialog();
                    toast(R.string.feedback_has_received);
                    finish();
                } else {
                    onFail(cmd, req, response.retCode, null);
                }
            }

            @Override
            public void onFail(int cmd, Message req, int code, Throwable e) {
                dismissDialog();
                toast(ErrorCodes.getErrorDescription(code));
            }
        }).execute();

    }

    private class ViewHolder {

        View rootView;

        ScrollView scrollView;
        TitleBar titleBar;
        EditText tvContent; // 用户输入的内容
        EditText tvContact; // 联系方式

        TextInputLayout til1;
        TextInputLayout til2;

        public ViewHolder(LayoutInflater inflater, ViewGroup container) {
            rootView = inflater.inflate(R.layout.fragment_feedback, container, false);

            titleBar = rootView.findViewById(R.id.title_view);

            scrollView = rootView.findViewById(R.id.scroll_view);

            til1 = rootView.findViewById(R.id.til1);
            til2 = rootView.findViewById(R.id.til2);


            tvContent = rootView.findViewById(R.id.et_content); // 用户输入的内容
            tvContact = rootView.findViewById(R.id.et_contact); // 联系方式

            til1.setOnClickListener(FeedbackFragment.this);
            til2.setOnClickListener(FeedbackFragment.this);

            til1.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    holder.scrollView.fullScroll(ScrollView.FOCUS_UP);
                    return false;
                }
            });

            til2.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    holder.scrollView.fullScroll(ScrollView.FOCUS_DOWN);
                    return false;
                }
            });
        }
    }
}
