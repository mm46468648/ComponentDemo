package com.mooc.my.widget

import android.content.Context
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.res.ResourcesCompat
import com.mooc.my.R

/**
 * @Description edittext with a button to clear
 * @Author adam289
 * @Date 2020/4/2
 */
class MyEditTextClear : AppCompatEditText {
    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int) : super(
        context,
        attributeSet,
        defStyleAttr
    )

    private val blackClearDrawable =
        ResourcesCompat.getDrawable(resources, R.mipmap.ic_mini_deletegray, null) as Drawable
    private val opaqueClearDrawable =
        ResourcesCompat.getDrawable(resources, R.mipmap.ic_mini_deletegray, null) as Drawable

    private var clearButtonImage: Drawable = opaqueClearDrawable

    private fun showClearButton() {
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, clearButtonImage, null)
    }

    private fun hideClearButton() {
        setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, null, null)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (isClearButtonVisible() && wasClearButtonTouched(event)) {
            onClearButtonTouched(event)
            return true
        }

        return super.onTouchEvent(event)
    }

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        if (text?.isNotEmpty() == true) {
            showClearButton()
        } else {
            hideClearButton()
        }
    }

    private fun isClearButtonVisible(): Boolean {
        return compoundDrawablesRelative[2] != null
    }

    private fun wasClearButtonTouched(event: MotionEvent): Boolean {
        val isClearButtonAtTheStart = layoutDirection == View.LAYOUT_DIRECTION_RTL

        return if (isClearButtonAtTheStart) {

            val clearButtonEnd = paddingStart + clearButtonImage.intrinsicWidth
            event.x < clearButtonEnd

        } else {

            val clearButtonStart = width - clearButtonImage.intrinsicWidth - paddingEnd
            event.x > clearButtonStart

        }
    }

    private fun onClearButtonTouched(event: MotionEvent) {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                clearButtonImage = blackClearDrawable
                showClearButton()
            }
            MotionEvent.ACTION_UP -> {
                clearButtonImage = opaqueClearDrawable
                text?.clear()
                hideClearButton()
            }
        }
    }
}