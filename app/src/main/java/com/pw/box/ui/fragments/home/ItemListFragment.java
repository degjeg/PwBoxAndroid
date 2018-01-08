package com.pw.box.ui.fragments.home;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.R;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.ItemRes;
import com.pw.box.cache.Cache;
import com.pw.box.core.ErrorCodes;
import com.pw.box.core.N;
import com.pw.box.core.cmds.DeleteDataTask;
import com.pw.box.tool.StatTool;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;
import com.pw.box.ui.base.ContainerActivity;
import com.pw.box.ui.fragments.data.ViewItemFragment;
import com.pw.box.ui.widgets.RecycleViewDivider;

import java.util.List;


/**
 * 数据列表界面
 * Created by danger on 16/8/30.
 */
public class ItemListFragment extends BaseFragment implements
        BaseRecyclerViewAdapterNew.OnItemClickListener<Data>,
        BaseRecyclerViewAdapterNew.OnItemLongClickListener<Data> {


    ViewHolder viewHolder;
    ItemListAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        viewHolder = new ViewHolder(inflater.inflate(R.layout.fragment_item_list, null, false));

        // viewHolder.recyclerView.showContentView();
        viewHolder.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        viewHolder.recyclerView.addItemDecoration(new RecycleViewDivider(getContext()));
        viewHolder.recyclerView.setPullRefreshEnabled(false);
        viewHolder.recyclerView.setLoadingMoreEnabled(false);

        int gpid = getArguments().getInt("type_id");
        String type = getArguments().getString("type_name");


        List<Data> dataList = Cache.get().getDataListByType(gpid);
        // if(L.E) L.get().e("ItemListFragment", "type=" + type + ",count" + dataList.size() + "," + this);

        if (!dataList.isEmpty()) {
            adapter = new ItemListAdapter(getContext());
            adapter.setData(dataList);
            viewHolder.recyclerView.setAdapter(adapter);
        } /*else {
            viewHolder.stateView.showEmptyView("暂时没有数据,点击添加", R.mipmap.ic_launcher);
        }*/
        adapter.setOnItemClickListener(this);
        adapter.setOnItemLongClickListener(this);
        adapter.notifyDataSetChanged();

        return viewHolder.rootView;
    }

    @Override
    public void onItemClick(Data data, int pos) {
        Bundle arg = new Bundle();
        arg.putLong("id", data.id);

        ContainerActivity.go(activity, ViewItemFragment.class, arg);
    }

    @Override
    public void onItemLongClick(final Data data, int pos) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(R.string.operation);
        builder.setItems(new CharSequence[]{getString(R.string.delete)}, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem(data);
            }
        });

        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.show();
    }

    private void deleteItem(final Data item) {
        showProgressDialog(false/*R.string.deleting_data*/);
        new DeleteDataTask(item).setCallBack(new N.NetHandler<ItemRes>() {
            @Override
            public void onSuccess(ItemRes retPack) {
                dismissDialog();
                if (retPack.retCode == ErrorCodes.SUCCESS) {
                    StatTool.trackEvent(StatTool.EVENT_DEL_DATA, retPack.retCode);

                    Cache.get().deleteData(item, retPack.newDataVer);
                    toast(R.string.data_deleted);
                    HomeFragment homeFragment = (HomeFragment) ((ContainerActivity) getContext()).getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                    homeFragment.notifyDataUpdate();
                } else {
                    toast(ErrorCodes.getErrorDescription(retPack.retCode));
                }
            }

            @Override
            public void onFail(int localErrorCode, Exception e) {
                dismissDialog();
                toast(ErrorCodes.getErrorDescription(localErrorCode));

                StatTool.trackEvent(StatTool.EVENT_DEL_DATA, localErrorCode);
            }
        }).execute();


    }

    private class ViewHolder {
        View rootView;
        XRecyclerView recyclerView;

        public ViewHolder(View rootView) {
            this.rootView = rootView;
            // titleBar =rootView.findViewById(R.id.ti)
            recyclerView = rootView.findViewById(R.id.recycler_view);

            // stateView.setPullRefreshEnabled(false);
            // stateView.setLoadingMoreEnabled(false);
        }
    }
}
