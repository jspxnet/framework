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
import com.github.jspxnet.txweb.*;
import com.github.jspxnet.txweb.annotation.Async;
import com.github.jspxnet.txweb.annotation.IgnoreIntercept;
import com.github.jspxnet.txweb.annotation.Intercept;
import com.github.jspxnet.txweb.annotation.Redirect;
import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.txweb.config.TxWebConfigManager;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.DefultContextHolderStrategy;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dispatcher.handle.*;
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
    final private ActionConfig actionConfig;

    private Iterator<Interceptor> interceptors;

    @Override
    public ActionProxy getActionProxy() {
        return actionProxy;
    }

    @Override
    public ActionConfig getActionConfig() {
        return actionConfig;
    }

    public DefaultActionInvocation(ActionConfig actionConfig, Map<String, Object> params, String exeType, JSONObject jsonData, HttpServletRequest request, HttpServletResponse response) throws Exception {
        DefultContextHolderStrategy.createContext(request,response,params);
        this.actionProxy = new DefaultActionProxy();
        String namespace = (String)params.getOrDefault(ActionEnv.Key_Namespace,actionConfig.getNamespace());

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
        //必须先设置action

        params.put(ActionEnv.Key_CallRocJsonData, jsonData);
        action.initEnv(params,exeType);

        //判断是否使用手机模式切换模版
        if (actionConfig.isMobile()) {
            action.put(ActionEnv.KEY_MobileTemplate, true);
        }

        //////////////载入配置参数 begin
        Placeholder placeholder = EnvFactory.getPlaceholder();
        Map<String, Object> paraMap = actionConfig.getParam();
        for (String pKey : paraMap.keySet()) {
            if (ActionEnv.Key_CallRocJsonData.equalsIgnoreCase(pKey))
            {
                continue;
            }
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

        //////////////载入配置参数 end

        //上传方式比较特殊 选方参数,保证上传的设置 begin
        TXWebUtil.setMulRequest(action);
        //上传方式比较特殊 选方参数,保证上传的设置 end

        //放入名称
        actionProxy.setCaption(actionConfig.getCaption());

        //放入action
        actionProxy.setAction(action);


        //设置执行方法 begin
        if ((exeType.equalsIgnoreCase(RocHandle.NAME) || exeType.equalsIgnoreCase(RsaRocHandle.NAME) || exeType.equalsIgnoreCase(CommandHandle.NAME)) && jsonData != null) {
            //当配置有方法的时候，只能执行配置中的方法，不能执行其他动作,避免安全漏洞
            //兼容简单方式 {"jsonrpc": "2.0", "method": "sum", "params": [1,2,4], "id": "1"},
            //如果ROC请求,调用访问为空，默认使用@表示action名称

            //先放入参数 begin
            TXWebUtil.putJsonParams(action,jsonData);
            //先放入参数 end

            String actionName = action.getEnv(ActionEnv.Key_ActionName);
            //这里只是做安全配置检查，屏蔽调用不允许的方法
            String requestMethodName;
            JSONObject methodJson = jsonData.getJSONObject(Environment.rocMethod);
            if (methodJson != null) {
                requestMethodName = methodJson.getString(Environment.rocName,actionName);
            } else {
                requestMethodName = jsonData.getString(Environment.rocMethod,actionName);
            }
            actionProxy.setMethod(requestMethodName);
        } else {
            //传统方式,如果发现是一个Roc配置的
            String methodName = actionConfig.getMethod();
            String actionName = action.getEnv(ActionEnv.Key_ActionName);
            if (TXWebUtil.AT.equals(methodName) && !StringUtil.isEmpty(actionName)) {
                actionProxy.setMethod(actionName);
            } else if(methodName!=null&&methodName.startsWith(TXWebUtil.AT)&&!StringUtil.isEmpty(StringUtil.substringAfter(methodName,TXWebUtil.AT)))
            {
                String reqMethod = StringUtil.substringAfter(methodName,TXWebUtil.AT);
                if (!StringUtil.isEmpty(reqMethod))
                {
                    actionProxy.setMethod(RequestUtil.getString(request,reqMethod,true));
                }
            } else
            {
                actionProxy.setMethod(methodName);
            }
            //这里处理直接配置的和 @开头的配置
        }
        //设置执行方法 end

        //设置默认的返回方式 begin
        if (exeType.equalsIgnoreCase(MarkdownHandle.NAME)) {
            action.setActionResult(ActionSupport.Markdown);
        }  else if (exeType.equalsIgnoreCase(ActionHandle.NAME)) {
            action.setActionResult(ActionSupport.INPUT);
        }
        //设置默认的返回方式 end

        this.actionConfig = actionConfig;
        interceptors = createInterceptorList(namespace);
    }

    /**
     * 拦截器初始化
     * @param namespace 命名空间
     * @return 拦截器列表
     */
    private Iterator<Interceptor> createInterceptorList(String namespace) {

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

        final LinkedList<Interceptor> interceptList = new LinkedList<>();
        for (String name : interceptNameList) {
            Interceptor interceptor = (Interceptor) BEAN_FACTORY.getBean(name, namespace);
            if (interceptor == null) {
                log.error("not find interceptors name:" + name + " namespace:" + namespace);
            } else {
                interceptList.addLast(interceptor);
            }
        }

        //放入标签注入的拦截器
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext!=null)
        {
            Method exeMethod = actionContext.getMethod();
            if (exeMethod != null) {
                Intercept intercept = actionContext.getMethod().getAnnotation(Intercept.class);
                if (intercept != null) {
                    String interceptNamespace = intercept.namespace();
                    if (StringUtil.isNull(interceptNamespace))
                    {
                        interceptNamespace = namespace;
                    }
                    Interceptor interceptor = EnvFactory.getBeanFactory().getBean(intercept.value(), interceptNamespace);
                    if (interceptor != null) {
                        interceptList.addLast(interceptor);
                    }
                }

                IgnoreIntercept ignoreIntercept = exeMethod.getAnnotation(IgnoreIntercept.class);
                if (ignoreIntercept != null && ignoreIntercept.value() !=null) {
                    String name = ignoreIntercept.value().getName();
                    for (int i=interceptList.size()-1;i>=0;i--)
                    {
                        Interceptor interceptor = interceptList.get(i);
                        if (interceptor.getClass().getName().equals(name))
                        {
                            synchronized (this)
                            {
                                interceptList.remove(interceptor);
                            }
                        }
                    }
                }
            }
        }

        /////////放入拦截器 end
        return interceptList.iterator();
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
            log.error("TXWeb不能创建类:{}",action);
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
        ActionContext actionContext = ThreadContextHolder.getContext();
        //外部配置优选
        ResultConfigBean resultBean = actionConfig.getResultConfig(actionContext.getActionResult());
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
            List<ResultConfigBean> list = WEB_CONFIG_MANAGER.getDefaultResults(actionContext.getNamespace());
            if (list != null && actionContext.getActionResult() != null) {
                for (ResultConfigBean resultConfigBean : list) {
                    if (resultConfigBean.getName().equalsIgnoreCase(actionContext.getActionResult())) {
                        resultBean = resultConfigBean;
                        break;
                    }
                }
            }
            //如果一个都没有就使用 resultCode 来查找默认返回方式
            if (resultBean == null && actionContext.getActionResult() != null) {
                String beanName = RESULT_MAP.get(actionContext.getActionResult());
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
            resultSupport = (ResultSupport) BEAN_FACTORY.getBean(resultType, actionContext.getNamespace());
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
     * @return 拦截器拦截并运行
     */
    @Override
    public String invoke()  {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext.isExecuted()) {
            return actionContext.getActionResult();
        }
        if (actionContext.getResult() instanceof Exception) {
            return ActionSupport.ERROR;
        }
        try {
            if (interceptors != null && interceptors.hasNext()) {
                Interceptor interceptor = interceptors.next();
                try {
                    interceptor.init();
                    actionContext.setActionResult(interceptor.intercept(this));
                } finally {
                    interceptor.destroy();
                }
            } else {

                Method method = actionContext.getMethod();
                if (method != null && method.getAnnotation(Async.class)!=null) {
                    //开启异步执行
                    DaemonThreadFactory threadFactory = new DaemonThreadFactory(method + "" + this.hashCode());
                    Thread thread = threadFactory.newThread(() -> {
                                try {
                                    actionContext.setActionResult(actionProxy.execute(actionContext));
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
                    actionContext.setActionResult(actionProxy.execute(actionContext));
                }
            }
        } catch (Exception e)
        {
            interceptors = null;
            actionContext.setResult(e);
            actionContext.setActionResult(ActionSupport.ERROR);
            return ActionSupport.ERROR;
        }
        return actionContext.getActionResult();
    }

    /**
     * 执行返回
     *
     * @throws Exception 异常
     */
    @Override
    public void executeResult(Result result) throws Exception {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext==null)
        {
            return;
        }
        try {
            if (ActionSupport.NONE.equalsIgnoreCase(actionContext.getActionResult())) {
                return;
            }
            if (ActionSupport.LOGIN.equalsIgnoreCase(actionContext.getActionResult()) || ActionSupport.UNTITLED.equalsIgnoreCase(actionContext.getActionResult())) {
                printResultError(actionProxy.getAction(), actionContext.getActionResult());
                return;
            }
            if (result != null) {
                result.execute(this);
            } else {
                result = getResultSupport();
                if (result == null) {
                    if (RocHandle.NAME.equalsIgnoreCase(actionContext.getExeType())) {
                        result = new RocResult();
                    } else if (CommandHandle.NAME.equalsIgnoreCase(actionContext.getExeType())) {
                        result = new RocResult();
                    } else if (MarkdownHandle.NAME.equalsIgnoreCase(actionContext.getExeType())) {
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
            } else
            if (ActionSupport.UNTITLED.equalsIgnoreCase(resultCode)) {

                RocResponse<?> rocResponse = RocResponse.error(ErrorEnumType.POWER.getValue(), action.getFailureMessage());
                rocResponse.setProperty("isGuest", NumberUtil.toString(ObjectUtil.toInt(action.isGuest())));
                TXWebUtil.print(new JSONObject(rocResponse).toString(),
                        WebOutEnumType.JSON.getValue(), action.getResponse(), HttpStatusType.HTTP_status_403);
            }
        } else {
            String loginUrl = EnvFactory.getEnvironmentTemplate().getString(Environment.userLoginUrl);
            String untitledUrl = EnvFactory.getEnvironmentTemplate().getString(Environment.untitledUrl);
            if (ActionSupport.LOGIN.equalsIgnoreCase(resultCode) && !StringUtil.isEmpty(loginUrl)) {
                RedirectResult redirectResult = new RedirectResult();
                redirectResult.setUrl(loginUrl);
                redirectResult.execute(this);
            } else
            if (ActionSupport.UNTITLED.equalsIgnoreCase(resultCode) && !StringUtil.isEmpty(untitledUrl)) {
                RedirectResult redirectResult = new RedirectResult();
                redirectResult.setUrl(untitledUrl);
                redirectResult.execute(this);
            } else
            {
                Result result = new ErrorResult();
                result.execute(this);
            }
        }
    }

    /**
     *  只为了兼容老版本
     * @return 返回是否已经执行
     */
    @Override
    public boolean isExecuted() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.isExecuted();
    }


    /**
     * 只为了兼容
     * @return 返回action name
     */
/*
    @Deprecated
    @Override
    public String getActionName() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getActionName();
    }
*/

}