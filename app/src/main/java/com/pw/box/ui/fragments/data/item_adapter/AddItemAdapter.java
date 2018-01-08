package com.pw.box.ui.fragments.data.item_adapter;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;

import com.pw.box.R;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.core.bean.BuiltInTypes;
import com.pw.box.core.bean.Item;
import com.pw.box.core.bean.ItemTypes;
import com.pw.box.databinding.AdapterAddItemAddBinding;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;

import java.util.ArrayList;
import java.util.List;

/**
 * 添加数据的adapter
 * Created by Administrator on 2017/2/21.
 */

public class AddItemAdapter extends BaseRecyclerViewAdapterNew<Item> {

    public static final int VIEW_TYPE_TEXT = 1; // 普通文本
    public static final int VIEW_TYPE = 2; // 可以下拉展开列表
    public static final int VIEW_TYPE_SUB_TYPE = 3; // 可以下拉展开列表
    public static final int VIEW_TYPE_PASSWORD = 4; // 可以弹出生成密码对话框
    // public static final int VIEW_TYPE_ADD = 5; // 可以弹出生成密码对话框

    int type;
    Integer gpId;

    public AddItemAdapter(Context context, Data data) {
        super(context);
        if (data != null) {
            type = data.type;
            gpId = data.gpid;
            setData(ItemTypes.getItems(data));
        }

        addFooter(R.layout.adapter_add_item_add);
    }

    public Integer getGpId() {
        return gpId;
    }

    public void setGpId(Integer gpId) {
        this.gpId = gpId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    @Override
    public int getItemViewType1(int position) {

        // if (editble && position == getItemCount() - 1) {
        //     return VIEW_TYPE_ADD;
        // }

        Item item = getItem(position);
        int type = item.getType();

        if (item.getType() == ItemTypes.SUB_TYPE_TYPE) {
            return VIEW_TYPE;
        } else if (item.getType() == ItemTypes.SUB_TYPE_SUB_TYPE) {
            return VIEW_TYPE_SUB_TYPE;
        } else if (type >= 30 && type < 50) {
            return VIEW_TYPE_PASSWORD;
        } else {
            return VIEW_TYPE_TEXT;
        }
    }

    @Override
    public Vh<Item> onCreateViewHolder1(ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE) {
            return new AddItemViewHolder_Type(this, View.inflate(context, R.layout.adapter_add_item_type, null));
        } else if (viewType == VIEW_TYPE_SUB_TYPE) {
            return new AddItemViewHolder_SubType(this, View.inflate(context, R.layout.adapter_add_item_password, null));
        } else if (viewType == VIEW_TYPE_PASSWORD) {
            return new AddItemViewHolder_Password(this, View.inflate(context, R.layout.adapter_add_item_password, null));
        } /*else if (viewType == VIEW_TYPE_ADD) {
            return new AddItemViewHolder_Add(this, View.inflate(context, R.layout.adapter_add_item_add, null));
        }*/ else { // VIEW_TYPE_TEXT
            return new AddItemViewHolder_Text(this, View.inflate(context, R.layout.adapter_add_item_password, null));
        }
    }

    @Override
    protected void bindFooter(Vh<Item> holder, int position, int footerPosition) {
        super.bindFooter(holder, position, footerPosition);

        AdapterAddItemAddBinding binding = holder.getBinding();
        final Context context = getContext();
        binding.btn.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle(R.string.add);

            builder.setItems(Item.getNamesArray(BuiltInTypes.SUB_TYPE_LIST()), (dialog, which) -> {
                dialog.dismiss();

                // int type = BuiltInTypes.SUB_TYPE_LIST()[which].getType();
                addItem(BuiltInTypes.SUB_TYPE_LIST()[which]);
                notifyDataSetChanged();
                // addItemAdapter.re
                // View v = View.inflate(context, R.layout.adapter_add_item, null);
            });
            builder.create().show();
        });
    }

    @Override
    public void setData(List<Item> data) {
        List<Item> tmp = new ArrayList<>();
        if (data != null) {
            for (Item item : data) {
                tmp.add(item.clone());
            }
        }
        super.setData(tmp);
    }
}
