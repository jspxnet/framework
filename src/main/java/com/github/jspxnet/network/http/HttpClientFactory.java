package com.github.jspxnet.network.http;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.http.adapter.HttpClientAdapter;
import com.github.jspxnet.network.http.adapter.HttpsClientAdapter;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.SystemUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public abstract class HttpClientFactory {
    public static final String HTTPS = "https";
    public static final String HTTP = "http";
    static private final Map<String, String> ROC_HEADERS = new HashMap<>();
    static private final Map<String, String> ROC_SECRET_HEADERS = new HashMap<>();

    static {
        ROC_HEADERS.put("Charset", StandardCharsets.UTF_8.name());
        ROC_HEADERS.put("Content-Type", "application/json; charset=UTF-8");
        ROC_HEADERS.put("X-Requested-With", Environment.jspxNetRoc);
        ROC_HEADERS.put("accept", "application/json");

        ROC_SECRET_HEADERS.put("Charset", StandardCharsets.UTF_8.name());
        ROC_SECRET_HEADERS.put("Content-Type", "application/json; charset=UTF-8");
        ROC_SECRET_HEADERS.put("X-Requested-With", "jspx.net-" + Environment.rocSecret);
        ROC_SECRET_HEADERS.put("accept", "application/json");

    }

    public static HttpClient createHttpClient(String url) {
        if (url.startsWith(HTTPS)) {
            return new HttpsClientAdapter().build(url);
        }
        return new HttpClientAdapter().build(url);
    }

    public static HttpClient createRocHttpClient(String url) {
        HttpClient client;
        if (url.startsWith(HTTPS)) {
            client = new HttpsClientAdapter().build(url);
        } else {
            client = new HttpClientAdapter().build(url);
        }
        client.setHeaders(ROC_HEADERS);
        return client;
    }

    public static HttpClient createRocSecretHttpClient(String url) {
        HttpClient client;
        if (url.startsWith(HTTPS)) {
            client = new HttpsClientAdapter().build(url);
        } else {
            client = new HttpClientAdapter().build(url);
        }
        client.cleanHeaders();
        client.setHeaders(ROC_SECRET_HEADERS);
        return client;
    }

    /**
     *
     * @return 得到证书的保存路径,默认的保存文件名为 cacerts
     */
    public static String getJdkSecurityCertFile() {

        String path = System.getProperty("java.home") + File.separatorChar + "lib"+ File.separatorChar + "security";
        path = FileUtil.mendPath(path);
        File file = new File(path,"cacerts");
        if (file.isFile()&&file.canRead())
        {
            return file.getPath();
        }
        file = new File(path,"jssecacerts");
        return file.getPath();
    }


    /**
     * 简化调用
     * @param url url地址
     * @return 返回数据
     * @throws Exception 异常
     */
    public static String getHttp(String url) throws Exception {
        HttpClient client = createHttpClient(url);
        return client.getString();
    }

    /**
     * 简化调用
     * @param url url地址
     * @return 返回数据
     * @throws Exception 异常
     */
    public static String postHttp(String url) throws Exception {

        HttpClient client = createHttpClient(url);
        return client.post();
    }

}
