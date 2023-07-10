package com.mooc.common.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by max on 15/4/24
 */
public class RegexUtil {
    /**
     * 手机号正则验证
     *
     * @param mobiles 手机号码
     * @return true or false
     */
    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0-9])|(17[0，6，7，8]))\\d{8}$");
        Matcher m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 邮箱正则验证
     *
     * @param email 邮箱
     * @return true or false
     */
    public static boolean isEmail(String email) {
        String str = "^([a-zA-Z0-9]+[_|\\.|\\-]?)*[a-zA-Z0-9]+@([a-zA-Z0-9]+\\.?)*[a-zA-Z0-9]+\\.[a-zA-Z]{2,}$";
        Pattern p = Pattern.compile(str);
        Matcher m = p.matcher(email);
        return m.matches();
    }

    public static boolean isPassword(String pwd) {
        Pattern p = Pattern.compile("^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[~!@#$%^&*()_+`\\-={}:;'<>?,./]).{6,16}$");
        Matcher m = p.matcher(pwd);
        return m.matches();
    }

    /**
     * 校验身份证号
     *
     * @param idNO
     * @return
     */
    public static boolean isIDNumber(String idNO) {
        String str15 = "^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$";
        String str18 = "^[1-9]\\d{5}[1-9]\\d{3}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}(\\d|X|x)?$";
        Pattern p15 = Pattern.compile(str15);
        Pattern p18 = Pattern.compile(str18);
        Matcher m15 = p15.matcher(idNO);
        Matcher m18 = p18.matcher(idNO);
        return m15.matches() || m18.matches();
    }

    /**
     * 隐藏邮箱的部分字符
     *
     * @param origin 原始字符串
     * @return 隐藏后的字符串
     */
    public static String getEmailHideStr(String origin) {
        return origin.replaceAll("^(.{3})(.*)(@.*)$", "$1***$3");
    }

    /**
     * 隐藏手机号的部分字符
     *
     * @param origin 原始字符串
     * @return 隐藏后的字符串
     */
    public static String getMobileHideStr(String origin) {
        return origin.replaceAll("^(\\d{3})(.*)(\\d{4})$", "$1****$3");
    }

    /**
     * 隐藏姓名的部分字符
     *
     * @param origin 原始字符串
     * @return 隐藏后的字符串
     */
    public static String getNameHideStr(String origin) {
        return origin.replaceAll("^(.)(.*)$", "*$2");
    }


}
