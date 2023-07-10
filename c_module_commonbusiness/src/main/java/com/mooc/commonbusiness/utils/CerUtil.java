package com.mooc.commonbusiness.utils;

import com.mooc.common.global.AppGlobals;

import java.io.IOException;
import java.io.InputStream;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import java.util.Objects;

/**
 * https证书相关
 */
public class CerUtil {

    private CerUtil() {
    }

    public static synchronized CerUtil getInstance() {
        return UtilHolder.instance;
    }


    private static class UtilHolder {
        private static final CerUtil instance = new CerUtil();
    }

    /**
     * 获取证书过期时间
     */
    public long getAuthenticationTime() {
        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
            InputStream inputStream;
            //是否需要切换证书
            if (isChangeCertificate()) {
                inputStream = Objects.requireNonNull(AppGlobals.INSTANCE.getApplication())
                        .getAssets().open("learning2022.crt");
            } else {
                inputStream = Objects.requireNonNull(AppGlobals.INSTANCE.getApplication())
                        .getAssets().open("learningCer.crt");
            }
            X509Certificate Cert = (X509Certificate) certificateFactory.generateCertificate(inputStream);//证书信息
//            Date dataBefore = Cert.getNotBefore();//证书开始时间
            Date dataAfter = Cert.getNotAfter();//证书过期时间
            return dataAfter.getTime();
        } catch (CertificateException | IOException e) {
            e.printStackTrace();
        }
        return 0;
    }


    //是否校验证书 true 不校验证书，false 校验证书
    public boolean isOrNoVerifyCertificate() {
        //用来试验 当前时间
//        long currentTime = TimeFormatUtil.getCurrentTime();
        //正式上线用服务器获取的时间
        long currentTime = ServerTimeManager.getInstance().getServiceTime();
        long cerOverdueTime = getAuthenticationTime();
        if (cerOverdueTime > 0) {
//            return currentTime >= cerOverdueTime;
            return true;
        } else {//获取证书过期时间异常，则不校验证书
            return true;
        }
    }

    /**
     * 是否需要切换证书
     */
    public boolean isChangeCertificate() {
        //用来试验 当前时间
//        long cerChangeTime = 1662520200000L;//2022年9月07日 11:10 证书切换时间
//        long currentTime = TimeFormatUtil.getCurrentTime();
        //正式上线用服务器获取的时间
        long cerChangeTime = 1663696800000L;//2022年9月21日 02:00 证书切换时间
        long currentTime = ServerTimeManager.getInstance().getServiceTime();
        return currentTime >= cerChangeTime;
    }

}
