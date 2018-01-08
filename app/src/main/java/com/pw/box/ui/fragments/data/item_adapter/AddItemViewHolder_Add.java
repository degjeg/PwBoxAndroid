//package com.pw.box.ui.fragments.data.item_adapter;
//
//import android.content.Context;
//import android.content.DialogInterface;
//import android.support.v7.app.AlertDialog;
//import android.view.View;
//import android.widget.TextView;
//
//import com.pw.box.R;
//import com.pw.box.core.bean.BuiltInTypes;
//import com.pw.box.core.bean.Item;
//
///**
// * 添加数据最底部的添加按钮
// * Created by Administrator on 2017/2/21.
// */
//
//public class AddItemViewHolder_Add extends BaseAddItemViewHolder implements View.OnClickListener {
//    TextView btn;
//
//    public AddItemViewHolder_Add(AddItemAdapter addItemAdapter, View itemView) {
//        super(addItemAdapter, itemView);
//        btn = itemView.findViewById(R.id.btn);
//    }
//
//    @Override
//    public void bindData(Item item) {
//        super.bindData(item);
//        btn.setOnClickListener(this);
//    }
//
//    private void showAddItemDialog() {
//        final Context context = addItemAdapter.getContext();
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle(R.string.add);
//
//        builder.setItems(Item.getNamesArray(BuiltInTypes.SUB_TYPE_LIST()), new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//
//                int type = BuiltInTypes.SUB_TYPE_LIST()[which].getType();
//                addItemAdapter.addItem(BuiltInTypes.SUB_TYPE_LIST()[which]);
//                addItemAdapter.notifyDataSetChanged();
//                // addItemAdapter.re
//                // View v = View.inflate(context, R.layout.adapter_add_item, null);
//            }
//        });
//        builder.create().show();
//    }
//
//    @Override
//    public void onClick(View v) {
//        showAddItemDialog();
//    }
//}
