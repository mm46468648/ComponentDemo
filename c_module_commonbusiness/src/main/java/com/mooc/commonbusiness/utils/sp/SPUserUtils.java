package com.mooc.commonbusiness.utils.sp;

import android.content.Context;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.mooc.common.global.AppGlobals;
import com.mooc.common.utils.sharepreference.BaseSPreferenceUtils;
import com.mooc.commonbusiness.model.UserInfo;

/**
 * 用户信息sp
 * 存储文件sp_user
 */
public class SPUserUtils extends BaseSPreferenceUtils {
    private static SPUserUtils spUserUtils;
    private final static String FILE_NAME = "sp_user";
    public final static String KEY_USER_ID = "user_id";
    public final static String KEY_USER_TOKEN = "user_token";
    private final static String KEY_USER_NICKNAME = "user_nickname";
    private final static String KEY_USER_NAME = "user_name";
    private final static String KEY_USER_AVATAR = "user_avatar";
    private final static String KEY_USER_ISHOME = "user_ishome";
    private final static String KEY_LOGIN_TIMESTAMP = "login_timestamp";
    private final static String KEY_USER_DESC = "key_user_desc";
    private final static String KEY_USER_FANS = "key_user_fans";
    private final static String KEY_USER_FOLLOW = "key_user_follow";
    private final static String KEY_USER_IS_SIGN = "key_user_is_sign";
    private final static String KEY_NAME_IS_LEGAL = "key_name_is_legal";
    private final static String KEY_UUID = "key_uuid";


    //新版用户信息sp，直接保存json数据
    private final static String KEY_USERINFO_JSON = "key_userinfo_json";


    private SPUserUtils(Context c) {
        super(c, FILE_NAME);
    }

    public static SPUserUtils getInstance() {
        if (spUserUtils == null) {
            spUserUtils = new SPUserUtils(AppGlobals.INSTANCE.getApplication());
        }
        return spUserUtils;
    }


    public void saveUserInfo(UserInfo userBean) {
        if(null == userBean){
            putString(KEY_USERINFO_JSON, "");
            putString(KEY_USER_ID, "");
            return;
        }
        Gson gson = new Gson();
        putString(KEY_USERINFO_JSON, gson.toJson(userBean));
        putString(KEY_USER_ID, userBean.getId());
    }

    public UserInfo getUserInfo() {
        Gson gson = new Gson();

        //新版重构
        String userInfoJson = getString(KEY_USERINFO_JSON,"");
        if(!TextUtils.isEmpty(userInfoJson)){
            return gson.fromJson(userInfoJson,UserInfo.class);
        }

        if(TextUtils.isEmpty(getString(KEY_USER_ID, ""))){
            return null;
        }
        //新版如果没有，兼容一次旧版
        UserInfo userBean = new UserInfo("","","","","","","",false,0,0,false,"","","",null);
        userBean.setId(getString(KEY_USER_ID, ""));
        userBean.setToken(getString(KEY_USER_TOKEN, ""));
        userBean.setAvatar(getString(KEY_USER_AVATAR, ""));
        userBean.setNickname(getString(KEY_USER_NICKNAME, ""));
        userBean.setName(getString(KEY_USER_NAME, ""));
        userBean.setIntroduction(getString(KEY_USER_DESC, ""));
        userBean.setHome(getBoolean(KEY_USER_ISHOME, false));
        userBean.set_checkin(getBoolean(KEY_USER_IS_SIGN, false));
        userBean.setUser_follow_count(getInteger(KEY_USER_FOLLOW, 0));
        userBean.setUser_be_followed_count(getInteger(KEY_USER_FANS, 0));
        userBean.setCheck_name_result(getString(KEY_NAME_IS_LEGAL, "1"));
        userBean.setUuid(getString(KEY_UUID, ""));
        return userBean;
    }

}
