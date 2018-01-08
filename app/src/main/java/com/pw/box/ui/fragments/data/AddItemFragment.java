package com.pw.box.ui.fragments.data;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.R;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.Item;
import com.pw.box.bean.protobuf.ItemRes;
import com.pw.box.cache.Cache;
import com.pw.box.cache.Constants;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.bean.BuiltInTypes;
import com.pw.box.core.bean.ItemTypes;
import com.pw.box.core.cmds.AddDataTask;
import com.pw.box.core.cmds.UpdateDataTask;
import com.pw.box.tool.StatTool;
import com.pw.box.tool.UnLock;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.data.item_adapter.AddItemAdapter;
import com.pw.box.ui.fragments.data.item_adapter.AddItemTouchHelper;
import com.pw.box.ui.widgets.TitleBar;
import com.pw.box.utils.PrefUtil;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * 添加数据的fragment
 * Created by danger on 16/9/12.
 */
public class AddItemFragment extends BaseFragment implements
        View.OnClickListener {

    Holder holder;

    Integer type;
    String tag;
    AddItemAdapter adapter;
    private Data data;
    private Data newData;
    N.NetHandler<ItemRes> addItemHandler = new N.NetHandler<ItemRes>() {
        @Override
        public void onSuccess(ItemRes retPack) {
            if (holder == null) {
                return;
            }
            dismissDialog();
            if (retPack.retCode == ErrorCodes.SUCCESS) {
                StatTool.trackEvent(StatTool.EVENT_ADD_DATA, retPack.retCode);
                toast(R.string.data_saved);


                // M.ItemOfServer itemReq = (M.ItemOfServer) reqPack;
                Cache.get().addData(newData.newBuilder().id(retPack.newDataId.longValue()).build(), retPack.newDataVer);
                //
                // Intent intent = new Intent();
                // intent.putExtra("data", itemReq.getData().toByteArray());
                // setResult(Activity.RESULT_OK, intent);
                finish();
            } else {
                toast(ErrorCodes.getErrorDescription(retPack.retCode));
            }
        }

        @Override
        public void onFail(int localErrorCode, Exception e) {
            if (holder == null) {
                return;
            }

            StatTool.trackEvent(StatTool.EVENT_ADD_DATA, localErrorCode);
            dismissDialog();
            toast(R.string.failed);

        }
    };
    N.NetHandler<ItemRes> editItemHandler = new N.NetHandler<ItemRes>() {
        @Override
        public void onSuccess(ItemRes retPack) {
            if (holder == null) {
                return;
            }
            dismissDialog();
            if (retPack.retCode == ErrorCodes.SUCCESS) {
                StatTool.trackEvent(StatTool.EVENT_EDIT_DATA, retPack.retCode);

                // M.ItemReq itemReq = (M.ItemReq) reqPack;
                toast(R.string.data_saved);
                Cache.get().updateData(newData, retPack.newDataVer);
                finish();
                // Intent intent = new Intent();
                // intent.putExtra("data", itemReq.getData().toByteArray());
                // setResult(Activity.RESULT_OK, intent);
            } else {
                toast(ErrorCodes.getErrorDescription(retPack.retCode));
            }
        }

        @Override
        public void onFail(int localErrorCode, Exception e) {
            if (holder == null) {
                return;
            }
            StatTool.trackEvent(StatTool.EVENT_EDIT_DATA, localErrorCode);
            dismissDialog();
            toast(R.string.failed);
        }
    };
    private boolean hasShowTotu = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        holder = new Holder(inflater.inflate(R.layout.fragment_add_item, container, false));

        initArg();

        adapter = new AddItemAdapter(getContext(), data);

        if (data == null && type == null) {
            /*holder.itemsView.postDelayed(new Runnable() {
                @Override
                public void run() {*/
            showSelectTypeDialog();
                /*}
            }, 1000);*/

        } else if (data == null) {
            initData();
            holder.titleBar.setLeftButtonText(R.string.add_item);
        } else {
            // initData(data);
            showTotuDlg();
            holder.titleBar.setLeftButtonText(R.string.edit_item);
        }
        holder.recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        holder.recycler_view.setAdapter(adapter);
        // holder.btn.setOnClickListener(this);
        holder.titleBar.setRightButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickSave();
            }
        });


        ItemTouchHelper helper = new ItemTouchHelper(new AddItemTouchHelper(new AddItemTouchHelper.ItemTouchHelperCallback() {
            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {
                if (fromPosition <= 1 || toPosition <= 1) {
                    return false;
                }
                if (fromPosition >= adapter.getItemCount()
                        || toPosition >= adapter.getItemCount()) {
                    return false;
                }

                Log.e("XXXXXXXXXXXXXX", String.format("%d x %d", fromPosition, toPosition));
                Collections.swap(adapter.getData(), fromPosition - 1, toPosition - 1);
                adapter.notifyItemMoved(fromPosition, toPosition);
                return true;
            }

            @Override
            public void onItemDismiss(int position) {
                adapter.removeItem(position - 1);
                adapter.notifyDataSetChanged();
            }
        }, holder.recycler_view));

        holder.recycler_view.setLoadingMoreEnabled(false);
        holder.recycler_view.setPullRefreshEnabled(false);

        helper.attachToRecyclerView(holder.recycler_view);

        return holder.rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        UnLock.unLockIfNeeded((ContainerActivity) getActivity());
    }

    private void initArg() {
        Bundle arg = getArguments();
        if (arg == null) {
            return;
        }
        byte rawData[] = arg.getByteArray("data");
        try {
            if (rawData != null)
                data = Data.ADAPTER.decode(rawData);
        } catch (Exception e) {
            // e.printStackTrace();
        }
    }

    private void initData() {
        switch (type) {
            case ItemTypes.TYPE_BANK:
                adapter.setData(Arrays.asList(BuiltInTypes.BANK()));
                break;
            case ItemTypes.TYPE_WEBSITE:
                adapter.setData(Arrays.asList((BuiltInTypes.WEB)));
                break;
            case ItemTypes.TYPE_IM:
            case ItemTypes.TYPE_GAME:
                adapter.setData(Arrays.asList((BuiltInTypes.IM)));
                break;

            case ItemTypes.SUB_TYPE_OTHER:
            default:
                adapter.setData(Arrays.asList((BuiltInTypes.OTHER)));
                break;
        }
    }

    private void showSelectTypeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.select_type);

        final List<com.pw.box.core.bean.Item> types = Arrays.asList(BuiltInTypes.TYPE_LIST()); // com.pw.box.core.bean.Item.combine(Cache.get().getAllTypes(), BuiltInTypes.TYPE_LIST());

        for (com.pw.box.core.bean.Item type : types) {
            Log.d("", "type:" + type);
        }
        final String[] names = com.pw.box.core.bean.Item.getNamesArray(types);
        builder.setItems(names, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                type = types.get(which).getType();
                tag = types.get(which).getValue();
                adapter.setType(type);
                initData();

                showTotuDlg();
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.create().show();
    }

    private void showTotuDlg() {
        if (hasShowTotu) {
            return;
        }
        hasShowTotu = true;
        int cnt = PrefUtil.getInt(getContext(), Constants.PREF_KEY_DRAG, 0);
        if (cnt >= 2) {
            return;
        }
        PrefUtil.setInt(getContext(), Constants.PREF_KEY_DRAG, ++cnt);

        AlertDialog dlg = new AlertDialog.Builder(getContext())
                .setMessage(R.string.tips_of_drag_item)
                .setPositiveButton(R.string.i_see, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create();
        dlg.show();
    }

    @Override
    public boolean onBackPressed() {
        Data.Builder dataBuilder = getNewData();

        newData = dataBuilder.build();
        if (this.data == null
                || Arrays.hashCode(this.data.encode())
                != Arrays.hashCode(newData.encode())) {

            AlertDialog dlg = new AlertDialog.Builder(getContext())
                    .setMessage(R.string.message_data_not_saved)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.sure, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .create();
            dlg.show();

            return true;
        }
        return super.onBackPressed();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        holder = null;
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        if (v.getId() == R.id.btn) {
            onClickSave();
        }
    }

    private void onClickSave() {
        Data.Builder dataBuilder = getNewData();

        if (dataBuilder.items.size() <= 0) {
            toast(R.string.content_is_empty);
            return;
        }
        newData = dataBuilder.build();
        if (newData.encode().length > 1500) {
            toast(R.string.content_too_long);
            return;
        }

        if (this.data == null) {
            showProgressDialog(false);
            new AddDataTask(newData).setCallBack(addItemHandler).execute();
            // Cm.get().addData(newData, addItemHandler);
            // Cache.get().addData(data.build());
        } else {
            if (Arrays.hashCode(this.data.encode())
                    == Arrays.hashCode(newData.encode())) {
                toast(R.string.data_is_saved);
                dismissDialog();
                finish();
                return;
            }
            showProgressDialog(false);
            new UpdateDataTask(newData).setCallBack(editItemHandler).execute();
            // Cm.get().updateData(newData, editItemHandler);
            // Cache.get().updateData(data.build());
        }
        // Intent intent = new Intent();
        // intent.putExtra("data", data.build().toByteArray());
        // setResult(Activity.RESULT_OK, intent);
    }

    @NonNull
    private Data.Builder getNewData() {
        Data.Builder dataBuilder = new Data.Builder();
        dataBuilder.type(adapter.getType());

        if (this.data != null) {
            dataBuilder.id(this.data.id);
        }

        dataBuilder.gpid = adapter.getGpId();
        List<com.pw.box.core.bean.Item> itemList = adapter.getData();
        for (int i = 0; i < itemList.size(); i++) {
            com.pw.box.core.bean.Item item = itemList.get(i);
            String val = item.getValue();

            switch (item.getType()) {
                case ItemTypes.SUB_TYPE_TYPE:
                    if (!TextUtils.isEmpty(val)) dataBuilder.tag(val);
                    else dataBuilder.tag(item.getName());

                    break;
                case ItemTypes.SUB_TYPE_SUB_TYPE:
                    dataBuilder.sub_type(val);
                    break;
                default:
                    if (!TextUtils.isEmpty(val) || !TextUtils.isEmpty(item.getName())) {
                        Item.Builder builder = new Item.Builder().type(item.getType()).value(val);
                        if (!TextUtils.isEmpty(item.getName())) {
                            builder.name(item.getName());
                        }
                        dataBuilder.items.add(builder.build());
                    }
                    break;
            }
        }
        return dataBuilder;
    }

    private class Holder {
        View rootView;
        TitleBar titleBar;
        // ScrollView scrollView;
        XRecyclerView recycler_view;
        ViewGroup itemsView;
        // TextView btn;

        public Holder(View v) {
            this.rootView = v;
            titleBar = v.findViewById(R.id.title);
            // scrollView = (ScrollView) v.findViewById(R.id.scroll_view);
            recycler_view = v.findViewById(R.id.recycler_view);
            // itemsView = (ViewGroup) v.findViewById(R.id.items_view);
            // btn = (TextView) v.findViewById(R.id.btn);
        }
    }
}

