package com.pw.box.core.cmds;


import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.ItemOfServer;
import com.pw.box.bean.protobuf.ItemRes;
import com.pw.box.cache.Cache;
import com.pw.box.core.PacketCreator;
import com.pw.box.utils.Aes256;
import com.squareup.wire.Message;

import okio.ByteString;

/**
 */
public class AddDataTask extends Task<ItemRes> {
    Data data;

    public AddDataTask(Data data) {
        this.data = data;
        cmd = CmdIds.ADD_ITEM;
    }

    @Override
    protected Message prepareData() throws Exception {
        ItemOfServer.Builder builder = new ItemOfServer.Builder();

        byte[] rawData = data.encode();
        byte[] compressedData = PacketCreator.tryToCompressData(rawData);

        builder.compressed(compressedData == rawData ? 0 : 1);

        // builder.setData(ByteString.copyFrom(data.toByteArray()));
        builder.data(ByteString.of(Aes256.encrypt(compressedData, Cache.get().getUser().getRawKey())));

        return builder.build();
    }
}
