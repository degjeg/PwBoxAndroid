package com.pw.box.ui.fragments.data.item_adapter;


import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.R;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.cache.GroupManager;
import com.pw.box.core.bean.Item;
import com.pw.box.core.bean.ItemTypes;
import com.pw.box.databinding.AdapterViewItem1Binding;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;

import java.util.HashSet;
import java.util.Set;

/**
 * 查看数据的adapter
 * Created by Administrator on 2017/2/21.
 */

public class ViewItemAdapter extends BaseRecyclerViewAdapterNew<Item> {

    public static final int VIEW_TYPE_TEXT = 1; // 普通文本
    final Set<Integer> selectedItems = new HashSet<>();
    int type;
    Integer gpid;
    boolean isEditing = false;
    CallBack callBack;

    public ViewItemAdapter(CallBack callBack, Context context, XRecyclerView recyclerView, Data data) {
        super(context, R.layout.adapter_view_item1);
        this.callBack = callBack;

        setData(data);
    }

    public void setData(Data data) {
        if (data != null) {
            type = data.type;
            gpid = data.gpid;
            setData(ItemTypes.getItems(data));
        }
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isEditing() {
        return isEditing;
    }

    public void setEditing(boolean editing) {
        isEditing = editing;
        if (!isEditing) {
            selectedItems.clear();
        }
    }

    public Set<Integer> getSelectedItems() {
        return selectedItems;
    }

    @Override
    protected Vh<Item> onCreateViewHolder1(ViewGroup parent, int viewType) {
        return new ItemViewHolder(DataBindingUtil.inflate(layoutInflater, getLayoutId(viewType), parent, false));
    }

    @Override
    protected void bindData(Vh<Item> vh, Item item, int pos) {
        super.bindData(vh, item, pos);
        vh.bindData(item, pos);
    }

    public interface CallBack {
        void onItemLongClick(Item item);

        void onSelectChange();
    }

    private class ItemViewHolder extends BaseRecyclerViewAdapterNew.Vh<Item> implements View.OnClickListener, View.OnLongClickListener, CompoundButton.OnCheckedChangeListener {

        final TextView tvName, tvContent;

        public ItemViewHolder(ViewDataBinding binding) {
            super(binding);

            tvName = itemView.findViewById(R.id.tv_name);
            tvContent = itemView.findViewById(R.id.content);
            itemView.setOnClickListener(this);
            itemView.setOnLongClickListener(this);

            AdapterViewItem1Binding binding1 = getBinding();
            binding1.cb.setOnCheckedChangeListener(this);
        }


        @Override
        public void bindData(Item item, int pos) {
            super.bindData(item, pos);
            AdapterViewItem1Binding binding = getBinding();
            if (tvContent != null) {
                if (item.getType() == ItemTypes.SUB_TYPE_TYPE) {
                    tvContent.setText(GroupManager.get().getGroupName(gpid, ""));
                } else {
                    tvContent.setText(item.getValue());
                }
                // tvContent.setHint(item.getName());
            }
            if (tvName != null) {
                String name = item.getName(getType());
                if (name.charAt(0) > 127) { // 是汉字
                    if (name.length() == 2) {
                        name = name.substring(0, 1) + "　　" + name.substring(1, 2);
                    } else if (name.length() == 3) {
                        name += "　";
                    }
                }
                tvName.setText(name);
            }

            if (isEditing) {
                binding.cb.setVisibility(View.VISIBLE);
                binding.cb.setOnCheckedChangeListener(null);

                if (selectedItems.contains(getAdapterPosition() - 1)) {
                    binding.cb.setChecked(true);
                } else {
                    binding.cb.setChecked(false);
                }

                binding.cb.setOnCheckedChangeListener(this);
            } else {
                binding.cb.setVisibility(View.GONE);
            }


        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            callBack.onSelectChange();
            int pos = getAdapterPosition() - 1;

            if (isChecked) { // 已经存在，移除
                selectedItems.add(pos);
            } else {
                selectedItems.remove(pos);
            }
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition() - 1;
            AdapterViewItem1Binding binding = getBinding();

            if (isEditing) {
                binding.cb.setOnCheckedChangeListener(null);
                if (selectedItems.contains(pos)) { // 已经存在，移除
                    binding.cb.setChecked(false);
                    selectedItems.remove(pos);
                } else {
                    binding.cb.setChecked(true);
                    selectedItems.add(pos);
                }

                binding.cb.setOnCheckedChangeListener(this);
                callBack.onSelectChange();
            }
        }

        @Override
        public boolean onLongClick(View v) {

            callBack.onItemLongClick(getItem(getAdapterPosition() - 1));
            return true;
        }


    }
}
