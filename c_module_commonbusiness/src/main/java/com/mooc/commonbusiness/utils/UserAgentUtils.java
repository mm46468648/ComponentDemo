package com.mooc.commonbusiness.utils;

import android.os.Build;
import android.text.TextUtils;

import com.mooc.common.utils.SystemUtils;
import com.mooc.commonbusiness.net.ApiService;
import com.mooc.common.utils.DebugUtil;

public class UserAgentUtils {

    /**
     * 获取useragent
     *
     * @return
     */
    public static String getUserAgent() {
        String strVersionName = SystemUtils.getVersionName();
        if (TextUtils.isEmpty(strVersionName)) {
            //TODO:替换当前app版本号
            strVersionName = "3.4.3.1";
        }
        //如果是调试模式，将版本改为2.8.5不加密模式
        //测试环境通过请求头加参数，这样能享受当前版本最新功能，正式环境如果抓包还是需要改为，2.8.5
        if (DebugUtil.isNoEncrypt && ApiService.BASE_URL.equals(ApiService.NORMAL_BASE_URL)) {
            strVersionName = "2.8.5";
        }
        String webUserAgent = "moocnd android/" + strVersionName + " (Linux; U; Android %s)";
        //不知道什么时候需要变为这个
//        webUserAgent = "moocnd android/" + strVersionName + " is_master_talk" + " (Linux; U; Android %s)";
//        Locale locale = Locale.getDefault();
        StringBuffer buffer = new StringBuffer();
        // Add version
        final String version = Build.VERSION.RELEASE;
        if (version.length() > 0) {
            buffer.append(version);
        } else {
            // default to "1.0"
            buffer.append(strVersionName);
        }
        if ("REL".equals(Build.VERSION.CODENAME)) {
            final String model = Build.MODEL;
            if (model.length() > 0) {
                buffer.append("; ");
                buffer.append(model);
            }
        }
        final String id = Build.ID;
        if (id.length() > 0) {
            buffer.append(" Build/");
            buffer.append(id);
        }
        return String.format(webUserAgent, buffer, "Mobile ");
    }
}
