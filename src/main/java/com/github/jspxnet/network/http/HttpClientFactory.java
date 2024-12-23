package com.github.jspxnet.network.http;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.http.adapter.HttpClientAdapter;
import com.github.jspxnet.network.http.adapter.HttpsClientAdapter;
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
     * 简化调用
     * @param url url地址
     * @return 返回数据
     * @throws Exception 异常
     */
    public static String getHttp(String url) throws Exception {
        HttpClient client = null;
        try
        {
            client = createHttpClient(url);
            return client.getString();
        } finally {
            assert client != null;
            client.close();
        }
    }

    /**
     * 简化调用
     * @param url url地址
     * @return 返回数据
     * @throws Exception 异常
     */
    public static String postHttp(String url) throws Exception {
        HttpClient client = null;
        try
        {
            client = createHttpClient(url);
            return client.post();
        } finally {
            assert client != null;
            client.close();
        }
    }

/*

    public static void main(String[] args) throws Exception {



        File file1 = new File("d:\\logs\\logs1.zip");
        File file2 = new File("d:\\logs\\info-2024-06-08.zip");
        testUpload(new File[]{file1,file2});


    //  File file = new File("d:\\logs\\test.pdf");
        //testDownload(file);
        testGet();
    }

    public static void testDownload(File file) throws Exception {
        HttpClient httpClient = createHttpClient("https://dlj.51fapiao.cn/dlj/v7/downloadFile/74b01db72c54f088a66d7724a00d5b9d5cc251");
        boolean b = httpClient.download(file,new JSONObject());
        System.out.println("b=" + b);
        httpClient.close();
    }

    public static void testGet() throws Exception {
        HttpClient httpClient = createHttpClient("http://www.jspx.net/jcms/htdoc/refresh.jhtml");
        String out = httpClient.getString();
        System.out.println("b=" + out);
        httpClient.close();
    }

    public static void testUpload(File[] files) throws Exception {

        HttpClient httpClient = createHttpClient("http://127.0.0.1:8089/k3business/upload.jwc");

        //efffc257c504507942c82a3e34832059
        long currentTimeMillis =  System.currentTimeMillis();
        Map<String,String> valueMap  = new HashMap<>();
        String signature = EncryptUtil.getMd5("efffc257c504507942c82a3e34832059" + currentTimeMillis);
        valueMap.put("timestamp",currentTimeMillis +"");
        valueMap.put("signature",signature);

        String out = httpClient.upload(files,"file",valueMap);
        System.out.println("out=" + out);
        httpClient.close();
    }*/
}
