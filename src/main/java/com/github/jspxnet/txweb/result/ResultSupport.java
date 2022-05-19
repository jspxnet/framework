/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.result;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.Result;
import com.github.jspxnet.txweb.annotation.Redirect;
import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.util.*;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 9:16:23
 * Slf4j
 */
@Slf4j
public abstract class ResultSupport implements Result {
    final static protected EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();
    final static protected boolean DEBUG = ENV_TEMPLATE.getBoolean(Environment.DEBUG);
    //json 无对应返回标识
    private static final String KEY_GRID = "grid";
    static final String KEY_ROC_XML = "xml"; //xml 需要设置 json 为默认


    private static final String[] GRID_METHODS = new String[]{"currentPage", "count", "totalCount", "sort", "list"};

    public ResultSupport() {

    }

    private ResultConfigBean resultConfig = null;

    @Override
    public ResultConfigBean getResultConfig() {
        return resultConfig;
    }

    @Override
    public void setResultConfig(ResultConfigBean resultConfig) {
        this.resultConfig = resultConfig;
    }


    /**
     * 得到配置的跳转路径
     *
     * @param actionInvocation 得到配置的地址
     * @return 返回响应对应的地址
     * 解析错误
     */
    String getConfigLocationUrl(ActionInvocation actionInvocation) {
        ActionProxy actionProxy = actionInvocation.getActionProxy();
        Action action = actionProxy.getAction();
        ResultConfigBean resultConfigBean = getResultConfig();
        if (resultConfigBean == null) {
            return StringUtil.empty;
        }

        String location = resultConfigBean.getValue();

        //设置变量在 KEY_RedirectUrl 中，将以这个变量为准
        if (StringUtil.isNull(location)) {
            location = action.getEnv(ActionEnv.KEY_RedirectUrl);
        }

        //注释方式
        if (StringUtil.isNull(location)) {
            Redirect redirect = action.getClass().getAnnotation(Redirect.class);
            if (redirect != null && !StringUtil.isNull(redirect.location())) {
                location = redirect.location();
            }
        }
        if (StringUtil.isNull(location)) {
            return "/";
        }
        if (location.contains("${") && location.contains("}")) {
            EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
            Map<String, Object> valueMap = new HashMap<>(envTemplate.getVariableMap());
            valueMap.putAll(action.getEnv());
            valueMap.put("action", action);
            Placeholder placeholder = EnvFactory.getPlaceholder();
            try {
                location = placeholder.processTemplate(valueMap, location);
            } catch (Exception e) {
                e.printStackTrace();
                return StringUtil.empty;
            }
        }
        return location;
    }
    //-----------------------------------------------------------------------------------------------------------

    /**
     * 解析得到ROC要返回的数据,这里是快捷的url参数方式
     * 先返回给前台来判断输出方式
     *
     * @param actionInvocation ActionInvocation
     * @return JSONObject
     */
    public static Object getRocAutoResult(ActionInvocation actionInvocation) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        ActionProxy actionProxy = actionInvocation.getActionProxy();
        Action action = actionProxy.getAction();
        String resultMethods = action.getString(ActionEnv.Key_ResultMethods);
        if (StringUtil.isNull(resultMethods)) {
            resultMethods = action.getString(ActionEnv.Key_ResultMethods);
        }
        //如果有错误信息，先返回错误信息
        //什么都没有的情况返回提示信息
        if (actionContext.hasFieldInfo()) {
            return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), actionContext.getFieldInfo());
        }
        if (KEY_GRID.equalsIgnoreCase(resultMethods)) {
            //默认的表格调用方式
            JSONObject json = new JSONObject();
            for (String callMethod : GRID_METHODS) {
                if (StringUtil.isNull(callMethod)) {
                    continue;
                }
                if (ClassUtil.isDeclaredMethod(ClassUtil.getClass(action.getClass()), ClassUtil.METHOD_NAME_GET + StringUtil.capitalize(callMethod))) {
                    json.put(callMethod, BeanUtil.getProperty(action, callMethod));
                }
            }
            return json;
        } else if (!StringUtil.isNull(resultMethods)) {
            //简单的URL直接调用
            String[] methods = StringUtil.split(resultMethods, StringUtil.SEMICOLON);
            JSONObject json = new JSONObject();
            for (String cMethod : methods) {
                if (!StringUtil.hasLength(cMethod)) {
                    continue;
                }
                String exeMethodName = cMethod;
                if (!exeMethodName.startsWith(ClassUtil.METHOD_NAME_GET)) {
                    exeMethodName = ClassUtil.METHOD_NAME_GET + StringUtil.capitalize(cMethod);
                }
                String methodFiled = ClassUtil.getMethodFiledName(exeMethodName);
                if (StringUtil.isNull(methodFiled)) {
                    methodFiled = exeMethodName;
                }
                if (ClassUtil.isDeclaredMethod(action.getClass(), exeMethodName)) {
                    json.put(methodFiled, BeanUtil.getProperty(action, exeMethodName));
                }
            }
            return json;
        } else if (actionContext.getResult()!=null) {
            //程序设置后指定返回，roc 配置的返回会在这里返回
            //发现一个问题JSONObject在这里会变成HashMap
             return actionContext.getResult();
        } else {
            //什么都没有的情况返回提示信息
            Method method = actionContext.getMethod();
            if (actionContext.hasFieldInfo())
            {
                //有错误信息
                return RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), actionContext.getFailureMessage());
            } else
            if (method==null||ActionEnv.DEFAULT_EXECUTE.equals(method.getName())&&actionContext.getResult()==null)
            {
                //没有找到执行的方法
                return RocResponse.error(ErrorEnumType.CALL_API.getValue(), "未知的接口").setStatus(HttpStatusType.HTTP_status_404);
            }
            else
            {
                return RocResponse.success(actionContext.getResult(), actionContext.getSuccessMessage());
            }
        }
    }


    /**
     * 载入环境变量
     *
     * @param action   action
     * @param valueMap 环境变量
     */
    static void initPageEnvironment(Action action, Map<String, Object> valueMap) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        valueMap.put(ActionEnv.Key_Request, new RequestMap(actionContext.getRequest()));
        valueMap.put(ActionEnv.Key_Response, RequestUtil.getResponseMap(actionContext.getResponse()));
        valueMap.put(ActionEnv.Key_Session, new SessionMap(actionContext.getRequest().getSession()));
        valueMap.put(ActionEnv.Key_Config, action.getConfig());
        valueMap.put(ActionEnv.Key_Language, action.getLanguage());
        valueMap.put(ActionEnv.Key_Option, action.getOption());
        valueMap.put(ActionEnv.Key_This, action);
    }


    /**
     * 判断是否让浏览器保持缓存
     * @param actionContext 上下文
     */
    static void checkCache(ActionContext actionContext) {
        String browserCache = actionContext.getString(ActionEnv.BROWSER_CACHE);
        if (!StringUtil.isNull(browserCache) && !StringUtil.toBoolean(browserCache)) {
            HttpServletResponse response = actionContext.getResponse();
            response.setHeader("Pragma", "No-cache");
            response.setHeader("Cache-Control", "no-cache,must-revalidate");
            response.setDateHeader("Expires", 0);
        }
    }

    static JSONObject getResultJson(Object methodResult) {
        JSONObject json;
        if (methodResult == null) {
            json = new JSONObject();
        } else if (methodResult instanceof JSONObject) {
            json = (JSONObject) methodResult;
        } else if (methodResult instanceof RocResponse) {
            json = new JSONObject(methodResult);
        } else {
            json = new JSONObject(RocResponse.success(methodResult));
        }
        return json;
    }


}