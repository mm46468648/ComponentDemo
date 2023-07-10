package com.mooc.commonbusiness.utils;

/**
 * 防连点
 */
public class ClickFilterUtil {

    static long lastClickTime = 0;
    public static boolean canClick() {

        long time = System.currentTimeMillis();
        long timeD = time - lastClickTime;
        if (0 < timeD && timeD < 1000) {
            return false;
        }
        lastClickTime = time;
        return true;
    }
}
