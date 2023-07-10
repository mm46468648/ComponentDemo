package com.mooc.home.ui.decoration

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.view.View
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.mooc.resource.ktextention.dp2px

/**
 * 学习计划Adapter，装饰
 * 边距和分割线
 */
class Padding15Divider1Decoration : RecyclerView.ItemDecoration(){

    val rectDp = 15.dp2px()
    private val dividerPadding = 15.dp2px()

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        super.getItemOffsets(outRect, view, parent, state)

        outRect.set(rectDp,rectDp,rectDp,rectDp)
    }


    private val mPaint = Paint()
    init {
        mPaint.color = Color.parseColor("#E2E2E2")
        mPaint.strokeWidth = 1f
        mPaint.isAntiAlias = true
    }

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        super.onDraw(c, parent, state!!)

        // 获取RecyclerView的Child view的个数
        val childCount = parent.childCount

        // 遍历每个Item，分别获取它们的位置信息，然后再绘制对应的分割线
        for (i in 0 until childCount) {
            // 获取每个Item的位置
            val child = parent.getChildAt(i)
            val index = parent.getChildAdapterPosition(child);
//            // 最后1个Item不需要绘制
//            if ( index == childCount-1 ) {
//                return;
//            }

            // 获取布局参数
            val params = child
                    .layoutParams as RecyclerView.LayoutParams
            // 设置矩形(分割线)的宽度为1px
            val mDivider = 1

            // 根据子视图的位置 & 间隔区域，设置矩形（分割线）的2个顶点坐标(左上 & 右下)

            // 矩形左上顶点 = (ItemView的左边界,ItemView的下边界)
            // ItemView的左边界 = RecyclerView 的左边界 + paddingLeft距离 后的位置
            val left = parent.paddingLeft
            // ItemView的下边界：ItemView 的 bottom坐标 + 距离RecyclerView底部距离 +translationY
            val top = (child.bottom + params.bottomMargin +
                    Math.round(ViewCompat.getTranslationY(child))).toInt() + rectDp

            // 矩形右下顶点 = (ItemView的右边界,矩形的下边界)
            // ItemView的右边界 = RecyclerView 的右边界减去 paddingRight 后的坐标位置
            val right = parent.width - parent.paddingRight
            // 绘制分割线的下边界 = ItemView的下边界+分割线的高度
            val bottom = top + mDivider

            if(index != childCount-1)
            c.drawLine(left + dividerPadding.toFloat(), top.toFloat(), right - dividerPadding.toFloat(), bottom.toFloat(), mPaint)
            // 通过Canvas绘制矩形（分割线）
//            c.drawRect(left,top,right,bottom,mPaint);
        }
    }
}