package com.mooc.common.utils.sharepreference;

import android.content.Context;


public class BaseSPreferenceUtils {

//    private SharedPreferences pre;
    SpUtils spUtils;

    public BaseSPreferenceUtils(Context c, String fileName) {
//        pre = c.getSharedPreferences(fileName, c.MODE_PRIVATE);

        spUtils = SpUtils.get();
    }

    public void putString(String name, String content) {
//        SharedPreferences.Editor edit = pre.edit();
//        edit.putString(name, content).apply();

        spUtils.putValue(name,content);

    }

    public String getString(String name, String defaultValue) {
//        return pre.getString(name, defaultValue);
        return  spUtils.getValue(name,defaultValue);
    }

    public Float getFloat(String name, Float defaultValue) {
//        return pre.getFloat(name, defaultValue);
        return spUtils.getValue(name,defaultValue);
    }

    public void putBoolean(String name, boolean value) {
//        SharedPreferences.Editor edit = pre.edit();
//        edit.putBoolean(name, value).apply();
        spUtils.putValue(name,value);

    }

    public boolean getBoolean(String name, boolean defaultValue) {
//        return pre.getBoolean(name, defaultValue);
        return spUtils.getValue(name,defaultValue);

    }

    public int getInteger(String name, int defaultValue) {
//        return pre.getInt(name, defaultValue);
        return spUtils.getValue(name,defaultValue);

    }

    public void putInteger(String name, int value) {
//        SharedPreferences.Editor edit = pre.edit();
//        edit.putInt(name, value).apply();
        spUtils.putValue(name,value);

    }

    public long getLong(String name, long defaultValue) {
//        return pre.getLong(name, defaultValue);
        return spUtils.getValue(name,defaultValue);

    }

    public void putLong(String name, long value) {
//        SharedPreferences.Editor edit = pre.edit();
//        edit.putLong(name, value).apply();
        spUtils.putValue(name,value);

    }


    public void putFloat(String name, Float value) {
//        SharedPreferences.Editor edit = pre.edit();
//        edit.putFloat(name, value).apply();
        spUtils.putValue(name,value);

    }

    public void remove(String name) {
//        SharedPreferences.Editor edit = pre.edit();
//        edit.remove(name).apply();
        spUtils.remove(name);
    }


}
