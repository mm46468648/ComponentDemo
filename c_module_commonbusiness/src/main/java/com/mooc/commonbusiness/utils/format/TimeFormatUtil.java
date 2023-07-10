package com.mooc.commonbusiness.utils.format;

import android.annotation.SuppressLint;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * 时间相关格式化工具类
 */
public class TimeFormatUtil {

    private static final long ONE_DAY = 24 * 60 * 60 * 1000;
    private static final long ONE_HOUR = 60 * 60 * 1000;
    private static final int ONE_MIN = 60 * 1000;
    private static final int ONE_SECOND = 1000;


    /**
     * 格式化时间以（01天10时21分）格式返回
     *
     * @param l 时间戳
     * @return
     */
    public static String formatDayTime(long l) {
        long ms = l;
        StringBuilder sb = new StringBuilder();
        int day = (int) (ms / ONE_DAY);
        ms -= (day * ONE_DAY);
        int hour = (int) (ms / ONE_HOUR);
        ms -= (hour * ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        if (day < 10) {
            sb.append("0").append(day).append("天");
        } else {
            sb.append(day).append("天");
        }
        if (hour < 10) {
            sb.append("0").append(hour).append("时");
        } else {
            sb.append(hour).append("时");
        }

        if (min == 0) {
            sb.append("0分");
        } else if (min < 10) {
            sb.append("0").append(min).append("分");
        } else {
            sb.append(min).append("分");
        }

        return sb.toString();
    }


    public static final String MMdd = "MM.dd";
    public static final String yyyy_MM_dd = "yyyy-MM-dd";
    public static final String yyyyMMdd = "yyyyMMdd";
    public static final String yyyydMMddd = "yyyy.MM.dd";
    public static final String yyyyNMMY = "yyyy年MM月";
    public static final String yyyyNMMYddR = "yyyy年MM月dd日";
    public static final String yyyy_MM_dd_HH_mm = "yyyy-MM-dd HH:mm";
    public static final String yyyy_MM_dd_HH_mm_ss = "yyyy-MM-dd HH:mm:ss";
    public static final String yyyy_MM_dd_HH_mm_ss_Tzone = "yyyy-MM-dd'T'HH:mm:ss'Z'";   //带有时区的格式

    /**
     * @param time   时间戳
     * @param format 需要转换的格式
     * @return
     */
    public static String formatDate(long time, String format) {
        String formatStr = "";
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
            formatStr = simpleDateFormat.format(new Date(time));
        } catch (Exception e) {

        }

        return formatStr;
    }

    public static long getCurrentTime() {
//        int offset = TimeZone.getDefault().getRawOffset();
        return System.currentTimeMillis();
    }

    /**
     * Date 根据需要的格式转换为时间
     *
     * @param date
     * @param format
     * @return
     */
    public static String dateToString(Date date, String format) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(date);
    }

    /**
     * 根据时间格式转换为时间戳
     *
     * @param dateStr
     * @param pattern
     * @return
     */
    public static long getStringToLong(String dateStr, String pattern) {
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
        Date date = new Date();
        try {
            date = dateFormat.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        assert date != null;
        return date.getTime();
    }

    /**
     * 根据Date获取时间戳
     *
     * @param dateStr
     * @return
     */
    public static long getDateToLong(Date dateStr) {
        return dateStr.getTime();
    }

    /**
     * 格式化字符串类型的时间
     *
     * @param strDate    字符串
     * @param fromFormat 原格式
     * @param toFormat   转换成的格式
     * @return
     */
    public static String formatDateFromString(String strDate, String fromFormat, String toFormat) {
        String formatStr = "";
        try {
            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat source = new SimpleDateFormat(fromFormat);
            source.setTimeZone(TimeZone.getTimeZone("GMT"));
            SimpleDateFormat dest = new SimpleDateFormat(toFormat);
            Date date = new Date(source.parse(strDate).getTime());
            return dest.format(date);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return formatStr;
    }


    /**
     * 格式化音频播放进度
     *
     * @param ms
     * @return
     */
    public static String formatAudioPlayTime(long ms) {
        StringBuilder sb = new StringBuilder();
        int hour = (int) (ms / ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        int sec = (int) (ms % ONE_MIN) / ONE_SECOND;
        if (hour == 0) {
//			sb.append("00:");
        } else if (hour < 10) {
            sb.append("0").append(hour).append(":");
        } else {
            sb.append(hour).append(":");
        }
        if (min == 0) {
            sb.append("00:");
        } else if (min < 10) {
            sb.append("0").append(min).append(":");
        } else {
            sb.append(min).append(":");
        }
        if (sec == 0) {
            sb.append("00");
        } else if (sec < 10) {
            sb.append("0").append(sec);
        } else {
            sb.append(sec);
        }
        return sb.toString();
    }

    /**
     * 格式化电子书阅读时间
     * 小于1小时显示分钟
     *
     * @param minuteStr 分钟
     * @return
     */
    public static String formatEbookReadTime(int minuteStr) {
        String resultStr = "";

        if (minuteStr < 60) {
            resultStr = minuteStr + "分钟";
            return resultStr;
        }

        if (minuteStr % 60 == 0) {
            resultStr = minuteStr / 60 + "小时";
        } else {
            float i = (float) minuteStr / 60;
            resultStr = String.format("%.2f", i) + "小时";
        }
        return resultStr;
    }


    /**
     * 格式化学堂课程剩余回顾时间
     */

    public static String formatXtCourseTime(long l) {
        long ms = l;
        StringBuilder sb = new StringBuilder();
        int day = (int) (ms / ONE_DAY);
        ms -= (day * ONE_DAY);
        int hour = (int) (ms / ONE_HOUR);
        ms -= (hour * ONE_HOUR);
        int min = (int) ((ms % ONE_HOUR) / ONE_MIN);
        if (day < 10) {
            sb.append("0").append(day).append("天");
        } else {
            sb.append(day).append("天");
        }
        if (hour < 10) {
            sb.append("0").append(hour).append("时");
        } else {
            sb.append(hour).append("时");
        }

        if (min == 0) {
            sb.append("0分");
        } else if (min < 10) {
            sb.append("0").append(min).append("分");
        } else {
            sb.append(min).append("分");
        }

        return sb.toString();
    }

    public static String formatDateISO4YMMDD(String strDate) {
        @SuppressLint("SimpleDateFormat") SimpleDateFormat source = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        @SuppressLint("SimpleDateFormat") SimpleDateFormat dest = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = source.parse(strDate);
            return dest.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static String formatPlayCount(long count) {
        if (count == 0) {
            return "0";
        }
        String str = "";
        String n = String.valueOf(count);
        java.text.DecimalFormat df = new java.text.DecimalFormat("#.0");
        if (n.length() > 4) {
            double num = (double) count / 10000;
            str = df.format(num) + "万";
        } else if (n.length() == 4) {
            double num = (double) count / 1000;
            str = df.format(num) + "千";
        } else if (n.length() <= 3) {
            str = df.format(count);
        }

        return str;
    }


    public static String timeParse(long duration) {
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
}
