package com.mooc.webview.x5kit

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import com.mooc.webview.stratage.BaseWebview
import com.qmuiteam.qmui.nestedScroll.IQMUIContinuousNestedScrollCommon
import com.qmuiteam.qmui.nestedScroll.IQMUIContinuousNestedTopView
import com.qmuiteam.qmui.nestedScroll.QMUIContinuousNestedTopWebView
import com.qmuiteam.qmui.util.QMUIDisplayHelper
import com.tencent.smtt.sdk.WebView

/**
 * X5内核，WebView包装类
 */
class X5kitWebView
    : WebView, BaseWebview ,IQMUIContinuousNestedTopView{

    constructor(context: Context) : super(context)
    constructor(context: Context, attr: AttributeSet) : super(context, attr)
    constructor(context: Context, attr: AttributeSet, defaultValue: Int) : super(context, attr, defaultValue)

    //    @JvmOverloads constructor(var mContext: Context, attr: AttributeSet? = null, defaultValue: Int = 0)
    var mOnX5ScrollChangedCallback: ((dx: Int, dy: Int) -> Unit)? = null

    var mScrollListener : ((t:Int)->Unit)? = null
    private var mScrollNotifier: IQMUIContinuousNestedScrollCommon.OnScrollNotifier? = null

    override fun saveScrollInfo(bundle: Bundle) {
        bundle.putInt(QMUIContinuousNestedTopWebView.KEY_SCROLL_INFO, scrollY)
    }



    override fun restoreScrollInfo(bundle: Bundle) {
        val scrollY = QMUIDisplayHelper.px2dp(context,
            bundle.getInt(QMUIContinuousNestedTopWebView.KEY_SCROLL_INFO, 0))
        exec("javascript:scrollTo(0, $scrollY)")
    }

    private fun exec(jsCode: String) {
        evaluateJavascript(jsCode, null)
    }

    override fun injectScrollNotifier(notifier: IQMUIContinuousNestedScrollCommon.OnScrollNotifier?) {
        mScrollNotifier=notifier
    }

    override fun consumeScroll(yUnconsumed: Int): Int {
        // compute the consumed value

        // compute the consumed value
        var scrollY = scrollY
        val maxScrollY = scrollOffsetRange
        // the scrollY may be negative or larger than scrolling range
        // the scrollY may be negative or larger than scrolling range
        scrollY = Math.max(0, Math.min(scrollY, maxScrollY))
        var dy = 0
        if (yUnconsumed < 0) {
            dy = Math.max(yUnconsumed, -scrollY)
        } else if (yUnconsumed > 0) {
            dy = Math.min(yUnconsumed, maxScrollY - scrollY)
        }
        scrollBy(0, dy)
        return yUnconsumed - dy
    }

    override fun getCurrentScroll(): Int {
        val scrollY = scrollY
        val scrollRange = scrollOffsetRange
        return Math.max(0, Math.min(scrollY, scrollRange))
    }

    override fun getScrollOffsetRange(): Int {
        return computeVerticalScrollRange() - height
    }

    override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
        super.onScrollChanged(l, t, oldl, oldt)
        mScrollListener?.invoke(t)

    }

}