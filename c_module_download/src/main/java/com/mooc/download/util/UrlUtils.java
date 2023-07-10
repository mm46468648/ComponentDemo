package com.mooc.download.util;

import java.util.Map;

public class UrlUtils {

    public static String getUrlParamsByMap(String url,Map<String, Object> map) {
        if (map == null) {
            return "";
        }

        StringBuffer sb = new StringBuffer(url.contains("?")? "&" : "?");
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            sb.append(entry.getKey() + "=" + entry.getValue());
            sb.append("&");
        }
        String s = sb.toString();
        if (s.endsWith("&")) {
            s = s.substring(0, s.length() - 1);
        }
        return s;
    }
}
