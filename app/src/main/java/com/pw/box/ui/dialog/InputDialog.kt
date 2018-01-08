package com.pw.box.ui.dialog

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.pw.box.R
import com.pw.box.databinding.DialogInputBinding
import com.pw.box.tool.DoubleTabHelper

/**
 * 提供一个输入框的dialog
 * Created by danger on 2016/11/12.
 */
class InputDialog(context: Context) : DialogWrapper<DialogInputBinding>(context, R.layout.dialog_input), View.OnClickListener {

    private var value: CharSequence? = null
    private var hint: CharSequence? = null
    private var title: CharSequence? = null
    private var listener: OnTextInputedListener? = null

    private var cancelTips: CharSequence? = null
    var checker: ((pw: String) -> Boolean)? = null

    fun setValue(value: CharSequence) {
        this.value = value

        binding?.et?.setText(value)
    }

    fun setHint(hint: CharSequence) {
        this.hint = hint

        binding?.et?.hint = hint
    }

    fun setcancelTips(cancelTips: Int) {
        setcancelTips(if (cancelTips > 0) context.getString(cancelTips) else null)
    }

    fun setcancelTips(cancelTips: CharSequence?) {
        this.cancelTips = cancelTips
    }

    fun setTitle(title: CharSequence?) {
        this.title = title

        binding?.let {
            if (title != null) {
                it.tvTitle.text = title
            } else {
                it.tvTitle.visibility = View.GONE
            }
        }
    }


    override fun initView(binding: DialogInputBinding) {
        binding.btnCancel.setOnClickListener(this)
        binding.btnSure.setOnClickListener(this)
        binding.btnClear.setOnClickListener(this)
        if (value != null) setValue(value!!)
        if (hint != null) setHint(hint!!)
        setTitle(title)
        binding.et.post {
            binding.et.requestFocus()
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(binding.et, InputMethodManager.SHOW_IMPLICIT)
        }

        dialog?.setOnDismissListener {
            try {
                binding?.let {
                    val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.et.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                }
            } catch (e: Exception) {
            }
        }
    }

    fun setListener(listener: OnTextInputedListener) {
        this.listener = listener
    }

    override fun onClick(v: View) {
        val binding = binding ?: return
        if (v.id == R.id.btn_cancel) {
            if (cancelTips != null) {
                DoubleTabHelper.pressAgainToDoSth(context, cancelTips!!) {
                    cancel()
                }
            } else {
                cancel()
            }
        } else if (v.id == R.id.btn_sure) {
            val str = binding.et.text.toString().trim()
            if (checker?.invoke(str) == false) return

            dismiss()
            if (listener != null) {
                listener?.onTextInputed(str)
            }
        } else if (v.id == R.id.btn_clear) {
            binding.et.setText("")
        }
    }


    interface OnTextInputedListener {
        fun onTextInputed(s: String)
    }

}
