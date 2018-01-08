package com.pw.box.ui.dialog

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.support.v7.app.AlertDialog
import android.view.LayoutInflater

/**
 * 提供一个输入框的dialog
 * Created by danger on 2016/11/12.
 */
abstract class DialogWrapper<Binding : ViewDataBinding>(protected var context: Context, private val layoutId: Int) : DialogInterface {
    protected var binding: Binding? = null
    protected var dialog: Dialog? = null

    private var cancelable = true
    private fun createDialog() {
        binding = DataBindingUtil.inflate(LayoutInflater.from(context), layoutId, null, false)

        // View v = LayoutInflater.from(context).inflate(R.layout.dialog_input, null);

        val builder = AlertDialog.Builder(context)

        builder.setView(binding!!.root)


        dialog = builder.create()

        dialog?.setCancelable(cancelable)
        initView(binding!!)
        // dialog.show();
    }

    abstract protected fun initView(binding: Binding)

    fun show() {
        if (dialog == null) {
            createDialog()
            dialog?.show()
        }
    }


    override fun cancel() {
        dialog?.cancel()
        dialog = null
    }

    override fun dismiss() {
        dialog?.dismiss()
        dialog = null
    }

    fun setCancelable(flag: Boolean) {
        cancelable = flag
        dialog?.setCancelable(false)
    }

    fun setOnCancelListener(listener: DialogInterface.OnCancelListener?) {
        dialog?.setOnCancelListener(listener)
    }

    fun setOnDismissListener(listener: DialogInterface.OnDismissListener?) {
        dialog?.setOnDismissListener(listener)
    }
}
