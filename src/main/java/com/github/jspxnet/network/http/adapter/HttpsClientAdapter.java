package com.github.jspxnet.network.http.adapter;

import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.security.NullX509TrustManager;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClients;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;

/**
 * Created by ChenYuan on 2017/5/23.
 */
public class HttpsClientAdapter extends HttpClientAdapter implements HttpClient {
    public HttpsClientAdapter() {

    }

    public HttpClient build(boolean needCert, String partnerId, String certPath, String certSecret) throws Exception {

        SSLContext sslContext;
        //不需要维修证书，则使用默认证书
        if (!needCert) {
            //创建https请求证书
            TrustManager[] tm = {new NullX509TrustManager()};
            //创建证书上下文对象
            sslContext = SSLContext.getInstance("SSL", "SunJSSE");
            //初始化证书信息
            sslContext.init(null, tm, new java.security.SecureRandom());
            // 从上述SSLContext对象中得到SSLSocketFactory对象
        } else {
            //指定读取证书格式为PKCS12
            KeyStore keyStore = KeyStore.getInstance("PKCS12");
            //读取本机存放的PKCS12证书文件

            try (FileInputStream instream = new FileInputStream(new File(certPath))) {
                //指定PKCS12的密码
                keyStore.load(instream, partnerId.toCharArray());
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, certSecret.toCharArray());
            //创建管理jks密钥库的x509密钥管理器，用来管理密钥，需要key的密码
            sslContext = SSLContext.getInstance("TLSv1");
            // 构造SSL环境，指定SSL版本为3.0，也可以使用TLSv1，但是SSLv3更加常用。
            sslContext.init(kmf.getKeyManagers(), null, null);
        }

        if (useProxy) {
            //设置代理IP、端口、协议（请分别替换）
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
            //把代理设置到请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
            ///实例化CloseableHttpClient对象
            httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setSSLContext(sslContext).setDefaultCookieStore(cookieStore).build();
        } else {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).setSSLContext(sslContext).build();
        }

        return this;
    }


    public static void main(String[] args) throws Exception {
        String url = "https://www.wosign.com";
        HttpClient httpClient = new HttpsClientAdapter();
        String out = httpClient.build().getString(url);
        System.out.println(out);
    }
}
