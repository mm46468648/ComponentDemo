package com.mooc.my.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarUtil;
import com.haibin.calendarview.MonthView;

/**
 * 高仿魅族日历布局
 * Created by huanghaibin on 2017/11/15.
 */

public class SimpleMonthView extends MonthView {

    private int mRadius;

    private Paint mOutRangePaint = new Paint();     //超出时间范围的日期
//    private Paint mSelectPaint = new Paint();     //超出时间范围的日期



    public SimpleMonthView(Context context) {
        super(context);

        mOutRangePaint.setColor(Color.parseColor("#D4D4D4"));
        mOutRangePaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));


//        mSelectPaint.setColor(Color.parseColor("#10955b"));
//        mSelectPaint.setStyle(Paint.Style.STROKE);

    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        mSchemePaint.setStyle(Paint.Style.STROKE);

        //当天文字改为绿色
        mCurDayTextPaint.setColor(Color.parseColor("#10955b"));

    }

    @Override
    protected void onLoopStart(int x, int y) {

    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;

        mSelectedPaint.setColor(Color.parseColor("#10955b"));
        mSelectedPaint.setStyle(Paint.Style.STROKE);
        //选中画一个空心的绿色圆圈
        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x, int y) {
        int cx = x + mItemWidth / 2;
        int cy = y + mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, int y, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine + y;
        int cx = x + mItemWidth / 2;

        if (isSelected) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    mSelectTextPaint);
        } else if (hasScheme) {
            canvas.drawText(String.valueOf(calendar.getDay()),
                    cx,
                    baselineY,
                    calendar.isCurrentDay() ? mCurDayTextPaint :
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mOtherMonthTextPaint);

        } else {

            //判断是否在限制范围内
            boolean inRange = isInRange(calendar);

            if(inRange){
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                        calendar.isCurrentDay() ? mCurDayTextPaint :
                                calendar.isCurrentMonth() ? mCurMonthTextPaint : mOtherMonthTextPaint);
            }else {
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                        mOutRangePaint);
            }

        }
    }
}
