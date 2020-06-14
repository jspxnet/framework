package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.support.ActionSupport;

import java.util.Map;

/**
 * Created by yuan on 2014/10/21 0021.
 * 显示所有配置开启的数据
 */
@HttpMethod(caption = "配置浏览")
public class ConfigView extends ActionSupport {
    public Map<String, String> getApplicationMap() {
        return EnvFactory.getBeanFactory().getApplicationMap();
    }
}
