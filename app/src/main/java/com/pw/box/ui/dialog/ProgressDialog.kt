package com.pw.box.ui.dialog

import android.content.Context

import com.pw.box.R
import com.pw.box.databinding.DialogProgressBinding

/**
 * 提供一个输入框的dialog
 * Created by danger on 2016/11/12.
 */
class ProgressDialog(context: Context) : DialogWrapper<DialogProgressBinding>(context, R.layout.dialog_progress) {

    private var message: CharSequence? = null

    fun setMessage(message: CharSequence) {
        this.message = message
        if (binding != null) {
            binding!!.tvTitle.text = message
        }
    }

    fun setMessage(message: Int) = setMessage(context.getString(message))

    override fun initView(binding: DialogProgressBinding) {
        if (message != null) setMessage(message!!)
    }


}
