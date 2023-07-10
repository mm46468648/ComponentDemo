package com.mooc.commonbusiness.net;

import android.annotation.SuppressLint;

import com.mooc.common.global.AppGlobals;
import com.mooc.commonbusiness.utils.CerUtil;
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

public class SSLSocketFactoryEx {

    public SSLSocketFactory getSSLSocketFactoryForOneWay() {

        try {
            CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");


            InputStream inputStream;
            inputStream = Objects.requireNonNull(AppGlobals.INSTANCE.getApplication())
                    .getAssets().open("learning2022.crt");
//            //是否需要切换证书 每年9月21日 02:00更换
//            if (CerUtil.getInstance().isChangeCertificate()) {
//                inputStream = Objects.requireNonNull(AppGlobals.INSTANCE.getApplication())
//                        .getAssets().open("learning2022.crt");
//            } else {
//                inputStream = Objects.requireNonNull(AppGlobals.INSTANCE.getApplication())
//                        .getAssets().open("learningCer.crt");
//            }

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
        public void checkClientTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {

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