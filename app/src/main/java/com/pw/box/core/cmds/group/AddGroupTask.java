package com.pw.box.core.cmds.group;

import com.pw.box.bean.protobuf.AddGroupRequest;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.core.cmds.Task;
import com.squareup.wire.Message;

/**
 * 添加数组分组的任务
 * Created by Administrator on 2017/3/10.
 */

public class AddGroupTask extends Task<byte[]> {

    String name;

    public AddGroupTask(String name) {
        cmd = CmdIds.ADD_GP;
        this.name = name;
    }

    @Override
    protected Message prepareData() throws Exception {
        return new AddGroupRequest(name, null);
    }

}
