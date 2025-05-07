package com.github.jspxnet.network.http;

import com.github.jspxnet.json.JSONObject;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.HttpResponse;
import org.apache.http.ParseException;
import java.io.File;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ChenYuan on 2017/5/24.
 * http适配接口
 */
public interface HttpClient {

    boolean isUseProxy();

    void setUseProxy(boolean useProxy);

    String getProxyHost();

    void setProxyHost(String proxyHost);

    int getProxyPort();

    void setProxyPort(int proxyPort);

    String getEncode();

    void setEncode(String encode);

    int getBufferSize();

    void setBufferSize(int bufferSize);

    int getConnectionTimeout();

    void setConnectionTimeout(int connectionTimeout);

    int getReadTimeout();

    void setReadTimeout(int readTimeout);

    CookieStore getCookieStore();

    HttpClient build();

    HttpResponse getHttpResponse(String url, Map<String, ?> parameterMap, Map<String, String> headers) throws Exception;


    boolean download(File file,Map<String,Object> map) throws Exception;


    String upload(File[] files, String name, Object params);

    boolean download(File file, JSONObject json) throws Exception;


    void close();

    void setUrl(String url);

    HttpClient build(String url);

    int getStatusCode();

    String get(JSONObject json) throws Exception;

    String get(JSONObject json, Map<String, String> headers) throws Exception;

    org.apache.hc.core5.http.HttpEntity get(String url, JSONObject json, Map<String, String> headers) throws Exception;

    String getString(String url, Map<String, ?> parameterMap, Map<String, String> headers) throws Exception;

    String getString(String url, Map<String, ?> parameterMap) throws Exception;

    String getString(String url) throws Exception;

    String getString() throws Exception;

    String getString(Map<String, Object> parameterMap) throws Exception;

    byte[] getBytes(String url, Map<String, ?> parameterMap) throws Exception;

    byte[] getBytes(String url, Map<String, ?> parameterMap, Map<String, String> headers) throws Exception;

    String getResponseString() throws Exception;

    String post() throws Exception;
    /**
     *
     * @param files 文件
     * @param name 文件变量名
     * @return  返回信息
     */
    String upload(File[] files, String name);

    org.apache.hc.core5.http.HttpEntity put(String url, Map<String, ?> params, Map<String, String> headers) throws Exception;

    org.apache.hc.core5.http.HttpEntity put(JSONObject json, Map<String, String> headers) throws Exception;

    String put(JSONObject json) throws Exception;

    org.apache.hc.core5.http.HttpEntity post(String url, Map<String, ?> params, Map<String, String> headers) throws Exception;

    String post(String url, Map<String, ?> params) throws Exception;

    String post(Map<String, ?> params) throws Exception;

    void setHeaders(Map<String, String> header);

    void cleanHeaders();

    org.apache.hc.core5.http.HttpEntity post(String url, JSONObject json, Map<String, String> headers) throws Exception;

    HttpEntity put(String url, JSONObject json, Map<String, String> headers) throws Exception;

    String post(String url, JSONObject json) throws Exception;

    String post(JSONObject json) throws Exception;

    org.apache.hc.core5.http.HttpEntity put(String url, String body, Map<String, String> headers) throws Exception;

    String post(String url, String body) throws Exception;

    org.apache.hc.core5.http.HttpEntity post(String url, String body, Map<String, String> headers) throws Exception;

    String post(String body) throws Exception;
}
