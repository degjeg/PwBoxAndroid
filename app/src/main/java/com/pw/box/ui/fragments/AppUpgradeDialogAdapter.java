package com.pw.box.ui.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.pw.box.R;
import com.pw.box.bean.protobuf.AppUpdateInfo;
import com.pw.box.ui.base.BaseFragment;
import com.pw.box.ui.base.DialogActivity;
import com.pw.box.utils.FileDownloader;
import com.pw.box.utils.L;
import com.pw.box.utils.Md5;
import com.pw.box.utils.ShellUtil;

import java.io.File;
import java.util.Locale;

/**
 * 软件更新的对话框
 * Created by danger on 16/10/31.
 */

public class AppUpgradeDialogAdapter extends DialogActivity.DialogActivityAdapter implements FileDownloader.FileDownloadListener {

    public static final String TAG = "AppUpgradeDialogAdapter";
    AppUpdateInfo info;
    DialogActivity context;
    long downloadTaskId = -1;

    @Override
    public void initWithDialogActivity(DialogActivity dialog, Object r) {
        super.initWithDialogActivity(dialog, r);
        info = (AppUpdateInfo) r; // ((Common.CheckAppVerResponse) r).getInfo();
        context = dialog;

        dialog.setTitle(dialog.getString(R.string.new_version_found, info.version_name));
        dialog.setMessage(info.desc);
        dialog.setNegativeButton(R.string.upgrade_later, null);

        String mBytes = String.format(dialog.getString(R.string.upgrade_now_with_size), info.size / 1024f / 1024f);
        dialog.setPositiveButton(mBytes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // dialog.dismiss();
                context.btn1.setText(R.string.download_in_background);
                startDownload();
            }
        });
    }


    void startDownload() {
        if (!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            context.toast(context.getString(R.string.sdcard_not_mounted));
            // context.finish();
            return;
        }

        File f = new File(Environment.getExternalStorageDirectory(), "Android/data/" + context.getPackageName() + "/app_" + info.version_code + ".apk");
        FileDownloader.getInstance().downloadFile(info.url, f.getAbsolutePath(), this);
        // DownloadManager downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        //
        // Uri uri = Uri.parse(info.getUrl());
        //
        // DownloadManager.Request request = new DownloadManager.Request(uri);
        // //设置通知栏标题
        // request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE);
        // // 下载过程和下载完成后通知栏有通知消息。
        // request.setNotificationVisibility(Request.VISIBILITY_VISIBLE | Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        // // request.setTitle("下载");
        // // request.setDescription("今日头条正在下载");
        // request.setAllowedOverRoaming(false);
        // //设置文件存放目录
        // // request.setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "mydown");
        //
        // downloadTaskId = downloadManager.enqueue(request);
    }

    @Override
    public void onProgress(String url, float percent, long nowSize, long totalSize) {
        if (L.D) L.get().d(TAG, "onSuccess:" + percent + "," + nowSize + "/" + totalSize);
        context.btn2.setText(String.format(Locale.getDefault(), "%.2f%%", 100.0f * nowSize / totalSize));
    }

    @Override
    public void onSuccess(String url, File file) {
        if (L.D) L.get().d(TAG, "onSuccess:" + file);

        String md5Real = Md5.md5(file);
        String md5Required = info.md5;

        if (!TextUtils.isEmpty(md5Required) && !md5Required.equalsIgnoreCase(md5Real)) {
            if (L.E)
                L.get().e(TAG, "md5check fail md5 is \n" + md5Real + " but must be:\n" + md5Required);
            file.delete();
            context.btn1.setText(R.string.latter);
            context.btn2.setText(R.string.Retry);
            BaseFragment.showTipDialog(context, context.getString(R.string.error_net_not_safe));
            return;
        }

        installApk(file);
    }

    @Override
    public void onFail(String url, int code, String message, Throwable errInfo) {
        if (L.E) L.get().d(TAG, "fail:" + url + "," + code);
        context.toast(context.getString(R.string.download_fail));
        context.btn1.setText(R.string.upgrade_later);
        context.btn2.setText(R.string.retry);
    }

    /**
     * 安装下载完成的APK
     */
    public void installApk(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                /*安装文件*/

                ShellUtil.grantFilePerm(file);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(file),
                        "application/vnd.android.package-archive");
                intent.setFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                context.btn1.setText(R.string.upgrade_later);
                context.btn2.setText(R.string.install);
            } else {
                file.delete();
                context.toast(context.getString(R.string.file_error));
                context.btn1.setText(R.string.upgrade_later);
                context.btn2.setText(R.string.re_download);
            }
        } else {
            file.delete();
            context.toast(context.getString(R.string.file_not_exist));
            context.btn1.setText(R.string.upgrade_later);
            context.btn2.setText(R.string.re_download);
        }
    }
}
