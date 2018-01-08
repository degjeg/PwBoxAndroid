package com.pw.box.core.cmds.group;

import com.pw.box.bean.protobuf.Group;
import com.pw.box.bean.protobuf.IntList;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.core.cmds.Task;
import com.squareup.wire.Message;

import java.util.ArrayList;
import java.util.List;

/**
 * 删除分组的任务
 * Created by Administrator on 2017/3/10.
 */

public class DeleteGroupTask extends Task<byte[]> {

    List<Integer> ids;

    public DeleteGroupTask(Group g) {
        cmd = CmdIds.DELETE_GP;
        ids = new ArrayList<>();
        ids.add(g.id);
    }

    @Override
    protected Message prepareData() throws Exception {
        return new IntList(ids);
    }
}
