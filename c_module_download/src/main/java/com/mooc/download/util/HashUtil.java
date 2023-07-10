package com.mooc.download.util;

/**
 * 根据字符串获取唯一的id
 */
public class HashUtil {
    public static long longHash(String string) {
        long h = 98764321261L;
        int l = string.length();
        char[] chars = string.toCharArray();

        for (int i = 0; i < l; i++) {
            h = 31*h + chars[i];
        }
        return h;
    }
}
