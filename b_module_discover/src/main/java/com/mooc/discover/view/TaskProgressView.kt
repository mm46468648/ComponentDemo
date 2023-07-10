package com.mooc.discover.view

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.discover.R
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.model.WeekLine
import java.util.*

/**
 * 签到天数展示控件
 */
class TaskProgressView @JvmOverloads constructor(
    context: Context?,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    var mHeight = 48.dp2px()      //总高度
    var progressBarHeight = 22.dp2px()     //进度区域高度
    var progressBarLineHeight = 8.dp2px()     //进度条高度
    var cornerPx = 10.dp2px()  //矩形元圆角角度
    var progressBarStartColor = Color.parseColor("#54B88D")
    var progressBarEndColor = Color.parseColor("#54B88D")
    var progressBarColor = Color.parseColor("#ffeef2f0")
    var progressSuccessColor = Color.parseColor("#10955b")
    var mWidth = 0

    var boderPadding = 28.dp2px()      //边缘距离
    var days = 7 //7天

    var bigRadius = 11.dp2px()    //状态圆圈半径
    var grayColor = Color.parseColor("#999999")
    var currentTimeColor = Color.parseColor("#3e3e3e")

    //时间轴相关的数据
    var taskWeekLine: ArrayList<WeekLine>? = null


    val timeStrPaint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = grayColor
        paint.textSize = 12.dp2px().toFloat()
        paint
    }

    val linePaint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = progressBarColor
        paint.style = Paint.Style.FILL
        paint
    }

    val lineSuccessPaint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = progressSuccessColor
        paint.style = Paint.Style.FILL
        paint
    }

    fun setWeekLineData(taskWeekLine: ArrayList<WeekLine>) {
        this.taskWeekLine = taskWeekLine
//          测试各种打卡状态
//        taskWeekLine.forEachIndexed { index, weekLine ->
//            when (index) {
//                0 -> {
//                    weekLine.status = TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS
//                }
//                1 -> {
//                    weekLine.status = TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS
//                }
//                2 -> {
//                    weekLine.status = TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS
//                }
//                3 -> {
//                    weekLine.status = TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS
//                }
//                4 -> {
//                    weekLine.status = TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS
//                }
//                5 -> {
//                    weekLine.status = TaskConstants.TASK_WEEKLINE_STATUS_FAIL
//                }
//                6 -> {
//                    weekLine.status = TaskConstants.TASK_WEEKLINE_STATUS_WILLDO
//                }
//                else -> {
//
//                }
//            }
//        }
        invalidate()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val w = MeasureSpec.getSize(widthMeasureSpec)
        mWidth = w
        //设置高度
        setMeasuredDimension(w, mHeight)

        loge("width: ${mWidth}")
    }

    var textRect = Rect()


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val cxStart = boderPadding   //起点圆x值
        val cy = progressBarHeight / 2        //圆中心y值 ，整体高度的1/4
        val bigdx = (mWidth - boderPadding * 2) / (days - 1)   //每个大圆中心x的偏移量

        val textCenterY = cy + 27.dp2px() //文字中心距离空心圆14dp

        //贴着顶部绘制最下面的进度条
        canvas.drawRoundRect(
            0f,
            (progressBarHeight - progressBarLineHeight) / 2f,
            mWidth.toFloat(),
            (progressBarHeight + progressBarLineHeight) / 2f,
            cornerPx.toFloat(),
            cornerPx.toFloat(),
            linePaint
        )

        var lastStart = 0f

        //画成功的线段
        taskWeekLine?.forEachIndexed { i, weekLine ->
            val status = weekLine.status

            val startX = cxStart.toFloat() + bigdx * i

            //如果今天是成功的将前面一段变为绿色
            if (status == TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS
                || status == TaskConstants.TASK_WEEKLINE_STATUS_COMPLETE || status == TaskConstants.TASK_WEEKLINE_STATUS_FAIL
            ) {

                if(i == 0){    //第一天成功怎么都要画
                    canvas.drawRoundRect(
                        lastStart,
                        (progressBarHeight - progressBarLineHeight) / 2f,
                        startX,
                        (progressBarHeight + progressBarLineHeight) / 2f,
                        cornerPx.toFloat(),
                        cornerPx.toFloat(),
                        lineSuccessPaint
                    )
                }else if(taskWeekLine!![i-1].status == TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS){
                    //前一天成功才画
                    canvas.drawRoundRect(
                        lastStart,
                        (progressBarHeight - progressBarLineHeight) / 2f,
                        startX,
                        (progressBarHeight + progressBarLineHeight) / 2f,
                        cornerPx.toFloat(),
                        cornerPx.toFloat(),
                        lineSuccessPaint
                    )
                }


                //最后一个成功,画结尾
                if (i == taskWeekLine!!.size - 1 && (status == TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS || status == TaskConstants.TASK_WEEKLINE_STATUS_COMPLETE)) {
                    canvas.drawRoundRect(
                        startX,
                        (progressBarHeight - progressBarLineHeight) / 2f,
                        mWidth.toFloat(),
                        (progressBarHeight + progressBarLineHeight) / 2f,
                        cornerPx.toFloat(),
                        cornerPx.toFloat(),
                        lineSuccessPaint
                    )
                }
            }
            lastStart = startX
        }

        //画状态提示,日期
        taskWeekLine?.forEachIndexed { i, weekLine ->
            val status = weekLine.status

            val startX = cxStart.toFloat() + bigdx * i


            //画每天的圆底,成功的画绿色
            if (status == TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS
                || status == TaskConstants.TASK_WEEKLINE_STATUS_COMPLETE
            ) {
                canvas.drawCircle(startX, cy.toFloat(), bigRadius.toFloat(), lineSuccessPaint)
            } else {
                canvas.drawCircle(startX, cy.toFloat(), bigRadius.toFloat(), linePaint)
            }
            val dateStr = TimeFormatUtil.formatDate(weekLine.date * 1000, TimeFormatUtil.MMdd)
            //测量文字宽高
            dateStr?.length?.let { timeStrPaint.getTextBounds(dateStr, 0, it, textRect) }
            val textCx = startX - textRect.width() / 2
            val textCy = textCenterY + textRect.height() / 2

            val todayStr = TimeFormatUtil.formatDate(System.currentTimeMillis(), TimeFormatUtil.MMdd)

            //画日期文字,当天变为黑色
            if(todayStr == dateStr){
                timeStrPaint.color = currentTimeColor
            }else{
                timeStrPaint.color = grayColor
            }
            dateStr?.let { canvas.drawText(it, textCx, textCy.toFloat(), timeStrPaint) }

            //绘制每天状态图片
            val statusBitmap = when (status) {
                TaskConstants.TASK_WEEKLINE_STATUS_NOTNEED -> {
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_task_progress_notneed)
                }
                TaskConstants.TASK_WEEKLINE_STATUS_SUCCESS -> {
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_task_progress_success)
                }
                TaskConstants.TASK_WEEKLINE_STATUS_FAIL -> {
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_task_progress_fail)
                }
                TaskConstants.TASK_WEEKLINE_STATUS_WILLDO -> {
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_task_progress_willdo)
                }
                TaskConstants.TASK_WEEKLINE_STATUS_WILLCOMPLETE -> {
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_task_progress_willcomplete)
                }
                TaskConstants.TASK_WEEKLINE_STATUS_COMPLETE -> {
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_task_progress_complete)
                }
                else -> {
                    BitmapFactory.decodeResource(getResources(), R.mipmap.ic_task_progress_notneed)
                }
            }

            val bitmapLeft = startX - statusBitmap.width / 2f
            val bitmapTop = cy - statusBitmap.height / 2f
            canvas.drawBitmap(statusBitmap, bitmapLeft, bitmapTop, linePaint);
        }
    }


}