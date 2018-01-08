package com.pw.box.ui.fragments.data;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.R;
import com.pw.box.ads.Ad;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.cache.Cache;
import com.pw.box.core.bean.Item;
import com.pw.box.core.bean.ItemTypes;
import com.pw.box.tool.UnLock;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.data.item_adapter.ViewItemAdapter;
import com.pw.box.ui.widgets.TitleBar;
import com.pw.box.utils.ClipBoardUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static android.R.attr.id;
import static android.R.attr.type;


/**
 * 查看数据的界面
 * Created by danger on 16/9/12.
 */
public class ViewItemFragment extends BaseFragment implements
        View.OnClickListener, ViewItemAdapter.CallBack {

    Holder holder;

    Data data;

    String keyWord;
    ViewItemAdapter adapter;

    long dataId;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        initData();

        holder = new Holder(inflater.inflate(R.layout.fragment_view_item, container, false));
        holder.titleBar.setRightButtonClickListener(this);

        Bundle arg = getArguments();
        if (arg.containsKey("keyWord")) {
            keyWord = arg.getString("keyWord");
        }

        Ad.showBanner(getActivity(), holder.adContainer);

        return holder.rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        UnLock.unLockIfNeeded((ContainerActivity) getActivity());

        data = Cache.get().getData(dataId);
        adapter.setData(data);
    }

    @Override
    public boolean onBackPressed() {
        if (isSelecting()) {
            adapter.setEditing(false);
            holder.titleBar.setRightButtonText(getString(R.string.edit));
            adapter.notifyDataSetChanged();
            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            adapter.notifyDataSetChanged();
        }
    }

    /*@Override
    public void onFragmentResult(int requestCode, int resultCode, Intent data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            byte[] rawData = data.getByteArrayExtra("data");

            try {
                this.data = M.Data.parseFrom(rawData);
            } catch (InvalidProtocolBufferException e) {
                e.printStackTrace();
                return;
            }
            initList();
        }
    }*/

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.btn_right:
                if (isSelecting()) {
                    onClickShare();
                } else {
                    onClickEdit();
                }
                break;
        }
    }


    private void onClickEdit() {
        Bundle arg = new Bundle();
        arg.putByteArray("data", data.encode());

        startActivityForResult(ContainerActivity.getIntent(activity, AddItemFragment.class, arg), 1);
        // ContainerActivity.go(activity, AddItemFragment.class, arg);
    }

    private void onClickShare() {

        if (adapter.getSelectedItems().size() == 0) {
            return;
        }
        List<Integer> selectedList = new ArrayList<>(adapter.getSelectedItems());
        Collections.sort(selectedList);

        for (int i : selectedList) {
            int type = adapter.getItem(i).getType();

            if (type >= ItemTypes.SUB_TYPE_LOGIN_PASSWORD
                    && type < 50) {

                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.warn);
                builder.setMessage(R.string.warn_sure_to_send_password);
                builder.setNegativeButton(R.string.misoperation, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        sureToShare();
                    }
                });
                builder.create().show();
                return;
            }
        }
        sureToShare();
    }

    private void sureToShare() {
        StringBuilder b = new StringBuilder();

        List<Integer> selectedList = new ArrayList<>(adapter.getSelectedItems());
        Collections.sort(selectedList);

        // Log.e("xxxxx", String.format("[sureToShare]%s", Arrays.toString(selectedList.toArray())));
        for (int i : selectedList) {
            Item item = adapter.getItem(i);
            b.append(item.getName(type));
            b.append(":");
            b.append(item.getValue());
            b.append("\n");
        }

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, b.toString());
        shareIntent.setType("text/plain");

        // 设置分享列表的标题，并且每次都显示分享列表
        // startActivity(Intent.createChooser(shareIntent, getString(R.string.send_to)));
        startActivity(shareIntent);
    }

    private void initData() {
        Bundle args = getArguments();
        dataId = args.getLong("id");

        data = Cache.get().getData(id);

        // if (data != null) {
        //
        // }
    }

    //    private void initList() {
    //        holder.itemsView.removeAllViews();
    //
    //        if (!TextUtils.isEmpty(data.getTag())) {
    //            View v = View.inflate(getContext(), R.layout.adapter_view_item, null);
    //            ItemHolder itemHolder = new ItemHolder(ItemTypes.SUB_TYPE_TYPE, data.getTag(), v);
    //            holders.add(itemHolder);
    //            holder.itemsView.addView(v, -1, -2);
    //        }
    //
    //        if (!TextUtils.isEmpty(data.getSubType())) {
    //            View v = View.inflate(getContext(), R.layout.adapter_view_item, null);
    //            ItemHolder itemHolder = new ItemHolder(ItemTypes.SUB_TYPE_SUB_TYPE, data.getSubType(), v);
    //            holders.add(itemHolder);
    //            holder.itemsView.addView(v, -1, -2);
    //        }
    //
    //        for (M.Item item : data.getItemsList()) {
    //            View v = View.inflate(getContext(), R.layout.adapter_view_item, null);
    //            ItemHolder itemHolder = new ItemHolder(item, v);
    //            holders.add(itemHolder);
    //            holder.itemsView.addView(v, -1, -2);
    //        }
    //    }
    //
    public boolean isSelecting() {
        return adapter.isEditing();
    }


    @Override
    public void onItemLongClick(final Item item) {
        // holder.titleBar.setRightButtonText(getString(R.string.send));
        final String[] menus = new String[]{
                getString(R.string.copy),
                getString(R.string.more),
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        // builder.setTitle(R.string.select_type);
        builder.setItems(menus, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                switch (which) {
                    case 0: // 拷贝
                        ClipBoardUtil.copy(getContext(), item.getValue());
                        toast(R.string.password_is_copyed);
                        break;

                    case 1: // 更多
                        holder.titleBar.setRightButtonText(getString(R.string.send));
                        adapter.setEditing(true);
                        // checkBox.setChecked(true);
                        adapter.notifyDataSetChanged();
                        // holder.onLongClick(null);
                        break;
                }
            }
        });
        builder.create().show();
    }

    @Override
    public void onSelectChange() {
        // adapter.setEditing(!adapter.isEditing());
        // adapter.notifyDataSetChanged();
    }

    //    public void showMenusDialog(final ItemHolder holder) {
    //        final  String[] menus= new String[]{
    //                 getString(R.string.copy),
    //                 getString(R.string.more),
    //        };
    //
    //        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
    //        // builder.setTitle(R.string.select_type);
    //        builder.setItems(menus, new DialogInterface.OnClickListener() {
    //            @Override
    //            public void onClick(DialogInterface dialog, int which) {
    //                dialog.dismiss();
    //                switch (which) {
    //                    case 0: // 拷贝
    //                        ClipBoardUtil.copy(getContext(), holder.getValue());
    //                        break;
    //
    //                    case 1: // 更多
    //                        setSelecting(true);
    //                        // holder.onLongClick(null);
    //                        break;
    //                }
    //            }
    //        });
    //        builder.create().show();
    //    }

    private class Holder {
        View rootView;
        TitleBar titleBar;
        XRecyclerView recyclerView;
        FrameLayout adContainer;

        public Holder(View v) {
            this.rootView = v;
            titleBar = v.findViewById(R.id.title);
            recyclerView = v.findViewById(R.id.recycler_view);
            adContainer = v.findViewById(R.id.ad_container);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setLoadingMoreEnabled(false);
            recyclerView.setPullRefreshEnabled(false);
            adapter = new ViewItemAdapter(ViewItemFragment.this, getContext(), recyclerView, data);
            recyclerView.setAdapter(adapter);
            // btn = (TextView) v.findViewById(R.id.btn);
        }
    }
    //
    //    private class ItemHolder implements
    //            View.OnClickListener,
    //            View.OnLongClickListener {
    //        M.Item item;
    //        View itemView;
    //        TextView tvName;
    //        // TextView tvValue;
    //
    //        CheckBox cb;
    //        ImageView btnDropDown;
    //
    //        public ItemHolder(int type, String value, View itemView) {
    //            this(M.Item.newBuilder().setType(type).setValue(value).build(), itemView);
    //        }
    //
    //        public ItemHolder(M.Item item, View itemView) {
    //            this.item = item;
    //            this.itemView = itemView;
    //            itemView.setLayoutParams(new RecyclerView.LayoutParams(-1, -2));
    //            tvName = (TextView) itemView.findViewById(R.id.tv_name);
    //            // tvValue = (TextView) itemView.findViewById(R.id.tv_value);
    //            cb = (CheckBox) itemView.findViewById(R.id.cb);
    //            btnDropDown = (ImageView) itemView.findViewById(R.id.btn_drop_down);
    //            cb.setVisibility(View.GONE);
    //
    //            SpannableStringBuilder builder = new SpannableStringBuilder();
    //            if (item.getType() == ItemTypes.SUB_TYPE_TYPE
    //                    || item.getType() == ItemTypes.SUB_TYPE_SUB_TYPE) {
    //                // tvName.setVisibility(View.GONE);
    //                builder.append(getValue());
    //                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text_color_black)), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    //            } else {
    //                // tvName.setText(ItemTypes.getName(getContext(), data.getType(), item.getType()));
    //                builder.append(ItemTypes.getItemByType(getContext(), data.getType(), item.getType()).getName());
    //
    //                builder.append(":");
    //                builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text_color_black)), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    //                // builder.setSpan(new AbsoluteSizeSpan(getResources().getDimensionPixelSize(R.dimen.text_size_large)), 0, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    //                builder.append(getValue());
    //            }
    //
    //            if (keyWord != null) {
    //                Range<Integer> r = PinyinUtils.matchsPinYin(builder.toString(), keyWord);
    //                if (r != null) {
    //                    builder.setSpan(new ForegroundColorSpan(ContextCompat.getColor(getContext(), R.color.text_color_red)), r.getStart(), r.getEnd() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    //                }
    //            }
    //
    //            tvName.setText(builder);
    //
    //            itemView.setOnClickListener(this);
    //            itemView.setOnLongClickListener(this);
    //        }
    //
    //
    //        @Override
    //        public void onClick(View v) {
    //            if (cb.getVisibility() == View.VISIBLE) {
    //                cb.setChecked(!cb.isChecked());
    //            }
    //        }
    //
    //        @Override
    //        public boolean onLongClick(View v) {
    //            if (!isSelecting()) {
    //                showMenusDialog(this);
    //            }
    //            return true;
    //        }
    //
    //        public void setSelecting(boolean selecting) {
    //            if (selecting) {
    //                cb.setVisibility(View.VISIBLE);
    //            } else {
    //                cb.setChecked(false);
    //                cb.setVisibility(View.GONE);
    //            }
    //        }
    //
    //        public String getValue() {
    //            StringBuilder builder = new StringBuilder();
    //            if (!TextUtils.isEmpty(item.getName())) {
    //                builder.append(item.getName());
    //                builder.append(" ");
    //            }
    //
    //            builder.append(item.getValue());
    //
    //            return builder.toString();
    //        }
    //
    //        public boolean isSelected() {
    //            return cb.isChecked();
    //        }
    //    }
}

