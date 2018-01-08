package com.pw.box.ui.fragments.data.item_adapter;

import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pw.box.R;
import com.pw.box.core.bean.Item;
import com.pw.box.core.bean.ItemTypes;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;
import com.pw.box.ui.dialog.InputDialog;

/**
 * 添加数据item的基类
 * Created by Administrator on 2017/2/21.
 */

public class BaseAddItemViewHolder extends BaseRecyclerViewAdapterNew.Vh<Item> implements TextWatcher {

    AddItemAdapter addItemAdapter;
    TextView tvContent;
    TextView tvName;
    ImageView btnDropDown;

    Item item;

    public BaseAddItemViewHolder(final AddItemAdapter addItemAdapter, View itemView) {
        super(itemView);
        itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
        this.addItemAdapter = addItemAdapter;
        tvContent = itemView.findViewById(R.id.content);
        tvName = itemView.findViewById(R.id.tv_name);
        btnDropDown = itemView.findViewById(R.id.btn_drop_down);

        if (tvContent != null) {
            /*if (addItemAdapter.editble) {*/
            tvContent.addTextChangedListener(this);
            // tvContent.setOnLongClickListener(new );
            // } else {
            //     tvContent.setEnabled(false);
            //     tvContent.setFocusable(false);
            // }
        }

        // if (btnDropDown != null) {
        //     if (!addItemAdapter.editble) {
        //         btnDropDown.setVisibility(View.GONE);
        //     }
        // }

        if (tvName != null) {
            tvName.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            /*if (item.getType() > 10)*/
                            {
                                if (item == null // 前2个不允许编辑
                                        || item.getType() == ItemTypes.SUB_TYPE_TYPE
                                        || item.getType() == ItemTypes.SUB_TYPE_SUB_TYPE) {
                                    return;
                                }
                                InputDialog dialog = new InputDialog(addItemAdapter.getContext());
                                dialog.setValue(tvName.getText().toString());
                                dialog.setListener(new InputDialog.OnTextInputedListener() {
                                    @Override
                                    public void onTextInputed(String s) {
                                        item.setName(s);
                                        tvName.setText(item.getName(addItemAdapter.getType()));
                                    }
                                });

                                dialog.show();

                            }
                        }
                    }
            );
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        int pos = getAdapterPosition() - 1;
        addItemAdapter.getItem(pos).setValue(tvContent.getText().toString());

    }

    @Override
    public void bindData(Item item, int pos) {
        super.bindData(item, pos);
        this.item = item;
        if (btnDropDown != null) {
            if (getItemViewType() == AddItemAdapter.VIEW_TYPE_PASSWORD
                    || getItemViewType() == AddItemAdapter.VIEW_TYPE_SUB_TYPE
                    ) {
                btnDropDown.setVisibility(View.VISIBLE);
            } else {
                btnDropDown.setVisibility(View.GONE);
            }
        }
        if (tvContent != null) {
            tvContent.setText(item.getValue());
            tvContent.setHint(item.getName(addItemAdapter.getType()));
        }
        if (tvName != null) {
            String name = item.getName(addItemAdapter.getType());
            if (name.charAt(0) > 127) { // 是汉字
                if (name.length() == 2) {
                    name = name.substring(0, 1) + "　　" + name.substring(1, 2);
                } else if (name.length() == 3) {
                    name += "　";
                }
            }
            tvName.setText(name);
        }
    }
}
