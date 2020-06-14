package com.github.jspxnet.txweb.view;


import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by ChenYuan on 2017/3/19.
 * 这里并不是实时的，会有延时来确保速度
 */
@Slf4j
@HttpMethod(caption = "WEB反向载入")
public class WebDataLoadView extends ActionSupport {

    private String url = "http://www.weather.com.cn/data/cityinfo/101260101.html";

    public String getUrl() {
        return url;
    }

    @Param(caption = "URL", max = 300)
    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtml() throws JSONException {
        if (StringUtil.isNull(url)) {
            return StringUtil.empty;
        }
        String key = EncryptUtil.getMd5(url);
        String data = (String) JSCacheManager.get(DefaultCache.class, key);
        if (StringUtil.hasLength(data)) {
            return data;
        }
        data = getValue();
        JSCacheManager.put(DefaultCache.class, key, data);
        return data;
    }


    private String getValue() {
        try {
            HttpClient httpClient = HttpClientFactory.createHttpClient(url);
            return httpClient.getString(url);
        } catch (Exception e) {
            log.info("反向载入失败:" + url, e);
            return StringUtil.empty;
        }
    }
}