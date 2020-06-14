package com.github.jspxnet.weixin;


import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.weixin4j.Configuration;
import org.weixin4j.Weixin;
import org.weixin4j.WeixinBuilder;
import org.weixin4j.component.SnsComponent;
import org.weixin4j.model.base.Token;

public class WeiXinTest {
    //WeixinBuilder

    @BeforeClass
    public static void init() {
        System.out.println("------------开始");

    }

    @AfterClass
    public void afterExit() {
        System.out.println("------------结束");
    }

    @Test
    public static void testWeinLogin() throws Exception {


        System.out.println("------------Configuration.getOAuthAppId()=" + Configuration.getOAuthAppId());
        Weixin weinxin = WeixinBuilder.newInstance().build();

        Token token = weinxin.getToken();

        System.out.println("token.getAccess_token()--------------" + token.getAccess_token());
        String authorize_url = "https://open.weixin.qq.com/connect/oauth2/authorize";
        SnsComponent snsComponent = weinxin.sns(authorize_url);
        String url = snsComponent.getOAuth2CodeUrl("http://www.jspbox.com/weixin/weixinauth.jhtml", "snsapi_userinfo", "456");


        System.out.println("---------url=" + url);


    }
}
