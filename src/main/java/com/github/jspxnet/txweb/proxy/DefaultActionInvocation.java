/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.proxy;

import com.github.jspxnet.boot.DaemonThreadFactory;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.txweb.annotation.Async;
import com.github.jspxnet.txweb.*;
import com.github.jspxnet.txweb.annotation.Intercept;
import com.github.jspxnet.txweb.annotation.Redirect;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.dispatcher.handle.CommandHandle;
import com.github.jspxnet.txweb.dispatcher.handle.MarkdownHandle;
import com.github.jspxnet.txweb.dispatcher.handle.RocHandle;
import com.github.jspxnet.txweb.dispatcher.handle.RsaRocHandle;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.result.*;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 0:20:12
 * action 参数 执行容器
 */
@Slf4j
public class DefaultActionInvocation implements ActionInvocation {

    final private static String[] NO_PARAMETER = new String[]{"session", "request", "response", "language", "config", "env", "actionLogTitle", "actionLogContent", "result", "templatePath", "actionResult"};
    final private static WebConfigManager WEB_CONFIG_MANAGER = TxWebConfigManager.getInstance();
    final private static BeanFactory BEAN_FACTORY = EnvFactory.getBeanFactory();
    final private static Map<String, String> RESULT_MAP = new HashMap<>(22);

    public static final JSONObject MULTIPART_SUPPORT_ERROR_JSON = new JSONObject();

    static {
        MULTIPART_SUPPORT_ERROR_JSON.put("repair", false);
        MULTIPART_SUPPORT_ERROR_JSON.put("success", 0);
        MULTIPART_SUPPORT_ERROR_JSON.put("error", 1);
        MULTIPART_SUPPORT_ERROR_JSON.put("OK", 0);
        MULTIPART_SUPPORT_ERROR_JSON.put("state", "error");
        MULTIPART_SUPPORT_ERROR_JSON.put("thumbnail", 0);
        MULTIPART_SUPPORT_ERROR_JSON.put(Environment.MESSAGE, "need login,no authority");
    }

    static {
        RESULT_MAP.put(ActionSupport.NONE, NoneResult.class.getName());
        RESULT_MAP.put(ActionSupport.TEMPLATE, TemplateResult.class.getName());
        RESULT_MAP.put(ActionSupport.HtmlImg, HtmlImgResult.class.getName());
        RESULT_MAP.put(ActionSupport.HtmlPdf, HtmlPdfResult.class.getName());
        RESULT_MAP.put(ActionSupport.Markdown, MarkdownResult.class.getName());
        RESULT_MAP.put(ActionSupport.FileSave, FileSaveResult.class.getName());
        RESULT_MAP.put(ActionSupport.PdfPageImg, PdfPageImgResult.class.getName());
        RESULT_MAP.put(ActionSupport.MESSAGE, MessageResult.class.getName());
        RESULT_MAP.put(ActionSupport.CHAIN, ChainResult.class.getName());
        RESULT_MAP.put(ActionSupport.QRCode, QRCodeResult.class.getName());
        RESULT_MAP.put(ActionSupport.REDIRECT, RedirectResult.class.getName());
        RESULT_MAP.put(ActionSupport.ROC, RocResult.class.getName());
        RESULT_MAP.put(ActionSupport.CHARTS, FusionChartsXmlResult.class.getName());
        RESULT_MAP.put(ActionSupport.EXCEL, ExcelResult.class.getName());
        RESULT_MAP.put(ActionSupport.ZipFile, ZipFileResult.class.getName());
        RESULT_MAP.put(ActionSupport.JXLS, JxlsResult.class.getName());
        RESULT_MAP.put(ActionSupport.PRINT, PrintResult.class.getName());
        RESULT_MAP.put(ActionSupport.FORWARD, ForwardResult.class.getName());
        RESULT_MAP.put(ActionSupport.ERROR, ErrorResult.class.getName());
    }

    final private DefaultActionProxy actionProxy;
    final private String namespace;

    private boolean executed = false;
    private String resultCode = null;
    private Iterator<Interceptor> interceptors;
    private ActionConfig actionConfig;


    @Override
    public boolean isExecuted() {
        return executed;
    }

    @Override
    public String getResultCode() {
        return resultCode;
    }

    @Override
    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    @Override
    public ActionProxy getActionProxy() {
        return actionProxy;
    }

    @Override
    public ActionConfig getActionConfig() {
        return actionConfig;
    }


    public DefaultActionInvocation(ActionConfig actionConfig, Map<String, Object> params, String exeType, JSONObject jsonData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        this.actionProxy = new DefaultActionProxy();
        this.namespace = (StringUtil.isNull(actionConfig.getNamespace()) || Sioc.global.equals(actionConfig.getNamespace())) ? (String) params.get(ActionEnv.Key_Namespace) : actionConfig.getNamespace();
        this.actionProxy.setExeType(exeType);
        this.actionProxy.setNamespace(this.namespace);
        if (!params.containsKey(ActionEnv.Key_Namespace)) {
            params.put(ActionEnv.Key_Namespace, this.namespace);
        }

        /////////创建bean对象 begin
        Object obj = BEAN_FACTORY.getBean(actionConfig.getIocBean(), namespace);
        Action action = null;
        try {
            action = (Action) obj;
        } catch (Exception e) {
            log.debug("ioc class " + obj + " is not action");
            e.printStackTrace();
        }

        if (action == null) {
            throw new Exception("ioc get action error :" + actionConfig.getIocBean() + "   namespace=" + namespace + ",检查ioc 配置或者txweb class 部分配置,或tomcat嵌入方式异常");
        }
        /////////创建bean对象 end

        //设置请求参数 begin
        action.setRequest(request);
        action.setResponse(response);
        //设置请求参数 end

        //////////////载入配置参数 begin
        Placeholder placeholder = EnvFactory.getPlaceholder();
        Map<String, Object> paraMap = actionConfig.getParam();
        for (String pKey : paraMap.keySet()) {
            String sValue = placeholder.processTemplate(params, (String) paraMap.get(pKey));
            params.put(pKey, sValue);
            ////////////如果action存在此方法，就传到action里边
            if (!ArrayUtil.inArray(NO_PARAMETER, pKey, true)) {
                Method method = ClassUtil.getSetMethod(action.getClass(), pKey);
                if (method != null) {
                    BeanUtil.setSimpleProperty(action, method.getName(), sValue);
                }
            }
        }

        action.initEnv(params);
        //判断是否使用手机模式切换模版
        if (actionConfig.isMobile()) {
            action.put(ActionEnv.KEY_MobileTemplate, true);
        }

        //////////////载入配置参数 end

        //上传方式比较特殊 选方参数,保证上传的设置 begin
        TXWebUtil.setMulRequest(action);
        //上传方式比较特殊 选方参数,保证上传的设置 end

        //放入名称
        actionProxy.setCaption(actionConfig.getCaption());

        //放入action
        actionProxy.setAction(action);
        //设置执行方法 begin
        //这里可能会存在安全问题

        String methodName = actionConfig.getMethod();
        String actionName = action.getEnv(ActionEnv.Key_ActionName);
        if (TXWebUtil.AT.equals(methodName) && !StringUtil.isEmpty(actionName)) {
            actionProxy.setMethod(actionName);
        }
        //--------------------

        if ((exeType.equalsIgnoreCase(RocHandle.NAME) || exeType.equalsIgnoreCase(RsaRocHandle.NAME)) && jsonData != null) {
            //当配置有方法的时候，只能执行配置中的方法，不能执行其他动作,避免安全漏洞
            //兼容简单方式 {"jsonrpc": "2.0", "method": "sum", "params": [1,2,4], "id": "1"},
            //如果ROC请求,调用访问为空，默认使用@表示action名称

            //必须先设置action
            actionProxy.setCallJson(jsonData);

            //先放入参数 begin
            TXWebUtil.putJsonParams(actionProxy);
            //先放入参数 end

            //这里只是做安全配置检查，屏蔽调用不允许的方法
            String requestMethodName;
            JSONObject methodJson = jsonData.getJSONObject(Environment.rocMethod);
            if (methodJson != null) {
                requestMethodName = methodJson.getString(Environment.rocName);
            } else {
                requestMethodName = jsonData.getString(Environment.rocMethod);
            }
            if (!StringUtil.isEmpty(methodName) && !methodName.startsWith(TXWebUtil.AT) && !methodName.equalsIgnoreCase(requestMethodName)) {
                log.debug(actionConfig + " 已配置了执行方法 method:" + methodName + "不允许在调用其它未指定方法");
                JSONObject errorJson = new JSONObject(RocResponse.error(ErrorEnumType.METHOD_NOT_FOUND.getValue(), actionConfig.getActionName() + " 已配置了执行方法 method:" + methodName + "不允许在调用其它未指定方法"));
                TXWebUtil.errorPrint(errorJson.toString(4), null, action.getResponse(), HttpStatusType.HTTP_status_403);
                return;
            }
            requestMethodName = StringUtil.hasLength(requestMethodName) ? requestMethodName : TXWebUtil.AT;
            actionProxy.setMethod(requestMethodName);
        } else {
            //传统方式,如果发现是一个Roc配置的
            actionProxy.setMethod(methodName);
            //这里处理直接配置的和 @开头的配置
        }
        //设置执行方法 end

        //设置默认的返回方式 begin
        if (exeType.equalsIgnoreCase(RocHandle.NAME)) {
            action.setActionResult(ActionSupport.ROC);
        } else if (exeType.equalsIgnoreCase(MarkdownHandle.NAME)) {
            action.setActionResult(ActionSupport.Markdown);
        } else {
            action.setActionResult(ActionSupport.INPUT);
        }
        //设置默认的返回方式 end

        this.actionConfig = actionConfig;
        createInterceptorList();

        //如果是void 方法，自动设置 NONE begin
        Method execMethod = actionProxy.getMethod();
        if (void.class.equals(execMethod.getGenericReturnType())) {
            actionProxy.getAction().setActionResult(ActionSupport.NONE);
        }
        //如果是void 方法，自动设置 NONE end

    }

    private void createInterceptorList() {

        LinkedList<String> interceptNameList = new LinkedList<>();
        List<String> tmpList = WEB_CONFIG_MANAGER.getDefaultInterceptors(namespace);
        if (tmpList != null && !tmpList.isEmpty()) {
            //过滤重复的拦截器
            for (String name : tmpList) {
                if (StringUtil.isEmpty(name)) {
                    continue;
                }
                if (!interceptNameList.contains(name)) {
                    interceptNameList.addLast(name);
                }
            }
        }

        //配置的拦截器
        tmpList = actionConfig.getInterceptors();
        if (tmpList != null) {
            //过滤重复的拦截器
            for (String name : tmpList) {
                if (StringUtil.isEmpty(name)) {
                    continue;
                }
                if (!interceptNameList.contains(name)) {
                    interceptNameList.addLast(name);
                }
            }
        }

        String[] notInterceptor = actionConfig.getPassInterceptor();
        if (notInterceptor != null) {
            for (String name : notInterceptor) {
                interceptNameList.remove(name);
            }
        }

        LinkedList<Interceptor> interceptList = new LinkedList<>();
        for (String name : interceptNameList) {
            Interceptor interceptor = (Interceptor) BEAN_FACTORY.getBean(name, namespace);
            if (interceptor == null) {
                log.error("not find interceptors name:" + name + " namespace:" + namespace);
            } else {
                interceptList.addLast(interceptor);
            }
        }

        //放入标签注入的拦截器
        Method method = actionProxy.getMethod();
        if (actionProxy.getMethod() != null) {
            Intercept intercept = method.getAnnotation(Intercept.class);
            if (intercept != null) {
                Interceptor interceptor = EnvFactory.getBeanFactory().getBean(intercept.value(), intercept.namespace());
                if (interceptor != null) {
                    interceptList.addLast(interceptor);
                }
            }
        }
        interceptors = interceptList.iterator();
        /////////放入拦截器 end
    }

    /**
     * 初始化
     *
     * @throws Exception 异常
     */
    @Override
    public void initAction() throws Exception {
        ////////////////////////////// 加载参数
        Action action = actionProxy.getAction();
        if (action == null) {
            log.error("TXWeb不能创建类," + actionProxy.getActionName());
            throw new Exception("ERROR :TXWeb不能创建类");
        }
        TXWebUtil.copyRequestProperty(action);
        //////////////////////////////
    }

    /**
     * 更具配置查找ioc配置，返回配置bean
     *
     * @return 得到配置返回对象
     * @throws Exception 异常
     */
    private ResultSupport getResultSupport() throws Exception {
        //外部配置优选
        ResultConfigBean resultBean = actionConfig.getResultConfig(resultCode);
        //注释配置
        if (resultBean == null) {
            Redirect redirect = actionProxy.getAction().getClass().getAnnotation(Redirect.class);
            if (redirect != null) {
                String beanName = RESULT_MAP.get(redirect.type());
                if (beanName != null) {
                    ResultSupport resultSupport = (ResultSupport) ClassUtil.newInstance(beanName);
                    if (!StringUtil.isNull(redirect.location())) {
                        ResultConfigBean tempResultBean = new ResultConfigBean();
                        tempResultBean.setName(StringUtil.ASTERISK);
                        tempResultBean.setType(redirect.type());
                        tempResultBean.setValue(redirect.location());
                        resultSupport.setResultConfig(tempResultBean);
                    }
                    return resultSupport;
                }
            }
        }
        //使用默认配置
        if (resultBean == null) {
            List<ResultConfigBean> list = WEB_CONFIG_MANAGER.getDefaultResults(namespace);
            if (list != null && resultCode != null) {
                for (ResultConfigBean resultConfigBean : list) {
                    if (resultConfigBean.getName().equalsIgnoreCase(resultCode)) {
                        resultBean = resultConfigBean;
                        break;
                    }
                }
            }
            //如果一个都没有就使用 resultCode 来查找默认返回方式
            if (resultBean == null && resultCode != null) {
                String beanName = RESULT_MAP.get(resultCode);
                if (StringUtil.hasLength(beanName)) {
                    return (ResultSupport) ClassUtil.newInstance(beanName);
                }
                return null;
            }
        }


        if (resultBean == null) {
            return null;
        }

        String resultType = resultBean.getType();
        //如果存在配置的，就载入配置参数
        ResultSupport resultSupport = null;
        //得到返回对应类型
        String beanName = RESULT_MAP.get(resultType);
        if (beanName != null) {
            resultSupport = (ResultSupport) ClassUtil.newInstance(beanName);
        }
        ////自定义扩展返回方式 begin
        if (resultSupport == null) {
            resultSupport = (ResultSupport) BEAN_FACTORY.getBean(resultType, namespace);
        }
        if (resultSupport == null) {
            log.error("没有找到返回配置bean对象 " + resultType + ",not found result config " + resultType);
            throw new Exception("没有找到返回配置bean对象 " + resultType + ",not found result config " + resultType);
        }
        resultSupport.setResultConfig(resultBean);
        return resultSupport;
        //自定义扩展返回方式 end
    }

    /**
     * @return 得到action name
     */
    @Override
    public String getActionName() {
        return actionProxy.getActionName();
    }

    /**
     * @return 得到命名空间
     */
    @Override
    public String getNamespace() {
        return actionProxy.getNamespace();
    }

    /**
     * @return 拦截器拦截并运行
     * @throws Exception 异常
     */
    @Override
    public String invoke() throws Exception {
        if (executed) {
            return resultCode;
        }
        if (interceptors != null && interceptors.hasNext()) {
            Interceptor interceptor = interceptors.next();
            try {
                interceptor.init();
                resultCode = interceptor.intercept(this);
            } finally {
                interceptor.destroy();
            }
        } else {
            executed = true;
            Method method = actionProxy.getMethod();
            if (method != null && method.getAnnotation(Async.class)!=null) {
                //开启异步执行
                DaemonThreadFactory threadFactory = new DaemonThreadFactory(method + "" + this.hashCode());
                Thread thread = threadFactory.newThread(() -> {
                            try {
                                resultCode = actionProxy.execute();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                );
                thread.start();
                Thread.sleep(20);
                thread.interrupt();
                return ActionSupport.NONE;
            } else {
                resultCode = actionProxy.execute();
            }
        }
        return resultCode;
    }

    /**
     * 执行返回
     *
     * @throws Exception 异常
     */
    @Override
    public void executeResult(Result result) throws Exception {
        if (resultCode == null || ActionSupport.NONE.equalsIgnoreCase(resultCode)) {
            return;
        }

        if (ActionSupport.LOGIN.equalsIgnoreCase(resultCode) || ActionSupport.UNTITLED.equalsIgnoreCase(resultCode)) {
            printResultError(actionProxy.getAction(), resultCode);
            return;
        }

        try {
            if (result != null) {
                result.execute(this);
            } else {
                result = getResultSupport();
                if (result == null) {
                    if (RocHandle.NAME.equalsIgnoreCase(actionProxy.getExeType())) {
                        result = new RocResult();
                    } else if (CommandHandle.NAME.equalsIgnoreCase(actionProxy.getExeType())) {
                        result = new RocResult();
                    } else if (MarkdownHandle.NAME.equalsIgnoreCase(actionProxy.getExeType())) {
                        result = new MarkdownResult();
                    } else {
                        if (RequestUtil.isRocRequest(this.actionProxy.getAction().getRequest())) {
                            result = new RocResult();
                        } else {
                            result = new TemplateResult();
                        }
                    }
                }
                result.execute(this);
            }
        } finally {
            actionProxy.destroy();
        }
    }


    /**
     * 打印错误信息,登录和无权限
     *
     * @throws Exception 异常
     */
    private void printResultError(Action action, String resultCode) throws Exception {

        if (ActionSupport.LOGIN.equalsIgnoreCase(resultCode) && StringUtil.isNull(action.getFailureMessage())) {
            action.addFieldInfo(Environment.warningInfo, "请登陆");
        }
        if (ActionSupport.UNTITLED.equalsIgnoreCase(resultCode) && StringUtil.isNull(action.getFailureMessage())) {
            action.addFieldInfo(Environment.warningInfo, "没有权限");
        }
        if (RequestUtil.isRocRequest(action.getRequest())) {
            if (ActionSupport.LOGIN.equalsIgnoreCase(resultCode)) {

                TXWebUtil.print(new JSONObject(RocResponse.error(ErrorEnumType.NEED_LOGIN.getValue(), action.getFailureMessage())),
                        WebOutEnumType.JSON.getValue(), action.getResponse(), HttpStatusType.HTTP_status_401);
            }
            if (ActionSupport.UNTITLED.equalsIgnoreCase(resultCode)) {
                TXWebUtil.print(new JSONObject(RocResponse.error(ErrorEnumType.POWER.getValue(), action.getFailureMessage())).toString(),
                        WebOutEnumType.JSON.getValue(), action.getResponse(), HttpStatusType.HTTP_status_403);
            }
        } else {
            String loginUrl = EnvFactory.getEnvironmentTemplate().getString(Environment.userLoginUrl);
            if (ActionSupport.LOGIN.equalsIgnoreCase(resultCode) && !StringUtil.isEmpty(loginUrl)) {
                RedirectResult redirectResult = new RedirectResult();
                redirectResult.setUrl(loginUrl);
                redirectResult.execute(this);
                return;
            }
            String untitledUrl = EnvFactory.getEnvironmentTemplate().getString(Environment.untitledUrl);
            if (ActionSupport.UNTITLED.equalsIgnoreCase(resultCode) && !StringUtil.isEmpty(untitledUrl)) {
                RedirectResult redirectResult = new RedirectResult();
                redirectResult.setUrl(untitledUrl);
                redirectResult.execute(this);
                return;
            }
            Result result = new ErrorResult();
            result.execute(this);
        }
    }

}