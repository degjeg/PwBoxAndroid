//package com.pw.box.ui.fragments.data;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//
//import com.pw.box.R;
//import com.pw.box.ui.base.BaseFragment;
//import com.pw.box.ui.widgets.TitleBar;
//
//
///**
// * Created by danger on 16/9/12.
// */
//public class EditItemFragment extends BaseFragment implements
//        View.OnClickListener {
//
//
//    Holder holder;
//
//    @Nullable
//    @Override
//    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        holder = new Holder(inflater.inflate(R.layout.fragment_edit_item, null));
//
//
//        return holder.rootView;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        holder = null;
//    }
//
//    @Override
//    public void onClick(View v) {
//        super.onClick(v);
//    }
//
//    private class Holder {
//        View rootView;
//        TitleBar titleBar;
//
//        public Holder(View v) {
//            this.rootView = v;
//            titleBar = (TitleBar) v.findViewById(R.id.title);
//        }
//    }
//}
//
