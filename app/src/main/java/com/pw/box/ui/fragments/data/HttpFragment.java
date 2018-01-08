//package com.pw.box.ui.fragments.data;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.Toolbar;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import com.pw.box.R;
//import com.pw.box.tool.HttpServer;
//import com.pw.box.ui.base.BaseFragment;
//import com.pw.box.utils.NetWorkUtils;
//
///**
// * http 服务器设计界面
// * Created by danger on 2016/11/28.
// */
//@Deprecated
//public class HttpFragment extends BaseFragment implements View.OnClickListener {
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        AppCompatActivity compatActivity = (AppCompatActivity) getActivity();
//
//        View v = inflater.inflate(R.layout.fragment_http, container);
//        TextView tv = v.findViewById(R.id.tv);
//        tv.setText(NetWorkUtils.getLocalIpAddress());
//
//        Toolbar toolbar = v.findViewById(R.id.tool_bar);
//        toolbar.setTitle("");
//
//        // final Drawable upArrow = getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
//        // upArrow.setColorFilter(getResources().getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP);
//        // compatActivity.getSupportActionBar().setHomeAsUpIndicator(upArrow);
//
//        toolbar.setNavigationIcon(R.drawable.met_ic_clear);
//        // toolbar.setLogo(R.drawable.met_ic_clear);
//        compatActivity.setSupportActionBar(toolbar);
//
//        v.findViewById(R.id.btn_start).setOnClickListener(this);
//        v.findViewById(R.id.btn_stop).setOnClickListener(this);
//        return v;
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//
//        switch (v.getId()) {
//            case R.id.btn_start:
//                HttpServer.get().start();
//                break;
//            case R.id.btn_stop:
//                HttpServer.get().stop();
//                break;
//        }
//    }
//}
