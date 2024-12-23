package com.github.jspxnet.network.http.adapter;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.security.asymmetric.AsyEncrypt;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.classic.methods.HttpPut;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.entity.mime.HttpMultipartMode;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.*;
import org.apache.hc.client5.http.impl.routing.DefaultProxyRoutePlanner;
import org.apache.hc.core5.http.*;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.io.CloseMode;
import org.apache.hc.core5.net.URIBuilder;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class HttpClientAdapter implements HttpClient {
    // OK: Success!
    public static final int SUCCESS = 200;
    protected org.apache.hc.client5.http.cookie.CookieStore cookieStore = new BasicCookieStore();
    protected CloseableHttpClient httpClient;
    protected boolean useProxy = false;
    protected String proxyHost = "127.0.0.1";
    protected int proxyPort = 8087;
    protected String encode = StandardCharsets.UTF_8.name();
    protected int bufferSize = 512;
    protected int connectionTimeout = 20000;
    protected int readTimeout = 20000;

    final private static String CREDENTIAL_CHARSET = "http.protocol.credential-charset";
    final private static String HTTP_CONTENT_CHARSET = "http.protocol.content-charset";

    //当前实体对象
    protected CloseableHttpResponse httpResponse;
    final private String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    protected final Map<String, String> defaultHeaders = new HashMap<>();

    @Getter
    protected String url;


    @Override
    public void close()
    {
        if (httpClient!=null)
        {
            try {
                cookieStore.clear();

                httpClient.close(CloseMode.IMMEDIATE);
            } catch (Exception e) {
                log.error(e.getMessage(),e);
            }
        }
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public boolean isUseProxy() {
        return useProxy;
    }

    @Override
    public void setUseProxy(boolean useProxy) {
        this.useProxy = useProxy;
    }

    @Override
    public String getProxyHost() {
        return proxyHost;
    }

    @Override
    public void setProxyHost(String proxyHost) {
        this.proxyHost = proxyHost;
    }

    @Override
    public int getProxyPort() {
        return proxyPort;
    }

    @Override
    public void setProxyPort(int proxyPort) {
        this.proxyPort = proxyPort;
    }

    @Override
    public String getEncode() {
        return encode;
    }

    @Override
    public void setEncode(String encode) {
        this.encode = encode;
    }

    @Override
    public int getBufferSize() {
        return bufferSize;
    }

    @Override
    public void setBufferSize(int bufferSize) {
        this.bufferSize = bufferSize;
    }

    @Override
    public int getConnectionTimeout() {
        return connectionTimeout;
    }

    @Override
    public void setConnectionTimeout(int connectionTimeout) {
        this.connectionTimeout = connectionTimeout;
    }

    @Override
    public int getReadTimeout() {
        return readTimeout;
    }

    @Override
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    @Override
    public HttpClient build() {
        return build(null);
    }

    @Override
    public HttpClient build(String url) {

        //HttpClients.
        // ContentType.APPLICATION_ATOM_XML
        defaultHeaders.put(CREDENTIAL_CHARSET, encode);
        //defaultHeaders.put("Content-Type","application/json; charset=UTF-8");
        defaultHeaders.put(HTTP_CONTENT_CHARSET, encode);
        defaultHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

        if (useProxy) {
            //设置代理IP、端口、协议（请分别替换）
            //HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");
            HttpHost proxy = new HttpHost(proxyHost, proxyPort);

            DefaultProxyRoutePlanner routePlanner = new DefaultProxyRoutePlanner(proxy);
            //把代理设置到请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom().build();

            //RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
            ///实例化CloseableHttpClient对象
            httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig)
                    .setDefaultCookieStore(cookieStore)
                    .setRoutePlanner(routePlanner)
                    .build();
        } else {
            httpClient = HttpClients.custom().setDefaultCookieStore(cookieStore).build();
        }

        if (!StringUtil.isNull(url)) {
            this.url = url;
        }
        return this;
    }

    @Override
    public CookieStore getCookieStore() {
        return cookieStore;
    }

    /**
     * GET 请求
     *
     * @param url url
     * @return 请求
     * @throws Exception 异常
     * @throws IOException             异常
     */
    @Override
    public CloseableHttpResponse getHttpResponse(String url, Map<String, ?> parameterMap, Map<String, String> headers) throws Exception {
        URIBuilder uriBuilder = new URIBuilder(url);
        if (parameterMap != null && !parameterMap.isEmpty()) {
            /* 添加参数的形式*/
            List<NameValuePair> param = getParam(parameterMap);
            uriBuilder.addParameters(param);
        }
        HttpGet httpGet = new HttpGet(uriBuilder.build());
        if (headers != null && !headers.isEmpty()) {
            addHeaders(httpGet, headers);
        }

        return httpClient.execute(httpGet);
    }

    /**
     * 未了支持 Elasticsearch
     * @param json json
     * @return 请求结果
     * @throws Exception 异常
     */
    @Override
    public String get(JSONObject json) throws Exception
    {
        return EntityUtils.toString(get( url,  json, defaultHeaders));
    }


    /**
     * 未了支持 Elasticsearch
     * @param json json
     * @param headers 请求头
     * @return 请求结果
     * @throws Exception 异常
     */
    @Override
    public String get(JSONObject json, Map<String, String> headers) throws Exception
    {
        return EntityUtils.toString(get( url,  json, headers));
    }


    @Override
    public HttpEntity get(String url, JSONObject json, Map<String, String> headers) throws Exception
    {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        HttpGet httpPost = new HttpGet(url);
        if (!ObjectUtil.isEmpty(headers)) {
            addHeaders(httpPost, headers);
        }
        String requestedWith = headers.get("X-Requested-With");
        if (requestedWith != null && requestedWith.contains(Environment.rocSecret)) {
            String k1 = RandomUtil.getRandomAlphanumeric(16);
            String iv = RandomUtil.getRandomAlphanumeric(16);

            Encrypt symmetryEncrypt = (Encrypt) ClassUtil.newInstance(com.github.jspxnet.security.symmetry.impl.AESEncrypt.class.getName());
            symmetryEncrypt.setCipherIv(iv);
            symmetryEncrypt.setSecretKey(k1);
            symmetryEncrypt.setCipherAlgorithm(CIPHER_ALGORITHM);
            String data = symmetryEncrypt.getEncode(json.toString());
            String ps = k1 + '-' + iv;

            AsyEncrypt asyEncrypt = EnvFactory.getAsymmetricEncrypt();
            byte[] key = asyEncrypt.encryptByPublicKey(ps.getBytes(Environment.defaultEncode), EnvFactory.getPublicKey());
            JSONObject posts = new JSONObject();
            posts.put("keyType", "rsa");
            posts.put("dataType", "aes");
            posts.put("key", EncryptUtil.byteToHex(key));
            posts.put("data", data);
            json = posts;
        }
        if (json != null) {
            //(String string, ContentType contentType, String contentEncoding, boolean chunked)
            StringEntity s = new StringEntity(json.toString(4),ContentType.APPLICATION_JSON.withCharset(Charset.forName(encode)));
            httpPost.setEntity(s);
        }
        /*AbstractHttpClientResponseHandler<String> handler = new BasicHttpClientResponseHandler();
        String out = httpClient.execute(httpPost,handler);
        */
        //HttpClientResponseHandler
        //ClassicHttpResponse response = httpClient.execute(httpPost);
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }

    @Override
    public String getString(String url, Map<String, ?> parameterMap, Map<String, String> headers) throws Exception {
        return EntityUtils.toString(getHttpResponse(url, parameterMap, headers).getEntity());
    }

    @Override
    public String getString(String url, Map<String, ?> parameterMap) throws Exception {
        return EntityUtils.toString(getHttpResponse(url, parameterMap, defaultHeaders).getEntity());
    }

    @Override
    public String getString() throws Exception {
        return EntityUtils.toString(getHttpResponse(url, null, defaultHeaders).getEntity());
    }

    @Override
    public String getString(String url) throws Exception {
        return EntityUtils.toString(getHttpResponse(url, null, defaultHeaders).getEntity());
    }

    @Override
    public String getString(Map<String, Object> parameterMap) throws Exception {
        return EntityUtils.toString(getHttpResponse(url, parameterMap, defaultHeaders).getEntity());
    }

    @Override
    public byte[] getBytes(String url, Map<String, ?> parameterMap, Map<String, String> headers) throws Exception {
        return EntityUtils.toByteArray(getHttpResponse(url, parameterMap, headers).getEntity());
    }

    @Override
    public byte[] getBytes(String url, Map<String, ?> parameterMap) throws Exception {
        return EntityUtils.toByteArray(getHttpResponse(url, parameterMap, defaultHeaders).getEntity());
    }


    /**
     * @param parameterMap 参数表
     * @return 参数
     */
    private static List<NameValuePair> getParam(Map<String, ?> parameterMap) {
        if (parameterMap == null) {
            return null;
        }
        List<NameValuePair> param = new ArrayList<>();
        for (Map.Entry<String, ?> entry : parameterMap.entrySet()) {
            param.add(new BasicNameValuePair(entry.getKey(), String.valueOf(entry.getValue())));
        }
        return param;
    }

    @Override
    public String post(String url,  Map<String, ?> params) throws Exception {
        return EntityUtils.toString(post(url, params, defaultHeaders));
    }

    @Override
    public String post( Map<String, ?> params) throws Exception {
        return post(url, params);
    }
    @Override
    public HttpEntity put(String url, Map<String, ?> params, Map<String, String> headers) throws Exception {
        HttpPut httpPost = new HttpPut(url);
        if (!ObjectUtil.isEmpty(params)) {
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(params), Charset.forName(encode));
            httpPost.setEntity(postEntity);
        }
        if (headers != null && !headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }

    @Override
    public HttpEntity put(JSONObject json, Map<String, String> headers) throws Exception
    {
        if (ObjectUtil.isEmpty(headers)) {
            return null;
        }
        HttpPut httpPost = new HttpPut(url);
        if (!headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        String requestedWith = headers.get("X-Requested-With");
        if (requestedWith != null && requestedWith.contains(Environment.rocSecret)) {
            String k1 = RandomUtil.getRandomAlphanumeric(16);
            String iv = RandomUtil.getRandomAlphanumeric(16);

            Encrypt symmetryEncrypt = (Encrypt) ClassUtil.newInstance(com.github.jspxnet.security.symmetry.impl.AESEncrypt.class.getName());
            symmetryEncrypt.setCipherIv(iv);
            symmetryEncrypt.setSecretKey(k1);
            symmetryEncrypt.setCipherAlgorithm(CIPHER_ALGORITHM);
            String data = symmetryEncrypt.getEncode(json.toString());
            String ps = k1 + '-' + iv;

            AsyEncrypt asyEncrypt = EnvFactory.getAsymmetricEncrypt();
            byte[] key = asyEncrypt.encryptByPublicKey(ps.getBytes(Environment.defaultEncode), EnvFactory.getPublicKey());
            JSONObject posts = new JSONObject();
            posts.put("keyType", "rsa");
            posts.put("dataType", "aes");
            posts.put("key", EncryptUtil.byteToHex(key));
            posts.put("data", data);
            json = posts;
        }
        if (json != null) {
            StringEntity s = new StringEntity(json.toString(4), ContentType.APPLICATION_JSON.withCharset(Charset.forName(encode)));
            httpPost.setEntity(s);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }

    @Override
    public HttpEntity put(String url, String body, Map<String, String> headers) throws Exception {
        HttpPut httpPost = new HttpPut(url);
        if (body!=null)
        {
            StringEntity postEntity = new StringEntity(body, Charset.forName(encode));
            httpPost.setEntity(postEntity);
        }
        if (headers != null && !headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();

    }

    @Override
    public String put(JSONObject json) throws Exception {
        return EntityUtils.toString(put(json, defaultHeaders));
    }

    /**
     * POST 请求
     *
     * @param url     url地址
     * @param params  参数
     * @param headers 请求头
     * @return 返回结果
     * @throws ParseException 异常
     * @throws IOException    异常
     */
    @Override
    public HttpEntity post(String url, Map<String, ?> params, Map<String, String> headers) throws Exception {

        HttpPost httpPost = new HttpPost(url);
        if (params != null && !params.isEmpty()) {
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(params), Charset.forName(encode));
            httpPost.setEntity(postEntity);
        }
        if (headers != null && !headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }

    @Override
    public HttpEntity post(String url, String body, Map<String, String> headers) throws Exception {
        HttpPost httpPost = new HttpPost(url);
        if (body!=null)
        {
            StringEntity postEntity = new StringEntity(body,Charset.forName(encode));
            httpPost.setEntity(postEntity);
        }
        if (headers != null && !headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }


    @Override
    public String post(String url, String body) throws Exception
    {
        return EntityUtils.toString(post(url, body, defaultHeaders));
    }

    @Override
    public String post(String body) throws Exception
    {
        return post(url, body);
    }

    @Override
    public HttpEntity post(String url, JSONObject json, Map<String, String> headers) throws Exception
    {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        HttpPost httpPost = new HttpPost(url);
        if (!headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        String requestedWith = headers.get("X-Requested-With");
        if (requestedWith != null && requestedWith.contains(Environment.rocSecret)) {
            String k1 = RandomUtil.getRandomAlphanumeric(16);
            String iv = RandomUtil.getRandomAlphanumeric(16);

            Encrypt symmetryEncrypt = (Encrypt) ClassUtil.newInstance(com.github.jspxnet.security.symmetry.impl.AESEncrypt.class.getName());
            symmetryEncrypt.setCipherIv(iv);
            symmetryEncrypt.setSecretKey(k1);
            symmetryEncrypt.setCipherAlgorithm(CIPHER_ALGORITHM);
            String data = symmetryEncrypt.getEncode(json.toString());
            String ps = k1 + '-' + iv;

            AsyEncrypt asyEncrypt = EnvFactory.getAsymmetricEncrypt();
            byte[] key = asyEncrypt.encryptByPublicKey(ps.getBytes(Environment.defaultEncode), EnvFactory.getPublicKey());
            JSONObject posts = new JSONObject();
            posts.put("keyType", "rsa");
            posts.put("dataType", "aes");
            posts.put("key", EncryptUtil.byteToHex(key));
            posts.put("data", data);
            json = posts;
        }
        if (json != null) {
            StringEntity s = new StringEntity(json.toString(4),ContentType.APPLICATION_JSON.withCharset(Charset.forName(encode)));
            httpPost.setEntity(s);
        }
        httpResponse = httpClient.execute(httpPost);
        if (httpResponse.getCode()==405)
        {
            httpResponse.close();
            return get(url,json,headers);
        }
        return httpResponse.getEntity();
    }

    @Override
    public HttpEntity put(String url, JSONObject json, Map<String, String> headers) throws Exception
    {
        if (headers == null || headers.isEmpty()) {
            return null;
        }
        HttpPut httpPost = new HttpPut(url);
        if (!headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        String requestedWith = headers.get("X-Requested-With");
        if (requestedWith != null && requestedWith.contains(Environment.rocSecret)) {
            String k1 = RandomUtil.getRandomAlphanumeric(16);
            String iv = RandomUtil.getRandomAlphanumeric(16);

            Encrypt symmetryEncrypt = (Encrypt) ClassUtil.newInstance(com.github.jspxnet.security.symmetry.impl.AESEncrypt.class.getName());
            symmetryEncrypt.setCipherIv(iv);
            symmetryEncrypt.setSecretKey(k1);
            symmetryEncrypt.setCipherAlgorithm(CIPHER_ALGORITHM);
            String data = symmetryEncrypt.getEncode(json.toString());
            String ps = k1 + '-' + iv;

            AsyEncrypt asyEncrypt = EnvFactory.getAsymmetricEncrypt();
            byte[] key = asyEncrypt.encryptByPublicKey(ps.getBytes(Environment.defaultEncode), EnvFactory.getPublicKey());
            JSONObject posts = new JSONObject();
            posts.put("keyType", "rsa");
            posts.put("dataType", "aes");
            posts.put("key", EncryptUtil.byteToHex(key));
            posts.put("data", data);
            json = posts;
        }
        if (json != null) {
            StringEntity s = new StringEntity(json.toString(4), ContentType.APPLICATION_JSON.withCharset(Charset.forName(encode)));
            httpPost.setEntity(s);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }

    @Override
    public String post(String url, JSONObject json) throws Exception {
        return EntityUtils.toString(post(url, json, defaultHeaders));
    }

    @Override
    public String post() throws Exception {
        return EntityUtils.toString(post(url, (String)null, defaultHeaders));
    }

    @Override
    public String post(JSONObject json) throws Exception {
        return EntityUtils.toString(post(url, json, defaultHeaders),encode);
    }

    @Override
    public String getResponseString() throws Exception {
        return EntityUtils.toString(httpResponse.getEntity());
    }

    @Override
    public int getStatusCode() {
        if (httpResponse == null) {
            return -1;
        }
        return httpResponse.getCode();
    }

    /**
     * 添加头
     *
     * @param httpRequest 请求
     * @param headerMap   头信息表
     */
    private static void addHeaders(HttpUriRequestBase httpRequest, Map<String, String> headerMap) {
        for (Map.Entry<String, String> entry : headerMap.entrySet()) {
            httpRequest.setHeader(entry.getKey(), entry.getValue());
        }
    }


    /**
     *
     * @param files 文件
     * @param name 文件变量名
     * @return  返回信息
     */
    @Override
    public String upload(File[] files, String name)
    {
        return upload( files,  name,null);
    }

    /**
     * 上传文件
     * @param files 文件
     * @param name 文件变量名
     * @param params 参数
     * @return 返回信息
     */
    @Override
    public String upload(File[] files, String name,Map<String,String> params)
    {
        String response = "";
        if (files==null) {
            return "file not exists";
        }

        HttpPost postMethod = new HttpPost(url);

        for (String key:defaultHeaders.keySet())
        {
            if ("CONTENT-TYPE".contains(key.toUpperCase()))
            {
                continue;
            }
            postMethod.addHeader(key,defaultHeaders.get(key));
        }

        Charset defCharset = Charset.forName(encode);
        MultipartEntityBuilder builder  = MultipartEntityBuilder.create();
        builder.setCharset(defCharset);
        builder.setMode(HttpMultipartMode.EXTENDED);
        builder.setContentType(ContentType.MULTIPART_FORM_DATA.withCharset(defCharset));
        try {
            //----------------------------------------------
            // FilePart：用来上传文件的类,file即要上传的文件
            for (File file : files) {
                builder.addBinaryBody(name,file,ContentType.APPLICATION_OCTET_STREAM.withCharset(defCharset),file.getName());
            }
           if (params!=null)
            {
                for (String key:params.keySet())
                {
                    if (key==null)
                    {
                        continue;
                    }
                    builder.addTextBody(key,params.get(key),ContentType.TEXT_PLAIN.withCharset(defCharset));
                }
            }
            postMethod.setEntity(builder.build());
            response = httpClient.execute(postMethod,new BasicHttpClientResponseHandler());

            //httpResponse = httpClient.execute(postMethod,basicHttpClientResponseHandler);

          /*  int status = httpResponse.getCode() ;
            if (status == HttpStatus.SC_OK) {
                response =  EntityUtils.toString(httpResponse.getEntity(), defCharset);
            } else {
                response = httpResponse.getReasonPhrase();
                if (StringUtil.isEmpty(response))
                {
                    response = "fail";
                }
            }*/
        } catch (Exception e) {
            log.error(e.getMessage(),e);
        } finally {
            // 释放连接
            postMethod.reset();
            postMethod.clear();
        }
        return response;
    }

    @Override
    public boolean download(File file,JSONObject json) throws Exception
    {
        return FileUtil.writeFile(file, EntityUtils.toByteArray(post(url, json, defaultHeaders)));
    }

    @Override
    public boolean download(File file,Map<String,Object> map) throws Exception
    {
        return FileUtil.writeFile(file, EntityUtils.toByteArray(post(url, map, defaultHeaders)));
    }

    @Override
    public void setHeaders(Map<String, String> header) {
        defaultHeaders.putAll(header);
    }

    @Override
    public void cleanHeaders() {
        defaultHeaders.clear();
    }

}
