package com.pw.box.core.cmds;


import com.pw.box.bean.protobuf.Data;
import com.pw.box.bean.protobuf.GetItemListReq;
import com.pw.box.bean.protobuf.GetItemListRes;
import com.pw.box.bean.protobuf.ItemOfServer;
import com.pw.box.cache.Cache;
import com.pw.box.cache.User;
import com.pw.box.core.N;
import com.pw.box.utils.Aes256;
import com.pw.box.utils.GZIPUtil;
import com.pw.box.utils.L;
import com.squareup.wire.Message;

import java.util.concurrent.atomic.AtomicBoolean;

import okio.ByteString;

/**
 * 同后台同步数据的工具类
 * Created by danger on 16/9/12.
 */
public class DataSyncTask extends Task implements N.NetHandler<GetItemListRes> {
    private static final java.lang.String TAG = "DataSyncTask_";

    final AtomicBoolean isCanceled = new AtomicBoolean(false);

    public DataSyncTask() {
        setCallBack(this);
        cmd = CmdIds.GET_ITEM_LIST;
    }

    @Override
    protected Message prepareData() throws Exception {
        GetItemListReq.Builder builder = new GetItemListReq.Builder();
        builder.v(Cache.get().getDataVersion());
        builder.from(Cache.get().getMaxDataId());
        return builder.build();
        // Cm.get().getConnection().sendPack(CmdIds.GET_ITEM_LIST, builder.build(), new DataSyncTask());
    }

    @Override
    public void onSuccess(GetItemListRes retPack) {
        if (isCanceled.get()) {
            return;
        }
        synchronized (isCanceled) {
            if (Cache.get().getDataVersion() != retPack.v) {
                Cache.get().clearAllData();
                Cache.get().setDataVersion(retPack.v);
            }

            User user = Cache.get().getUser();
            int oldCount = Cache.get().getDataCount();
            for (int i = 0; i < retPack.items.size(); i++) {
                try {
                    ItemOfServer itemOfServer = retPack.items.get(i);
                    ByteString encryptedData = itemOfServer.data;
                    byte[] decryptedData = Aes256.decrypt(encryptedData.toByteArray(), user.getRawKey());
                    byte[] decompressedData = decryptedData; // GZIPUtil.gZIPUncompress(decryptedData);

                    if (itemOfServer.compressed != 0) {
                        decompressedData = GZIPUtil.gZIPUncompress(decryptedData);
                    }
                    Data data = Data.ADAPTER.decode(decompressedData).newBuilder().id(itemOfServer.id).build();
                    Cache.get().addData(data, retPack.v);
                } catch (Exception e) {
                    if (L.E) L.get().e(TAG, "", e);
                }
            }

            int newCount = Cache.get().getDataCount();
            if (newCount < retPack.total
                    && oldCount < newCount) {
                // syncDataList();
                Cache.get().syncData();
            } else {
                Cache.get().onSyncDataSuccess();
            }
            // if(L.E) L.get().e("");
        }

    }

    @Override
    public void onFail(int localErrorCode, Exception e) {
        if (isCanceled.get()) {
            return;
        }

        synchronized (isCanceled) {
            Cache.get().onSyncDataFail();
        }
    }

    public void cancel() {
        synchronized (isCanceled) {
            isCanceled.set(true);
        }
    }

    //    public void syncDataList() {
    //        Cm.get().getDataList(Cache.get().getDataVersion(), Cache.get().getMaxDataId(), this);
    //    }
}