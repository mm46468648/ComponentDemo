package com.mooc.commonbusiness.net;

import android.annotation.SuppressLint;

import com.mooc.common.global.AppGlobals;
import com.mooc.commonbusiness.utils.ServerTimeManager;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.InputStream;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.security.SignatureException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Objects;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/**
 * 老域名证书Factory
 */
public class SSLSocketFactoryExOld {


    public SSLSocketFactory getSSLSocketFactoryForOneWay() {

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");

//            long currentServerTime = ServerTimeManager.getInstance().getServiceTime();//当前时间
//            //2022年3月10日凌晨新证书生效
//            long setTime = 1646838000000L;//2022年03月09日 23:00时间戳
//
//            if (currentServerTime != 0) {
//                if (currentServerTime >= setTime) {
//                    inputStream = Objects.requireNonNull(AppGlobals.INSTANCE.getApplication()).getAssets().open("oldCertificates2022.crt");
//                }
//            }
            InputStream inputStream = Objects.requireNonNull(AppGlobals.INSTANCE.getApplication()).getAssets().open("oldCertificates2022.crt");

            Certificate certificate = certificateFactory.generateCertificate(inputStream);
            inputStream.close();

            KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());

            keyStore.load(null, null);
            keyStore.setCertificateEntry("", certificate);

            //3.使用KeyStore初始化TrustManagerFactory
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);

            tmf.init(keyStore);

            SSLContext context = SSLContext.getInstance("TLSv1.2");

            context.init(null, tmf.getTrustManagers(), new SecureRandom());


            return context.getSocketFactory();

        } catch (Exception e) {

            e.printStackTrace();
            CrashReport.postCatchedException(e);
        }

        return null;

    }

    public X509TrustManager getTrustManager() {
        return new trustManager();
    }

    public static class trustManager implements X509TrustManager {

        @SuppressLint("TrustAllX509TrustManager")
        @Override
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) {

        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String s) throws CertificateException {
            for (X509Certificate cert : chain) {
                try {
                    cert.checkValidity();
                    cert.verify(cert.getPublicKey());
                } catch (NoSuchAlgorithmException | InvalidKeyException | NoSuchProviderException | SignatureException ignored) {

                }
            }
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            return new X509Certificate[0];
        }
    }


}