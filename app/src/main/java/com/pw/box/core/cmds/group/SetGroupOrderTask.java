package com.pw.box.core.cmds.group;

import com.pw.box.bean.protobuf.IntList;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.core.cmds.Task;
import com.squareup.wire.Message;

import java.util.List;

/**
 * 设置group的名称的任务
 * Created by Administrator on 2017/3/10.
 */

public class SetGroupOrderTask extends Task<byte[]> {
    List<Integer> orders;

    public SetGroupOrderTask(List<Integer> orders) {
        super();
        cmd = CmdIds.CHANGE_GP_ORDER;
        this.orders = orders;
    }

    @Override
    protected Message prepareData() throws Exception {
        return new IntList(orders);
    }
}
