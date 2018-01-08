package com.pw.box.core.cmds.group;


import com.pw.box.bean.protobuf.GetGroupListRequest;
import com.pw.box.bean.protobuf.GetGroupListResponse;
import com.pw.box.bean.protobuf.Group;
import com.pw.box.cache.GroupManager;
import com.pw.box.core.N;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.core.cmds.Task;
import com.pw.box.utils.Md5;
import com.squareup.wire.Message;

import java.io.IOException;
import java.util.List;

/**
 * 获取分组列表工具类
 * Created by Administrator on 2017/3/10.
 */

public class GetGroupListTask extends Task<byte[]> implements N.NetHandler<byte[]> {

    public GetGroupListTask() {
        super();
        cmd = CmdIds.GET_GPS;
        setCallBack(this);
    }

    @Override
    protected Message prepareData() throws Exception {
        List<Group> gps = GroupManager.get().getPgs();
        String md5 = "";
        if (gps != null && gps.size() > 0) {
            md5 = Md5.md5(new GetGroupListResponse.Builder().gps(gps).build().encode());
        }

        return new GetGroupListRequest.Builder().md5(md5).build();
    }

    @Override
    public void onSuccess(byte[] retPack) {
        try {
            if (retPack != null) {
                GetGroupListResponse groupList = GetGroupListResponse.ADAPTER.decode(retPack);
                if (groupList.retCode == 0) {
                    GroupManager.get().setGps(groupList.gps);
                    GroupManager.get().notifyGpsChange();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFail(int localErrorCode, Exception e) {

    }
}
