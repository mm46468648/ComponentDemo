package com.mooc.common.ktextends

import android.graphics.drawable.Drawable
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat

fun TextView.setDrawable(str: @Orientation String,res:Int,padding: Int = 0){
    val drawable = ContextCompat.getDrawable(this.context, res)
    setDrawable(str,drawable,padding)
}

fun TextView.setDrawable(str: @Orientation String,drawable: Drawable? = null,padding: Int = 0){
    when(str){
        Orientation.LEFT->  this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        Orientation.TOP->  this.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        Orientation.RIGHT->  this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        Orientation.BOTTOM->  this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
    }
    if(padding!=0){
        this.compoundDrawablePadding = padding
    }

}
fun TextView.setDrawLeft(res: Int,padding:Int = 0) {
    val drawable = ContextCompat.getDrawable(this.context, res)
    setDrawLeft(drawable,padding)

}

fun TextView.setDrawLeft(drawable: Drawable? = null,padding:Int = 0) {
    this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
    if(padding!=0){
        this.compoundDrawablePadding = padding
    }
}


fun TextView.setDrawRight(res: Int,padding:Int = 0) {
    val drawable = ContextCompat.getDrawable(this.context, res)
    setDrawRight(drawable,padding)
}

fun TextView.setDrawRight(drawable: Drawable? = null,padding:Int = 0) {
    this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
    if(padding!=0){
        this.compoundDrawablePadding = padding
    }
}

fun View.gone(shouldBeGone: Boolean) {
    this.visibility = if (shouldBeGone) {
        View.GONE
    } else {
        View.VISIBLE
    }
}
fun View.visiable(shouldBeVisiable: Boolean) {
    this.visibility = if (shouldBeVisiable) {
        View.VISIBLE
    } else {
        View.GONE
    }
}
