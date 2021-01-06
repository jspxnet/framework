package com.github.jspxnet.txweb.service;


import java.net.MalformedURLException;

public interface HessianClient {


    void setChunkedPost(boolean isChunked);

    <T> T create(Class<T> api,  String urlName, String token) throws MalformedURLException;
}
