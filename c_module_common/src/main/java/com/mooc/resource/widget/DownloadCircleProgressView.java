package com.mooc.resource.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.mooc.common.ktextends.IntExtentionKt;
import com.mooc.common.R;;

import static com.mooc.common.ktextends.AnyExtentionKt.loge;


public class DownloadCircleProgressView extends FrameLayout {

    int currentStatus = 0;
    int progress = 0;
    private StateDrawables mStateDrawble;
    private boolean simpleMode = true;   //不需要显示进度
    private RectF circleBounds = new RectF();
    Context mContext;

    private Paint mPaintOut;
    private Paint mPaintCurrent;
    private Paint mPaintText;
    private float mPaintWidth;//画笔宽度
    private int mPaintColor = Color.parseColor("#0795DF");//画笔颜色
    private int mTextColor = Color.parseColor("#C4C8D0");//字体颜色
    private int circleProgressMargin = IntExtentionKt.dp2px(2);

    private float mTextSize;//字体大小
    private int location;//从哪个位置开始
    private float startAngle;//开始角度

    private ProgressBar mSimpleProgressBar;
    private CircleProgressView mCircleProgressView;

    public DownloadCircleProgressView(Context context) {
        this(context, null);
    }

    public DownloadCircleProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("CustomViewStyleable")
    public DownloadCircleProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        _init(context.obtainStyledAttributes(attrs, R.styleable.DownloadStateView));
    }

    private void _init(TypedArray attrs) {

        mStateDrawble = new StateDrawables(attrs);

        initPaint(attrs);
        attrs.recycle();


        if (simpleMode) {
            addProgressView();
        } else {
            addCirCleProgressView();
        }
    }

    private void addCirCleProgressView() {
        if (mCircleProgressView == null) {
            mCircleProgressView = new CircleProgressView(mContext);
            LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            layoutParams.setMargins(circleProgressMargin, circleProgressMargin, circleProgressMargin, circleProgressMargin);
            this.addView(mCircleProgressView);
        }


    }

    private void addProgressView() {
        if (mSimpleProgressBar == null) {
            mSimpleProgressBar = new ProgressBar(mContext);
            mSimpleProgressBar.setIndeterminateDrawable(ContextCompat.getDrawable(mContext, R.drawable.common_rotate_downloading_pb));
            this.addView(mSimpleProgressBar);
        }
    }

    private void initPaint(TypedArray array) {
        //获取属性值
        location = array.getInt(R.styleable.CircleProgressView_cpv_location, 2);
        mPaintWidth = array.getDimension(R.styleable.DownloadStateView_dsv_paint_width, 4);//默认4dp
        mPaintColor = array.getColor(R.styleable.DownloadStateView_dsv_paint_color, mPaintColor);
        mTextSize = array.getDimension(R.styleable.DownloadStateView_dsv_text_size, 18);//默认18sp
        mTextColor = array.getColor(R.styleable.DownloadStateView_dsv_text_color, mTextColor);
        simpleMode = array.getBoolean(R.styleable.DownloadStateView_dsv_simple_mode, true);
    }

    public final static int STATUS_UNSTART = 0;
    public final static int STATUS_LOAING = 1;
    public final static int STATUS_DOING = 2;
    public final static int STATUS_WAIT = 3;
    public final static int STATUS_PAUSE = 4;
    public final static int STATUS_COMPLETE = 5;
    public final static int STATUS_ERROR = -1;


    static class StateDrawables {
        Drawable drawableError, drawableLoading, drawableUnstart, drawableComplete, drawablePause, drawableDoing;

        public StateDrawables(TypedArray a) {
            drawableUnstart = initDrawable(a, R.styleable.DownloadStateView_dsv_state_unstart, R.mipmap.common_ic_download_start);
            drawablePause = initDrawable(a, R.styleable.DownloadStateView_dsv_state_pause, R.mipmap.common_ic_download_pause);
            drawableComplete = initDrawable(a, R.styleable.DownloadStateView_dsv_state_complete, R.mipmap.common_ic_download_complete);
            drawableLoading = initDrawable(a, R.styleable.DownloadStateView_dsv_state_loading, R.mipmap.common_ic_download_start);
            drawableError = initDrawable(a, R.styleable.DownloadStateView_dsv_state_error, R.mipmap.common_ic_download_error);
            drawableDoing = initDrawable(a, R.styleable.DownloadStateView_dsv_state_doing, R.mipmap.common_ic_download_doing);
        }

        private Drawable initDrawable(TypedArray a, int stypeId, int defaultDrawableId) {
            Drawable drawable = a.getDrawable(stypeId);
            if (drawable == null) {
                drawable = a.getResources().getDrawable(defaultDrawableId);
            }
            return drawable;
        }

        public Rect getRect(Drawable drawable, int width, int height) {
            Rect newRect = new Rect();
            newRect.top = (height - drawable.getIntrinsicHeight()) / 2;
            newRect.bottom = newRect.top + drawable.getIntrinsicHeight();
            newRect.left = (width - drawable.getIntrinsicWidth()) / 2;
            newRect.right = newRect.left + drawable.getIntrinsicWidth();
            return newRect;
        }

        public Drawable getDrawable(int currentStatus) {
            switch (currentStatus) {
                case STATUS_COMPLETE:
                    return drawableComplete;
                case STATUS_DOING:
                    return drawableDoing;
                case STATUS_ERROR:
                    return drawableError;
                case STATUS_WAIT:    //等待改为不返回
                    return null;
                case STATUS_LOAING:
                    return drawableLoading;
                case STATUS_UNSTART:
                    return drawableUnstart;
                case STATUS_PAUSE:
                    return drawablePause;
            }
            return drawableUnstart;
        }
    }


    @Override
    protected void dispatchDraw(Canvas canvas) {

//        loge(this, "dcp state: " + currentStatus);


        Drawable currentDrawable = mStateDrawble.getDrawable(currentStatus);
        if (currentStatus != STATUS_DOING && currentDrawable != null) {
            Rect drawableRect = mStateDrawble.getRect(currentDrawable, this.getMeasuredWidth(), this.getMeasuredHeight());
            currentDrawable.setBounds(drawableRect);
            currentDrawable.draw(canvas);
        }
        super.dispatchDraw(canvas);
        setProgressStatus();


    }


    /**
     * 设置进度状态
     */
    private void setProgressStatus() {


        if (simpleMode && mSimpleProgressBar != null) {
            if (currentStatus == STATUS_DOING)
                mSimpleProgressBar.setVisibility(View.VISIBLE);
            else
                mSimpleProgressBar.setVisibility(View.GONE);
        }
//
        if (!simpleMode && mCircleProgressView != null) {
            mCircleProgressView.setOnlyCircle(!(currentStatus == STATUS_DOING || currentStatus == STATUS_WAIT));
            if (currentStatus == STATUS_DOING || currentStatus == STATUS_PAUSE || currentStatus == STATUS_WAIT) {
                mCircleProgressView.setVisibility(View.VISIBLE);
                mCircleProgressView.setmCurrent(progress);
            } else {
                mCircleProgressView.setVisibility(View.GONE);
            }
        }

    }

    public void setState(int state) {
        this.currentStatus = state;
        postInvalidate();

    }

    public int getState() {
        return currentStatus;
    }

    /**
     * 获取当前进度值
     *
     * @return
     */
    public int getmCurrent() {
        return progress;
    }

    /**
     * 设置当前进度并重新绘制界面
     *
     * @param mCurrent
     */
    public void setmCurrent(int mCurrent) {
        this.progress = mCurrent;
        postInvalidate();
    }

}
