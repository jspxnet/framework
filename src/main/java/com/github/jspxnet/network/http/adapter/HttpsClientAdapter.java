package com.github.jspxnet.network.http.adapter;


import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.security.NullX509TrustManager;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManager;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.client5.http.socket.ConnectionSocketFactory;
import org.apache.hc.client5.http.socket.PlainConnectionSocketFactory;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.config.Registry;
import org.apache.hc.core5.http.config.RegistryBuilder;

import javax.net.ssl.*;
import java.io.*;
import java.security.*;

/**
 * Created by ChenYuan on 2017/5/23.
 * modify 2024/06/08
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

            try (FileInputStream inStream = new FileInputStream(certPath)) {
                //指定PKCS12的密码
                keyStore.load(inStream, partnerId.toCharArray());
            }
            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(keyStore, certSecret.toCharArray());
            //创建管理jks密钥库的x509密钥管理器，用来管理密钥，需要key的密码
            sslContext = SSLContext.getInstance("TLSv1");
            // 构造SSL环境，指定SSL版本为3.0，也可以使用TLSv1，但是SSLv3更加常用。
            sslContext.init(kmf.getKeyManagers(), null, null);
        }

        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("http", PlainConnectionSocketFactory.INSTANCE)
                .register("https", new SSLConnectionSocketFactory(sslContext))
                .build();

        PoolingHttpClientConnectionManager connManager = new PoolingHttpClientConnectionManager(socketFactoryRegistry);

        if (useProxy) {
            //设置代理IP、端口、协议（请分别替换）
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);
            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            //把代理设置到请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom().build();
            ///实例化CloseableHttpClient对象
            httpClient = HttpClients.custom()
                    .setDefaultRequestConfig(defaultRequestConfig).setConnectionManager(connManager)
                    .setRoutePlanner(routePlanner)
                    .setDefaultCookieStore(cookieStore).build();
        } else {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).setConnectionManager(connManager).build();
        }
        return this;
    }
/*
    public static void main(String[] args) throws Exception {
        String url = "https://www.wosign.com";
        HttpClient httpClient = new HttpsClientAdapter();
        String out = httpClient.build().getString(url);
        System.out.println(out);
    }*/
}
