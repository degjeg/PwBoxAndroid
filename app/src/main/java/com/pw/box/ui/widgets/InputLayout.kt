package com.pw.box.ui.widgets

import android.content.Context
import android.util.AttributeSet
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import com.pw.box.R

/**
 * 用于输入的layout，左右排列，
 * Created by danger on 2017/12/31.
 */
@Deprecated("")
class InputLayout(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : LinearLayout(context, attrs, defStyleAttr) {

    /*var binding: ViewInputLayoutBinding = DataBindingUtil.inflate(LayoutInflater.from(context),
            R.layout.view_input_layout, this, true)*/

    constructor(context: Context) : this(context, null, 0)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)

    init {
        orientation = HORIZONTAL

        var tagThemeResId = 0
        var contentThemeResId = 0

        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.InputLayout)

            val N = a.indexCount
            for (i in 0 until N) {
                val index = a.getIndex(i)
                when (index) {
                    R.styleable.InputLayout_tagStyle -> {
                        tagThemeResId = a.getResourceId(index, 0)
                        // val context = android.view.ContextThemeWrapper(context, themeResId)
                        // addView(TextView(context, attrs, themeResId))
                    }
                    R.styleable.InputLayout_contentStyle -> {
                        contentThemeResId = a.getResourceId(index, 0)
                        // val context = android.view.ContextThemeWrapper(context, themeResId)
                        // addView(EditText(context, attrs, themeResId))
                    }
                }

                /*when (index) {
                    R.styleable.InputLayout_tagText ->
                        binding.tvTag.text = a.getText(index)
                    R.styleable.InputLayout_tagTextSize ->
                        binding.tvTag.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(index, 10).toFloat())
                    R.styleable.InputLayout_tagTextColor ->
                        binding.tvTag.setTextColor(a.getColor(index, 10))
                    R.styleable.InputLayout_tagWidth -> binding.tvTag.layoutParams.width = a.getDimensionPixelSize(index, -2)
                    R.styleable.InputLayout_tagHeight -> binding.tvTag.layoutParams.height = a.getDimensionPixelSize(index, -2)

                    R.styleable.InputLayout_contentText ->
                        binding.etContent.text = a.getText(index)
                    R.styleable.InputLayout_contentTextSize ->
                        binding.etContent.setTextSize(TypedValue.COMPLEX_UNIT_PX, a.getDimensionPixelSize(index, 10).toFloat())
                    R.styleable.InputLayout_contentTextColor ->
                        binding.etContent.setTextColor(a.getColor(index, 10))
                    R.styleable.InputLayout_dividerWidth ->
                        binding.divider.layoutParams.width = a.getDimensionPixelSize(index, 0)
                    R.styleable.InputLayout_dividerColor ->
                        binding.divider.setBackgroundColor(a.getColor(index, 0))
                }*/
            }

            a.recycle()
        }

        var context = android.view.ContextThemeWrapper(context, tagThemeResId)
        addView(TextView(context, attrs, tagThemeResId))

        context = android.view.ContextThemeWrapper(context, contentThemeResId)
        addView(EditText(context, attrs, contentThemeResId))

    }
}