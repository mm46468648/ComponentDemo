/*
 * *************************************
 *  Title: ProgressWheel.java
 *  Description:
 *  Copyright: Copyright (c)  2015
 *  Company: 学堂在线
 *  @author Yan
 *  @date 15-5-11 上午10:17
 *  @version 1.0
 * *****************************
 */

package com.mooc.home.widget;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.mooc.home.R;


public class ProgressWheel extends View {

    //Sizes (with defaults)
    private int layout_height = 0;
    private int layout_width = 0;
    private int fullRadius = 100;
    private int circleRadius = 80;
    private int barLength = 60;
    private int barWidth = 20;
    private int rimWidth = 20;
    private int textSize = 20;
    private float contourSize = 0;
    private float loadingWdith = 2;
    private int loadingLength = 340;
    //Padding (with defaults)
    private int paddingTop = 5;
    private int paddingBottom = 5;
    private int paddingLeft = 5;
    private int paddingRight = 5;

    //Colors (with defaults)
    private int barColor = 0x7f0c00c8;
    private int contourColor = 0x7f0c00c8;
    private int circleColor = 0x00000000;
    private int loadingColor = 0x7f0c00c8;
    private int rimColor = 0x7f0c00c8;
    private int textColor = 0xFF000000;

    //Paints
    private Paint barPaint = new Paint();
    private Paint barPaintBig = new Paint();
    private Paint circlePaint = new Paint();
    private Paint rimPaint = new Paint();
    private Paint textPaint = new Paint();
    private Paint contourPaint = new Paint();

    //Rectangles
    @SuppressWarnings("unused")
    private RectF rectBounds = new RectF();
    private RectF circleBounds = new RectF();
    private RectF circleOuterContour = new RectF();
    private RectF circleInnerContour = new RectF();

    //Animation
    //The amount of pixels to move the bar by on each draw
    private int spinSpeed = 2;
    //The number of milliseconds to wait inbetween each draw
    private int delayMillis = 0;
    int progress = 0;
    int progressSpin = 0;
    boolean isSpinning = false;

    //Other
    private String text = "";
    private String[] splitText = {};

    //download status.
    private int currentStatus = 0;
    public final static int STATUS_UNSTART = 0;
    public final static int STATUS_ERROR = -1;
    public final static int STATUS_LOAING = 1;
    public final static int STATUS_PAUSE = 2;
    public final static int STATUS_DOING = 3;
    public final static int STATUS_COMPLETE = 5;
    // download drawable.
    private StateDrawables mDrawble;
    private int marginPix = 2; //margin between bar and drawable.
    private static final int[] STATE_DOWNLOAD = {R.attr.wheelStateDrawable};

    static class StateDrawables {
        Drawable drawableError, drawableLoading, drawableUnstart, drawableComplete, drawablePause, drawableDoing;

        public StateDrawables(TypedArray a) {
            drawableUnstart = initDrawable(a, R.styleable.ProgressWheel_wheelStateUnstart, R.mipmap.home_ic_download_state_start);
            drawablePause = initDrawable(a, R.styleable.ProgressWheel_wheelStatePause, R.mipmap.home_ic_download_state_start);
            drawableComplete = initDrawable(a, R.styleable.ProgressWheel_wheelStateComplete, R.mipmap.home_ic_download_state_complete);
            drawableLoading = initDrawable(a, R.styleable.ProgressWheel_wheelStateLoading, R.mipmap.home_ic_download_state_loading);
            drawableError = initDrawable(a, R.styleable.ProgressWheel_wheelStateError, R.mipmap.home_ic_download_state_error);
            drawableDoing = initDrawable(a, R.styleable.ProgressWheel_wheelStateDoing, R.mipmap.home_ic_download_state_doing);
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

    /**
     * The constructor for the ProgressWheel
     *
     * @param context
     * @param attrs
     */
    public ProgressWheel(Context context, AttributeSet attrs) {
        super(context, attrs);

        parseAttributes(context.obtainStyledAttributes(attrs,
                R.styleable.ProgressWheel));
    }

    //----------------------------------
    //Setting up stuff
    //----------------------------------

    /*
     * When this is called, make the view square.
     * From: http://www.jayway.com/2012/12/12/creating-custom-android-views-part-4-measuring-and-how-to-force-a-view-to-be-square/
     *
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // The first thing that happen is that we call the superclass
        // implementation of onMeasure. The reason for that is that measuring
        // can be quite a complex process and calling the super method is a
        // convenient way to get most of this complexity handled.
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        // We can’t use getWidth() or getHight() here. During the measuring
        // pass the view has not gotten its final size yet (this happens first
        // at the start of the layout pass) so we have to use getMeasuredWidth()
        // and getMeasuredHeight().
        int size = 0;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();
        int widthWithoutPadding = width - getPaddingLeft() - getPaddingRight();
        int heigthWithoutPadding = height - getPaddingTop() - getPaddingBottom();

        // Finally we have some simple logic that calculates the size of the view
        // and calls setMeasuredDimension() to set that size.
        // Before we compare the width and height of the view, we remove the padding,
        // and when we set the dimension we add it back again. Now the actual content
        // of the view will be square, but, depending on the padding, the total dimensions
        // of the view might not be.
        if (widthWithoutPadding > heigthWithoutPadding) {
            size = heigthWithoutPadding;
        } else {
            size = widthWithoutPadding;
        }

        // If you override onMeasure() you have to call setMeasuredDimension().
        // This is how you report back the measured size.  If you don’t call
        // setMeasuredDimension() the parent will throw an exception and your
        // application will crash.
        // We are calling the onMeasure() method of the superclass so we don’t
        // actually need to call setMeasuredDimension() since that takes care
        // of that. However, the purpose with overriding onMeasure() was to
        // change the default behaviour and to do that we need to call
        // setMeasuredDimension() with our own values.
        setMeasuredDimension(size + getPaddingLeft() + getPaddingRight(), size + getPaddingTop() + getPaddingBottom());
    }

    /**
     * Use onSizeChanged instead of onAttachedToWindow to get the dimensions of the view,
     * because this method is called after measuring the dimensions of MATCH_PARENT & WRAP_CONTENT.
     * Use this dimensions to setup the bounds and paints.
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        // Share the dimensions
        layout_width = w;
        layout_height = h;
        TextView view;
        setupBounds();
        setupPaints();
        invalidate();
    }

    /**
     * Set the properties of the paints we're using to
     * draw the progress wheel
     */
    private void setupPaints() {
        barPaint.setColor(barColor);
        barPaint.setAntiAlias(true);
        barPaint.setStyle(Style.STROKE);
        barPaint.setStrokeWidth(barWidth);

        barPaintBig.setColor(loadingColor);
        barPaintBig.setAntiAlias(true);
        barPaintBig.setStyle(Style.STROKE);
        barPaintBig.setStrokeWidth(loadingWdith);

        rimPaint.setColor(rimColor);
        rimPaint.setAntiAlias(true);
        rimPaint.setStyle(Style.STROKE);
        rimPaint.setStrokeWidth(rimWidth);

        circlePaint.setColor(circleColor);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Style.FILL);

        textPaint.setColor(textColor);
        textPaint.setStyle(Style.FILL);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);

        contourPaint.setColor(contourColor);
        contourPaint.setAntiAlias(true);
        contourPaint.setStyle(Style.STROKE);
        contourPaint.setStrokeWidth(contourSize);
    }

    /**
     * Set the bounds of the component
     */
    private void setupBounds() {
        // Width should equal to Height, find the min value to setup the circle
        int minValue = Math.min(layout_width, layout_height);

        // Calc the Offset if needed
        int xOffset = layout_width - minValue;
        int yOffset = layout_height - minValue;

        // Add the offset
        paddingTop = this.getPaddingTop() + (yOffset / 2);
        paddingBottom = this.getPaddingBottom() + (yOffset / 2);
        paddingLeft = this.getPaddingLeft() + (xOffset / 2);
        paddingRight = this.getPaddingRight() + (xOffset / 2);

        int width = getWidth(); //this.getLayoutParams().width;
        int height = getHeight(); //this.getLayoutParams().height;

        rectBounds = new RectF(paddingLeft,
                paddingTop,
                width - paddingRight,
                height - paddingBottom);

       /* circleBounds = new RectF(paddingLeft + barWidth,
                paddingTop + barWidth,
                width - paddingRight - barWidth,
                height - paddingBottom - barWidth);*/
        circleBounds = new RectF(paddingLeft, paddingTop, width - paddingRight, height - paddingBottom);
        circleInnerContour = new RectF(circleBounds.left + (rimWidth / 2.0f) + (contourSize / 2.0f), circleBounds.top + (rimWidth / 2.0f) + (contourSize / 2.0f), circleBounds.right - (rimWidth / 2.0f) - (contourSize / 2.0f), circleBounds.bottom - (rimWidth / 2.0f) - (contourSize / 2.0f));
        circleOuterContour = new RectF(circleBounds.left - (rimWidth / 2.0f) - (contourSize / 2.0f), circleBounds.top - (rimWidth / 2.0f) - (contourSize / 2.0f), circleBounds.right + (rimWidth / 2.0f) + (contourSize / 2.0f), circleBounds.bottom + (rimWidth / 2.0f) + (contourSize / 2.0f));

        fullRadius = (width - paddingRight - barWidth) / 2;
        circleRadius = (fullRadius - barWidth) + 1;
    }

    /**
     * Parse the attributes passed to the view from the XML
     *
     * @param a the attributes to parse
     */
    private void parseAttributes(TypedArray a) {
        barWidth = (int) a.getDimension(R.styleable.ProgressWheel_wheelBarWidth,
                barWidth);
        loadingWdith = (int) a.getDimension(R.styleable.ProgressWheel_wheelLoadingWidth, loadingWdith);
        rimWidth = (int) a.getDimension(R.styleable.ProgressWheel_wheelRimWidth,
                rimWidth);

        spinSpeed = (int) a.getDimension(R.styleable.ProgressWheel_wheelSpinSpeed,
                spinSpeed);

        delayMillis = a.getInteger(R.styleable.ProgressWheel_wheelDelayMillis,
                delayMillis);
        if (delayMillis < 0) {
            delayMillis = 0;
        }


        barColor = a.getColor(R.styleable.ProgressWheel_wheelBarColor, barColor);

        barLength = (int) a.getDimension(R.styleable.ProgressWheel_wheelBarLength,
                barLength);

        textSize = (int) a.getDimension(R.styleable.ProgressWheel_wheelTextSize,
                textSize);

        textColor = (int) a.getColor(R.styleable.ProgressWheel_wheelTextColor,
                textColor);

        //if the text is empty , so ignore it
        if (a.hasValue(R.styleable.ProgressWheel_wheelText)) {
            setText(a.getString(R.styleable.ProgressWheel_wheelText));
        }

        rimColor = (int) a.getColor(R.styleable.ProgressWheel_wheelRimColor,
                rimColor);

        circleColor = (int) a.getColor(R.styleable.ProgressWheel_wheelCircleColor,
                circleColor);

        contourColor = a.getColor(R.styleable.ProgressWheel_wheelContourColor, contourColor);
        contourSize = a.getDimension(R.styleable.ProgressWheel_wheelContourSize, contourSize);

        mDrawble = new StateDrawables(a);
        marginPix = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1.5f, getResources().getDisplayMetrics());
        loadingLength = a.getInt(R.styleable.ProgressWheel_wheelLoadingLength, 340);
        loadingColor = a.getColor(R.styleable.ProgressWheel_wheelLoadingColor, loadingColor);
        // Recycle
        a.recycle();
    }

    boolean loadingAnimal = false;

    public void setStartLoadingAni(boolean loadingAnimal) {
        this.loadingAnimal = loadingAnimal;
    }

    //----------------------------------
    //Animation stuff
    //----------------------------------
    int startPosition = 0;
    int endPosition = 270;

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //Draw the inner circle
        canvas.drawArc(circleBounds, 360, 360, false, circlePaint);
        //Draw the rim
        canvas.drawArc(circleBounds, 360, 360, false, rimPaint);
        canvas.drawArc(circleOuterContour, 360, 360, false, contourPaint);
        canvas.drawArc(circleInnerContour, 360, 360, false, contourPaint);
        Drawable currentDrawable = mDrawble.getDrawable(currentStatus);
        if (currentDrawable != null) {
            Rect drawableRect = mDrawble.getRect(currentDrawable, this.getMeasuredWidth(), this.getMeasuredHeight());
            if (currentStatus == STATUS_LOAING) {
                circleBounds.top = drawableRect.top;
                circleBounds.bottom = drawableRect.bottom;
                circleBounds.left = drawableRect.left;
                circleBounds.right = drawableRect.right;
                if (loadingAnimal) {
                    barPaintBig.setColor(Color.parseColor("#4685FB"));
                    startPosition += 3;
                    if (progressSpin > 360) {
                        progressSpin = 0;
                    }
                    canvas.drawArc(circleBounds, startPosition, endPosition, false, barPaintBig);
                    scheduleRedraw();
                    return;
                }
                canvas.drawArc(circleBounds, progressSpin - 90, loadingLength, false, barPaintBig);
                scheduleRedraw();
                return;
            }
            currentDrawable.setBounds(drawableRect);
            currentDrawable.draw(canvas);
            if (currentStatus == STATUS_COMPLETE || currentStatus == STATUS_ERROR || currentStatus == STATUS_UNSTART) {
                return;
            }
            circleBounds.top = drawableRect.top + marginPix;
            circleBounds.bottom = drawableRect.bottom - marginPix;
            circleBounds.left = drawableRect.left + marginPix;
            circleBounds.right = drawableRect.right - marginPix;
            //Draw the bar
            if (isSpinning) {
                canvas.drawArc(circleBounds, progress - 90, barLength, false,
                        barPaint);
            } else {
                canvas.drawArc(circleBounds, -90, progress, false, barPaint);
            }
            //Draw the text (attempts to center it horizontally and vertically)
            float textHeight = textPaint.descent() - textPaint.ascent();
            float verticalTextOffset = (textHeight / 2) - textPaint.descent();

            for (String s : splitText) {
                float horizontalTextOffset = textPaint.measureText(s) / 2;
                canvas.drawText(s, this.getWidth() / 2 - horizontalTextOffset,
                        this.getHeight() / 2 + verticalTextOffset, textPaint);
            }
            if (isSpinning) {
                scheduleRedraw();
            }
        }

    }

    private void scheduleRedraw() {
        if (currentStatus == STATUS_LOAING) {
            progressSpin += spinSpeed;
            if (progressSpin > 360) {
                progressSpin = 0;
            }
        } else {
            progress += spinSpeed;
            if (progress > 360) {
                progress = 0;
            }
        }
        postInvalidateDelayed(delayMillis);
    }

    /**
     * Check if the wheel is currently spinning
     */
    public boolean isSpinning() {
        return isSpinning;
    }

    /**
     * Reset the count (in increment mode)
     */
    public void resetCount() {
        progress = 0;
        setText("0%");
        invalidate();
    }

    /**
     * Turn off spin mode
     */
    public void stopSpinning() {
        isSpinning = false;
        progress = 0;
        postInvalidate();
    }


    /**
     * Puts the view on spin mode
     */
    public void spin() {
        isSpinning = true;
        postInvalidate();
    }

    /**
     * Increment the progress by 1 (of 360)
     */
    public void incrementProgress() {
        isSpinning = false;
        progress++;
        if (progress > 360)
            progress = 0;
        //setText(Math.round(((float) progress / 360) * 100) + "%");
        postInvalidate();
    }


    /**
     * Set the progress to a specific value
     */
    public void setProgress(int i) {
        isSpinning = false;
        if (progress == i) {
            return;
        }
        progress = i;
        if (i >= 360) {
            currentStatus = STATUS_COMPLETE;
        }
        //postInvalidate();
        invalidate();
    }

    public void setProgressStatus(int statusType) {
        if (currentStatus == statusType) {
            return;
        }
        currentStatus = statusType;
        //postInvalidate();
        invalidate();
    }

    public int getProgressStatus() {
        return currentStatus;
    }

    @Override
    protected int[] onCreateDrawableState(int extraSpace) {
        final int[] drawableState = super.onCreateDrawableState(extraSpace + 6);
        mergeDrawableStates(drawableState, STATE_DOWNLOAD);
        return drawableState;
    }

    public void setDownloadState(int downloadState) {
        this.currentStatus = downloadState;
    }
    //----------------------------------
    //Getters + setters
    //----------------------------------

    /**
     * Set the text in the progress bar
     * Doesn't invalidate the view
     *
     * @param text the text to show ('\n' constitutes a new line)
     */
    public void setText(String text) {
        this.text = text;
        splitText = this.text.split("\n");
    }

    public int getCircleRadius() {
        return circleRadius;
    }

    public void setCircleRadius(int circleRadius) {
        this.circleRadius = circleRadius;
    }

    public int getBarLength() {
        return barLength;
    }

    public void setBarLength(int barLength) {
        this.barLength = barLength;
    }

    public int getBarWidth() {
        return barWidth;
    }

    public void setBarWidth(int barWidth) {
        this.barWidth = barWidth;

        if (this.barPaint != null) {
            this.barPaint.setStrokeWidth(this.barWidth);
        }
    }

    public int getTextSize() {
        return textSize;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;

        if (this.textPaint != null) {
            this.textPaint.setTextSize(this.textSize);
        }
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getBarColor() {
        return barColor;
    }

    public void setBarColor(int barColor) {
        this.barColor = barColor;

        if (this.barPaint != null) {
            this.barPaint.setColor(this.barColor);
        }
    }

    public int getCircleColor() {
        return circleColor;
    }

    public void setCircleColor(int circleColor) {
        this.circleColor = circleColor;

        if (this.circlePaint != null) {
            this.circlePaint.setColor(this.circleColor);
        }
    }

    public int getRimColor() {
        return rimColor;
    }

    public void setRimColor(int rimColor) {
        this.rimColor = rimColor;

        if (this.rimPaint != null) {
            this.rimPaint.setColor(this.rimColor);
        }
    }

    public Shader getRimShader() {
        return rimPaint.getShader();
    }

    public void setRimShader(Shader shader) {
        this.rimPaint.setShader(shader);
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;

        if (this.textPaint != null) {
            this.textPaint.setColor(this.textColor);
        }
    }

    public int getSpinSpeed() {
        return spinSpeed;
    }

    public void setSpinSpeed(int spinSpeed) {
        this.spinSpeed = spinSpeed;
    }

    public int getRimWidth() {
        return rimWidth;
    }

    public void setRimWidth(int rimWidth) {
        this.rimWidth = rimWidth;

        if (this.rimPaint != null) {
            this.rimPaint.setStrokeWidth(this.rimWidth);
        }
    }

    public int getDelayMillis() {
        return delayMillis;
    }

    public void setDelayMillis(int delayMillis) {
        this.delayMillis = delayMillis;
    }

    public int getContourColor() {
        return contourColor;
    }

    public void setContourColor(int contourColor) {
        this.contourColor = contourColor;

        if (contourPaint != null) {
            this.contourPaint.setColor(this.contourColor);
        }
    }

    public float getContourSize() {
        return this.contourSize;
    }

    public void setContourSize(float contourSize) {
        this.contourSize = contourSize;

        if (contourPaint != null) {
            this.contourPaint.setStrokeWidth(this.contourSize);
        }
    }

    public float getPix(int dip) {
        Resources r = getResources();
        float px = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dip, r.getDisplayMetrics());
        return px;
    }
}