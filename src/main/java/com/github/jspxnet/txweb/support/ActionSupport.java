/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.support;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.txweb.*;

import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;

import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.bundle.Bundle;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.txweb.table.Role;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.txweb.util.ActionLogUtil;
import com.github.jspxnet.txweb.util.ParamUtil;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-26
 * Time: 17:23:13
 * InterceptorSupport
 */
public abstract class ActionSupport implements Action {


    public ActionSupport() {

    }


    @Override
    public void initialize() {

    }

    @Override
    public void destroy() {
        try {
            ActionContext actionContext = ThreadContextHolder.getContext();
            if (actionContext==null)
            {
                return;
            }
            actionContext.clean();
            actionContext.setResult(null);
            actionContext.setActionResult(null);
            actionContext.setRequest(null);
            actionContext.setResponse(null);
            actionContext.setExecuted(false);
        } finally {
            ThreadContextHolder.clearContext();
        }
    }

    protected OnlineManager onlineManager;

    @Ref
    public void setOnlineManager(OnlineManager onlineManager) {
        this.onlineManager = onlineManager;
    }
    //采用方法注入目的是让外部可以重载

    //设置为false表示不使用模板
    @Ref(name = Environment.language, test = true)
    protected Bundle language;

    public void setLanguage(Bundle language) {
        this.language = language;
    }

    protected Bundle config;
    @Ref(name = Environment.config, test = true)
    public void setConfig(Bundle config) {
        this.config = config;
    }

    @Ref(name = Environment.option, test = true)
    protected Option option;

    //这里要放置ajax方式调用出去

    //兼容页面模式用的
    /**
     *
     * @return 配置
     */
    @Override
    public Bundle getConfig() {
        return config;
    }

    /**
     *
     * @return 语言库
     */
    @Override
    public Bundle getLanguage() {
        return language;
    }

    /**
     *
     * @return 字典表
     */
    @Override
    public Option getOption() {
        return option;
    }

    /**
     * @return 得到国家识别码
     */
    @Override
    public String getLocaleName() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return RequestUtil.getLocale(actionContext.getRequest());
    }

    /**
     * @return 当前输出编码
     */
    @Override
    public String getEncode() {
        return Dispatcher.getEncode();
    }

    /**
     * @return 得到应用的根目录名
     */
    @Override
    public String getRootNamespace() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return URLUtil.getRootNamespace(actionContext.getNamespace());
    }

    @Override
    public boolean isComponent()
    {
        ActionContext actionContext = ThreadContextHolder.getContext();
        Map<String, Object> actionEnv = actionContext.getComponentEnvironment(this.getClass(),this.hashCode());
        return ActionEnv.COMPONENT_MODEL.equalsIgnoreCase((String)actionEnv.get(ActionEnv.ACTION_RUN_MODEL));
    }

    /**
     * @return 得到环境空间
     */
    @Deprecated
    @Override
    public Map<String, Object> getEnv() {
        return ThreadContextHolder.getContext().getEnvironment();
    }

    @Override
    public void initEnv(Map<String, Object> paramMap, String exeType) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.setExeType(exeType);
        actionContext.getEnvironment().putAll(paramMap);
    }
    /**
     * @param key 放入环境变量
     * @param obj 放入环境对象
     */
    @Override
    public void put(String key, Object obj) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.put(key, obj);
    }

    /**
     * @param key 变量名称
     * @return 判断是否存在环境变量
     */

    @Override
    public boolean containsKey(String key) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.containsKey(key);
    }


    /**
     * @param environment 放入全新的环境变量
     */
    @Override
    public void setEnv(Map<String, Object> environment) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.getEnvironment().clear();
        actionContext.getEnvironment().putAll(environment);
    }


    @Override
    public String getEnv(String keys) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        String o = actionContext.getString(keys);
        if (o == null) {
            return StringUtil.empty;
        }
        return o;
    }

    /**
     * @return 得到用户在线信息
     */
    @Override
    public UserSession getUserSession() {
        //调整到cahce里边保存，不保存在session里边了，session里边只保存关联ID
        AssertException.isNull(onlineManager,"配置不完整,onlineManager在容器中没有找到");
        return onlineManager.getUserSession(this);
    }

    /**
     * @return 判断是否为游客
     */
    @Override
    public boolean isGuest()
    {
        return isGuest(getUserSession());
    }


    private static boolean isGuest(IUserSession userSession) {
        return userSession == null || userSession.isGuest();
    }
    ////////////提交错误的时候显示的错误字段信息 begin  如果使用校验后错误信息会保存在这里边

    /**
     * @return 得到错误信息
     */
    @Override
    public Map<String, String> getFieldInfo() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getFieldInfo();
    }

    /**
     * 添加提示信息或者错误错误类型
     *
     * @param keys 错误类型
     * @param msg  信息
     */
    @Override
    public void addFieldInfo(String keys, String msg) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.addFieldInfo(keys,msg);
    }

    /**
     * @param errors 放入错误信息
     */
    @Override
    public void addFieldInfo(Map<String, String> errors) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.addFieldInfo(errors);
    }

    /**
     * @return 判断是否有错误信息
     */
    @Override
    public boolean hasFieldInfo() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext==null)
        {
            return false;
        }
        return actionContext.hasFieldInfo();
    }


    /**
     * 满足ROC方式
     *
     * @return 失败消息
     */
    @Override
    public String getFailureMessage() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getFailureMessage();
    }
    ////////////提交错误的时候显示的错误字段信息 end

    ////////////信息文字说明

    /**
     * @param msg 添加说明信息
     */
    @Override
    public void addActionMessage(String msg) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.addActionMessage(msg);
    }

    /**
     * @return 说明信息列表
     */
    @Override
    public List<String> getActionMessage() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext==null)
        {
            return new ArrayList<>(0);
        }
        return actionContext.getActionMessage();
    }

    @Override
    public String getSuccessMessage() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext==null)
        {
            return StringUtil.empty;
        }
        return actionContext.getSuccessMessage();
    }

    /**
     * @return 是否有说明信息
     */
    @Override
    public boolean hasActionMessage() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext==null)
        {
            return false;
        }
        return actionContext.hasActionMessage();
    }

    /**
     * @param value 放入日志对象
     */
    @Override
    public void setActionLogTitle(String value) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.getEnvironment().put(ActionEnv.Key_ActionLogTitle, value);
    }

    /**
     * @return 得到日志记录对象
     */
    @Override
    public String getActionLogTitle() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return (String) actionContext.getEnvironment().get(ActionEnv.Key_ActionLogTitle);
    }

    /**
     * @param value 放入日志对象
     */
    @Override
    public void setActionLogContent(Serializable value) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.getEnvironment().put(ActionEnv.Key_ActionLogContent, value);
    }

    /**
     *
     * @param value 机构id
     */
    @Override
    public void setOrganizeId(Serializable value) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.getEnvironment().put(ActionEnv.KEY_organizeId, value);
    }

    /**
     * @return 得到日志记录对象
     */
    @Override
    public Object getActionLogContent() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getEnvironment().get(ActionEnv.Key_ActionLogContent);
    }

    /**
     * @param value 当返回为excel等需要处理的数据是，这里放入数据 对象
     */
    @Override
    public void setResult(Object value) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext!=null&&!actionContext.hasFieldInfo() && ActionSupport.ERROR.equalsIgnoreCase(actionContext.getActionResult()))
        {
            actionContext.setResult(value);
        }
    }

    /**
     * 这里只是提供一个保存的空间，方便返回接口统一的到一个地方取数据
     *
     * @return 返回对象
     */
    @Override
    public Object getResult() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getResult();
    }

    @Override
    public HttpSession getSession() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext.getRequest()==null)
        {
            return null;
        }
        return actionContext.getRequest().getSession();
    }

    @Override
    public HttpServletRequest getRequest() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getRequest();
    }


    @Override
    public HttpServletResponse getResponse() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getResponse();
    }

    @Override
    public <T> T getBean(Class<T> cla) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData))
        {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return params.parseObject(cla,true);
            }
        }
        return RequestUtil.getBean(request, cla, false);
    }


    @Override
    public String getString(String name) {
        return getString(name, false);
    }

    @Override
    public String getString(String name, boolean checkSql) {
        return getString(name, StringUtil.empty, checkSql);
    }


    @Override
    public String getString(String name, String def, boolean checkSql) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null && params.containsKey(name)) {
                if (checkSql) {
                    return ParamUtil.getSafeFilter(params.getString(name,def), RequestUtil.paramMaxLength, SafetyEnumType.LOW);
                }
                return params.getString(name);
            }
        }
        return RequestUtil.getString(request, name, def, checkSql);
    }

    @Override
    public int getInt(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return params.getInt(name);
            }
        }
        return RequestUtil.getInt(request, name);
    }

    @Override
    public int getInt(String name, int def) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return params.getInt(name);
            }
        }
        return RequestUtil.getInt(request, name, def);
    }

    @Override
    public String[] getArray(String name, boolean checkSql) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), String[].class);
            }
        }
        return RequestUtil.getArray(request, name, checkSql);
    }

    @Override
    public int[] getIntArray(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), int[].class);
            }
        }
        return RequestUtil.getIntArray(request, name);
    }

    @Override
    public int[] getIntArray(String name, int[] defArray) {
        int[] ids = getIntArray(name);
        if (ArrayUtil.isEmpty(ids)) {
            return defArray;
        }
        return ids;
    }

    @Override
    public Integer[] getIntegerArray(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), Integer[].class);
            }
        }
        return RequestUtil.getIntegerArray(request, name);
    }

    @Override
    public Integer[] getIntegerArray(String name, Integer[] defArray) {
        Integer[] ids = getIntegerArray(name);
        if (ArrayUtil.isEmpty(ids)) {
            return defArray;
        }
        return ids;
    }

    @Override
    public Long[] getLongArray(String name, Long[] defArray) {
        Long[] ids = getLongArray(name);
        if (ArrayUtil.isEmpty(ids)) {
            return defArray;
        }
        return ids;
    }

    @Override
    public Long[] getLongJoinArray(String name1, String name2) {
        Long[] ids1 = getLongArray(name1);
        Long[] ids2 = getLongArray(name2);
        return ArrayUtil.join(ids1, ids2);
    }

    @Override
    public Long[] getLongArray(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), Long[].class);
            }
        }
        return RequestUtil.getLongArray(request, name);
    }

    @Override
    public float[] getFloatArray(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), float[].class);
            }
        }
        return RequestUtil.getFloatArray(request, name);
    }

    @Override
    public double[] getDoubleArray(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), double[].class);
            }
        }
        return RequestUtil.getDoubleArray(request, name);
    }

    @Override
    public Float[] getFloatObjectArray(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), Float[].class);
            }
        }
        return RequestUtil.getFloatObjectArray(request, name);
    }

    @Override
    public Double[] getDoubleObjectArray(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), Double[].class);
            }
        }
        return RequestUtil.getDoubleObjectArray(request, name);
    }

    @Override
    public double getDouble(String name, double def) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), double.class);
            }
        }
        return RequestUtil.getDouble(request, name, def);
    }

    @Override
    public BigDecimal[] getBigDecimalArray(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return BeanUtil.getTypeValue(params.get(name), BigDecimal[].class);
            }
        }
        return ArrayUtil.getBigDecimalArray(RequestUtil.getDoubleObjectArray(request, name));
    }

    @Override
    public long getLong(String name) {
        return getLong(name, 0L);
    }

    @Override
    public long getLong(String name, long def) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return params.getLong(name);
            }
        }
        return RequestUtil.getLong(request, name, def);
    }

    @Override
    public float getFloat(String name, float def) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return params.getFloat(name);
            }
        }
        return RequestUtil.getFloat(request, name, def);
    }

    @Override
    public boolean getBoolean(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                return params.getBoolean(name);
            }
        }
        return RequestUtil.getBoolean(request, name);
    }

    @Override
    public Date getDate(String name) {
        return getDate(name, null);
    }

    @Override
    public Date getDate(String name, String format) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                try {
                    return StringUtil.getDate(params.getString(name), format);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return RequestUtil.getDate(request, name, format, DateUtil.empty);
    }

    @Override
    public String[] getParameterNames() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (!RequestUtil.isMultipart(request)&&actionContext.containsKey(ActionEnv.Key_CallRocJsonData)) {
            JSONObject params = actionContext.getJsonParams();
            if (params != null) {
                try {
                    return params.keySet().toArray(new String[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return RequestUtil.getParameterNames(request);
    }

    @Override
    public String[] getAttributeNames() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return RequestUtil.getAttributeNames(actionContext.getRequest());
    }

    @Override
    public boolean isMobileBrowser() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return RequestUtil.isMobileBrowser(actionContext.getRequest());
    }

    @Override
    public boolean containsUserAgent(String str) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        if (request == null || str == null) {
            return false;
        }
        String s = request.getHeader("User-Agent");
        return StringUtil.hasLength(s) && s.toLowerCase().contains(str.toLowerCase());
    }

    /**
     * @param name 参数
     * @return 满足ajax 请求转换,方便数组传递
     */
    @Override
    public String toQueryString(String name) {
        String[] array = getArray(name, false);
        StringBuilder sb = new StringBuilder();
        for (String value : array) {
            sb.append(name).append(StringUtil.EQUAL).append(URLUtil.getUrlEncoder(value, Dispatcher.getEncode())).append(StringUtil.AND);
        }
        if (sb.toString().endsWith(StringUtil.AND)) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * @param param 参数
     * @return 转换为参数格式
     */
    @Override
    public String toQueryString(Map<String, String> param) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        return RequestUtil.toQueryString(request, param);
    }

    /**
     * @return 得到ip
     */
    @Override
    public String getRemoteAddr() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext==null)
        {
            return "127.0.0.1";
        }
        HttpServletRequest request = actionContext.getRequest();
        return RequestUtil.getRemoteAddr(request);
    }

    /**
     * 在拦截器里边放入如下代码就可以保存日志了
     * actionLog.setCaption(actionInvocation.getCaption());
     * actionLog.setClassMethod(operation);
     * actionLog.setMethodCaption(TXWebUtil.getMethodCaption(getClass(),operation));
     * actionLog.setNamespace(JUWebIoc.namespace);
     * actionLog.setPutName(userSession.getName());
     * actionLog.setPutUid(userSession.getUid());
     *
     * @return 得到一个已经初始化好的动作日志
     */
    @Override
    public ActionLog getActionLog() {
        if (hasFieldInfo()) {
            return null;
        }
        return ActionLogUtil.createActionLog(this);
    }

    /**
     * @param level 级数
     * @return 得到路径
     */
    @Override
    public String getPathLevel(int level) {
        level = level - 1;
        if (level < 0) {
            level = SafetyEnumType.NONE.getValue();
        }
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletRequest request = actionContext.getRequest();
        String url = request.getRequestURI();
        String[] paths = StringUtil.split(url, StringUtil.BACKSLASH);
        if (paths.length >= level) {
            if (paths[level].endsWith(StringUtil.DOT + getEnv(Environment.filterSuffix))) {
                return FileUtil.getNamePart(paths[level]);
            }
            return paths[level];
        }
        return StringUtil.empty;
    }

    @Override
    public long getUrlNumber() {
        String actionName = getEnv(ActionEnv.Key_ActionName);
        return StringUtil.toLong(StringUtil.getNumber(actionName));
    }

    /**
     * @return 文件真实执行的方法，而不是预执行的方法
     */
    @Override
    public boolean isMethodInvoked() {
        if (isComponent())
        {
            return false;
        }
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext==null || actionContext.getMethod()==null)
        {
            return false;
        }
        return actionContext.isExecuted() && actionContext.getMethod().toString().contains("Action." + actionContext.getMethod().getName());
    }

    private String templatePath = null;

    @Override
    public void setTemplatePath(String templatePath) {
        this.templatePath = templatePath;
    }

    /**
     * @return 返回模版文件路径部分
     */
    @Override
    public String getTemplatePath() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (StringUtil.isNull(templatePath)) {
            StringBuilder sb = new StringBuilder();
            sb.append(Dispatcher.getRealPath());
            if (!TXWeb.global.equals(actionContext.getNamespace())) {
                sb.append("/").append(actionContext.getNamespace()).append("/");
            }
            return FileUtil.mendPath(sb.toString());
        }
        return templatePath;
    }

    /**
     * @return 返回模版文件的名称, 只有文件名称
     */
    @Override
    public String getTemplateFile() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        String actionName = actionContext.getActionName();
        if (StringUtil.isNull(actionName)) {
            return null;
        }
        if (ObjectUtil.toBoolean(actionContext.get(ActionEnv.KEY_MobileTemplate)) && isMobileBrowser()) {
            return actionName + StringUtil.DOT + ActionEnv.mobileTemplateSuffix + StringUtil.DOT + actionContext.getString(Environment.templateSuffix);
        }
        return actionName + StringUtil.DOT + actionContext.getString(Environment.templateSuffix);
    }

    /**
     * @param name Cookie名称
     * @return 得到Cookie值
     */
    @Override
    public String getCookie(String name) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return CookieUtil.getCookieString(actionContext.getRequest(), name, StringUtil.empty);
    }

    @Override
    public String getActionResult() {
        ActionContext actionContext = ThreadContextHolder.getContext();
        return actionContext.getActionResult();
    }

    /**
     * @param actionResult 放入 execute 返回标识
     */
    @Override
    public void setActionResult(String actionResult) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (actionContext!=null)
        {
            //&&ObjectUtil.isEmpty(actionContext.getActionResult())
            actionContext.setActionResult(actionResult);
        }
    }

    @Override
    public void print(Object html) {
        printError(html, HttpStatusType.HTTP_status_OK);
    }

    @Override
    public void printError(Object out, int status) {
        ActionContext actionContext = ThreadContextHolder.getContext();
        HttpServletResponse response = actionContext.getResponse();
        if (response == null) {
            System.err.println(out);
            return;
        }

        if (out instanceof JSONObject) {
            String info = ((JSONObject) out).toString(4);
            TXWebUtil.print(info, WebOutEnumType.JSON.getValue(), response, status);
        } else if (out instanceof RocResponse) {
            String info = new JSONObject(out).toString(4);
            TXWebUtil.print(info, WebOutEnumType.JSON.getValue(), response, status);
        }
        else
        {
            TXWebUtil.print(ObjectUtil.toString(out), WebOutEnumType.TEXT.getValue(), response, status);
        }
    }

    public IRole getRole() {
        UserSession userSession = getUserSession();
        if (userSession == null) {
            Role guestRole = new Role();
            guestRole.setId(config.getString(Environment.guestRole));
            guestRole.setName(language.getLang(Environment.guestName));
            guestRole.setUserType(UserEnumType.NONE.getValue());
            guestRole.setNamespace(getRootNamespace());
            return guestRole;
        }
        ActionContext actionContext = ThreadContextHolder.getContext();
        IRole role = userSession.getRole(getRootNamespace(),getString(ActionEnv.KEY_organizeId, (String)actionContext.getOrDefault(ActionEnv.KEY_organizeId,StringUtil.empty),true));
        if (role==null||role.getId()==null)
        {
            Role guestRole = new Role();
            guestRole.setId(config.getString(Environment.guestRole));
            guestRole.setName(language.getLang(Environment.guestName));
            guestRole.setUserType(UserEnumType.NONE.getValue());
            guestRole.setNamespace(getRootNamespace());
            return guestRole;
        }
        return role;
    }

    /**
     * execute方法为默认执行必须执行， Operate 不生效
     *
     * @return 返回
     * @throws Exception 异常
     */

    @Override
    public String execute() throws Exception {
        return getActionResult();
    }
}