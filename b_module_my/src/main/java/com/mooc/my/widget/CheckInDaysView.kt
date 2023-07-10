package com.mooc.my.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.View
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import java.text.SimpleDateFormat
import java.util.*

/**
 * 签到天数展示控件
 */
class CheckInDaysView @JvmOverloads constructor(context: Context?, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : View(context, attrs, defStyleAttr) {

    var mHeight = 60.dp2px()
    var mWidth = 0

    var pointMargin = 20.dp2px()
    var smallPointMargin = 10.dp2px()
    var days = 7 //7天

    var bigRadius = 3.dp2px()
    var redRadius = 3.dp2px()
    var pinkRadius = 6.dp2px()
    var smallRadius = 2.dp2px()

    var daysArray = arrayOfNulls<String>(7)
    var grayColor =  Color.parseColor("#707070")
    val bigPaint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = grayColor
        paint.style = Paint.Style.STROKE
        paint.strokeWidth = 1.dp2px().toFloat()
        paint
    }

    val smallPaint by lazy {
        val paint = Paint()
        paint.isAntiAlias = true
        paint.color = grayColor
        paint.textSize = 10.dp2px().toFloat()
        paint
    }

    init {

        initDayArray()
    }

    /**
     * 初始化天数
     * 从当前天往前数七天
     */
    private fun initDayArray() {
        for (i in -6 .. 0) {
            @SuppressLint("SimpleDateFormat") val dateFormat = SimpleDateFormat("MM.dd")
            val calendar = Calendar.getInstance()
            calendar.time = Date()
            calendar.add(Calendar.DATE, i)
            val date = calendar.time
            daysArray[i + 6] = dateFormat.format(date)
        }
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

        val cxStart = pointMargin   //起点圆x值
        val cy = mHeight / 2 / 2        //圆中心y值 ，整体高度的1/4
        val bigdx = (mWidth - pointMargin * 2) / (days - 1)   //每个大圆中心x的偏移量

        val textCenterY = cy + 28.dp2px() //文字中心距离空心圆14dp

        loge("bigStartx : ${cxStart}  bigdx: ${bigdx}")

        for (i in 0 until daysArray.size) {
            //画每天的空心圆
            val startX = cxStart.toFloat() + bigdx * i
            canvas.drawCircle(startX, cy.toFloat(), bigRadius.toFloat(), bigPaint)


            val dateStr = daysArray[i]
            //测量文字宽高
            dateStr?.length?.let { smallPaint.getTextBounds(dateStr, 0, it, textRect) }
            val textCx = startX - textRect.width() / 2
            val textCy = textCenterY + textRect.height() / 2
            //画日期文字
            smallPaint.color = grayColor
            dateStr?.let { canvas.drawText(it, textCx, textCy.toFloat(), smallPaint) }


            //不画最后一轮实心小圆点
            if (i != days - 1){
                val smallDx = (bigdx - smallPointMargin * 2) / 4    //每个小圆的偏移量
                val smallStart = startX + smallPointMargin      //小圆的起始点
                //画天数中间实心小点点
                for (j in 0 until 5) {
                    val cx = smallStart + smallDx * j     //每个实心小圆的起始点
//                    canvas.drawCircle(cx, cy.toFloat(), smallRadius.toFloat(), smallPaint)
                    //新版改为矩形（可看作是一条线）
//                    canvas.drawCircle(cx, cy.toFloat(), smallRadius.toFloat(), smallPaint)
                    canvas.drawLine(cx-smallRadius.toFloat(),cy.toFloat(),cx+smallRadius.toFloat(),cy.toFloat(),smallPaint)
                }
            }


            //画签到那天的红点，和上一天的中间连线
            if (daysArray[i] in signDayList) {

                if(i>0 && daysArray[i-1] in signDayList){
                    //如果上一天，也是签到的，则在当前点和之前一点画一条绿线#76241A
                    smallPaint.color = Color.parseColor("#BFEDD9")
                    smallPaint.strokeWidth = (smallRadius ).toFloat()
                    canvas.drawLine(startX,cy.toFloat(),startX - bigdx + pinkRadius,cy.toFloat(),smallPaint)
                }


                smallPaint.color = Color.parseColor("#BFEDD9")   //浅绿色
                canvas.drawCircle(startX, cy.toFloat(), pinkRadius.toFloat(), smallPaint)
                smallPaint.color = Color.parseColor("#10955B")  //主题色
                canvas.drawCircle(startX, cy.toFloat(), redRadius.toFloat(), smallPaint)
            }
        }


    }

    /**
     * 实际的签到的日期
     * MM.dd格式
     */
    var signDayList = arrayListOf<String>()
        set(value) {
            field = value
            this.invalidate()
        }

}