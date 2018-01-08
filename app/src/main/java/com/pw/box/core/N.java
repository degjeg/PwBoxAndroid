package com.pw.box.core;

import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;

import com.common.bean.BaseRes;
import com.pw.box.bean.protobuf.GetItemListRes;
import com.pw.box.bean.protobuf.GetProtectRes;
import com.pw.box.bean.protobuf.ItemRes;
import com.pw.box.bean.protobuf.LoginRes;
import com.pw.box.bean.protobuf.RetrievePassRes;
import com.pw.box.cache.Constants;
import com.pw.box.core.bean.Pack;
import com.pw.box.core.cmds.CmdIds;
import com.pw.box.utils.L;
import com.squareup.wire.Message;
import com.squareup.wire.ProtoAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;

/**
 * 用于请求服务器的类
 * Created by danger on 16/8/19.
 */
public class N {

    public static final HashMap<Short, ProtoAdapter> parses = new HashMap<>();
    private static short reqId = 0;
    private static N n = new N("");

    static {
        // parses.put(CmdIds.GET_HOST, M.GetHostRes.PARSER);
        // parses.put(CmdIds.GETKEY, GetKeyRes.PARSER);

        parses.put(CmdIds.PING, BaseRes.ADAPTER);
        parses.put(CmdIds.LOGIN, LoginRes.ADAPTER);
        parses.put(CmdIds.REGISTER, BaseRes.ADAPTER);

        parses.put(CmdIds.GET_QUESTION, GetProtectRes.ADAPTER);
        parses.put(CmdIds.SET_ANSWER, BaseRes.ADAPTER);
        parses.put(CmdIds.VERIFY_ANSWER, RetrievePassRes.ADAPTER);
        parses.put(CmdIds.RETRIEVE_PASSWORD, BaseRes.ADAPTER);
        parses.put(CmdIds.MODIFY_PASSWORD, BaseRes.ADAPTER);

        parses.put(CmdIds.ADD_ITEM, ItemRes.ADAPTER);
        parses.put(CmdIds.DELETE_ITEM, ItemRes.ADAPTER);
        parses.put(CmdIds.EDIT_ITEM, ItemRes.ADAPTER);
        parses.put(CmdIds.GET_ITEM_LIST, GetItemListRes.ADAPTER);
    }

    private final HashMap<Short, Task> tasks = /*Collections.synchronizedMap(*/new LinkedHashMap<>()/*)*/;
    private final String TAG;
    Handler handler;

    public N(String tag) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("mmss", Locale.ENGLISH);
        reqId = Short.valueOf(simpleDateFormat.format(new Date()));
        handler = new Handler(Looper.getMainLooper());
        this.TAG = tag;

    }

    public static final short reqID() {
        return ++reqId;
    }

    public Pack request(short cmd, Message message, NetHandler handler, List<byte[]> signKey) throws Exception {
        return request(cmd, message == null ? null : message.encode(), handler, signKey);
    }

    public Pack request(short cmd, byte[] message, NetHandler handler, List<byte[]> signKey) throws Exception {
        Task task = new Task();

        task.cmd = cmd;

        Pack pack = PacketCreator.createPack(cmd, reqID(), message, signKey);

        // task.reqPack = message;
        task.handler = handler;

        if (handler != null) {
            synchronized (tasks) {
                tasks.put(pack.getReqcode(), task);
                if (L.E) L.get().e(TAG, "req:" + pack.getReqcode() + " is added");
            }
        }
        return pack;
    }

    public void onReceive(Pack pack) {
        short reqCode = pack.getReqcode();
        Task task = null;
        synchronized (tasks) {
            task = tasks.remove(reqCode);
        }

        if (task != null) {
            Message message = null;
            if (pack.getData() != null) {
                ProtoAdapter parser = parses.get(pack.getCommand());
                if (parser != null) {
                    try {
                        message = (Message) parser.decode(pack.getData());

                        if (L.D) L.get().d(TAG, "receive:[" + pack.getCommand() + "]" + message);
                    } catch (Exception e) {
                        if (L.E) L.get().e(TAG, "parse receive:", e);
                    }
                }
            }
            // task.handler.onSuccess(task.reqPack, message);
            handler.post(new Notifier(task, message));
            return;
        } else {
            if (L.E) L.get().e(TAG, "req:" + pack.getReqcode() + " is ignored");
        }
    }

    // public Pack request(short cmd, AbstractMessage message, NetHandler handler, List<byte[]> signKey) throws Exception {
    //     return request(cmd, message == null ? null : message.toByteArray(), handler, signKey);
    // }

    public void onConnectionClosed() {
        notifyAllFailed();
    }

    public void notifyAllFailed() {
        synchronized (tasks) {
            for (Short key : tasks.keySet()) {
                Task task = tasks.get(key);
                // task.handler.onFail(task.reqPack);
                handler.post(new Notifier(task, null));
            }
            tasks.clear();
        }
    }

    public void checkTimeout() {
        synchronized (tasks) {
            for (Short key : new ArrayList<>(tasks.keySet())) {
                Task task = tasks.get(key);
                if (SystemClock.elapsedRealtime() - task.startTime >= Constants.PACK_TIMEOUT) {
                    handler.post(new Notifier(task, ErrorCodes.TIMEOUT));
                    if (L.E) L.get().e(TAG, "req:" + key + " is timeout");
                    // task.handler.onFail(task.reqPack);
                    tasks.remove(key);
                }
            }
        }
    }

    public interface NetHandler<T> {
        void onSuccess(T retPack);

        void onFail(int localErrorCode, Exception e);
    }

    private class Task {
        long startTime;
        short cmd;
        Message reqPack;
        NetHandler handler;

        public Task() {
            startTime = SystemClock.elapsedRealtime();
        }
    }

    private class Notifier implements Runnable {
        Task task;
        Message retPack;

        int errCode = ErrorCodes.UNKNOWN_ERROR;

        public Notifier(Task task, int errCode) {
            this.task = task;
            this.errCode = errCode;
        }

        public Notifier(Task task, Message retPack) {
            this.task = task;
            this.retPack = retPack;
        }

        @Override
        public void run() {
            if (task != null && task.handler != null) {
                if (retPack != null) {
                    task.handler.onSuccess(retPack);
                } else {
                    task.handler.onFail(errCode, null);
                }
            }
        }
    }

}
