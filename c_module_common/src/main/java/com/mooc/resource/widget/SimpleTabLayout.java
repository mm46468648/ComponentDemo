                                     /*
 * Copyright (C) 2013 Andreas Stuetz <andreas.stuetz@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mooc.resource.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import com.mooc.common.R;;
import com.mooc.resource.ktextention.IntExtentionKt;

import java.util.ArrayList;
import java.util.Locale;

/**
 * @ProjectName: SimpleTabLayout
 * @Package: com.moocxuetang.c_module_resource.widget
 * @ClassName: SimpleTabLayout
 * @Description: 升级版Tablayout指示器，支持ViewPager2
 * @Author: xym
 * @CreateDate: 2020/8/13 12:58 PM
 * @UpdateUser: 更新者
 * @UpdateDate: 2020/8/13 12:58 PM
 * @UpdateRemark: 更新内容
 * @Version: 1.0
 */
@SuppressWarnings("ResourceType")
public class SimpleTabLayout extends HorizontalScrollView {
	private ViewPager2 pager2;
	private String[] tabStrs;
    private TabSelectListener tabSelectListener;


    public interface IconTabProvider {
        public int getPageIconResId(int position);
    }

    // @formatter:off
    private static final int[] ATTRS = new int[]{android.R.attr.textSize,
            android.R.attr.textColor};
    // @formatter:on

    private LinearLayout.LayoutParams defaultTabLayoutParams;
    private LinearLayout.LayoutParams expandedTabLayoutParams;

    private final PageListener pageListener = new PageListener();
    public ViewPager.OnPageChangeListener delegatePageListener;


    private LinearLayout tabsContainer;
    private ViewPager pager;

    private int tabCount;

    private int currentPosition = 0;
    private int selectedPosition = 0;
    private float currentPositionOffset = 0f;

    private Paint rectPaint;
    private Paint dividerPaint;

    private int indicatorColor = 0xFF666666;
    private int underlineColor = Color.TRANSPARENT;
    private int dividerColor = 0x1A000000;

    private boolean shouldExpand = false;
    private boolean textAllCaps = true;

    private int scrollOffset = 52;
    private int indicatorHeight = 8;
    private int underlineHeight = 2;
    private int dividerPadding = 12;
    private int tabPadding = 24;
    private int dividerWidth = 1;

    private int tabTextSize = 12;
    private int tabTextSelectedSize = 13;
    private int tabTextColor = 0xFF666666;
    private int tabTextSelectedColor = 0xFF666666;


    private Typeface tabTypeface = null;
    private int tabTypefaceStyle = Typeface.NORMAL;
    private boolean tabTextSelectedBold = false;    //是否选中加粗

    private int lastScrollX = 0;

    private int tabBackgroundResId = Color.TRANSPARENT;

    private Locale locale;

    private int lineSize;

    private int underLineBottomMargin = 0;

    private int underlineCorner = 0;
    public View getTabChildAt(int index) {
        if (index < tabsContainer.getChildCount()) {
            return tabsContainer.getChildAt(index);
        }
        return null;
    }

    public SimpleTabLayout(Context context) {
        this(context, null);
    }

    public SimpleTabLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SimpleTabLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        setFillViewport(true);
        setWillNotDraw(false);

        tabsContainer = new LinearLayout(context);
        tabsContainer.setOrientation(LinearLayout.HORIZONTAL);
        tabsContainer.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(tabsContainer);

        DisplayMetrics dm = getResources().getDisplayMetrics();

        scrollOffset = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, scrollOffset, dm);
        indicatorHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, indicatorHeight, dm);
        underlineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, underlineHeight, dm);
        dividerPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerPadding, dm);
        tabPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, tabPadding, dm);
        dividerWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dividerWidth, dm);
        tabTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, tabTextSize, dm);

        // get system attrs (android:textSize and android:textColor)

        TypedArray a = context.obtainStyledAttributes(attrs, ATTRS);

        tabTextSize = a.getDimensionPixelSize(0, tabTextSize);
        tabTextColor = a.getColor(1, tabTextColor);

        a.recycle();

        // get custom attrs

        a = context.obtainStyledAttributes(attrs, R.styleable.SimpleTabLayout);

        indicatorColor = a.getColor(R.styleable.SimpleTabLayout_stlIndicatorColor,
                indicatorColor);
        underlineColor = a.getColor(R.styleable.SimpleTabLayout_stlUnderlineColor, underlineColor);
        dividerColor = a.getColor(R.styleable.SimpleTabLayout_stlDividerColor, dividerColor);
        indicatorHeight = a.getDimensionPixelSize(R.styleable
                .SimpleTabLayout_stlIndicatorHeight, indicatorHeight);
        underlineHeight = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlUnderlineHeight, underlineHeight);
        dividerPadding = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlDividerPadding, dividerPadding);
        tabPadding = a.getDimensionPixelSize(R.styleable
                .SimpleTabLayout_stlTabPaddingLeftRight, tabPadding);
        tabBackgroundResId = a.getResourceId(R.styleable.SimpleTabLayout_stlTabBackground,
                tabBackgroundResId);
        shouldExpand = a.getBoolean(R.styleable.SimpleTabLayout_stlShouldExpand, shouldExpand);
        scrollOffset = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlScrollOffset,
                scrollOffset);
        textAllCaps = a.getBoolean(R.styleable.SimpleTabLayout_stlTextAllCaps, textAllCaps);
        tabTextSize = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlTabTextSize,
                tabTextSize);
        tabTextSelectedSize = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlSelectedTabTextSize,
                IntExtentionKt.dp2px(tabTextSelectedSize));
        tabTextSelectedColor = a.getColor(R.styleable
                .SimpleTabLayout_stlSelectedTabTextColor, tabTextSelectedColor);
        tabTextSelectedBold = a.getBoolean(R.styleable
                .SimpleTabLayout_stlSelectedTabTextStyle, false);

        tabTextColor = a.getColor(R.styleable.SimpleTabLayout_stlTabTextColor, tabTextColor);

        //下划线长度
        lineSize = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlLineSize, -1);
        //下划线距离底部距离
		underLineBottomMargin = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlUnderlineMarginBottom, underLineBottomMargin);
		//下划线圆角
		underlineCorner = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlUnderlineCorner, underlineCorner);
        int tabPaddingStart = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlTabPaddingStart, 0);
        int tabPaddingEnd = a.getDimensionPixelSize(R.styleable.SimpleTabLayout_stlTabPaddingEnd, 0);
        a.recycle();

        rectPaint = new Paint();
        rectPaint.setAntiAlias(true);
        rectPaint.setStyle(Style.FILL);

        dividerPaint = new Paint();
        dividerPaint.setAntiAlias(true);
        dividerPaint.setStrokeWidth(dividerWidth);

        defaultTabLayoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        expandedTabLayoutParams = new LinearLayout.LayoutParams(0, LayoutParams.MATCH_PARENT, 1.0f);

        if (locale == null) {
            locale = getResources().getConfiguration().locale;
        }

        if (tabPaddingStart != -1 || tabPaddingEnd != -1) {
            tabsContainer.setPadding(tabPaddingStart, 0, tabPaddingEnd, 0);
        }
    }

    public void setViewPager(ViewPager pager) {
        selectedPosition = 0;
        this.pager = pager;

        if (pager.getAdapter() == null) {
            throw new IllegalStateException("ViewPager does not have adapter instance.");
        }

        pager.setOnPageChangeListener(pageListener);

        notifyDataSetChanged();
    }

    public void setOnPageChangeListener(ViewPager.OnPageChangeListener listener) {
        this.delegatePageListener = listener;
    }


	PageChangeCallback pageChangeCallback = new PageChangeCallback();

	public void setTabStrs(String[] tabStrs) {
		this.tabStrs = tabStrs;

		notifyDataSetChanged();
	}

	public void setViewPager2(ViewPager2 pager2) {

		this.pager2 = pager2;
		pager2.registerOnPageChangeCallback(pageChangeCallback);

//		notifyDataSetChanged();
	}

    public void notifyDataSetChanged() {
		if (tabStrs != null && tabStrs.length > 0) {     //兼容ViewPage2
			tabsContainer.removeAllViews();
			tabCount = tabStrs.length;

			for (int i = 0; i < tabCount; i++) {
				addTextTab(i, tabStrs[i].toString());
			}

			updateTabStyles();

			getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

				@Override
				public void onGlobalLayout() {
					getViewTreeObserver().removeGlobalOnLayoutListener(this);
					if(pager2!=null){
                        currentPosition = pager2.getCurrentItem();
                    }
					Log.d("viewlog", "onGlobalLayout");
					scrollToChild(currentPosition, 0);
				}
			});
			return;
		}

        tabsContainer.removeAllViews();

        tabCount = pager.getAdapter().getCount();

        for (int i = 0; i < tabCount; i++) {

            if (pager.getAdapter() instanceof IconTabProvider) {
                addIconTab(i, ((IconTabProvider) pager.getAdapter()).getPageIconResId(i));
            } else {
                addTextTab(i, pager.getAdapter().getPageTitle(i).toString());
            }

        }

        updateTabStyles();

        getViewTreeObserver().addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @SuppressLint("NewApi")
            @Override
            public void onGlobalLayout() {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                    getViewTreeObserver().removeGlobalOnLayoutListener(this);
                } else {
                    getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }

                currentPosition = pager.getCurrentItem();
                scrollToChild(currentPosition, 0);
            }
        });

    }
//    public ArrayList<MyMsgBean> msgList;
//	public boolean isReadNotice=false;
//
//    public void setReadNotice(boolean readNotice) {
//        isReadNotice = readNotice;
//    }
//
//    public void setMsgList(ArrayList<MyMsgBean> msgList) {
//        this.msgList = msgList;
//    }

    private RedPointVisiablity redPointVisiablity;

    public View getTabView(int position) {
//        MyMsgBean myMsgBean = msgList.get(position);
        @SuppressLint("InflateParams") View tabView = LayoutInflater.from(getContext()).inflate(R.layout.studyroom_item_tab_layout, null);
        TextView tabTitle = tabView.findViewById(R.id.tv_tab_title);
        ImageView tabRed = tabView.findViewById(R.id.iv_tab_red);
//        tabTitle.setText(myMsgBean.title);
//        if (myMsgBean.unread_num > 0) {
//            if (position == 0) {
//                tabRed.setVisibility(View.INVISIBLE);
//            } else {
//                tabRed.setVisibility(View.VISIBLE);
//            }
//        } else {
//            tabRed.setVisibility(View.INVISIBLE);
//        }

        if(redPointVisiablity!=null){
            boolean show = redPointVisiablity.show(position);
            int i = show ? View.VISIBLE : View.INVISIBLE;
            tabRed.setVisibility(i);

            tabTitle.setText(redPointVisiablity.title(position));
        }
        return tabView;
    }
    private void addTextTab(final int position, String title) {
        if (redPointVisiablity!=null) {
            addTab(position, getTabView(position));

        }else{

            TextView tab = new TextView(getContext());
            tab.setText(title);
            tab.setGravity(Gravity.CENTER);
            tab.setSingleLine();
            addTab(position, tab);

        }


    }

    private void addIconTab(final int position, int resId) {

        ImageButton tab = new ImageButton(getContext());
        tab.setImageResource(resId);

        addTab(position, tab);

    }


    private void addTab(final int position, View tab) {
        tab.setFocusable(true);
        tab.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tabSelectListener!=null){
                    tabSelectListener.onTabSelct(position);
                }
                if(pager2!=null){
                    pager2.setCurrentItem(position,false);
                    return;
                }
                if(pager!=null){
                    pager.setCurrentItem(position, true);
                    return;
                }


                //如果没有关联，则自己动，并暴露选择位置
                selectPosition(position);

            }
        });

        tab.setPadding(tabPadding, 0, tabPadding, 0);
        tabsContainer.addView(tab, position, shouldExpand ? expandedTabLayoutParams : defaultTabLayoutParams);
    }

    /**
     * 修改样式
     */
    private void updateTabStyles() {

        for (int i = 0; i < tabCount; i++) {

            View v = tabsContainer.getChildAt(i);

            v.setBackgroundResource(tabBackgroundResId);

            if (v instanceof TextView) {

                TextView tab = (TextView) v;
                tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSize);
				tab.setTypeface(tabTypeface, tabTypefaceStyle);
                tab.setTextColor(tabTextColor);

                // setAllCaps() is only available from API 14, so the upper case is made manually if we are on a
                // pre-ICS-build
                if (textAllCaps) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                        tab.setAllCaps(true);
                    } else {
                        tab.setText(tab.getText().toString().toUpperCase(locale));
                    }
                }

                if (i == selectedPosition) {
                    tab.setTextSize(TypedValue.COMPLEX_UNIT_PX, tabTextSelectedSize);
                    tab.setTypeface(tabTypeface, tabTextSelectedBold?Typeface.BOLD:Typeface.NORMAL);
                    tab.setTextColor(tabTextSelectedColor);
                }
            }
        }

    }

	RectF underLineRectF = new RectF();


	public void scrollToChild(int position, int offset) {

        if (tabCount == 0) {
            return;
        }

        int newScrollX = tabsContainer.getChildAt(position).getLeft() + offset;

        if (position > 0 || offset > 0) {
            newScrollX -= scrollOffset;
        }

        if (newScrollX != lastScrollX) {
            lastScrollX = newScrollX;
            scrollTo(newScrollX, 0);
        }

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isInEditMode() || tabCount == 0) {
            return;
        }

        final int height = getHeight();

        // draw indicator line

        rectPaint.setColor(indicatorColor);

        // default: line below current tab
        View currentTab = tabsContainer.getChildAt(currentPosition);
        float lineLeft = currentTab.getLeft();
        float lineRight = currentTab.getRight();

        // if there is an offset, start interpolating left and right coordinates between current and next tab
        if (currentPositionOffset > 0f && currentPosition < tabCount - 1) {

            View nextTab = tabsContainer.getChildAt(currentPosition + 1);
            final float nextTabLeft = nextTab.getLeft();
            final float nextTabRight = nextTab.getRight();

            lineLeft = (currentPositionOffset * nextTabLeft + (1f - currentPositionOffset) * lineLeft);
            lineRight = (currentPositionOffset * nextTabRight + (1f - currentPositionOffset) * lineRight);
        }

        if (lineSize == -1) {
            canvas.drawRect(lineLeft, height - indicatorHeight, lineRight, height, rectPaint);
        } else {
            float newLineLeft = (lineRight - lineLeft) / 2 + lineLeft;
            float newLineRight = (lineRight - lineLeft) / 2 + lineLeft;

			underLineRectF.left = newLineLeft - lineSize / 2;
			underLineRectF.top = height - indicatorHeight - underLineBottomMargin;
			underLineRectF.right = newLineRight + lineSize / 2;
			underLineRectF.bottom = height - underLineBottomMargin;
//            canvas.drawRect(newLineLeft - lineSize / 2, height - indicatorHeight - underLineBottomMargin, newLineRight + lineSize / 2, height, rectPaint);
            canvas.drawRoundRect(underLineRectF,underlineCorner,underlineCorner,rectPaint);
        }

        // draw underline

        rectPaint.setColor(underlineColor);
        canvas.drawRect(0, height - underlineHeight, tabsContainer.getWidth(), height, rectPaint);

        // draw divider

//        dividerPaint.setColor(dividerColor);
//        for (int i = 0; i < tabCount - 1; i++) {
//            View tab = tabsContainer.getChildAt(i);
//            canvas.drawLine(tab.getRight(), dividerPadding, tab.getRight(), height - dividerPadding, dividerPaint);
//        }
    }

    private class PageListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            currentPosition = position;
            currentPositionOffset = positionOffset;

            scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

            invalidate();

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_IDLE) {
                scrollToChild(pager.getCurrentItem(), 0);
            }

            if (delegatePageListener != null) {
                delegatePageListener.onPageScrollStateChanged(state);
            }
        }

        @Override
        public void onPageSelected(int position) {
            selectedPosition = position;
            updateTabStyles();
            if (delegatePageListener != null) {
                delegatePageListener.onPageSelected(position);
            }
        }

    }

	private class PageChangeCallback extends ViewPager2.OnPageChangeCallback {

		@Override
		public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
			currentPosition = position;
			currentPositionOffset = positionOffset;
			Log.d("viewlog", "onPageScrolled");

			scrollToChild(position, (int) (positionOffset * tabsContainer.getChildAt(position).getWidth()));

			invalidate();

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrolled(position, positionOffset, positionOffsetPixels);
			}
		}

		@Override
		public void onPageSelected(int position) {
			selectedPosition = position;
			updateTabStyles();
			if (delegatePageListener != null) {
				delegatePageListener.onPageSelected(position);
			}
		}

		@Override
		public void onPageScrollStateChanged(int state) {
			if (state == ViewPager.SCROLL_STATE_IDLE) {
				Log.d("viewlog", "onPageScrollStateChanged");
				scrollToChild(pager2.getCurrentItem(), 0);
			}

			if (delegatePageListener != null) {
				delegatePageListener.onPageScrollStateChanged(state);
			}
		}
	}


    public void setIndicatorColor(int indicatorColor) {
        this.indicatorColor = indicatorColor;
        invalidate();
    }

    public void setIndicatorColorResource(int resId) {
        this.indicatorColor = getResources().getColor(resId);
        invalidate();
    }

    public int getIndicatorColor() {
        return this.indicatorColor;
    }

    public void setIndicatorHeight(int indicatorLineHeightPx) {
        this.indicatorHeight = indicatorLineHeightPx;
        invalidate();
    }

    public int getIndicatorHeight() {
        return indicatorHeight;
    }

    public void setUnderlineColor(int underlineColor) {
        this.underlineColor = underlineColor;
        invalidate();
    }

    public void setUnderlineColorResource(int resId) {
        this.underlineColor = getResources().getColor(resId);
        invalidate();
    }

    public int getUnderlineColor() {
        return underlineColor;
    }

    public void setDividerColor(int dividerColor) {
        this.dividerColor = dividerColor;
        invalidate();
    }

    public void setDividerColorResource(int resId) {
        this.dividerColor = getResources().getColor(resId);
        invalidate();
    }

    public int getDividerColor() {
        return dividerColor;
    }

    public void setUnderlineHeight(int underlineHeightPx) {
        this.underlineHeight = underlineHeightPx;
        invalidate();
    }

    public int getUnderlineHeight() {
        return underlineHeight;
    }

    public void setDividerPadding(int dividerPaddingPx) {
        this.dividerPadding = dividerPaddingPx;
        invalidate();
    }

    public int getDividerPadding() {
        return dividerPadding;
    }

    public void setScrollOffset(int scrollOffsetPx) {
        this.scrollOffset = scrollOffsetPx;
        invalidate();
    }

    public int getScrollOffset() {
        return scrollOffset;
    }

    public void setShouldExpand(boolean shouldExpand) {
        this.shouldExpand = shouldExpand;
        requestLayout();
    }

    public boolean getShouldExpand() {
        return shouldExpand;
    }

    public boolean isTextAllCaps() {
        return textAllCaps;
    }

    public void setAllCaps(boolean textAllCaps) {
        this.textAllCaps = textAllCaps;
    }

    public void setTextSize(int textSizePx) {
        this.tabTextSize = textSizePx;
        updateTabStyles();
    }

    public int getTextSize() {
        return tabTextSize;
    }

    public void setTextColor(int textColor) {
        this.tabTextColor = textColor;
        updateTabStyles();
    }

    public void setTextColorResource(int resId) {
        this.tabTextColor = getResources().getColor(resId);
        updateTabStyles();
    }

    public void setTabTextSelectedColor(int color) {
        tabTextSelectedColor = color;
        updateTabStyles();
    }

    public int getTextColor() {
        return tabTextColor;
    }

    public void setTypeface(Typeface typeface, int style) {
        this.tabTypeface = typeface;
        this.tabTypefaceStyle = style;
        updateTabStyles();
    }

    public void setTabBackground(int resId) {
        this.tabBackgroundResId = resId;
    }

    public int getTabBackground() {
        return tabBackgroundResId;
    }

    public void setTabPaddingLeftRight(int paddingPx) {
        this.tabPadding = paddingPx;
        updateTabStyles();
    }

    public int getTabPaddingLeftRight() {
        return tabPadding;
    }

    @Override
    public void onRestoreInstanceState(Parcelable state) {
        SavedState savedState = (SavedState) state;
        super.onRestoreInstanceState(savedState.getSuperState());
        currentPosition = savedState.currentPosition;
        requestLayout();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        Parcelable superState = super.onSaveInstanceState();
        SavedState savedState = new SavedState(superState);
        savedState.currentPosition = currentPosition;
        return savedState;
    }

    static class SavedState extends BaseSavedState {
        int currentPosition;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        private SavedState(Parcel in) {
            super(in);
            currentPosition = in.readInt();
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            dest.writeInt(currentPosition);
        }

        public static final Creator<SavedState> CREATOR = new Creator<SavedState>() {
            @Override
            public SavedState createFromParcel(Parcel in) {
                return new SavedState(in);
            }

            @Override
            public SavedState[] newArray(int size) {
                return new SavedState[size];
            }
        };
    }


    /**
     * 直接调用选择position方法
     * @param position
     */
    public void selectPosition(int position){
        currentPosition = position;
        selectedPosition = position;
        updateTabStyles();
    }
    public interface TabSelectListener{
	    void onTabSelct(int position);
    }

    public void setTabSelectListener(TabSelectListener tabSelectListener){

        this.tabSelectListener = tabSelectListener;
    }

    public interface RedPointVisiablity{
        boolean show(int position);     //红点是否显示

        String title(int position);
    }

    public void setRedPointVisiablity(RedPointVisiablity redPointVisiablity) {
        this.redPointVisiablity = redPointVisiablity;
    }
}
