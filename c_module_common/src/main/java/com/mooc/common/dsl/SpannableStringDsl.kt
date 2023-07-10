package com.mooc.common.dsl

import android.graphics.Color
import android.graphics.Typeface
import android.os.Parcel
import android.text.ParcelableSpan
import android.text.SpannableString
import android.text.style.*
import android.view.View


/**
 * 安卓富文本配置DSL
 * 示例代码：
 *  val trip = spannableString {
str = currentScore
scaleSpan {
scale = 1.2f
start = 0
end = 2
}
colorSpan {
color = Color.BLUE
start = 0
end = 2
}
colorSpan {
color = Color.RED
start = 2
end = 4
}

clickSpan {
clickCallBack = {
toast("haha")
}
start = 4
end = 7
}
}
tvTest.text = trip
 */
class SpannableStringBuild() {
    var str: CharSequence = ""
    var spanWrappers = mutableListOf<BaseSpanWrapper>()

    fun colorSpan(block: ColorSpanWrapper.() -> Unit) {
        val colorSpanBuild = ColorSpanWrapper()
        block.invoke(colorSpanBuild)
        spanWrappers.add(colorSpanBuild.build())

    }

    fun scaleSpan(block: ScaleSpanWrapper.() -> Unit) {
        val spanWrapperBuilder = ScaleSpanWrapper()
        block.invoke(spanWrapperBuilder)
        spanWrappers.add(spanWrapperBuilder.build())
    }

    fun absoluteSpan(block: AbsoluteSizeSpanWrapper.() -> Unit) {
        val clickSpanWrapper = AbsoluteSizeSpanWrapper()
        block.invoke(clickSpanWrapper)
        spanWrappers.add(clickSpanWrapper.build())
    }

    fun clickSpan(block: ClickSpanWrapper.() -> Unit) {
        val clickSpanWrapper = ClickSpanWrapper()
        block.invoke(clickSpanWrapper)
        spanWrappers.add(clickSpanWrapper.build())
    }

    fun build(): SpannableString {
        val spannableString = SpannableString(str)
        spanWrappers.forEach {
            spannableString.setSpan(it.span, it.start, it.end, it.mode)
        }
        return spannableString
    }

    fun styleSpan(block: StyleSpanWrapper.() -> Unit) {
        val styleSpanBuild = StyleSpanWrapper()
        block.invoke(styleSpanBuild)
        spanWrappers.add(styleSpanBuild.build())

    }
}


class ColorSpanWrapper() {
    var color: Int = Color.BLACK
    var start: Int = 0
    var end: Int = 0


    fun build(): BaseSpanWrapper {
        val span: ParcelableSpan = ForegroundColorSpan(color)
        val mode: Int = SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        return BaseSpanWrapper(span, start, end, mode)
    }
}

class ScaleSpanWrapper() {
    var scale: Float = 1f
    var start: Int = 0
    var end: Int = 0


    fun build(): BaseSpanWrapper {
        val span: ParcelableSpan = RelativeSizeSpan(scale)
        val mode: Int = SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        return BaseSpanWrapper(span, start, end, mode)
    }
}

class AbsoluteSizeSpanWrapper() {
    var size: Int = 12
    var start: Int = 0
    var end: Int = 0


    fun build(): BaseSpanWrapper {
        val span: ParcelableSpan = AbsoluteSizeSpan(size)
        val mode: Int = SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        return BaseSpanWrapper(span, start, end, mode)
    }
}

class ClickSpanWrapper() {

    var clickCallBack: (() -> Unit)? = null
    var start: Int = 0
    var end: Int = 0
    fun build(): BaseSpanWrapper {
        val span: ParcelableSpan = MClickableSpan(clickCallBack!!)
        val mode: Int = SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        return BaseSpanWrapper(span, start, end, mode)
    }
}

class BaseSpanWrapper(
    var span: ParcelableSpan,
    var start: Int,
    var end: Int,
    var mode: Int
)

class MClickableSpan(var clickCallBack: () -> Unit) : ClickableSpan(), ParcelableSpan {
    override fun onClick(p0: View) {
        clickCallBack.invoke()
    }

//    override fun writeToParcel(p0: Parcel?, p1: Int) {
//
//    }

    override fun getSpanTypeId(): Int {
        return -1
    }

    override fun describeContents(): Int {
        return -1
    }

    override fun writeToParcel(p0: Parcel, p1: Int) {

    }

}

/**
 * 设置字体
 * Typeface.NORMAL正常 Typeface.BOLD粗体 Typeface.ITALIC斜体
 */
class StyleSpanWrapper() {
    var styleSpan: Int = Typeface.NORMAL
    var start: Int = 0
    var end: Int = 0


    fun build(): BaseSpanWrapper {
        val span: ParcelableSpan = StyleSpan(styleSpan)
        val mode: Int = SpannableString.SPAN_INCLUSIVE_EXCLUSIVE
        return BaseSpanWrapper(span, start, end, mode)
    }
}

fun spannableString(block: SpannableStringBuild.() -> Unit): SpannableString =
    SpannableStringBuild().apply(block).build()

