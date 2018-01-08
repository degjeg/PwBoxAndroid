package com.pw.box.ui.fragments.data.item_adapter;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;

import com.pw.box.R;
import com.pw.box.core.bean.BuiltInTypes;
import com.pw.box.core.bean.Item;
import com.pw.box.core.bean.ItemTypes;


/**
 * 添加数据最底部的类型item
 * Created by Administrator on 2017/2/21.
 */

public class AddItemViewHolder_SubType extends BaseAddItemViewHolder implements View.OnClickListener {
    ImageView btnDropDown;

    Item[] types = null;
    String[] names; // = Item.getNamesArray(types);

    public AddItemViewHolder_SubType(AddItemAdapter addItemAdapter, View itemView) {
        super(addItemAdapter, itemView);
        btnDropDown = itemView.findViewById(R.id.btn_drop_down);
    }

    @Override
    public void bindData(Item item, int pos) {
        super.bindData(item, pos);
        btnDropDown.setOnClickListener(this);

        int type = addItemAdapter.getType();

        if (type == ItemTypes.TYPE_BANK) {
            types = BuiltInTypes.BANK_LIST();
        } else if (type == ItemTypes.TYPE_IM) {
            types = BuiltInTypes.IM_LIST();
        } else {
            btnDropDown.setVisibility(View.GONE);
            return;
        }

        btnDropDown.setVisibility(View.VISIBLE);
        names = Item.getNamesArray(types);
    }

    private void showSubTypeDialog() {
        if (names == null) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(addItemAdapter.getContext());
        builder.setTitle(R.string.select_type);

        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                // ts[which].getType();
                // tvContent.setText(ItemTypes.getItemNameByType(App.getContext(), types[which].getType(), 0));
                tvContent.setText(types[which].getName());
            }
        });
        builder.create().show();
    }

    @Override
    public void onClick(View v) {
        showSubTypeDialog();
    }
}
