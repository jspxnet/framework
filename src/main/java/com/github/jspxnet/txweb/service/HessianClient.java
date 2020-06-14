package com.github.jspxnet.txweb.service;


import java.net.MalformedURLException;

public interface HessianClient {


    /**
     * 远程访问的sessionId 也是token ,Bearer Token Authentication
     *
     * @param token sessionId
     */
    void setToken(String token);

    void setUser(String user);

    void setPassword(String password);

    void setChunkedPost(boolean isChunked);

    <T> T getInterface(Class<T> api) throws MalformedURLException;

    <T> T getInterface(Class<T> api, String urlName) throws MalformedURLException;
}
