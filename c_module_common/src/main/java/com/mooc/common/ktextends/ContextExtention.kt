package com.mooc.common.ktextends

import android.content.Context
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat

fun Context.getColorRes(colorRes:Int) : Int{
    return ContextCompat.getColor(this,colorRes)
}

fun Context.getDrawableRes(drawableRes:Int): Drawable? {
    return ContextCompat.getDrawable(this,drawableRes)
}