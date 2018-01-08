package com.pw.box.ui.fragments.data;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.pw.box.R;
import com.pw.box.bean.protobuf.AddGroupResponse;
import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.Group;
import com.pw.box.cache.GroupManager;
import com.pw.box.core.N;
import com.pw.box.core.cmds.group.AddGroupTask;
import com.pw.box.core.cmds.group.DeleteGroupTask;
import com.pw.box.core.cmds.group.GetGroupListTask;
import com.pw.box.core.cmds.group.SetGroupOrderTask;
import com.pw.box.core.cmds.group.UpdateGroupTask;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;
import com.pw.box.ui.fragments.data.item_adapter.AddItemTouchHelper;
import com.pw.box.ui.widgets.TitleBar;
import com.pw.box.utils.DensitiUtil;

import java.io.IOException;
import java.util.Collections;
import java.util.List;


/**
 * 数据分组列表
 * Created by danger on 16/9/12.
 */
public class GroupListFragment extends BaseFragment implements
        BaseRecyclerViewAdapterNew.OnItemClickListener<Group>,
        GroupManager.GroupChangeListener {

    Holder holder;

    Data data;

    String keyWord;
    GroupListAdapter adapter;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        new GetGroupListTask().execute();

        holder = new Holder(inflater.inflate(R.layout.fragment_group_list, container, false));
        holder.titleBar.setRightButtonText(R.string.add);
        holder.titleBar.setRightButtonClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // toast("添加");
                        showAddGroupDialog(null);
                    }
                }
        );
        holder.titleBar.setLeftButtonText(R.string.gp_edit);

        List<Group> gps = GroupManager.get().getPgs();
        adapter.setData(gps);
        changeStatusIfNeeded();


        ItemTouchHelper helper = new ItemTouchHelper(new AddItemTouchHelper(new AddItemTouchHelper.ItemTouchHelperCallback() {
            @Override
            public boolean onItemMove(int fromPosition, int toPosition) {
                //                if (fromPosition <= 1 || toPosition <= 1) {
                //                    return false;
                //                }
                //                if (fromPosition >= adapter.getItemCount()
                //                        || toPosition >= adapter.getItemCount()) {
                //                    return false;
                //                }

                Log.e("XXXXXXXXXXXXXX", String.format("%d x %d", fromPosition, toPosition));
                Collections.swap(adapter.getData(), fromPosition - 1, toPosition - 1);
                changeStatusIfNeeded();
                adapter.notifyItemMoved(fromPosition, toPosition);

                return true;
            }

            @Override
            public void onItemDismiss(int position) {
                Group gp = adapter.getItem(position - 1);

                if (GroupManager.get().getDataCount(gp) > 0) {
                    toast(R.string.data_in_group_not_empty);
                } else {
                    adapter.removeItem(position - 1);
                    new DeleteGroupTask(gp).execute();
                    GroupManager.get().deleteGroup(gp);
                    changeStatusIfNeeded();
                }

                adapter.notifyDataSetChanged();

            }
        }, holder.recyclerView) {
            public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
                final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                final int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;

                return makeMovementFlags(dragFlags, swipeFlags);
            }
        });

        helper.attachToRecyclerView(holder.recyclerView);

        GroupManager.get().addListener(this);

        return holder.rootView;
    }

    void changeStatusIfNeeded() {

        if (adapter.getItemCount() == 0) { // 数据为空
            holder.emptyView.setVisibility(View.VISIBLE);
            holder.emptyView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new GetGroupListTask().execute();
                }
            });
            holder.recyclerView.setVisibility(View.GONE);

        } else {// 数据不为空
            holder.emptyView.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.VISIBLE);

        }
    }

    private void showAddGroupDialog(final Group group) {
        FrameLayout c = new FrameLayout(getContext());
        final TextInputEditText et = new TextInputEditText(getContext());
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(-1, -2);
        lp.leftMargin = DensitiUtil.dp2px(getContext(), 10);
        lp.rightMargin = DensitiUtil.dp2px(getContext(), 10);

        et.setFilters(new InputFilter[]{new InputFilter.LengthFilter(32)});
        c.addView(et, lp);

        if (group != null) {
            et.setText(group.name);
        }
        Dialog dialog = new AlertDialog.Builder(getContext())
                .setTitle(group == null ? R.string.add_group : R.string.change_group)
                .setView(c)
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(final DialogInterface dialog, int which) {
                        final String name = et.getText().toString();

                        if (group == null) {
                            if (name.trim().length() == 0) {
                                dialog.dismiss();
                                return;
                            }

                            showProgressDialog(R.string.saving);
                            N.NetHandler<byte[]> addHandler = new N.NetHandler<byte[]>() {
                                @Override
                                public void onSuccess(byte[] retPack) {
                                    dialog.dismiss();
                                    dismissDialog();
                                    try {
                                        AddGroupResponse response = AddGroupResponse.ADAPTER.decode(retPack);
                                        if (response.retCode == 0) {
                                            Group group1 = new Group(response.id, name, null);
                                            adapter.addItem(group1);
                                            adapter.notifyDataSetChanged();
                                            GroupManager.get().updateGroup(group1);
                                            changeStatusIfNeeded();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFail(int localErrorCode, Exception e) {
                                    dialog.dismiss();
                                    dismissDialog();
                                    toast(R.string.failed);
                                }
                            };
                            new AddGroupTask(name).setCallBack(addHandler).execute();
                        } else {
                            final Group group1 = new Group(group.id, name, null);
                            // adapter.setData()
                            updateGroup(group1);
                            GroupManager.get().updateGroup(group1);
                            showProgressDialog(R.string.saving);
                            dismissDialog();
                            N.NetHandler<byte[]> editHandler = new N.NetHandler<byte[]>() {
                                @Override
                                public void onSuccess(byte[] retPack) {
                                    dialog.dismiss();
                                    dismissDialog();
                                    adapter.update(group1);
                                    GroupManager.get().setGps(adapter.getData());
                                }

                                @Override
                                public void onFail(int localErrorCode, Exception e) {
                                    dialog.dismiss();
                                    toast(R.string.failed);
                                }
                            };
                            if (TextUtils.equals(name.trim(), group.name)) {
                                dialog.dismiss();
                                dismissDialog();
                                return;
                            }
                            new UpdateGroupTask(group, name).setCallBack(editHandler).execute();
                        }
                    }
                })
                .create();
        dialog.show();
    }

    private void updateGroup(Group group1) {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        GroupManager.get().removeListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onBackPressed() {
        if (saveData()) {
            return true;
        }
        return super.onBackPressed();
    }

    private boolean saveData() {
        // 本地删除的,要去服务器删除,本地排序的,要通知服务器
        // List<Group>newList = adapter.getData();
        // List<Group>oldList = GroupManager.get().getPgs();

        // List<Group>addList = new ArrayList<>();
        // List<Group>editList = new ArrayList<>();
        // List<Integer>delList = new ArrayList<>();
        final List<Integer> l = GroupManager.get().getOrdersIfChanged(adapter.getData());


        if (l != null) {

            AlertDialog dlg = new AlertDialog.Builder(getMyContext())
                    .setMessage(R.string.sure_to_save_orders)
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sureToSaveOrders(l);
                        }
                    })
                    .create();

            dlg.show();

            return true;
        }
        return false;
    }

    private void sureToSaveOrders(final List<Integer> l) {
        showProgressDialog(R.string.saving);
        N.NetHandler<byte[]> h = new N.NetHandler<byte[]>() {
            @Override
            public void onSuccess(byte[] retPack) {
                dismissDialog();
                GroupManager.get().setOrders(l);
                GroupManager.get().setGps(adapter.getData());
                finish();
            }

            @Override
            public void onFail(int localErrorCode, Exception e) {
                dismissDialog();
                toast(R.string.failed);
            }
        };
        new SetGroupOrderTask(l).setCallBack(h).execute();
    }

    @Override
    public void onGroupChanged(List<Group> gps) {
        adapter.setData(gps);
    }

    @Override
    public void onItemClick(Group group, int pos) {
        showAddGroupDialog(adapter.getItem(pos));
    }

    private class Holder {
        View rootView;
        TitleBar titleBar;
        View emptyView;
        XRecyclerView recyclerView;

        public Holder(View v) {
            this.rootView = v;
            titleBar = v.findViewById(R.id.title_view);
            emptyView = v.findViewById(R.id.empty_view);

            recyclerView = v.findViewById(R.id.recycler_view);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setLoadingMoreEnabled(false);
            recyclerView.setPullRefreshEnabled(false);
            adapter = new GroupListAdapter(getContext());
            adapter.setOnItemClickListener(GroupListFragment.this);
            recyclerView.setAdapter(adapter);
            // btn = (TextView) v.findViewById(R.id.btn);
        }
    }
}

