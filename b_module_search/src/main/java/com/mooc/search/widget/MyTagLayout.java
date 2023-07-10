package com.mooc.search.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.customview.widget.ViewDragHelper;
import com.mooc.search.R;
import com.mooc.common.ktextends.IntExtentionKt;
import java.util.ArrayList;
import java.util.List;
/**
 *添加搜索历史的 taglayout
 * @author limeng
 * @date 2020/8/6
 */
public class MyTagLayout extends ViewGroup {

    private boolean isResourcePage = false;

    /**
     * Vertical interval, default 5(dp)
     */
    private int mVerticalInterval = (int) IntExtentionKt.dp2px(15);

    /**
     * Horizontal interval, default 5(dp)
     */
    private int mHorizontalInterval = (int) IntExtentionKt.dp2px(12);

    /**
     * TagContainerLayout border width(default 0.5dp)
     */
    private float mBorderWidth = 0.5f;

    /**
     * TagContainerLayout border radius(default 5.0dp)
     */
    private float mBorderRadius = 5.0f;

    /**
     * The sensitive of the ViewDragHelper(default 1.0f, normal)
     */
    private float mSensitivity = 1.0f;

    /**
     * TagView average height
     */
    private int mChildHeight;

    /**
     * TagContainerLayout border color
     */
    private int mBorderColor = Color.parseColor("#F5F5F5");

    /**
     * TagContainerLayout background color(default #11FF0000)
     */
    private int mBackgroundColor = Color.TRANSPARENT;

    /**
     * The container layout gravity(default left)
     */
    private int mGravity = Gravity.LEFT;

    /**
     * The max length for TagView(default max length 30)
     */
    private int mTagMaxLength = 100;

    /** TagView Border width(default 0.5dp)*/
//    private float mTagBorderWidth = 0.5f;

    /**
     * TagView Border radius(default 5.0dp)
     */
    private float mTagBorderRadius = 5.0f;

    /**
     * TagView Text size(default 14sp)
     */
    private float mTagTextSize = 13;

    /**
     * Text direction(support:TEXT_DIRECTION_RTL & TEXT_DIRECTION_LTR, default TEXT_DIRECTION_LTR)
     */
    @SuppressLint("InlinedApi")
    private int mTagTextDirection = View.TEXT_DIRECTION_LTR;

    /**
     * Horizontal padding for TagView, include left & right padding(left & right padding are equal, default 10dp)
     */
    private int mTagHorizontalPadding = (int) IntExtentionKt.dp2px(10);

    /**
     * Vertical padding for TagView, include top & bottom padding(top & bottom padding are equal, default 8dp)
     */
    private int mTagVerticalPadding = (int) IntExtentionKt.dp2px(8);
    ;

    /**
     * TagView border color
     */
    private int mTagBorderColor = Color.parseColor("#4182FA");

    /**
     * TagView background color
     */
    private int mTagBackgroundColor = Color.parseColor("#F5F5F5");

    /**
     * TagView text color
     */
    private int mTagTextColor = Color.parseColor("#666666");

    /**
     * TagView typeface
     */
    private Typeface mTagTypeface = Typeface.DEFAULT;


    /**
     * Tags
     */
    private List<String> mTags;

    public void updateTagBeanList(List<String> mTags) {
        this.mTags = mTags;
    }

    public List<String> getPlatformTagBeanList() {
        return mTags;
    }

    /**
     * Can drag TagView(default false)
     */
    private boolean mDragEnable;

    /**
     * TagView drag state(default STATE_IDLE)
     */
    private int mTagViewState = ViewDragHelper.STATE_IDLE;

    /**
     * The distance between baseline and descent(default 5.5px)
     */
    private float mTagBdDistance = 5.5f;

    /**
     * OnTagClickListener for TagView
     */
    private OnTagClickListener mOnTagClickListener;

    private Paint mPaint;

    private RectF mRectF;

    private ViewDragHelper mViewDragHelper;

    private List<TextView> mChildViews;

    private int[] mViewPos;

    /**
     * View theme(default PURE_CYAN)
     */
    private int mTheme = 1;

    /**
     * Default interval(dp)
     */
    private static final float DEFAULT_INTERVAL = 5;

    /**
     * Default tag min length
     */
    private static final int TAG_MIN_LENGTH = 3;

    public MyTagLayout(Context context) {
        this(context, null);
    }

    public MyTagLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyTagLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mRectF = new RectF();
        mChildViews = new ArrayList<TextView>();
        mViewDragHelper = ViewDragHelper.create(this, mSensitivity, new DragHelperCallBack());
        setWillNotDraw(false);
        setTagMaxLength(mTagMaxLength);
        setTagHorizontalPadding(mTagHorizontalPadding);
        setTagVerticalPadding(mTagVerticalPadding);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        measureChildren(widthMeasureSpec, heightMeasureSpec);
        final int childCount = getChildCount();
        int lines = childCount == 0 ? 0 : getChildLines(childCount);
        int widthSpecSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpecSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightSpecMode = MeasureSpec.getMode(heightMeasureSpec);

        if (childCount == 0) {
            setMeasuredDimension(0, 0);
        } else if (heightSpecMode == MeasureSpec.AT_MOST
                || heightSpecMode == MeasureSpec.UNSPECIFIED) {
            setMeasuredDimension(widthSpecSize, (mVerticalInterval + mChildHeight) * lines
                    + getPaddingTop() + getPaddingBottom());
        } else {
            setMeasuredDimension(widthSpecSize, heightSpecSize);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mRectF.set(0, 0, w, h);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount;
        if ((childCount = getChildCount()) <= 0) {
            return;
        }
        int availableW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int curRight = getMeasuredWidth() - getPaddingRight();
        int curTop = getPaddingTop();
        int curLeft = getPaddingLeft();
        int sPos = 0;
        mViewPos = new int[childCount * 2];

        for (int i = 0; i < childCount; i++) {
            final View childView = getChildAt(i);
            if (childView.getVisibility() != GONE) {
                int width = childView.getMeasuredWidth();
                if (mGravity == Gravity.RIGHT) {
                    if (curRight - width < getPaddingLeft()) {
                        curRight = getMeasuredWidth() - getPaddingRight();
                        curTop += mChildHeight + mVerticalInterval;
                    }
                    mViewPos[i * 2] = curRight - width;
                    mViewPos[i * 2 + 1] = curTop;
                    curRight -= width + mHorizontalInterval;
                } else if (mGravity == Gravity.CENTER) {
                    if (curLeft + width - getPaddingLeft() > availableW) {
                        int leftW = getMeasuredWidth() - mViewPos[(i - 1) * 2]
                                - getChildAt(i - 1).getMeasuredWidth() - getPaddingRight();
                        for (int j = sPos; j < i; j++) {
                            mViewPos[j * 2] = mViewPos[j * 2] + leftW / 2;
                        }
                        sPos = i;
                        curLeft = getPaddingLeft();
                        curTop += mChildHeight + mVerticalInterval;
                    }
                    mViewPos[i * 2] = curLeft;
                    mViewPos[i * 2 + 1] = curTop;
                    curLeft += width + mHorizontalInterval;

                    if (i == childCount - 1) {
                        int leftW = getMeasuredWidth() - mViewPos[i * 2]
                                - childView.getMeasuredWidth() - getPaddingRight();
                        for (int j = sPos; j < childCount; j++) {
                            mViewPos[j * 2] = mViewPos[j * 2] + leftW / 2;
                        }
                    }
                } else {
                    if (curLeft + width - getPaddingLeft() > availableW) {
                        curLeft = getPaddingLeft();
                        curTop += mChildHeight + mVerticalInterval;
                    }
                    mViewPos[i * 2] = curLeft;
                    mViewPos[i * 2 + 1] = curTop;
                    curLeft += width + mHorizontalInterval;
                }
                // curLeft +=Utils.dip2px(getContext(),15.0f);
            }
        }

        // layout all child views
        for (int i = 0; i < mViewPos.length / 2; i++) {
            View childView = getChildAt(i);
            childView.layout(mViewPos[i * 2], mViewPos[i * 2 + 1],
                    mViewPos[i * 2] + childView.getMeasuredWidth(),
                    mViewPos[i * 2 + 1] + mChildHeight);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mBackgroundColor);
        canvas.drawRoundRect(mRectF, mBorderRadius, mBorderRadius, mPaint);

        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setColor(mBorderColor);
        canvas.drawRoundRect(mRectF, mBorderRadius, mBorderRadius, mPaint);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mViewDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mViewDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mViewDragHelper.continueSettling(true)) {
            requestLayout();
        }
    }

    private int getChildLines(int childCount) {
        int availableW = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();
        int lines = 1;
        for (int i = 0, curLineW = 0; i < childCount; i++) {
            View childView = getChildAt(i);
            int dis = childView.getMeasuredWidth() + mHorizontalInterval;
            int height = childView.getMeasuredHeight();
            mChildHeight = i == 0 ? height : Math.min(mChildHeight, height);
            curLineW += dis;
            if (curLineW - mHorizontalInterval > availableW) {
                lines++;
                curLineW = dis;
            }
        }
        return lines;
    }

    private void onSetTag() {
        if (mTags == null) {
            throw new RuntimeException("NullPointer exception!");
        }
        removeAllTags();
        if (mTags.size() == 0) {
            return;
        }
        for (int i = 0; i < mTags.size(); i++) {
            onAddTag(mTags.get(i), mChildViews.size());
        }
        postInvalidate();
    }

    private void onAddTag(String areaBean, int position) {
        if (position < 0 || position > mChildViews.size()) {
            throw new RuntimeException("Illegal position!");
        }
        TextView tagView = new TextView(getContext());
        tagView.setText(areaBean);
        initTagView(tagView);

        mChildViews.add(position, tagView);
        if (position < mChildViews.size()) {
            for (int i = position; i < mChildViews.size(); i++) {
                mChildViews.get(i).setTag(i);

            }
        } else {
            tagView.setTag(position);
        }
        addView(tagView, position);
    }

    private void initTagView(TextView tagView) {
        int[] colors = new int[]{mTagBackgroundColor, mTagBorderColor, mTagTextColor};
        tagView.setTextColor(mTagTextColor);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            tagView.setTextDirection(mTagTextDirection);
        }
        tagView.setTypeface(mTagTypeface);
        tagView.setTextSize(mTagTextSize);
        tagView.setHeight(30);
        tagView.setMaxLines(1);
        tagView.setEllipsize(TextUtils.TruncateAt.END);
        String text = "";
        text = tagView.getText().toString().trim();
        if (text.length() > mTagMaxLength) {
            text = text.substring(0, mTagMaxLength - 3) + "...";
        }
        tagView.setText(text);
        setmBackgroundGray(tagView);
        tagView.setGravity(Gravity.CENTER);
        tagView.setPadding((int) IntExtentionKt.dp2px(12), (int) IntExtentionKt.dp2px(2), (int) IntExtentionKt.dp2px(12), (int) IntExtentionKt.dp2px(2));
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, (int) IntExtentionKt.dp2px(30));
        tagView.setLayoutParams(lp);
    }

    private void setmBackgroundGray(TextView tagView) {
            tagView.setBackgroundResource(R.drawable.search_bg_tag_gray);
    }


    private void setLayoutParams(TextView view) {
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, (int) IntExtentionKt.dp2px(15), 0);
        view.setLayoutParams(layoutParams);
    }

    private void invalidateTags() {
        for (final TextView view : mChildViews) {
            if (mOnTagClickListener != null) {
                view.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mOnTagClickListener.onTagClick((int) view.getTag(), view.getText().toString().trim());
                    }
                });
            }
        }
    }

    private void onRemoveTag(int position) {
        if (position < 0 || position >= mChildViews.size()) {
            throw new RuntimeException("Illegal position!");
        }
        mChildViews.remove(position);
        removeViewAt(position);
        for (int i = position; i < mChildViews.size(); i++) {
            mChildViews.get(i).setTag(i);
        }
        // TODO, make removed view null?
    }


//    private int ceilTagBorderWidth(){
//        return (int)Math.ceil(mTagBorderWidth);
//    }

    private class DragHelperCallBack extends ViewDragHelper.Callback {

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            mTagViewState = state;
        }

        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            requestDisallowInterceptTouchEvent(true);
            return mDragEnable;
        }

        @Override
        public int clampViewPositionHorizontal(View child, int left, int dx) {
            final int leftX = getPaddingLeft();
            final int rightX = getWidth() - child.getWidth() - getPaddingRight();
            return Math.min(Math.max(left, leftX), rightX);
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
            final int topY = getPaddingTop();
            final int bottomY = getHeight() - child.getHeight() - getPaddingBottom();
            return Math.min(Math.max(top, topY), bottomY);
        }

        @Override
        public int getViewHorizontalDragRange(View child) {
            return getMeasuredWidth() - child.getMeasuredWidth();
        }

        @Override
        public int getViewVerticalDragRange(View child) {
            return getMeasuredHeight() - child.getMeasuredHeight();
        }

    }

    /**
     * Get current drag view state.
     *
     * @return
     */
    public int getTagViewState() {
        return mTagViewState;
    }

    /**
     * Get TagView text baseline and descent distance.
     *
     * @return
     */
    public float getTagBdDistance() {
        return mTagBdDistance;
    }

    /**
     * Set TagView text baseline and descent distance.
     *
     * @param tagBdDistance
     */
    public void setTagBdDistance(float tagBdDistance) {
        this.mTagBdDistance = dp2px(getContext(), tagBdDistance);
    }

    /**
     * Set tags
     *
     * @param tags
     */
    public void setTags(List<String> tags) {
        mTags = tags;
        onSetTag();
    }


    /**
     * Inserts the specified TagView into this ContainerLayout at the end.
     *
     * @param areaBean
     */
    public void addTag(String areaBean) {
        addTag(areaBean, mChildViews.size());
    }

    /**
     * Inserts the specified TagView into this ContainerLayout at the specified location.
     * The TagView is inserted before the current element at the specified location.
     *
     * @param areaBean
     * @param position
     */
    public void addTag(String areaBean, int position) {
        onAddTag(areaBean, position);
        postInvalidate();
    }

    /**
     * Remove a TagView in specified position.
     *
     * @param position
     */
    public void removeTag(int position) {
        onRemoveTag(position);
        postInvalidate();
    }

    /**
     * Remove all TagViews.
     */
    public void removeAllTags() {
        mChildViews.clear();
        removeAllViews();
        postInvalidate();
    }

    /**
     * Set OnTagClickListener for TagView.
     *
     * @param listener
     */
    public void setOnTagClickListener(OnTagClickListener listener) {
        mOnTagClickListener = listener;
        invalidateTags();
    }

    /**
     * Get TagView text.
     *
     * @param position
     * @return
     */
    public String getTagText(int position) {
        return ((TextView) mChildViews.get(position)).getText().toString().trim();
    }

    /**
     * Get a string list for all tags in TagContainerLayout.
     *
     * @return
     */
//    public List<FilterCategoryBean.AreaBean> getTags(){
//        List<FilterCategoryBean.AreaBean> tmpList = new ArrayList<FilterCategoryBean.AreaBean>();
//        for (View view : mChildViews){
//            if (view instanceof TextView){
//                tmpList.add(((TextView) view).getText().toString().trim());
//            }
//        }
//        return tmpList;
//    }
    public List<TextView> getTagViews() {
        if (mChildViews != null && mChildViews.size() > 0) {
            return mChildViews;
        }
        return new ArrayList<>();
    }

    /**
     * Set whether the child view can be dragged.
     *
     * @param enable
     */
    public void setDragEnable(boolean enable) {
        this.mDragEnable = enable;
    }

    /**
     * Get current view is drag enable attribute.
     *
     * @return
     */
    public boolean getDragEnable() {
        return mDragEnable;
    }

    /**
     * Set vertical interval
     *
     * @param interval
     */
    public void setVerticalInterval(float interval) {
        mVerticalInterval = (int) dp2px(getContext(), interval);
        postInvalidate();
    }


    /**
     * Set TagContainerLayout background color.
     *
     * @param color
     */
    public void setBackgroundColor(int color) {
        this.mBackgroundColor = color;
    }

    /**
     * Get container layout gravity.
     *
     * @return
     */
    public int getGravity() {
        return mGravity;
    }

    /**
     * Set container layout gravity.
     *
     * @param gravity
     */
    public void setGravity(int gravity) {
        this.mGravity = gravity;
    }

    /**
     * Set the TagView text max length(must greater or equal to 3).
     *
     * @param maxLength
     */
    public void setTagMaxLength(int maxLength) {
        mTagMaxLength = maxLength < TAG_MIN_LENGTH ? TAG_MIN_LENGTH : maxLength;
    }


    /**
     * Set TagView theme.
     *
     * @param theme
     */
    public void setTheme(int theme) {
        mTheme = theme;
    }

    /**
     * Get TagView theme.
     *
     * @return
     */
    public int getTheme() {
        return mTheme;
    }


    /**
     * Set TagView horizontal padding.
     *
     * @param padding
     */
    public void setTagHorizontalPadding(int padding) {
//        int ceilWidth = ceilTagBorderWidth();
        this.mTagHorizontalPadding = padding;
//        this.mTagHorizontalPadding = padding < ceilWidth ? ceilWidth : padding;
    }

    /**
     * Set TagView vertical padding.
     *
     * @param padding
     */
    public void setTagVerticalPadding(int padding) {
//        int ceilWidth = ceilTagBorderWidth();
        this.mTagVerticalPadding = padding;
//        this.mTagVerticalPadding = padding < ceilWidth ? ceilWidth : padding;
    }


    public float dp2px(Context context, float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return dp * scale + 0.5f;
    }

    public float sp2px(Context context, float sp) {
        final float scale = context.getResources().getDisplayMetrics().scaledDensity;
        return sp * scale;
    }

    public interface OnTagClickListener {
        void onTagClick(int position, String text);
    }

    public void setResourcePage(boolean resourcePage) {
        isResourcePage = resourcePage;
    }
}
