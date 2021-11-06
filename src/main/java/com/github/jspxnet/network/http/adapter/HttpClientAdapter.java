package com.github.jspxnet.network.http.adapter;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.security.asymmetric.AsyEncrypt;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.utils.*;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpClientParams;
import org.apache.http.*;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import java.io.*;
import java.util.*;

/**
 * Created by ChenYuan on 2017/5/23.
 * http 请求
 */
public class HttpClientAdapter implements HttpClient {
    // OK: Success!
    public static final int SUCCESS = 200;
    protected org.apache.http.client.CookieStore cookieStore = new BasicCookieStore();
    protected CloseableHttpClient httpClient;
    protected boolean useProxy = false;
    protected String proxyHost = "127.0.0.1";
    protected int proxyPort = 8087;
    protected String encode = Environment.defaultEncode;
    protected int bufferSize = 512;
    protected int connectionTimeout = 20000;
    protected int readTimeout = 20000;
    //当前实体对象
    protected HttpResponse httpResponse;
    final private static String CIPHER_ALGORITHM = "AES/CBC/PKCS7Padding";

    protected final Map<String, String> defaultHeaders = new HashMap<>();

    protected String url;

    public String getUrl() {

        return url;
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

        final String CREDENTIAL_CHARSET = "http.protocol.credential-charset";
        final String HTTP_CONTENT_CHARSET = "http.protocol.content-charset";
        //HttpClients.
       // ContentType.APPLICATION_ATOM_XML
        defaultHeaders.put(CREDENTIAL_CHARSET, encode);
        //defaultHeaders.put("Content-Type","application/json; charset=UTF-8");
        defaultHeaders.put(HTTP_CONTENT_CHARSET, encode);
        defaultHeaders.put("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/33.0.1750.146 Safari/537.36");

        if (useProxy) {
            //设置代理IP、端口、协议（请分别替换）
            HttpHost proxy = new HttpHost(proxyHost, proxyPort, "http");

            //把代理设置到请求配置
            RequestConfig defaultRequestConfig = RequestConfig.custom().setProxy(proxy).build();
            ///实例化CloseableHttpClient对象
            httpClient = HttpClients.custom().setDefaultRequestConfig(defaultRequestConfig).setDefaultCookieStore(cookieStore).build();
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
     * @throws ClientProtocolException 异常
     * @throws IOException             异常
     */
    @Override
    public HttpResponse getHttpResponse(String url, Map<String, ?> parameterMap, Map<String, String> headers) throws Exception {
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
        HttpGetEx httpPost = new HttpGetEx(url);
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
            StringEntity s = new StringEntity(json.toString(4), encode);
            s.setContentEncoding(encode);
            s.setContentType("application/json;charset=" + encode);//发送json数据需要设置contentType
            httpPost.setEntity(s);
        }
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
    public String post(String url,  Map<String, ?> params) throws ParseException, IOException {
        return EntityUtils.toString(post(url, params, defaultHeaders));
    }

    @Override
    public String post( Map<String, ?> params) throws ParseException, IOException {
        return post(url, params);
    }
    @Override
    public HttpEntity put(String url, Map<String, ?> params, Map<String, String> headers) throws ParseException, IOException {
        HttpPut httpPost = new HttpPut(url);
        if (!ObjectUtil.isEmpty(params)) {
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(params), encode);
            postEntity.setContentEncoding(encode);
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
            StringEntity s = new StringEntity(json.toString(4), encode);
            s.setContentEncoding(encode);
            s.setContentType("application/json;charset=" + encode);//发送json数据需要设置contentType
            httpPost.setEntity(s);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }

    @Override
    public HttpEntity put(String url, String body, Map<String, String> headers) throws ParseException, IOException {
        HttpPut httpPost = new HttpPut(url);
        if (body!=null)
        {
            StringEntity postEntity = new StringEntity(body, encode);
            postEntity.setContentEncoding(encode);
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
    public HttpEntity post(String url, Map<String, ?> params, Map<String, String> headers) throws ParseException, IOException {

        HttpPost httpPost = new HttpPost(url);
        if (params != null && !params.isEmpty()) {
            UrlEncodedFormEntity postEntity = new UrlEncodedFormEntity(getParam(params), encode);
            postEntity.setContentEncoding(encode);
            httpPost.setEntity(postEntity);
        }
        if (headers != null && !headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }

    @Override
    public HttpEntity post(String url, String body, Map<String, String> headers) throws ParseException, IOException {
        HttpPost httpPost = new HttpPost(url);
        if (body!=null)
        {
            StringEntity postEntity = new StringEntity(body, encode);
            postEntity.setContentEncoding(encode);
            httpPost.setEntity(postEntity);
        }
        if (headers != null && !headers.isEmpty()) {
            addHeaders(httpPost, headers);
        }
        httpResponse = httpClient.execute(httpPost);
        return httpResponse.getEntity();
    }


    @Override
    public String post(String url, String body) throws ParseException, IOException
    {
        return EntityUtils.toString(post(url, body, defaultHeaders));
    }

    @Override
    public String post(String body) throws ParseException, IOException
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
            StringEntity s = new StringEntity(json.toString(4), encode);
            s.setContentEncoding(encode);
            s.setContentType("application/json;charset=" + encode);//发送json数据需要设置contentType
            httpPost.setEntity(s);
        }
        httpResponse = httpClient.execute(httpPost);
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
            StringEntity s = new StringEntity(json.toString(4), encode);
            s.setContentEncoding(encode);
            s.setContentType("application/json;charset=" + encode);//发送json数据需要设置contentType
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
    public String getResponseString() throws ParseException, IOException {
        return EntityUtils.toString(httpResponse.getEntity());
    }

    @Override
    public int getStatusCode() throws ParseException {
        if (httpResponse == null) {
            return -1;
        }
        return httpResponse.getStatusLine().getStatusCode();
    }

    /**
     * 添加头
     *
     * @param httpRequest 请求
     * @param headerMap   头信息表
     */
    private static void addHeaders(HttpRequestBase httpRequest, Map<String, String> headerMap) {
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
     *
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
        PostMethod postMethod = new PostMethod(url);
        for (String key:defaultHeaders.keySet())
        {
            if ("CONTENT-TYPE".contains(key.toUpperCase()))
            {
                continue;
            }
            postMethod.setRequestHeader(key,defaultHeaders.get(key));
        }
        int len = files.length + (params==null?0:params.size());
        Part[] parts = new Part[len];
        try {
            //----------------------------------------------
            // FilePart：用来上传文件的类,file即要上传的文件
            for (int i=0;i<files.length;i++)
            {
                CustomFilePart filePart = new CustomFilePart(name,files[i]);
                filePart.setCharSet(Environment.defaultEncode);
                filePart.setContentType("text/plain");
                parts[i]= filePart;
            }
            if (params!=null)
            {
                int i = files.length;
                for (String key:params.keySet())
                {
                    if (key==null)
                    {
                        continue;
                    }
                    StringPart strPart = new StringPart(key,params.get(key),Environment.defaultEncode);
                    strPart.setContentType("text/plain");
                    strPart.setTransferEncoding(Environment.defaultEncode);
                    parts[i]= strPart;
                    i++;

                    postMethod.setParameter(key,params.get(key));
                }
            }


            // 对于MIME类型的请求，httpclient建议全用MulitPartRequestEntity进行包装

            MultipartRequestEntity multipartRequest = new MultipartRequestEntity(parts, postMethod.getParams());
            postMethod.setRequestEntity(multipartRequest);
            //---------------------------------------------


            org.apache.commons.httpclient.HttpClient client = new org.apache.commons.httpclient.HttpClient();

            HttpClientParams httpClientParams = client.getParams();
            httpClientParams.setContentCharset(Environment.defaultEncode);
            httpClientParams.setConnectionManagerTimeout(10000);
            httpClientParams.setHttpElementCharset(Environment.defaultEncode);
            httpClientParams.setUriCharset(Environment.defaultEncode);
            client.setParams(httpClientParams);

            int status = client.executeMethod(postMethod);
            if (status == HttpStatus.SC_OK) {
                InputStream inputStream = postMethod.getResponseBodyAsStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(
                        inputStream));
                StringBuilder sendBuffer = new StringBuilder();
                String str;
                while ((str = br.readLine()) != null) {
                    sendBuffer.append(str);
                }
                response = sendBuffer.toString();
            } else {
                response = postMethod.getResponseBodyAsString();
                if (StringUtil.isEmpty(response))
                {
                    response = "fail";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 释放连接
            postMethod.releaseConnection();
        }
        return response;
    }

    @Override
    public boolean download(File file,JSONObject json) throws Exception
    {
        return FileUtil.writeFile(file, EntityUtils.toByteArray(post(url, json, defaultHeaders)));
    }

    @SuppressWarnings("all")
    @Override
    public boolean download(File file,Map map) throws Exception
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

    public static void main(String[] args) throws Exception {


        HttpClient httpClient =  HttpClientFactory.createHttpClient("https://www.baidu.com");

        String out = httpClient.getString();
        System.out.println(out);
    }
}
