package com.pw.box.ui.widgets;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.pw.box.R;


/**
 * 自定义标题栏
 *
 * @author Danger
 * @date :2015年12月17日
 */
public class TitleBar extends FrameLayout implements View.OnClickListener {
    /**
     * 左侧按钮（返回）
     */
    public TextView mButtonLeft;
    /**
     * 右侧按钮
     */
    public TextView mButtonRight;

    private int leftIcon = R.drawable.title_bar_left_selecter, rightIcon = 0;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TitleBar(Context context) {
        super(context);
        initView(context, null);
    }

    private void initView(Context context, AttributeSet attrs) {
        inflate(this.getContext(), R.layout.title_bar, this);

        mButtonLeft = this.findViewById(R.id.title_bar_name);
        mButtonRight = this.findViewById(R.id.btn_right);

        if (attrs == null) {
            return;
        }

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TitleBar);

        final int N = a.getIndexCount();

        leftIcon = a.getResourceId(R.styleable.TitleBar_leftIcon, leftIcon);
        rightIcon = a.getResourceId(R.styleable.TitleBar_rightIcon, rightIcon);

        String leftText = a.getString(R.styleable.TitleBar_leftText);
        String rightText = a.getString(R.styleable.TitleBar_rightText);

        a.recycle();

        setLeftButton(leftIcon, leftText);
        setRightButton(rightIcon, rightText);

        setBackClickListener(this);
        setRightButtonClickListener(this);
    }

    public void setLeftButtonText(int text) {
        if (text <= 0) {
            setLeftButton(leftIcon, null);
        } else {
            setLeftButton(leftIcon, getContext().getResources().getString(text));
        }
    }

    public void setLeftButtonText(CharSequence text) {
        setLeftButton(leftIcon, text);
    }

    public void setLeftButton(int icon, int text) {
        if (text <= 0) {
            setLeftButton(icon, null);
        } else {
            setLeftButton(icon, getContext().getResources().getString(text));
        }
    }

    public void setLeftButton(int icon, CharSequence text) {
        leftIcon = icon;
        setText(mButtonLeft, icon, text);
    }

    public void setRightButtonText(int text) {
        if (text <= 0) {
            setRightButton(rightIcon, null);
        } else {
            setRightButton(rightIcon, getContext().getResources().getString(text));
        }
    }

    public void setRightButtonText(CharSequence text) {
        setRightButton(rightIcon, text);
    }

    public void setRightButton(int icon, int text) {
        if (text <= 0) {
            setRightButton(icon, null);
        } else {
            setRightButton(icon, getContext().getResources().getString(text));
        }
    }

    public void setRightButton(int icon, CharSequence text) {
        rightIcon = icon;
        setText(mButtonRight, icon, text);
    }

    public void setRightButtonVisible(int visible) {
        mButtonRight.setVisibility(visible);
    }

    private void setText(TextView tv, int icon, CharSequence text) {
        if (icon == 0 && TextUtils.isEmpty(text)) {
            tv.setVisibility(View.INVISIBLE);
        } else {
            tv.setVisibility(View.VISIBLE);
            tv.setCompoundDrawablesWithIntrinsicBounds(icon, 0, 0, 0);
            tv.setText(text);
        }
    }

    public void setBackClickListener(OnClickListener onClickListener) {
        mButtonLeft.setOnClickListener(onClickListener);
    }

    public void setRightButtonClickListener(OnClickListener onClickListener) {
        mButtonRight.setOnClickListener(onClickListener);
    }

    public void setRightButtonLongListener(OnLongClickListener onLongClickListener) {
        mButtonRight.setOnLongClickListener(onLongClickListener);
    }

    public void setBackgroundColor(int color) {
        findViewById(R.id.root_view).setBackgroundColor(color);
    }

    public void setBackgroundResource(int resId) {
        findViewById(R.id.root_view).setBackgroundResource(resId);
    }

    @Override
    public void onClick(View v) {
        if (v == mButtonLeft) {
            processBack();
        } else if (mButtonRight == v) {
            processBack();
        }
    }

    private void processBack() {
        if (getContext() instanceof Activity) { // 默认处理为finish
            ((Activity) getContext()).onBackPressed();
        }
    }
}
