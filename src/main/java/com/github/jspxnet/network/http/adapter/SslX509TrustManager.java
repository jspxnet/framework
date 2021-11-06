package com.github.jspxnet.network.http.adapter;

import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/6 23:23
 * description:
 *
 * 证书导入命令
 * keytool -import -alias ebay.cn -file d:\tmp\ebay.cn.cer -keystore cacerts
 *
 * keytool -import -alias club.autohome.com.cn:8080 -file d:\tmp\ebay.cn.cer -keystore cacerts
 *
 **/
public class SslX509TrustManager implements X509TrustManager {

    X509TrustManager trustManager;

    SslX509TrustManager(KeyStore ks) throws Exception {
        TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509", "SunJSSE");
        tmf.init(ks);
        TrustManager[] tms = tmf.getTrustManagers();
        for (TrustManager tm : tms) {
            if (tm instanceof X509TrustManager) {
                trustManager = (X509TrustManager) tm;
                return;
            }
        }
        throw new Exception("Couldn't initialize");
    }

    @Override
    public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            trustManager.checkClientTrusted(chain, authType);
        } catch (CertificateException excep) {
            excep.printStackTrace();
        }
    }

    @Override
    public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
        try {
            trustManager.checkServerTrusted(chain, authType);
        } catch (CertificateException excep) {
            excep.printStackTrace();
        }
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return trustManager.getAcceptedIssuers();
    }

}
