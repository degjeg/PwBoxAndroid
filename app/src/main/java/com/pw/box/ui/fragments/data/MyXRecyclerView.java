package com.pw.box.ui.fragments.data;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.jcodecraeer.xrecyclerview.XRecyclerView;

import java.util.ArrayList;

/**
 * recycler view的封装，记录按下的坐标
 * Created by danger on 2017/2/26.
 */

public class MyXRecyclerView extends XRecyclerView {
    private final ArrayList<OnItemTouchListener> touchListeners = new ArrayList<>();
    public int downX = 0;


    public MyXRecyclerView(Context context) {
        super(context);
    }

    public MyXRecyclerView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyXRecyclerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            downX = (int) e.getX();
        }
        return super.onInterceptTouchEvent(e);
    }
}
