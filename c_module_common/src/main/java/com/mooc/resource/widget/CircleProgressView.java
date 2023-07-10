package com.mooc.resource.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import com.mooc.common.R;

public class CircleProgressView extends View {

    private int mCurrent;//当前进度
    private Paint mPaintOut;
    private Paint mPaintCurrent;
    private Paint mPaintText;
    private float mPaintWidth;//画笔宽度
    private int mPaintColor = Color.parseColor("#1982FF");//进度条颜色
    private int mTextColor = Color.parseColor("#999999");//字体颜色
    private int mCirCleColor = Color.parseColor("#C3C3C3");//圆圈背景颜色
    private float mTextSize;//字体大小
    private int location;//从哪个位置开始
    private float startAngle;//开始角度

    private boolean onlyCircle;  //只显示圆圈，不显示文字

    private OnLoadingCompleteListener mLoadingCompleteListener;

    public CircleProgressView(Context context) {
        this(context, null);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        //获取属性值
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CircleProgressView);
        location = array.getInt(R.styleable.CircleProgressView_cpv_location, 2);
        mPaintWidth = array.getDimension(R.styleable.CircleProgressView_cpv_paint_width, dip2px(context, 2));//默认4dp
        mPaintColor = array.getColor(R.styleable.CircleProgressView_cpv_paint_color, mPaintColor);
        mCirCleColor = array.getColor(R.styleable.CircleProgressView_cpv_circle_color, mCirCleColor);
        mTextSize = array.getDimension(R.styleable.CircleProgressView_cpv_text_size, dip2px(context, 7));//默认14sp
        mTextColor = array.getColor(R.styleable.CircleProgressView_cpv_text_color, mTextColor);
        onlyCircle = array.getBoolean(R.styleable.CircleProgressView_cpv_only_circle, false);
        array.recycle();

        //画笔->背景圆弧
        mPaintOut = new Paint();
        mPaintOut.setAntiAlias(true);
        mPaintOut.setStrokeWidth(mPaintWidth);
        mPaintOut.setStyle(Paint.Style.STROKE);
        mPaintOut.setColor(mCirCleColor);
        mPaintOut.setStrokeCap(Paint.Cap.ROUND);
        //画笔->进度圆弧
        mPaintCurrent = new Paint();
        mPaintCurrent.setAntiAlias(true);
        mPaintCurrent.setStrokeWidth(mPaintWidth);
        mPaintCurrent.setStyle(Paint.Style.STROKE);
        mPaintCurrent.setColor(mPaintColor);
        mPaintCurrent.setStrokeCap(Paint.Cap.ROUND);
        //画笔->绘制字体
        mPaintText = new Paint();
        mPaintText.setAntiAlias(true);
        mPaintText.setStyle(Paint.Style.FILL);
        mPaintText.setColor(mTextColor);
        mPaintText.setTextSize(mTextSize);

        if (location == 1) {//默认从左侧开始
            startAngle = -180;
        } else if (location == 2) {
            startAngle = -90;
        } else if (location == 3) {
            startAngle = 0;
        } else if (location == 4) {
            startAngle = 90;
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int size = width > height ? height : width;
        setMeasuredDimension(size, size);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制背景圆弧,因为画笔有一定的宽度，所有画圆弧的范围要比View本身的大小稍微小一些，不然画笔画出来的东西会显示不完整
        RectF rectF = new RectF(mPaintWidth / 2, mPaintWidth / 2, getWidth() - mPaintWidth / 2, getHeight() - mPaintWidth / 2);
        canvas.drawArc(rectF, 0, 360, false, mPaintOut);

        //绘制当前进度
        float sweepAngle = 360 * mCurrent / 100;
        canvas.drawArc(rectF, startAngle, sweepAngle, false, mPaintCurrent);

        //绘制进度数字
        String text = mCurrent + "%";
//        String text = mCurrent+"";
        if(onlyCircle){
            text = "";
        }
        //获取文字宽度
        float textWidth = mPaintText.measureText(text, 0, text.length());
        float dx = getWidth() / 2 - textWidth / 2;
        Paint.FontMetricsInt fontMetricsInt = mPaintText.getFontMetricsInt();
        float dy = (fontMetricsInt.bottom - fontMetricsInt.top) / 2 - fontMetricsInt.bottom;
        float baseLine = getHeight() / 2 + dy;
        canvas.drawText(text, dx, baseLine, mPaintText);

        if (mLoadingCompleteListener != null && mCurrent == 100) {
            mLoadingCompleteListener.complete();
        }
    }

    /**
     * 获取当前进度值
     *
     * @return
     */
    public int getmCurrent() {
        return mCurrent;
    }

    /**
     * 设置当前进度并重新绘制界面
     *
     * @param mCurrent
     */
    public void setmCurrent(int mCurrent) {
        this.mCurrent = mCurrent;
        invalidate();
    }

    public void setOnlyCircle(boolean onlyCircle){
        this.onlyCircle = onlyCircle;
        invalidate();
    }

    public void setOnLoadingCompleteListener(OnLoadingCompleteListener loadingCompleteListener) {
        this.mLoadingCompleteListener = loadingCompleteListener;
    }

    public interface OnLoadingCompleteListener {
        void complete();
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
