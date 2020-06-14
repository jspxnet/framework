package com.github.jspxnet.weixin;


import org.weixin4j.Weixin;
import org.weixin4j.WeixinBuilder;

/**
 * 主要管理 token 有效期,和weixin单例话，降低内存使用
 */
public class WeiXinManager {

    private static Weixin weiXin = WeixinBuilder.newInstance().build();

    public static Weixin getWeinXin() {
        return weiXin;
    }


}
