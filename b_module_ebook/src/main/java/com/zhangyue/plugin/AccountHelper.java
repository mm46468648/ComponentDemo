package com.zhangyue.plugin;

import com.mooc.commonbusiness.global.GlobalsUserManager;
import com.ireader.plug.api.ZYReaderPluginApi;

/**
 * 宿主帐号信息管理类
 *
 * @author jdxu
 * @date 2019-09-12 16:39
 */
public class AccountHelper {
    /**
     * 掌阅分配给宿主的企业ID
     */
    public static String sPlatform = "100401";
    /**
     * 拓展字段，需要时会告知如何使用，默认为空就好
     */
    public static String sAccountExt = "";
    /**
     * 该key由掌阅提供给贵方，利用其对登录信息加密，由服务端解密以确认身份，防止被盗刷。请妥善保管，防止被其他人拿到该KEY
     */
    public static String sKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJmf5q38+MAtskKJlLxtHQwcCejHyHYTTXZK+HclB/XkN6mNoUnL3Bhk1gZ+1pwYUXYrvXnZsl55LwZU/1yIShV0X5HmqAUN7g2KcUH62tNnNqjdpnSInYEoNAvPPGSid/0P4wFsg5toUKPiRvpqaPMztNXTWaeEjfWYo45iHJ1/AgMBAAECgYEAkqc+DMxZHwTAjppOXc6AE0ZVA15s9KMaqJmLNhoozkjOBlOJLCyrcLIaStscvqjMY1YALGlwyJVBGfdcS5ZjuTkEuSUd9l4+qbvg3f7GGq5LLV684KmGpdlMG273FyV6zNgqiSmtNEA8ZF5EXXOLUy5Nnd4xSdV8vZHdwZ09D4kCQQDa2sWc5n6Mx2w1cpqfb/tclXhfCoQAPRsfG7AbsoGGO6AwAI8Yxv33EPS2RBH/98AIxLenLMLPrWjc5A6qInZrAkEAs7LjYiBxUewe3Uv3VZLhjKZKDo5jxk9WiBlVSwvdeCfpB5QKcytHsw06ncImqF2OT8pLw9gcD/bK/i2laLCyPQJAWQIs6LkdsufQbJRlqn6Cvo7T2+OM+APiKe662yjoYM2TQrKXgD4+P+OXgyGAKJh8c5R+FPGxcYFXaq8d/7fwbQJAOxmQcwtceacAl6OOWtSN3aYrIRJveh9JxEUgqifi1Mu7dkSYEzyeviKRrqTV9fWfAlr0BBslT5LJPENi7UV/uQJAOBbb0/Strg8XQEWC6+MpIiVs94wJSydQ4WGj4GC9ZqBpCXJehS8uYQ34Dr9AkbyNPBMqwOqQRng7xkELRGRyRw==";

    public static String getToken() {
        /**
         * 宿主用户唯一标识
         */
        String sUid = GlobalsUserManager.INSTANCE.getUid();
        return ZYReaderPluginApi.getToken(sKey, sUid, sPlatform);
    }

}
