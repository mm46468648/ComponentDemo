package com.mooc.battle.util;

import com.mooc.battle.UserApi;
import com.mooc.commonbusiness.net.ApiService;

public class RequestUtil {

    private static UserApi userApi;

    public static UserApi getUserApi() {
        if (userApi == null) {
            synchronized (UserApi.class) {
                if (userApi == null) {
                    userApi = ApiService.getRetrofit().create(UserApi.class);
                }
            }
        }
        return userApi;
    }
}
