package com.github.jspxnet.network.http;

import com.github.jspxnet.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.CookieStore;

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

    HttpResponse getHttpResponse(String url, Map<String, Object> parameterMap, Map<String, String> headers) throws Exception;


    boolean download(File file,Map map) throws Exception;

    boolean download(File file,JSONObject json) throws Exception;

    void setUrl(String url);

    HttpClient build(String url);

    int getStatusCode() throws ParseException;

    String getString(String url, Map<String, Object> parameterMap, Map<String, String> headers) throws Exception;

    String getString(String url, Map<String, Object> parameterMap) throws Exception;

    String getString(String url) throws Exception;

    String getString() throws Exception;

    String getString(Map<String, Object> parameterMap) throws Exception;

    byte[] getBytes(String url, Map<String, Object> parameterMap) throws Exception;

    byte[] getBytes(String url, Map<String, Object> parameterMap, Map<String, String> headers) throws Exception;

    String getResponseString() throws ParseException, IOException;

    String post() throws Exception;

    String upload(File[] files, String name);

    HttpEntity put(String url, Map<String, String> params, Map<String, String> headers) throws ParseException, IOException;

    HttpEntity put(String url, JSONObject json, Map<String, String> headers) throws Exception;

    String put(String url, JSONObject json) throws Exception;

    HttpEntity post(String url, Map<String, String> params, Map<String, String> headers) throws ParseException, IOException;

    String post(String url, Map<String, String> params) throws ParseException, IOException;

    String post(Map<String, ?> params) throws ParseException, IOException;

    void setHeaders(Map<String, String> header);

    void cleanHeaders();

    HttpEntity post(String url, JSONObject json, Map<String, String> headers) throws Exception;

    String post(String url, JSONObject json) throws Exception;

    String post(JSONObject json) throws Exception;

    String post(String url, String body) throws ParseException, IOException;

    HttpEntity post(String url, String body, Map<String, String> headers) throws ParseException, IOException;

    String post(String body) throws ParseException, IOException;
}
