package com.pw.box.ui.fragments.home;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pw.box.R;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.widgets.StateView;

/**
 * 数据列表界面
 * Created by danger on 16/8/30.
 */
public class RecentItemListFragment extends BaseFragment {
    ViewHolder viewHolder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewHolder = new ViewHolder(inflater.inflate(R.layout.fragment_item_list, container));
        viewHolder.stateView.showEmptyView(getString(R.string.message_data_is_empty), R.mipmap.ic_launcher);
        return viewHolder.rootView;
    }

    private class ViewHolder {
        View rootView;
        StateView stateView;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            // titleBar =rootView.findViewById(R.id.ti)
            stateView = rootView.findViewById(R.id.state_view);
        }
    }

}
