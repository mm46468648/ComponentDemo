package com.mooc.resource.ktextention

import android.graphics.drawable.Drawable
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mooc.common.utils.net.NetType
import com.mooc.resource.Orientation


fun TextView.setDrawable(str: @com.mooc.resource.Orientation String, res:Int, padding: Int = 0){
    val drawable = ContextCompat.getDrawable(this.context, res)
    setDrawable(str,drawable,padding)
}

fun TextView.setDrawable(str: @com.mooc.resource.Orientation String, drawable: Drawable? = null, padding: Int = 0){
    when(str){
        com.mooc.resource.Orientation.LEFT->  this.setCompoundDrawablesWithIntrinsicBounds(drawable, null, null, null)
        com.mooc.resource.Orientation.TOP->  this.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null)
        com.mooc.resource.Orientation.RIGHT->  this.setCompoundDrawablesWithIntrinsicBounds(null, null, drawable, null)
        com.mooc.resource.Orientation.BOTTOM->  this.setCompoundDrawablesWithIntrinsicBounds(null, null, null, drawable)
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

