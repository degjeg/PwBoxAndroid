package com.pw.box.ui.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.pw.box.R;


/**
 * Created by danger on
 * 2016/08/23
 * 功能:
 */
public class StateView extends FrameLayout {

    public static final int STATE_NONE = 1;
    public static final int STATE_LOADING = 2;
    public static final int STATE_CONTENT = 3;
    public static final int STATE_EMPTY = 4;
    public static final int STATE_FAIL = 5;
    int layoutIdLoading = R.layout.view_state_loading;
    int layoutIdContent = R.layout.view_state_recyclerview_content;
    int layoutIdEmpty = R.layout.view_state_empty;
    int layoutIdFail = R.layout.view_state_fail;
    int currentState = STATE_NONE;
    int currentLayoutId = -1;
    StateChangeListener stateChangeListener;

    public StateView(Context context) {
        super(context);
        init(context, null);
    }

    public StateView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        // LayoutInflater.from(context).inflate(R.layout.view_state, this);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.StateView);

            layoutIdLoading = a.getResourceId(R.styleable.StateView_loadingView, layoutIdLoading);
            layoutIdContent = a.getResourceId(R.styleable.StateView_contentView, layoutIdContent);
            layoutIdEmpty = a.getResourceId(R.styleable.StateView_emptyView, layoutIdEmpty);
            layoutIdFail = a.getResourceId(R.styleable.StateView_failView, layoutIdFail);

            a.recycle();
        }
    }

    public void setStateChangeListener(StateChangeListener stateChangeListener) {
        this.stateChangeListener = stateChangeListener;
    }

    public int getState() {
        return currentState;
    }

    public void setState(int state) {
        if (this.currentState == state) {
            return;
        }
        this.currentState = state;

        int layoutId = getLayoutIdByState(state);
        if (currentLayoutId == layoutId) {
            return;
        }
        currentLayoutId = layoutId;
        removeAllViews();

        if (currentLayoutId != -1) {
            inflate(getContext(), currentLayoutId, this);
        }

        if (stateChangeListener != null) {
            stateChangeListener.onStateChange(currentState);
        }
    }

    private int getLayoutIdByState(int state) {
        switch (state) {
            case STATE_LOADING:
                return layoutIdLoading;
            case STATE_CONTENT:
                return layoutIdContent;
            case STATE_EMPTY:
                return layoutIdEmpty;
            case STATE_FAIL:
                return layoutIdFail;
        }
        return -1;
    }

    private void setData(int text, int img) {
        if (text <= 0) {
            setData(null, img);
        } else {
            setData(getContext().getString(text), img);
        }
    }

    private void setData(String text, int img) {
        TextView tv = findViewById(R.id.tv);
        ImageView iv = findViewById(R.id.iv);

        if (tv != null) {
            if (text != null) {
                tv.setVisibility(View.VISIBLE);
                tv.setText(text);
            } else {
                tv.setVisibility(View.GONE);
            }
        }


        if (iv != null) {
            if (img > 0) {
                iv.setImageResource(img);
            } else {
                iv.setVisibility(View.GONE);
            }
        }
    }

    public void showContentView() {
        setState(STATE_CONTENT);
    }

    public void showEmptyView(String text, int img) {
        setState(STATE_EMPTY);
        setData(text, img);
    }

    public void showEmptyView(int text, int img) {
        setState(STATE_EMPTY);
        setData(text, img);
    }

    public void showLoadingView(String text, int img) {
        setState(STATE_LOADING);
        setData(text, img);
    }

    public void showLoadingView(int text, int img) {
        setState(STATE_LOADING);
        setData(text, img);
    }

    public void showFailView(String text, int img) {
        setState(STATE_FAIL);
        setData(text, img);
    }

    public void showFailView(int text, int img) {
        setState(STATE_FAIL);
        setData(text, img);
    }

    public interface StateChangeListener {
        void onStateChange(int state);
    }
}
