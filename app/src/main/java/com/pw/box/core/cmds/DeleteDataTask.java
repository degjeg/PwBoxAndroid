package com.pw.box.core.cmds;


import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.DeleteItemReq;
import com.pw.box.bean.protobuf.ItemRes;
import com.squareup.wire.Message;

/**
 */
public class DeleteDataTask extends Task<ItemRes> {
    Data data;

    public DeleteDataTask(Data data) {
        this.data = data;
        cmd = CmdIds.DELETE_ITEM;
    }

    @Override
    protected Message prepareData() throws Exception {
        DeleteItemReq.Builder builder = new DeleteItemReq.Builder();

        builder.id.add(data.id);
        // builder.setData(ByteString.copyFrom(data.toByteArray()));
        // builder.setData(ByteString.copyFrom(Aes256.encrypt(data.toByteArray(), Cache.get().getUser().getRawKey())));
        return builder.build();
    }
}
