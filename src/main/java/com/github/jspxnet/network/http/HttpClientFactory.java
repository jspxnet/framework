package com.github.jspxnet.network.http;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.http.adapter.HttpClientAdapter;
import com.github.jspxnet.network.http.adapter.HttpsClientAdapter;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public abstract class HttpClientFactory {
    public static final String HTTPS = "https";
    public static final String HTTP = "http";
    static private final Map<String, String> rocHeaders = new HashMap<>();
    static private final Map<String, String> rocSecretHeaders = new HashMap<>();

    static {
        rocHeaders.put("Charset", "UTF-8");
        rocHeaders.put("Content-Type", "application/json; charset=UTF-8");
        rocHeaders.put("X-Requested-With", Environment.jspxNetRoc);
        rocHeaders.put("accept", "application/json");

        rocSecretHeaders.put("Charset", "UTF-8");
        rocSecretHeaders.put("Content-Type", "application/json; charset=UTF-8");
        rocSecretHeaders.put("X-Requested-With", "jspx.net-" + Environment.rocSecret);
        rocSecretHeaders.put("accept", "application/json");

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

        client.setHeaders(rocHeaders);
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
        client.setHeaders(rocSecretHeaders);
        return client;
    }

    public static String doHttp(String url, String postOrGet, String conf) throws Exception {
        Map<String, Object> param = new HashMap<>();
        if (StringUtil.isJsonObject(conf)) {
            JSONObject jsonObject = new JSONObject(conf);
            jsonObject.toMap();
        } else {
            StringMap map = new StringMap();
            map.setString(conf);
            param.putAll(map);
        }
        HttpClient client = createHttpClient(url);
        if (postOrGet.toLowerCase().contains("post")) {
            return client.getString(param);
        } else {
            return client.post(param);
        }
    }

}
