package com.mooc.course.binding;

import android.text.TextUtils;
import android.widget.TextView;

import androidx.databinding.BindingAdapter;

import com.mooc.commonbusiness.utils.format.TimeFormatUtil;
import com.mooc.course.R;

import java.text.SimpleDateFormat;
import java.util.Date;

public class XtCourseDetialBindings {

    public static final String CLASS_NOT_START_TIME = "3000-01-01";   //如果报名时间是这个,则代表报名未开始
    public static final String CLASS_TIME_NOT_SURE = "开课时间待定";

    /**
     * 新学堂开课时间
     *
     * @param tv
     * @param startTime
     * @param endTime
     */
    public static void courseDuration(TextView tv, String startTime, String endTime) {
        StringBuilder stringBuilder = new StringBuilder();
        String startStr = "";
        if (!TextUtils.isEmpty(startTime) && !"0".equals(startTime)) {
            startStr = formatDate(startTime);
            stringBuilder.append(startStr);
        } else {
            stringBuilder.append("-");
        }
        stringBuilder.append("-");
        if (!TextUtils.isEmpty(endTime) && !"0".equals(startTime)) {
            stringBuilder.append(formatDate(endTime));
        }
        tv.setText(stringBuilder.toString());

        //如果开课时间是3000-01-01,设置成开课时间待定
        if (startStr.equals(XtCourseDetialBindings.CLASS_NOT_START_TIME)) {
            tv.setText(XtCourseDetialBindings.CLASS_TIME_NOT_SURE);
        }

    }


    /**
     * 特殊转换需求，需要将后端返回的数据乘1000
     *
     * @param dateStr
     * @return
     */
    private static String formatDate(String dateStr) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateStr = simpleDateFormat.format(new Date(Long.parseLong(dateStr) * 1000));
        } catch (Exception ignored) {

        }

        return dateStr;
    }


}
