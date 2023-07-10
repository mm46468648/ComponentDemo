package com.mooc.resource.widget

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import com.mooc.resource.ktextention.dp2px

/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    提示小红点View
 * @Author:         xym
 * @CreateDate:     2020/8/11 4:34 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/11 4:34 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class RedDotView@JvmOverloads constructor(var mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(mContext, attrs, defStyleAttr) {

    var mPaint = Paint()

    var radius = 5.dp2px()
    init {

        mPaint.setColor(Color.RED)
        mPaint.setStyle(Paint.Style.FILL)
        mPaint.setAntiAlias(true)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.drawCircle((measuredWidth/2).toFloat(), (measuredHeight/2).toFloat(), radius.toFloat(),mPaint)
    }
}