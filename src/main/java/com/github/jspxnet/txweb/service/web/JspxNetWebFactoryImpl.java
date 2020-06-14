package com.github.jspxnet.txweb.service.web;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.action.AuthenticationAction;
import com.github.jspxnet.txweb.dispatcher.handle.RocHandle;
import com.github.jspxnet.txweb.dispatcher.service.RocService;
import com.github.jspxnet.txweb.dispatcher.service.RsaRocService;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.util.TXWebUtil;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.service.WebBeanFactory;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.*;
import javax.annotation.Resource;
import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;
import java.util.Map;

/**
 * Created by chenyuan on 2015-7-8.
 * <p>
 * 这里是一个 jax-ws 方式的WebService 调用接口
 * sun-jaxws.xml 配置如下
 * [?xml version="1.0" encoding="UTF-8"?]
 * [endpoints  xmlns="http://java.sun.com/xml/ns/jax-ws/ri/runtime"  version="2.0"]
 * [endpoint name="JspxNetWebFactory"
 * implementation="com.github.jspxnet.txweb.service.web.JspxNetWebFactoryImpl"
 * interface="com.github.jspxnet.txweb.service.web.WebBeanFactoryAPI"
 * url-pattern="/services/jspxnetwebfactory"/]
 * [/endpoints]
 * <p>
 * AopEndpointPublishServices 是启动发布功能
 * <p>
 * [bean id="aopEndpointPublishServices" class="com.github.jspxnet.sioc.aop.AopEndpointPublishServices" singleton="false" ]
 * [string name="host"]xxx[/string]
 * [/bean]
 * <p>
 * [bean id="aopBootBean" class="com.github.jspxnet.sioc.aop.AopAppCommandImpl" singleton="true"]
 * [boolean name="enable"]${aopboot}[/boolean]
 * [array name="beanArray" class="string"]
 * [value]aopEndpointPublishServices[/value]
 * [/array]
 * [/bean]
 * <p>
 * 配置好后，使用jdk自带工具wsimport生成客户端：
 * wsimport -d ./bin -s ./src http://xxxxxxxxxxxxxxxxxxxxxxx
 * <p>
 * eg: wsimport -s . http://127.0.0.1/service/jspxnetwebfactory?wsdl
 * 然后就可以调用了，
 * soap方式，就是接口描述比较清晰，时候第三放调用，但是要麻烦一点。*  好像端口不能公用80，必须单独的端口发布
 * hessian方便一点，只是接口要单独发布给对方，不能自动生成
 * soap接口方式，需要String 来返回给用户，不能直接打印到 request
 */
@Slf4j
@WebService(serviceName = "jspxnetwebfactory")
public class JspxNetWebFactoryImpl extends AuthenticationAction implements WebBeanFactory {
    @Ref(namespace = Sioc.global)
    private OnlineManager onlineManager;

    @Ref(name = Environment.language, namespace = Sioc.global)
    protected Bundle language;

    @Ref(name = Environment.config)
    protected Bundle config;

    private String token;

    public JspxNetWebFactoryImpl() {

    }

    @Resource
    private WebServiceContext wsContext;




    @WebMethod
    @Override
    public void setToken(String token) {
        this.token = token;
    }

    @WebMethod
    @Override
    public boolean isOnline() {
        if (StringUtil.isNull(token)) {
            return false;
        }
        IUserSession userSession = onlineManager.getUserSession(token,getRemoteAddr());
        return userSession != null && !userSession.isGuest();
    }


    @Override
    @WebMethod
    public void exit() {
        MessageContext messageContext = wsContext.getMessageContext();
        setRequest((HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST));
        setResponse((HttpServletResponse) messageContext.get(MessageContext.SERVLET_RESPONSE));
        try {
            if (session != null && session.getAttribute(TXWeb.token) == null) {
                session.setAttribute(TXWeb.token, token);
            }
            onlineManager.exit(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @WebMethod
    public String getNetPublicKey() {
        MessageContext messageContext = wsContext.getMessageContext();
        setRequest((HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST));
        setResponse((HttpServletResponse) messageContext.get(MessageContext.SERVLET_RESPONSE));
        String publicKeyHost = StringUtil.trim(config.getString(Environment.publicKeyHost));
        if (!IpUtil.interiorly(publicKeyHost, getRemoteAddr())) {
            return language.getLang(LanguageRes.notAllowedIpLimits);
        }
        int publicKeyHour = config.getInt(Environment.publicKeyHour, 1);
        if (publicKeyHour <= 0) {
            publicKeyHour = 2;
            try {
                config.save(Environment.publicKeyHour, publicKeyHour + StringUtil.empty);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (StringUtil.isNull(TXWeb.publicKey) || System.currentTimeMillis() - TXWeb.publicKeyCreateTimeMillis > DateUtil.HOUR * publicKeyHour) {
            TXWeb.publicKey = EncryptUtil.getMd5(System.currentTimeMillis() + RandomUtil.getRandomNumeric(32));
            TXWeb.publicKeyCreateTimeMillis = System.currentTimeMillis();
            try {
                config.flush();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return TXWeb.publicKey;
    }

    /**
     * @return 时间校对，避免出现超时登陆,必须先登录
     */
    @WebMethod
    public long getCurrentTimeMillis() {
        try {
            if (isOnline()) {
                return System.currentTimeMillis();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }


    /**
     * 方法调用接口
     *
     * @param call 可以是json或者xml的调用,格式看ROC
     * @return 返回调用结果，调用权限和系统配置权限绑定
     */
    @WebMethod
    @Override
    public String process(String call) {
        MessageContext messageContext = wsContext.getMessageContext();
        setRequest((HttpServletRequest) messageContext.get(MessageContext.SERVLET_REQUEST));
        setResponse((HttpServletResponse) messageContext.get(MessageContext.SERVLET_RESPONSE));

        ///////////////////////////////////环境参数 begin
        Map<String, Object> envParams = TXWebUtil.createEnvironment();
        envParams.put(ActionEnv.Key_RealPath, Dispatcher.getRealPath());
        envParams.put(ActionEnv.Key_Request, request);
        envParams.put(ActionEnv.Key_Response, response);
        envParams.put(TXWeb.token, token);
        //setSessionId(sessionId);
        session.setAttribute(TXWeb.token, token);
        ///////////////////////////////////环境参数 end
        ///////////////////读取ajax请求 end
        if (StringUtil.isNull(call)) {
            return new JSONObject(RocResponse.error(-32600, "Invalid Request.无效的请求")).toString(4);
        }
        //////////////////初始begin

        if (call.length() > RocHandle.REQUEST_MAX_LENGTH) {
            return new JSONObject(RocResponse.error(-32602, "Invalid params.参数无效")).toString(4);
        }

        //判断是XML还是JSON begin
        String rpc = StringUtil.trim(call);
        JSONObject jsonData = null;

        if (StringUtil.isXml(rpc)) {
            //XML格式
            //蒋XML转化到JSON
            try {
                jsonData = XML.toJSONObject(rpc);
            } catch (JSONException e) {
                return new JSONObject(RocResponse.error(-32600, "Invalid Request.无效的请求")).toString(4);
            }
        }
        if (StringUtil.isJsonObject(rpc)) {
            //JSON格式
            try {
                jsonData = new JSONObject(rpc);
            } catch (JSONException e) {
                return new JSONObject(RocResponse.error(-32600, "Invalid Request.无效的请求")).toString(4);
            }
        }
        if (jsonData == null) {
            return new JSONObject(RocResponse.error(-32700, "Parse error.解析错误,不能识别的格式")).toString(4);
        }
        //判断是XML还是JSON end
        String requestedWith = jsonData.getString(Environment.rocSecret);
        if (requestedWith.contains(Environment.rocSecret)) {
            //这种方式主要是返回Rsa的结果
            RsaRocService rsaRocService = new RsaRocService();
            try {
                return rsaRocService.doing(request, response, jsonData.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return new JSONObject(RocResponse.error(-32700, "Parse error.调用发生异常")).toString(4);
            }
        } else {
            RocService rocService = new RocService();
            try {
                return rocService.doing(request, response, jsonData.toString());
            } catch (Exception e) {
                e.printStackTrace();
                return new JSONObject(RocResponse.error(-32700, "Parse error.调用发生异常")).toString(4);
            }
        }

    }

}
