//package com.pw.box.ui.base;
//
//import android.app.Dialog;
//import android.app.ProgressDialog;
//import android.content.Context;
//import android.content.DialogInterface;
//import android.support.v7.app.AlertDialog;
//import android.text.TextUtils;
//
//import com.pw.box.App;
//import com.pw.box.R;
//import com.pw.box.cache.Cache;
//import com.pw.box.cache.User;
//import com.pw.box.cache.UserStatusChangeListener;
//import com.pw.box.core.Cm;
//import com.pw.box.core.ConnectionListener;
//import com.pw.box.core.cmds.LoginTask;
//import com.pw.box.utils.NetWorkUtils;
//
///**
// * Created by danger on 16/9/25.
// */
//
//public class DefaultConnectionStatusListener implements UserStatusChangeListener,
//        Dialog.OnDismissListener {
//
//    BaseFragment fragment;
//    ProgressDialog dialog;
//
//    public int status;
//
//    private boolean reLogin() {
//        User user = Cache.get().getUser();
//
//        // 不满足自动登录的条件
//        if (TextUtils.isEmpty(user.getAccount()) || null == user.getPwFilledLogin()) {
//            if (Cache.get().getPatternUtil().havePattern()) {
//                int unlockResult = Cache.get().getPatternUtil().unLock(Cache.get().getPatternUtil().getPatternString());
//                if (unlockResult == 0) return true;
//            }
//            return false;
//        }
//
//        Cm.get().login(user.getAccount(),
//                user.getPwFilledLogin(),
//                user.getPwFilledRawKey(),
//                new LoginTask.LoginHandler() {
//
//                    @Override
//                    public void onLoginSuccess(boolean isIgnored) {
//                        if(!isIgnored)Cm.get().notifyStatusChange(ConnectionListener.STATUS_LOGGEDIN);
//                    }
//
//                    @Override
//                    public void onLoginFail(int code) {
//                        Cm.get().notifyStatusChange(ConnectionListener.STATUS_LOGIN_FAIL);
//                    }
//                });
//
//        return true;
//    }
//
//    public DefaultConnectionStatusListener(BaseFragment fragment) {
//        this.fragment = fragment;
//        int status = Cm.get().getDataStatus();
//
//        if (status == STATUS_LOGIN_FAIL
//                ) { // 登录失败
//
//            AlertDialog.Builder dlgBuidler = new AlertDialog.Builder(fragment.getContext());
//            dlgBuidler.setMessage(R.string.error_dlg_login_fail);
//            dlgBuidler.setPositiveButton(R.string.re_login, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    // dialog.dismiss();
//                    reLogin();
//                }
//            });
//            dlgBuidler.setCancelable(false);
//            dlgBuidler.create().show();
//
//        } else if (status == STATUS_SYNCDATA_FAIL) {
//            AlertDialog.Builder dlgBuidler = new AlertDialog.Builder(fragment.getContext());
//            dlgBuidler.setMessage(R.string.error_dlg_connect_error);
//            dlgBuidler.setPositiveButton(R.string.retry, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    Cm.get().syncData();
//                }
//            });
//            dlgBuidler.setCancelable(false);
//            dlgBuidler.create().show();
//        } else if (status == STATUS_CLOSED) {
//            String message;
//            if (Cm.get().syncDataSuccess) {
//                message = App.getContext().getString(R.string.error_dlg_disconnected_by_reduce_data);
//            } else if (!NetWorkUtils.isConnected(App.getContext())) {
//                message = App.getContext().getString(R.string.error_dlg_network_error);
//            } else {
//                message = App.getContext().getString(R.string.error_dlg_disconnected);
//            }
//            AlertDialog.Builder dlgBuidler = new AlertDialog.Builder(fragment.getContext());
//            dlgBuidler.setMessage(message);
//            dlgBuidler.setPositiveButton(R.string.reconnect, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    reLogin();
//
//                }
//            });
//            dlgBuidler.setCancelable(false);
//            dlgBuidler.create().show();
//        } else {
//            showProgressDialog();
//        }
//    }
//
//    @Override
//    public void onConnectionStatusChange(int status) {
//        this.status = status;
//
//        if (dialog == null) {
//            return;
//        }
//        Context ctx = App.getContext();
//        switch (status) {
//            case STATUS_CLOSED:
//                fragment.toast(R.string.error_lost_connection_);
//                dialog.dismiss();
//                dialog = null;
//                break;
//            case STATUS_CONNECTED:
//                dialog.setMessage(ctx.getString(R.string.message_connected_to_server));
//                break;
//            case STATUS_CONNECTING:
//                dialog.setMessage(ctx.getString(R.string.message_connection_server));
//                break;
//            case STATUS_LOGGING:
//                dialog.setMessage(ctx.getString(R.string.message_connection_to_server));
//                break;
//
//            case STATUS_LOGGEDIN:
//                dialog.setMessage(ctx.getString(R.string.message_login_success));
//                break;
//
//            case STATUS_SYNCDATA_START:
//                dialog.setMessage(ctx.getString(R.string.message_syncing_data));
//                break;
//            case STATUS_SYNCDATA_SUCCESS:
//                fragment.toast(R.string.message_sync_data_success);
//                dialog.dismiss();
//                dialog = null;
//                break;
//
//            case STATUS_LOGIN_FAIL:
//                fragment.toast(R.string.message_login_fail);
//                dialog.dismiss();
//                dialog = null;
//                break;
//            case STATUS_SYNCDATA_FAIL:
//                fragment.toast(R.string.message_sync_data_fail);
//                dialog.dismiss();
//                dialog = null;
//                break;
//        }
//    }
//
//    public void showProgressDialog() {
//        if (dialog == null && fragment.getContext() != null) {
//            dialog = new ProgressDialog(fragment.getContext());
//        }
//        dialog.setOnDismissListener(this);
//        onConnectionStatusChange(Cm.get().getDataStatus());
//        dialog.show();
//    }
//
//    @Override
//    public void onDismiss(DialogInterface dialog) {
//        dialog = null;
//        Cm.get().removeConnectionListener(this);
//    }
//
//    @Override
//    public void onUserLoginStart() {
//
//    }
//
//    @Override
//    public void onUserLoginSuccess() {
//
//    }
//
//    @Override
//    public void onUserLoginFail() {
//
//    }
//
//    @Override
//    public void onUserSyncDataStart() {
//
//    }
//
//    @Override
//    public void onUserSyncDataSuccess() {
//
//    }
//
//    @Override
//    public void onUserSyncDataFail() {
//
//    }
//
//    @Override
//    public void onConnectionClosed() {
//
//    }
//}
