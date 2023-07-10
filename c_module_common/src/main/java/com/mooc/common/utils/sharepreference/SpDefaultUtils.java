package com.mooc.common.utils.sharepreference;

import com.mooc.common.global.AppGlobals;

/**
 * 系统默认sp存储位置
 * _preferences
 */
public class SpDefaultUtils extends BaseSPreferenceUtils{
    private static SpDefaultUtils spUserUtils;

    private SpDefaultUtils() {
        super(AppGlobals.INSTANCE.getApplication(),  AppGlobals.INSTANCE.getApplication().getPackageName() + "");
    }

    public  static synchronized SpDefaultUtils getInstance() {
        if (spUserUtils == null) {
            spUserUtils = new SpDefaultUtils();
        }
        return spUserUtils;
    }
}
