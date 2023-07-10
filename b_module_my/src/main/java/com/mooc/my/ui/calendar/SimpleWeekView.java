package com.mooc.my.ui.calendar;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.haibin.calendarview.Calendar;
import com.haibin.calendarview.CalendarUtil;
import com.haibin.calendarview.WeekView;

/**
 * 简单周视图
 * Created by huanghaibin on 2017/11/29.
 */

public class SimpleWeekView extends WeekView {
    private int mRadius;

    private Paint mOutRangePaint = new Paint();     //超出时间范围的日期

    public SimpleWeekView(Context context) {
        super(context);

        mOutRangePaint.setColor(Color.parseColor("#D4D4D4"));
        mOutRangePaint.setTextSize(CalendarUtil.dipToPx(context, TEXT_SIZE));


    }

    @Override
    protected void onPreviewHook() {
        mRadius = Math.min(mItemWidth, mItemHeight) / 5 * 2;
        mSchemePaint.setStyle(Paint.Style.STROKE);

        //当天文字改为绿色
        mCurDayTextPaint.setColor(Color.parseColor("#10955b"));

    }

    @Override
    protected boolean onDrawSelected(Canvas canvas, Calendar calendar, int x, boolean hasScheme) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;

        mSelectedPaint.setColor(Color.parseColor("#10955b"));
        mSelectedPaint.setStyle(Paint.Style.STROKE);

        canvas.drawCircle(cx, cy, mRadius, mSelectedPaint);
        return false;
    }

    @Override
    protected void onDrawScheme(Canvas canvas, Calendar calendar, int x) {
        int cx = x + mItemWidth / 2;
        int cy = mItemHeight / 2;
        canvas.drawCircle(cx, cy, mRadius, mSchemePaint);
    }

    @Override
    protected void onDrawText(Canvas canvas, Calendar calendar, int x, boolean hasScheme, boolean isSelected) {
        float baselineY = mTextBaseLine;
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
                            calendar.isCurrentMonth() ? mSchemeTextPaint : mSchemeTextPaint);

        } else {

            boolean inRange = isInRange(calendar);

            if(inRange){
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                        calendar.isCurrentDay() ? mCurDayTextPaint :
                                calendar.isCurrentMonth() ? mCurMonthTextPaint : mCurMonthTextPaint);
            }else {
                canvas.drawText(String.valueOf(calendar.getDay()), cx, baselineY,
                        mOutRangePaint);
            }

        }
    }
}
