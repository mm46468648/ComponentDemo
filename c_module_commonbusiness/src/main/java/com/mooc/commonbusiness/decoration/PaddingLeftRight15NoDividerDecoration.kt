package com.mooc.commonbusiness.decoration

import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.mooc.resource.ktextention.dp2px

/**
 * 学习计划Adapter，装饰
 * 边距和分割线
 */
class PaddingLeftRight15NoDividerDecoration : RecyclerView.ItemDecoration(){

    val rectDp = 15.dp2px()
    private val dividerPadding = 15.dp2px()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.set(rectDp,0,rectDp,0)
    }


    private val mPaint = Paint()
    init {
        mPaint.color = Color.parseColor("#E2E2E2")
        mPaint.strokeWidth = 1f
        mPaint.isAntiAlias = true
    }


}