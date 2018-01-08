package com.pw.box.ui.fragments.data.item_adapter;

import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.pw.box.core.bean.Item;
import com.pw.box.ui.fragments.data.PasswordGenerateDialog;

/**
 * 添加数据的密码类型的item
 * Created by Administrator on 2017/2/21.
 */

public class AddItemViewHolder_Password extends BaseAddItemViewHolder implements View.OnClickListener {


    public AddItemViewHolder_Password(AddItemAdapter addItemAdapter, View itemView) {
        super(addItemAdapter, itemView);

    }

    @Override
    public void bindData(Item item, int pos) {
        super.bindData(item, pos);
        btnDropDown.setOnClickListener(this);
        /*itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClick(v);
                return true;
            }
        });*/
    }

    @Override
    public void onClick(View v) {
        PasswordGenerateDialog f = new PasswordGenerateDialog();
        f.setOri(tvContent.getText().toString());
        f.setListener(new PasswordGenerateDialog.PasswordGeneratedListener() {
            @Override
            public void onPasswordGenerated(String s) {
                if (s != null && s.length() > 0)
                    tvContent.setText(s);
            }
        });
        f.show(((FragmentActivity) addItemAdapter.getContext()).getSupportFragmentManager(), null);
    }
}
