package com.pw.box.ui.fragments.home;

import android.content.Context;
import android.text.TextUtils;

import com.pw.box.R;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.Item;
import com.pw.box.core.bean.ItemTypes;
import com.pw.box.databinding.AdapterItemBinding;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;


/**
 * 数据item
 * Created by danger on 16/9/17.
 */
public class ItemListAdapter extends BaseRecyclerViewAdapterNew<Data> {

    public ItemListAdapter(Context context) {
        super(context, R.layout.adapter_item);
    }

    public static final String getValueByType(Data data, int type) {
        if (data == null || data.items == null) {
            return null;
        }
        for (Item item : data.items) {
            if (item.type == type && !TextUtils.isEmpty(item.value)) {
                return item.value;
            }
        }
        return null;
    }

    public static CharSequence getAccount(Data data) {
        StringBuilder acc = new StringBuilder();
        for (Item item : data.items) {
            if (item.type >= 10 && item.type < 30) {
                acc.append(item.value);
                acc.append(",");
            }
        }
        if (acc.length() > 0) {
            acc.deleteCharAt(acc.length() - 1);
        }
        return acc;
    }

    @Override
    protected void bindData(Vh<Data> vh, Data data, int pos) {
        super.bindData(vh, data, pos);

        AdapterItemBinding binding = vh.getBinding();
        binding.tvName.setText(getName(data));
        binding.tvAccount.setText(getAccount(data));

        // h.itemView.setOnClickListener(new View.OnClickListener() {
        //     @Override
        //     public void onClick(View v) {
        //         onItemClickListener.onItemClick(null, null, position, 0);
        //     }
        // });

        // h.itemView.setOnLongClickListener(new View.OnLongClickListener() {
        //     @Override
        //     public boolean onLongClick(View v) {
        //         if (onItemLongClickListener != null) {
        //             onItemLongClickListener.onItemLongClick(null, null, position, 0);
        //         }
        //
        //         return true;
        //     }
        // });
    }

    private String getName(Data data) {
        String name = data.sub_type == null ? "" : data.sub_type;

        for (Item item : data.items) {
            if (item.type == ItemTypes.SUB_TYPE_SUB_TYPE
                    || item.type == ItemTypes.SUB_TYPE_MARK
                    ) {
                String val = item.value;
                if (item.type == ItemTypes.SUB_TYPE_MARK && val.length() > 10) {
                    val = val.substring(0, 10) + ".";
                }
                name = val + " " + name;
            }
        }

        /* 把网址显示在第一行 */
        if (TextUtils.isEmpty(name) && data.type == ItemTypes.TYPE_WEBSITE) {
            name = getValueByType(data, ItemTypes.SUB_TYPE_URL);
        }
        return name;
    }

    // private class Holder extends RecyclerView.ViewHolder {
    //     TextView tvName;
    //     TextView tvAccount;
    //
    //     public Holder(View itemView) {
    //         super(itemView);
    //         itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
    //         tvName = itemView.findViewById(R.id.tv_name);
    //         tvAccount = itemView.findViewById(R.id.tv_account);
    //     }
    // }

}
