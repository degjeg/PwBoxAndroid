package com.pw.box.ui.base;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.pw.box.R;
import com.pw.box.tool.UnLock;
import com.pw.box.utils.L;


/**


 */
public class BaseFragment extends Fragment implements View.OnClickListener {

    protected Toast mToast;

    protected Dialog dialog;
    protected ContainerActivity activity;
    Handler handler;

    public BaseFragment() {
    }

    public static Dialog showTipDialog(Context context, int message) {
        return showTipDialog(context, context.getString(message));
    }

    public static Dialog showTipDialog(Context context, CharSequence message) {
        if (context == null) {
            return null;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(R.string.tip);
        builder.setMessage(message);
        builder.setPositiveButton(R.string.i_see, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        Dialog dialog = builder.create();

        dialog.show();
        return dialog;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        hideInput();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    protected Context getMyContext() {
        if (activity != null) {
            return activity;
        }
        return getContext();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onPause() {
        super.onPause();
        UnLock.feedDog();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        activity = (ContainerActivity) getActivity();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (handler != null) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    public void showProgressDialog(boolean c) {
        showProgressDialog(-1, R.string.please_wait_moment, c);
    }

    public void showProgressDialog(int text) {
        showProgressDialog(R.string.please_wait_moment, text, false);
    }

    public void showProgressDialog(int title, int text, boolean cancelble) {
        try {
            Context context = getMyContext();
            if (context == null) {
                return;
            }
            if (title > 0 && text > 0) {
                dialog = ProgressDialog.show(context, getString(title), getString(text));
            } else if (text > 0) {
                dialog = ProgressDialog.show(context, "", getString(text));
            } else {
                return;
            }

            // TODO
            dialog.setCancelable(cancelble);
        } catch (Exception ignore) {

        }
    }

    public void showTipDialog(int message) {
        dialog = showTipDialog(getMyContext(), message);
    }

    public void showTipDialog(CharSequence message) {
        dialog = showTipDialog(getMyContext(), message);
    }

    public void dismissDialog() {
        if (dialog != null) {
            try {
                dialog.dismiss();
            } catch (Exception e) {
                // e.printStackTrace();
            }
            dialog = null;
        }
    }

    public boolean onBackPressed() {
        return false;
    }

    public void toast(int string) {
        if (isAdded()) {
            Context context = getMyContext();
            if (context != null) {
                toast(context.getString(string));
            }
        }
    }

    public void toast(String message) {
        Context context = getMyContext();
        if (context == null) {
            return;
        }

        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
            mToast.setGravity(Gravity.CENTER, 0, 0);
        } else {
            mToast.setText(message);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public void finishDelayed(int time) {
        if (handler == null) {
            handler = new Handler();
        }
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, time);
    }

    public void finish() {
        if (getActivity() == null) {
            return;
        }

        if (L.E) L.get().e("basefragment", "finish");

        activity.finish();
        // removeFragment(this);
    }


    //隐藏软键盘
    public void hideInput() {
        try {
            /* if (isDetached() || !isVisible()) {
                return;
            } */

            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {

    }

    public void setResult(int result) {
        setResult(result, null);
    }

    public void setResult(int result, Intent data) {
        ContainerActivity containerActivity = (ContainerActivity) getActivity();
        if (containerActivity == null) {
            return;
        }

        containerActivity.setResult(result, data);
    }

}
