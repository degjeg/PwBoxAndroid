package com.pw.box.ui.fragments.data;

import android.content.Context;

import com.pw.box.R;
import com.pw.box.bean.protobuf.Group;
import com.pw.box.databinding.AdapterGpListBinding;
import com.pw.box.ui.base.BaseRecyclerViewAdapterNew;

import java.util.List;

/**
 * 分组列表adapter
 * Created by Administrator on 2017/3/9.
 */

public class GroupListAdapter extends BaseRecyclerViewAdapterNew<Group> {
    public GroupListAdapter(Context context) {
        super(context, R.layout.adapter_gp_list);
    }

    void update(Group group) {
        List<Group> data = getData();
        for (int i = 0; i < data.size(); i++) {
            if (group.id.equals(data.get(i).id)) {
                data.set(i, group);
                notifyDataSetChanged();
                return;
            }
        }
    }

    @Override
    protected void bindData(Vh<Group> vh, Group group, int pos) {
        super.bindData(vh, group, pos);
        vh.bindData(group, pos);
        AdapterGpListBinding binding = vh.getBinding();
        binding.tvName.setText(group.name);

    }
}
