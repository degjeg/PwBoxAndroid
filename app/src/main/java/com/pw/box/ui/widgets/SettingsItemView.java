//package com.pw.box.ui.widgets;
//
//import android.content.Context;
//import android.util.AttributeSet;
//import android.widget.FrameLayout;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//
//import com.pw.box.R;
//
///**
// * Created by danger on 16/9/16.
// */
//public class SettingsItemView extends RelativeLayout {
//
//    TextView tvName;
//    TextView tvVale;
//
//    public SettingsItemView(Context context) {
//        super(context);
//
//        init(context, null);
//    }
//
//    public SettingsItemView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//        init(context, attrs);
//    }
//
//    public SettingsItemView(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//        init(context, attrs);
//    }
//
//    private void init(Context context, AttributeSet attrs) {
//        inflate(context, R.layout.view_settings_item, this);
//        tvName = (TextView) findViewById(R.id.tv_name);
//        tvVale = (TextView) findViewById(R.id.tv_value);
//        setBackgroundResource(R.drawable.list_item_bg);
//    }
//
//
//    public void setName(String name) {
//        tvName.setText(name);
//    }
//
//    public void setName(int name) {
//        tvName.setText(name);
//    }
//
//    public void setValue(String name) {
//        tvVale.setText(name);
//    }
//
//    public void setValue(int name) {
//        tvVale.setText(name);
//    }
//}
