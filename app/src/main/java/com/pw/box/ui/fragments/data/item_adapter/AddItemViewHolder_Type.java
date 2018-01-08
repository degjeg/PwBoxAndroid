package com.pw.box.ui.fragments.data.item_adapter;


import android.content.DialogInterface;
import android.view.View;

import com.pw.box.R;
import com.pw.box.bean.protobuf.Group;
import com.pw.box.cache.GroupManager;
import com.pw.box.core.bean.Item;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.data.GroupListFragment;

import java.util.List;

/**
 * 添加数据最底部的类型
 * Created by Administrator on 2017/2/21.
 */

public class AddItemViewHolder_Type extends BaseAddItemViewHolder implements View.OnClickListener {
    public AddItemViewHolder_Type(AddItemAdapter addItemAdapter, View itemView) {
        super(addItemAdapter, itemView);
    }

    @Override
    public void bindData(Item item, int pos) {
        super.bindData(item, pos);

        // tvContent.setEnabled(false);

        final List<Group> gps = GroupManager.get().getPgs();
        /*if (gps.size() > 0)*/
        {

            btnDropDown.setVisibility(View.VISIBLE);
            btnDropDown.setOnClickListener(this);
            tvContent.setOnClickListener(this);
        } /*else {
            btnDropDown.setVisibility(View.GONE);
        }*/

        String name = GroupManager.get().getGroupName(addItemAdapter.getGpId(), ""/*item.getValue()*/);
        tvContent.setText(name);
        tvName.setText(R.string.group);
        tvContent.setHint(R.string.group);
    }

    @Override
    public void onClick(View v) {
        final List<Group> gps = GroupManager.get().getPgs();
        if (gps.isEmpty()) {
            ContainerActivity.go(addItemAdapter.getContext(), GroupListFragment.class, null);
            return;
        }
        final String[] gpNames = new String[gps.size() + 1];
        for (int i = 0; i < gps.size(); i++) {
            gpNames[i] = gps.get(i).name;
        }

        gpNames[gpNames.length - 1] = addItemAdapter.getContext().getString(R.string.add_group);
        android.support.v7.app.AlertDialog dlg = new android.support.v7.app.AlertDialog.Builder(addItemAdapter.getContext())
                .setTitle(R.string.select_group)
                .setItems(gpNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which < gpNames.length - 1) {
                            tvContent.setText(gpNames[which]);
                            addItemAdapter.gpId = gps.get(which).id;
                        } else {
                            ContainerActivity.go(addItemAdapter.getContext(), GroupListFragment.class, null);
                        }
                    }
                }).create();
        dlg.show();
    }
}
