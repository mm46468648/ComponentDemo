package com.mooc.my.widget

import android.animation.Animator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import androidx.annotation.Nullable
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.logd
import java.util.*


/**
 * 签到打卡，积分抽奖View
 */
class PieView(context: Context?, @Nullable attrs: AttributeSet?, defStyleAttr: Int) :
    View(context, attrs, defStyleAttr) {
    //    private val mStrings = arrayOf("1\n积分", "4\n积分", "7\n积分", "10\n积分")
    private val mStrings = arrayOf("1", "4", "7", "10")
    private val mCount = mStrings.size
    private val scoreStr = "积    分"

    // private int[] mImages = new int[]{R.drawable.iphone, R.drawable.danfan, R.drawable.f040, R.drawable.ipad, R.drawable.f015};
//    private val mImages = intArrayOf(R.drawable.f040, R.drawable.f040,
//            R.drawable.f015, R.drawable.f040, R.drawable.f015)
    private val sectorColor = intArrayOf(Color.parseColor("#F6A93D"), Color.parseColor("#FACD55"))

    /**
     * 图片
     */
//    private val mBitmaps = arrayOfNulls<Bitmap>(mStrings.size)

    val bgColor = Color.parseColor("#FF4500")

    /**
     * 画背景
     */
    private val mBgPaint: Paint by lazy {
        val mBgPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mBgPaint?.setColor(bgColor)
        mBgPaint
    }

    /**
     * 绘制扇形
     */
    private val mArcPaint: Paint by lazy {
        Paint(Paint.ANTI_ALIAS_FLAG)
    }

    /**
     * 绘制积分数值文字
     */
    private val mTextPaint: Paint by lazy {
        val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.setColor(Color.WHITE)
        mTextPaint.setTextSize(20.dp2px().toFloat())
        mTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD))
        mTextPaint
    }

    /**
     * 绘制积分文案文字
     */
    private val mScoreTextPaint: Paint by lazy {
        val mTextPaint = Paint(Paint.ANTI_ALIAS_FLAG)
        mTextPaint.setColor(Color.WHITE)
        mTextPaint.setTextSize(14.dp2px().toFloat())
        mTextPaint.setTypeface(Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL))
        mTextPaint
    }

    /**
     * 半径
     */
    private var mRadius = 0

    /**
     * 圆心坐标
     */
    private var mCenter = 0

    /**
     * 弧形的起始角度
     */
    private var startAngle = 0
    private val angles = IntArray(mCount)

    //    private val mTextSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SHIFT, 48f, getResources().getDisplayMetrics())
    private val mTextSize = 20.dp2px().toFloat()
    private var sectorRectF: RectF? = null

    //小的举行
    private var smallSectorRectF: RectF? = null

    /**
     * 弧形划过的角度
     */
    private var sweepAngle = 0

    /**
     * 下标
     */
    private var position = 0
    var animator: ObjectAnimator? = null
    private var listener: RotateListener? = null
    private var rotateToPosition = 0

    constructor(context: Context?) : this(context, null) {}
    constructor(context: Context?, @Nullable attrs: AttributeSet?) : this(context, attrs, 0) {}

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val w = MeasureSpec.getSize(widthMeasureSpec)
        val h = MeasureSpec.getSize(heightMeasureSpec)
        val width = Math.min(w, h)
        mCenter = width / 2
        //半径
        mRadius = (width - getPaddingLeft() * 2) / 2

        //设置框高都一样
        setMeasuredDimension(width, width)
    }

    private val mScoreTextBound = Rect()

    /**
     * 初始化
     */
    private fun init() {


//        for (i in 0 until mCount) {
//            mBitmaps[i] = BitmapFactory.decodeResource(getResources(), mImages[i % 5])
//        }
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            val x = event.x - mCenter
            val y = event.y - mCenter
            var touchAngle = 0f
            var touchRadius = 0
            //判断点击的范围是否在转盘内
            if (x < 0 && y > 0) { //第二象限
                touchAngle += 180f
            } else if (x < 0 && y < 0) { //第三象限
                touchAngle += 180f
            } else if (x > 0 && y < 0) { //第四象限
                touchAngle += 360f
            }
            //Math.atan(y/x) 返回正数值表示相对于 x 轴的逆时针转角，返回负数值则表示顺时针转角。
            // 返回值乘以 180/π，将弧度转换为角度。
            touchAngle += Math.toDegrees(Math.atan(y / x.toDouble())).toFloat()
            touchRadius = Math.sqrt(x * x + y * y.toDouble()).toInt()
            if (touchRadius < mRadius) {
                position = -Arrays.binarySearch(angles, touchAngle.toInt())
                logd(TAG, "onTouchEvent: " + position)
            }
            return true
        }
        return super.onTouchEvent(event)
    }

    override fun onDraw(canvas: Canvas) {
        //1.绘制背景
        mBgPaint.color = bgColor
        canvas.drawCircle(
            mCenter.toFloat(),
            mCenter.toFloat(),
            (mCenter - getPaddingLeft() / 2).toFloat(),
            mBgPaint
        )
        //2.绘制扇形
        //2.1设置每一个扇形的角度
        sweepAngle = 360 / mCount
        startAngle = 135
        //2.2设置扇形绘制的范围
        sectorRectF = RectF(
            paddingLeft.toFloat(), paddingLeft.toFloat(),
            (mCenter * 2 - paddingLeft).toFloat(), (mCenter * 2 - getPaddingLeft()).toFloat()
        )

        val smallOffset = mRadius / 2

        smallSectorRectF = RectF(
            paddingLeft.toFloat() - smallOffset,
            paddingLeft.toFloat() - smallOffset,
            (mCenter * 2 - paddingLeft).toFloat() - smallOffset,
            (mCenter * 2 - getPaddingLeft()).toFloat() - smallOffset
        )
        for (i in 0 until mCount) {
            mArcPaint.setColor(sectorColor[i % 2])
            //sectorRectF 扇形绘制范围  startAngle 弧开始绘制角度 sweepAngle 每次绘制弧的角度
            // useCenter 是否连接圆心
            canvas.drawArc(
                sectorRectF!!,
                startAngle.toFloat(),
                sweepAngle.toFloat(),
                true,
                mArcPaint
            )
            //3.绘制文字
            drawTexts(canvas, mStrings[i])
            //4.绘制图片
//            drawIcons(canvas, mBitmaps[i])
            angles[i] = startAngle
            logd(TAG, "onDraw: " + angles[i] + "     " + i)
            startAngle += sweepAngle
        }

//        val smallweepAngle = 90;
//        val smallstartAngle = 180;
//        val rectF = RectF(sectorRectF, 100f, (100 + mRadius).toFloat(), (100 + mRadius).toFloat())
//        for (i in 0 until 4) {
//            val path = Path()
//            path.addArc(sectorRectF, startAngle.toFloat(), sweepAngle.toFloat());
//            val textLength = mTextPaint.measureText(scoreStr);//获得字体长度
//            val hOffset =
//                (mRadius * Math.PI * (sweepAngle * 1.0 / 360) / 2 - textLength / 2).toFloat();//The distance along the path
//            val vOffset = (mRadius / 2 / 4f); //垂直路径方向上的偏移
//            canvas.drawTextOnPath(scoreStr, path, hOffset, vOffset, mTextPaint);
//            startAngle += sweepAngle;
//        }

        //画转盘上的小圆点
        drawAroundSmallCircle(canvas)
        super.onDraw(canvas)
    }

    /**
     * 画转盘上的小圆点
     */
    private fun drawAroundSmallCircle(canvas: Canvas) {
        val mCount = 20
        val sweepAngle = 360 / mCount
        var startAngle = 0

        mBgPaint.color = Color.WHITE
        for (i in 0 until mCount) {
            //计算半边扇形的角度 度=Math.PI/180 弧度=180/Math.PI
            val angle = ((startAngle + sweepAngle / 2) * Math.PI / 180).toFloat()
            //计算中心点的坐标,等于半径+padding的一半
            val r = mRadius + 10.dp2px() / 2
            val x = (mCenter + r * Math.cos(angle.toDouble())).toFloat()
            val y = (mCenter + r * Math.sin(angle.toDouble())).toFloat()

            canvas.drawCircle(x, y, 2.dp2px().toFloat(), mBgPaint)
            startAngle += sweepAngle
        }

    }

    /**
     * 以二分之一的半径的长度，扇形的一半作为图片的中心点
     * 图片的宽度为imageWidth
     *
     * @param canvas
     * @param mBitmap
     */
    private fun drawIcons(canvas: Canvas, mBitmap: Bitmap) {
        val imageWidth = mRadius / 10
        //计算半边扇形的角度 度=Math.PI/180 弧度=180/Math.PI
        val angle = ((startAngle + sweepAngle / 2) * Math.PI / 180).toFloat()
        //计算中心点的坐标
        val r = mRadius / 2
        val x = (mCenter + r * Math.cos(angle.toDouble())).toFloat()
        val y = (mCenter + r * Math.sin(angle.toDouble())).toFloat()
        //设置绘制图片的范围
        val rectF = RectF(x - imageWidth, y - imageWidth, x + imageWidth, y + imageWidth)
        canvas.drawBitmap(mBitmap, null, rectF, null)
    }


    /**
     * 使用path添加一个路径
     * 绘制文字的路径
     *
     * @param canvas
     * @param mString
     */
    private fun drawTexts(canvas: Canvas, mString: String) {
        val path = Path()
        //添加一个圆弧的路径
        sectorRectF?.let { path.addArc(it, startAngle.toFloat(), sweepAngle.toFloat()) }

        //测量文字的宽度
        var textWidth = 0
        var textHeight = 0

//        var textWidth: Float = mTextPaint.measureText(mString)
        mTextPaint.getTextBounds(mString, 0, mString.length, mScoreTextBound)
        textWidth = mScoreTextBound.width()
        textHeight = mScoreTextBound.height()

        //水平偏移
        val hOffset = (mRadius * 2 * Math.PI / mCount / 2 - textWidth / 2).toInt()

        val scoreTextWidth = mScoreTextPaint.measureText(scoreStr)
//        mScoreTextPaint.getTextBounds(scoreStr, 0, scoreStr.length, mScoreTextBound)
//        scoreTextWidth = mScoreTextBound.width()


        //积分水平偏移
//        val scoreHoffset = (mRadius * 2 * Math.PI / mCount / 2 - scoreTextWidth / 2).toInt()
        val scoreHoffset = (mRadius * 2 * Math.PI * (sweepAngle * 1.0 / 360) / 2 - scoreTextWidth / 2)
//        var scoreHoffset = (sectorRectF?.width()?.toInt()?:0/2 - scoreTextWidth / 2).toInt()

        val allOffset = 5.dp2px()


        //根据路径绘制文字
        canvas.drawTextOnPath(mString, path, hOffset.toFloat(), (mRadius / 2).toFloat() - textHeight - allOffset, mTextPaint)
        //绘制积分
        canvas.drawTextOnPath(scoreStr, path, scoreHoffset.toFloat(), (mRadius / 2).toFloat() - allOffset, mScoreTextPaint)
    }


    /**
     * 旋转到指定积分
     * @param awardNum 奖励积分数量
     */
    fun rotate(awardNum: String) {

        //动画执行中，不再执行
        if (animator?.isRunning == true) {
            return
        }
        var i = -1     //找到奖励数据索引
        for (num in mStrings.indices) {
            if (mStrings[num].startsWith(awardNum)) {
                i = num//找到第一个就退出
                break
            }
        }
//        toast("需要奖励${awardNum},索引${i}")

        rotateToPosition = 360 / mCount * (mCount - i) + 90
        val toDegree = 360f * 5 + rotateToPosition
        animator = ObjectAnimator.ofFloat(this@PieView, "rotation", 0f, toDegree)
        animator?.setRepeatCount(0)
        animator?.setDuration(300)
        animator?.setInterpolator(DecelerateInterpolator())
        animator?.setAutoCancel(true)
        animator?.addListener(object : Animator.AnimatorListener {
            override fun onAnimationStart(animation: Animator) {}
            override fun onAnimationEnd(animation: Animator) {
                //指针指向的方向为270度
                if (listener != null) {
                    rotateToPosition = 270 - rotateToPosition
                    if (rotateToPosition < 0) {
                        rotateToPosition += 360
                    } else if (rotateToPosition == 0) {
                        rotateToPosition = 270
                    }
                    position = -Arrays.binarySearch(angles, rotateToPosition)
                    logd(TAG, "onCallBackPosition: " + position)

                    listener?.value(awardNum)
                }
            }

            override fun onAnimationCancel(animation: Animator) {}
            override fun onAnimationRepeat(animation: Animator) {}
        })
        animator?.start()
    }

    fun setListener(listener: RotateListener?) {
        this.listener = listener
    }

    /**
     * 开始动画
     */
    fun startAnimation() {
        //动画执行中，不再执行
        if (animator?.isRunning == true) {
            return
        }
        animator = ObjectAnimator.ofFloat(this@PieView, "rotation", 0f, 360f)
        animator?.setDuration(200)
        animator?.setRepeatCount(Animation.INFINITE)
        animator?.repeatMode = ValueAnimator.RESTART
        animator?.start()
    }

    /**
     * 结束动画
     */
    fun stopAnimation() {
        if (animator?.isRunning == true) {
            animator?.cancel()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        stopAnimation()
    }

    interface RotateListener {
        fun value(s: String)
    }

    companion object {
        private val TAG = PieView::class.java.simpleName
    }

    init {
        init()
    }
}