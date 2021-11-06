package com.github.jspxnet.network.http.adapter;

import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.security.NullX509TrustManager;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import javax.net.ssl.*;
import java.io.*;
import java.security.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * Created by ChenYuan on 2017/5/23.
 */
public class HttpsClientAdapter extends HttpClientAdapter implements HttpClient {
    private static final String DEFAULT_PASSWORD = "changeit";

    public HttpsClientAdapter() {

    }

    @Override
    public HttpClient build(String url)  {
        KeyStore keyStore = null;
        String domain = URLUtil.getHostNameAndPort(url);
        try {

            keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
            try (FileInputStream instream = new FileInputStream(new File(HttpClientFactory.getJdkSecurityCertFile()))) {
                keyStore.load(instream, DEFAULT_PASSWORD.toCharArray());
            }
            boolean needCert = keyStore.containsAlias(domain);
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
            }
            else
            {
                TrustManager[] tm = { new SslX509TrustManager(keyStore)};
                //创建管理jks密钥库的x509密钥管理器，用来管理密钥，需要key的密码
                sslContext = SSLContext.getInstance("TLS");
                // 构造SSL环境，指定SSL版本为3.0，也可以使用TLSv1，但是SSLv3更加常用。
                sslContext.init(null, tm, new java.security.SecureRandom());
            }
        } catch (Exception e)
        {
            e.printStackTrace();
            try {
                installCert( keyStore, domain,DEFAULT_PASSWORD);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        super.build(url);
        return this;
    }



    public static void installCert(KeyStore keyStore,String domain,String passphrase) throws Exception {

        if (keyStore==null)
        {
            return;
        }
        if (keyStore.containsAlias(domain))
        {
            return;
        }
        if (StringUtil.isEmpty(passphrase))
        {
            passphrase =  "changeit";
        }

        SSLContext context = SSLContext.getInstance("TLS");
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(keyStore);
        X509TrustManager defaultTrustManager = (X509TrustManager) tmf.getTrustManagers()[0];
        SavingTrustManager tm = new SavingTrustManager(defaultTrustManager);
        context.init(null, new TrustManager[]{tm}, null);
        SSLSocketFactory factory = context.getSocketFactory();

        int port = 443;
        String host = domain;
        if (domain.contains(StringUtil.COLON))
        {
            host = StringUtil.substringBefore(domain,StringUtil.COLON);
            port = StringUtil.toInt(StringUtil.substringAfter(domain,StringUtil.COLON));
        }
        System.out.println("Opening connection to " + host + ":" + port + "...");

        SSLSocket socket = (SSLSocket) factory.createSocket(host, port);
        socket.setSoTimeout(10000);
        try {
            System.out.println("Starting SSL handshake...");
            socket.startHandshake();
            socket.close();
            System.out.println("No errors, certificate is already trusted");
        } catch (SSLException e) {
            System.out.println();
            e.printStackTrace(System.out);
        }

        X509Certificate[] chain = tm.chain;
        if (chain == null) {
            System.out.println("Could not obtain server certificate chain");
            return;
        }

        //chain 过来会有多个证书,默认这里放入第一个
        X509Certificate cert = chain[0];
        keyStore.setCertificateEntry(domain, cert);
        OutputStream out = new FileOutputStream(HttpClientFactory.getJdkSecurityCertFile());
        keyStore.store(out, passphrase.toCharArray());
        out.close();
    }

/*    private static final char[] HEXDIGITS = "0123456789abcdef".toCharArray();

    private static String toHexString(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 3);
        for (int b : bytes) {
            b &= 0xff;
            sb.append(HEXDIGITS[b >> 4]);
            sb.append(HEXDIGITS[b & 15]);
            sb.append(' ');
        }
        return sb.toString();
    }*/

    private static class SavingTrustManager implements X509TrustManager {

        private final X509TrustManager tm;
        private X509Certificate[] chain;

        SavingTrustManager(X509TrustManager tm) {
            this.tm = tm;
        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            throw new UnsupportedOperationException();
        }

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            throw new UnsupportedOperationException();
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            this.chain = chain;
            tm.checkServerTrusted(chain, authType);
        }
    }

    public static void main(String[] args) throws Exception {

        String url = "https://www.baidu.com";
        HttpsClientAdapter httpClient = new HttpsClientAdapter();

        String out = httpClient.build(url).getString(url);
        System.out.println(out);


        /*
        String domain = URLUtil.getHostNameAndPort(url);
        KeyStore keyStore = KeyStore.getInstance(KeyStore.getDefaultType());
        try (FileInputStream instream = new FileInputStream(new File(HttpClientFactory.getJdkSecurityCertFile()))) {
            //指定PKCS12的密码
            keyStore.load(instream, "changeit".toCharArray());
        }

        HttpsClientAdapter.installCert(keyStore,domain,null);
        */




    }
}
