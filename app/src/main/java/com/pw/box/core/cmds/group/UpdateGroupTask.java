package com.pw.box.core.cmds.group;

import com.pw.box.bean.protobuf.EditGroupRequest;
import com.pw.box.bean.protobuf.Group;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.core.cmds.Task;
import com.squareup.wire.Message;

/**
 * 更新分组的网络任务
 * Created by Administrator on 2017/3/10.
 */

public class UpdateGroupTask extends Task<byte[]> {

    Group group;

    public UpdateGroupTask(Group group, String name) {
        cmd = CmdIds.EDIT_GP;
        this.group = new Group(group.id, name, null);
    }

    @Override
    protected Message prepareData() throws Exception {
        return new EditGroupRequest(group.id, group.name);
    }
}
