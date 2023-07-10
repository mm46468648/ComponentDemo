
package com.mooc.common.utils;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

@SuppressLint("SimpleDateFormat")
public class TimeUtils {

    public static void main(String[] args) {
    }


    public static long getCurrentTime() {
//        int offset = TimeZone.getDefault().getRawOffset();
        return System.currentTimeMillis();
    }


    /**
     * 获得输入日期的星期
     *
     * @param inputDate 需要转换的日期 yyyy-MM-dd
     * @return 星期×
     */
    public static String getWeekDay(String inputDate) {
        String weekStrArr1[] = {"星期日", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = simpleDateFormat.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int outWeek = calendar.get(Calendar.DAY_OF_WEEK);// 返回的是1-7的整数，1为周日，2为周一，以此类推。
        return weekStrArr1[outWeek - 1];
    }



    /**
     * 判断是否是今天
     *
     * @param timestamp 时间戳.
     * @return true该时间点
     */
    public static boolean isTimeToday(String timestamp) {
        if (TextUtils.isEmpty(timestamp)) {
            return true;
        }
//        int offset = TimeZone.getDefault().getRawOffset();
        long client = System.currentTimeMillis();
        Date now = new Date(client);
        String nowString = DateUtil.DateToString(now, "yyyy-MM-dd");
        return timestamp.equals(nowString);
    }

    /*
     * 格式化播放时间  00:00  分钟为单位  duration：秒
     * */
    public static String timeParse(long duration) {
        if (duration == 0) {
            return "00:00";
        }
        String time = "";
        long minute = duration / 60;
        long seconds = duration % 60;
        if (minute < 10) {
            time += "0";
        }
        time += minute + ":";
        if (seconds < 10) {
            time += "0";
        }
        time += seconds;
        return time;
    }

    /**
     * @param min 秒
     * @return
     */
    public static String formatUnitWithMin(int min) {
//        if (min > 60 * 60 * 24 * 365) {
//            return "年";
//        }
//        if (min > 60 * 60 * 24 * 30) {
//            return "月";
//        }
//        if (min > 60 * 60 * 24) {
//            return "天";
//        }
        if (min > 60 * 60) {
            return "小时";
        }
        if (min > 60) {
            return "分钟";
        }
        return "秒";
    }
    @SuppressLint("DefaultLocale")
    public static String formatTimeWithMin(double min) {
        double time;
//        if (min > 60 * 60 * 24 * 30 * 365) {
//            time = min / 60 / 60 / 24 / 30 / 365;
//            return String.format("%.2f", time);
//        }
//        if (min > 60 * 60 * 24 * 30) {
//            time = min / 60 / 60 / 24 / 30;
//            return String.format("%.2f", time);
//        }
//        if (min > 60 * 60 * 24) {
//            time = min / 60 / 60 / 24;
//            return String.format("%.2f", time);
//        }
        if (min > 60 * 60) {
            time = min / 60 / 60;
            return String.format("%.2f", time);
        }
        if (min > 60) {
            time = min / 60;
            return String.format("%.2f", time);
        }
        return String.valueOf(min);
    }
}
