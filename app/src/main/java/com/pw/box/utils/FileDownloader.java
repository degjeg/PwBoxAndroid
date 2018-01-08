package com.pw.box.utils;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 文件下载器
 * Created by danger
 * on 16/7/13.
 */

public class FileDownloader implements Handler.Callback {

    private static final int MSG_FAIL = 1;
    private static final int MSG_SUCCESS = 2;
    private static final int MSG_PROGRESS = 3;
    private static final String TAG = "FileDownloader";
    private static FileDownloader instance = new FileDownloader();
    private final HashMap<String, FileDownloadTaskInfo> tasks;
    Handler handler;

    public FileDownloader() {
        tasks = new HashMap<>();
        handler = new Handler(Looper.getMainLooper(), this);
    }

    public static FileDownloader getInstance() {
        return instance;
    }


    @Override
    public boolean handleMessage(Message msg) {
        FileDownloadTaskInfo info = (FileDownloadTaskInfo) msg.obj;
        if (info == null || info.fileDownloadListener == null || info.isCanceled.get()) {
            return true;
        }

        switch (msg.what) {
            case MSG_SUCCESS:
                info.fileDownloadListener.onSuccess(info.url, info.file);
                break;
            case MSG_FAIL:
                info.fileDownloadListener.onFail(info.url, msg.arg1, "", null);
                break;
            case MSG_PROGRESS:
                info.fileDownloadListener.onProgress(info.url, 0f, (long) msg.arg1, (long) msg.arg2);
                break;
        }
        return true;
    }


/*    public void downloadVoice(String url, FileDownloadListener callBack) {
        File voiceFileName = getVoiceCacheFile(url);
        if (voiceFileName == null) {
            return;
        }
        downloadFile(url, voiceFileName.getAbsolutePath(), callBack);
    }*/

    public void downloadFile(String url, String saveFileName, FileDownloadListener callBack) {

        downloadByOkHttp(url, saveFileName, callBack);
        //        SuperParameter parameter = new SuperParameter();
        //        HttpTaskManger manger = new HttpTaskManger(VApplication.getInstance(), url, parameter);
        //        File file = new File(saveFileName);
        //
        //        if (file.exists()) {
        //            // 如果文件存在,直接通知成功
        //            if (callBack != null) callBack.onSuccess(url, file);
        //            return;
        //        }
        //
        //        if (!FileUtils.createDirs(file)) {
        //            if (callBack != null) callBack.onFail(url, 1, "创建文件夹失败", null);
        //            return;
        //        }
        //
        //        FileDownloadTaskInfo info = tasks.get(url);
        //        if (info != null) { // 任务已存在,避免重复下载,直接更新listener
        //            synchronized (info) {
        //                info.fileDownloadListener = callBack;
        //            }
        //            return;
        //        }
        //        info = new FileDownloadTaskInfo();
        //        info.url = url;
        //        info.file = file;
        //        info.fileDownloadListener = callBack;
        //        tasks.put(url, info);
        //
        //        manger.setResponseFile(file)
        //                .setPost(false)
        //                .setProcess(true)
        //                .run(info);
    }

    public void downloadByOkHttp(String url, String saveFileName, FileDownloadListener callBack) {
        File file = new File(saveFileName);

        if (file.exists()) {
            // 如果文件存在,直接通知成功
            if (callBack != null) callBack.onSuccess(url, file);
            return;
        }

        FileDownloadTaskInfo info = tasks.get(url);
        if (info != null) { // 任务已存在,避免重复下载,直接更新listener
            synchronized (info) {
                info.fileDownloadListener = callBack;
            }
            return;
        }


        info = new FileDownloadTaskInfo();
        info.url = url;
        info.file = file;
        info.fileDownloadListener = callBack;

        // step1,创建文件夹,临时文件
        if (prepareFile(info) != 0) {
            if (callBack != null) callBack.onFail(url, 1, "无法下载文件", null);
            return;
        }

        tasks.put(url, info);

        OkHttpClient mOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        Request.Builder reqBuilder = new Request.Builder()
                .url(url);
        if (info.startPosition > 0) {
            reqBuilder.header("RANGE", String.format(Locale.ENGLISH, "bytes=%d-", info.startPosition));
        }
        if (L.E) L.get().e("start from:" + info.startPosition);
        Request request = reqBuilder.build();
        info.call = mOkHttpClient.newCall(request);
        info.call.enqueue(info);
    }

    public void stop(String url) {
        FileDownloadTaskInfo info = tasks.get(url);
        if (info == null) { // 任务已存在,避免重复下载,直接更新listener
            return;
        }

        synchronized (tasks) {
            info.isCanceled.set(true);
            info.call.cancel();
            tasks.remove(url);
        }
    }

    /**
     * @param info
     * @return 0成功
     * -1:目标文件不合法,是一个文件夹
     * -2:创建父文件夹失败
     * -3:临时文件失败
     */
    private int prepareFile(FileDownloadTaskInfo info) /*throws FileNotFoundException*/ {
        if (info.file.exists()) { // 目标是一个文件夹
            if (!info.file.isFile()) return -1;
            else return 0;
        }
        File parentDir = info.file.getParentFile();
        if (!parentDir.exists()) { // 创建父文件夹
            if (!parentDir.mkdirs()) {
                return -2;
            }
        }

        if (info.tmpFile == null) {
            try {
                File tmpFile1 = new File(info.file.getAbsolutePath() + ".tmp");

                if (tmpFile1.length() > 0) {
                    String jsonString = null;
                    JSONObject jsonObject = null;
                    try {
                        jsonString = FileUtils.readFile(info.file.getAbsolutePath() + ".info", "utf-8").toString();
                        jsonObject = new JSONObject(jsonString);
                    } catch (Exception e) {
                        // e.printStackTrace();
                    }

                    if (jsonObject == null
                            || !jsonObject.has("url")
                            || !TextUtils.equals(jsonObject.getString("url"), info.url)
                            ) {
                        if (!tmpFile1.delete()) {
                            return -3;
                        }
                    } else {
                        info.totalBytes = jsonObject.getInt("size");
                    }
                }
                info.tmpFile = new RandomAccessFile(tmpFile1, "rws");
                info.startPosition = (int) info.tmpFile.length();
            } catch (FileNotFoundException e) {
                //  e.printStackTrace();
                return -4;
            } catch (IOException e) {
                // e.printStackTrace();
                return -5;
            } catch (Exception e) {
                // e.printStackTrace();
                return -6;
            }
        }


        return 0;
    }


    public interface FileDownloadListener {
        void onProgress(String url, float percent, long nowSize, long totalSize);

        void onSuccess(String url, File file);

        void onFail(String url, int code, String message, Throwable errInfo);
    }

    private class FileDownloadTaskInfo implements Callback {
        String url;
        File file;

        int startPosition = 0; // 断点继传的位置,从tmpFile文件的大小而来
        int totalBytes = 0; //  file size
        /**
         * 文件内容先下载至.tmp
         */
        RandomAccessFile tmpFile;

        FileDownloadListener fileDownloadListener;
        Call call;
        AtomicBoolean isCanceled = new AtomicBoolean(false);


        @Override
        public void onFailure(Call call, IOException e) {
            if (L.E) L.get().d("h_bl", "onFailure");

            // synchronized (this) {
            //     fileDownloadListener.onFail(url, -1, "", e);
            //     tasks.remove(url);
            // }
            handler.obtainMessage(MSG_FAIL, -1, 0, this).sendToTarget();
            tasks.remove(url);
        }

        int parseTotalBytes(Response response) {
            if (startPosition == 0) {
                return (int) response.body().contentLength();
            }


            String rangeBytes = response.header("Content-Range");

            int last = rangeBytes.lastIndexOf('/');
            return Integer.valueOf(rangeBytes.substring(last + 1));
        }

        @Override
        public void onResponse(Call call, Response response) throws IOException {
            InputStream is = null;
            byte[] buf = new byte[2048];
            int len = 0;
            try {
                if (!response.isSuccessful()) {
                    throw new IOException("http:" + response.code());
                }
                is = response.body().byteStream();
                int totalBytes = parseTotalBytes(response); // (int) response.body().contentLength();
                if (this.totalBytes > 0 && this.totalBytes != totalBytes) {
                    if (!new File(file.getAbsolutePath() + ".tmp").delete()) {
                        throw new FileNotFoundException(); // 无法删除旧文件
                    }
                    tmpFile.close();
                    tmpFile = new RandomAccessFile(file.getAbsolutePath() + ".tmp", "rws");
                    startPosition = 0;
                }
                this.totalBytes = totalBytes;
                saveInfoToFile();

                long sum = startPosition;
                long notifyTime = 0;
                tmpFile.seek(startPosition);
                while ((len = is.read(buf)) != -1) {
                    if (isCanceled.get()) return;

                    // fos.write(buf, 0, len);
                    tmpFile.write(buf, 0, len);

                    if (isCanceled.get()) return;
                    sum += len;
                    // int progress = (int) (sum * 1.0f / total * 100);
                    // if(L.D) L.get().d(TAG, "progress=" + progress);
                    if (SystemClock.elapsedRealtime() - notifyTime > 300) {
                        // 下载时避免过于频繁刷新ui
                        notifyTime = SystemClock.elapsedRealtime();
                        handler.obtainMessage(MSG_PROGRESS, (int) sum, totalBytes, this).sendToTarget();
                    }
                }
                tmpFile.getFD().sync();
                if (L.D) L.get().d(TAG, "文件下载成功");

                if (isCanceled.get()) return;
                new File(file.getAbsolutePath() + ".tmp").renameTo(file);
                handler.obtainMessage(MSG_SUCCESS, 0, 0, this).sendToTarget();
            } catch (Exception e) {
                if (L.E) L.get().e(TAG, "文件下载失败", e);
                handler.obtainMessage(MSG_FAIL, -2, 0, this).sendToTarget();
            } finally {
                tasks.remove(url);
                try {
                    if (is != null)
                        is.close();
                } catch (IOException ignore) {
                }
                try {
                    if (tmpFile != null)
                        tmpFile.close();
                } catch (IOException ignore) {
                }
            }
        }

        public String toJsonString() {
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("url", url);
                jsonObject.put("size", totalBytes);
            } catch (JSONException e) {
                // should not happen
                // e.printStackTrace();
            }
            return jsonObject.toString();
        }

        public boolean saveInfoToFile() {
            return FileUtils.writeFile(file.getAbsolutePath() + ".info", toJsonString());
        }
    }
}
