package com.pw.box.ui.fragments.data.item_adapter;

import android.view.View;

import com.pw.box.core.bean.Item;

/**
 * 添加数据最底部的文本类型item
 * Created by Administrator on 2017/2/21.
 */

public class AddItemViewHolder_Text extends BaseAddItemViewHolder {
    public AddItemViewHolder_Text(AddItemAdapter addItemAdapter, View itemView) {
        super(addItemAdapter, itemView);
    }

    @Override
    public void bindData(Item item, int pos) {
        super.bindData(item, pos);
        tvContent.setText(item.getValue());
    }
}
