/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.cache.DefaultCache;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.load.AbstractSource;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.scriptmark.load.InputStreamSource;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.exception.TransactionException;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.annotation.*;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.dispatcher.handle.CommandHandle;
import com.github.jspxnet.txweb.dispatcher.handle.RocHandle;
import com.github.jspxnet.txweb.enums.FileCoveringPolicyEnumType;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.interceptor.InterceptorSupport;
import com.github.jspxnet.txweb.result.RocException;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.support.ApacheMultipartRequest;
import com.github.jspxnet.txweb.support.MultipartRequest;
import com.github.jspxnet.txweb.support.MultipartSupport;
import com.github.jspxnet.txweb.turnpage.TurnPageButton;
import com.github.jspxnet.txweb.turnpage.impl.TurnPageButtonImpl;
import com.github.jspxnet.upload.CosMultipartRequest;
import com.github.jspxnet.utils.*;
import com.thetransactioncompany.cors.CORSResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ResponseFacade;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 * <p>
 * author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 15:16:56
 * LifecycleManager
 */
@Slf4j
public final class TXWebUtil {

    public final static String REPEAT_VERIFY_KEY = "jspx:operate:repeat:verify:%s";
    public final static String AT = "@";
    //安全跳过,这些方法不接受请求发送的参数
    private final static String[] ACTION_SAFE_METHOD = new String[]{"setActionLogTitle", "setActionLogContent", "setActionResult", "isRepeatPost"};


    private TXWebUtil() {

    }

    /**
     * 上传请求
     *
     * @param action 设置上传方式的请求
     * @throws Exception 异常 错误
     */
    public static void setMulRequest(Action action) throws Exception {
        if (!RequestUtil.isMultipart(action.getRequest())) {
            return;
        }
        Class<?> cls = ClassUtil.getClass(action.getClass());
        Method[] methods = ClassUtil.getDeclaredMethods(cls);//字段
        for (Method method : methods) {
            MulRequest mulRequest = method.getAnnotation(MulRequest.class);
            if (mulRequest == null) {
                continue;
            }
            String saveDirectory = mulRequest.saveDirectory();
            if (saveDirectory.startsWith(AT)) {
                saveDirectory = saveDirectory.substring(1);
                saveDirectory = (String) BeanUtil.getProperty(action, saveDirectory);
            }

            String fileType = mulRequest.fileTypes();
            if (fileType.startsWith(AT)) {
                fileType = fileType.substring(1);
                fileType = (String) BeanUtil.getProperty(action, fileType);
            }
            String maxPostSize = mulRequest.maxPostSize();
            if (maxPostSize.startsWith(AT)) {
                maxPostSize = maxPostSize.substring(1);
                maxPostSize = BeanUtil.getProperty(action, maxPostSize).toString();
            }
            int iMaxPostSize = StringUtil.toInt(maxPostSize);

            String[] fileTypes = null;
            if (!StringUtil.isNull(fileType) && !StringUtil.ASTERISK.equals(fileType)) {
                fileTypes = StringUtil.split(StringUtil.replace(fileType, StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON);
            }

            if (action.getRequest() instanceof MultipartRequest) {
                continue;
            }
            FileCoveringPolicyEnumType fileCoveringPolicy = mulRequest.covering();
            if (FileCoveringPolicyEnumType.Method.equals(mulRequest.covering()) && ClassUtil.isDeclaredMethod(action.getClass(), "covering")) {

                int covering = ObjectUtil.toInt(BeanUtil.getProperty(action, "covering"));
                fileCoveringPolicy = FileCoveringPolicyEnumType.find(covering);
            }

            MultipartRequest multipartRequest;
            if ("cos".equalsIgnoreCase(mulRequest.component())) {
                multipartRequest = new CosMultipartRequest(action.getRequest(), saveDirectory, iMaxPostSize, null, fileCoveringPolicy.getRenamePolicy(), fileTypes);
            } else {
                multipartRequest = new ApacheMultipartRequest(action.getRequest(), saveDirectory, iMaxPostSize, null, fileCoveringPolicy.getRenamePolicy(), fileTypes);
            }

            BeanUtil.setSimpleProperty(action, method.getName(), multipartRequest);
            ActionContext actionContext = ThreadContextHolder.getContext();
            actionContext.setRequest(multipartRequest);

        }
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 推荐更多的使用ajax方式分页
     *
     * @param action action
     * @throws Exception 异常
     */
    public static void setTurnPage(final Action action) throws Exception {
        HttpServletRequest request = action.getRequest();
        if (request == null) {
            return;
        }
        Class<?> cls = ClassUtil.getClass(action.getClass());
        Field[] fields = ClassUtil.getDeclaredFields(cls);//字段
        for (Field field : fields) {
            TurnPage turnPage = field.getAnnotation(TurnPage.class);
            if (turnPage != null) {
                String enable = action.getEnv(ActionEnv.Key_PageEnable);
                if (StringUtil.toBoolean(enable)) {
                    enable = "true";
                } else {
                    enable = turnPage.enable();
                    if (enable.startsWith(AT)) {
                        enable = enable.substring(1);
                        enable = ObjectUtil.toString(BeanUtil.getProperty(action, enable));
                    } else if (enable.startsWith("[") && enable.endsWith("]")) {
                        enable = StringUtil.substringBetween(enable, "[", "]");
                        String urlName = action.getEnv(ActionEnv.Key_ActionName);
                        assert enable != null;
                        enable = BooleanUtil.toString(urlName.matches(enable));
                    }
                }
                if (!StringUtil.toBoolean(enable)) {
                    continue;
                }
                TurnPageButton turnPageButton = new TurnPageButtonImpl();
                String currentPage = turnPage.currentPage();
                int iCurrentPage = 0;
                if (currentPage.startsWith(AT)) {
                    currentPage = currentPage.substring(1);
                    iCurrentPage = ObjectUtil.toInt(BeanUtil.getProperty(action, currentPage));
                } else if (ValidUtil.isNumber(currentPage)) {
                    iCurrentPage = StringUtil.toInt(currentPage);
                }
                turnPageButton.setCurrentPage(iCurrentPage);
                //参数方式载入 默认行数 begin
                String rows = turnPage.rows();
                int iRow = 0;
                if (rows.startsWith(AT)) {
                    rows = rows.substring(1);
                    iRow = ObjectUtil.toInt(BeanUtil.getProperty(action, rows));
                } else if (ValidUtil.isNumber(rows)) {
                    iRow = StringUtil.toInt(rows);
                }

                turnPageButton.setCount(iRow);
                //参数方式载入 默认行数 end

                /////////设置参数 begin
                String sParas = turnPage.params();
                if (StringUtil.hasLength(sParas) && sParas.startsWith(AT)) {
                    sParas = sParas.substring(1);
                    sParas = (String) BeanUtil.getProperty(action, sParas);
                }
                turnPageButton.setQuerystring(ParamUtil.getQueryString(request, sParas, SafetyEnumType.MIDDLE));
                /////////设置参数 end

                ////得到行总数 begin
                int iTotalCount;
                String sTotalCount = turnPage.totalCount();
                if (StringUtil.hasLength(sTotalCount) && sTotalCount.startsWith(AT)) {
                    sTotalCount = sTotalCount.substring(1);
                    iTotalCount = ObjectUtil.toInt(BeanUtil.getProperty(action, sTotalCount));
                } else {
                    iTotalCount = StringUtil.toInt(sTotalCount);
                }
                turnPageButton.setTotalCount(iTotalCount);
                ////得到行总数 end

                ////得到模版文件 begin
                String turnFile = turnPage.file();
                if (StringUtil.hasLength(turnFile) && turnFile.startsWith(AT)) {
                    turnFile = turnFile.substring(1);
                    turnFile = (String) BeanUtil.getProperty(action, turnFile);
                }
                ////得到行总数 end

                ////得到模版文件 begin
                String bound = turnPage.bound();
                if (StringUtil.hasLength(bound) && bound.startsWith(AT)) {
                    bound = bound.substring(1);
                    bound = (String) BeanUtil.getProperty(action, bound);
                }
                ////得到行总数 end
                turnPageButton.setRootDirectory(action.getEnv(ActionEnv.Key_RealPath));
                turnPageButton.setCurrentPath(action.getTemplatePath());
                turnPageButton.setFileName(turnFile);
                turnPageButton.setBound(StringUtil.toInt(bound, 3));
                field.setAccessible(true);
                field.set(action, turnPageButton.getTurnPage());
            }
        }
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 拷贝设置请求参数   拷贝代码
     * 注入action,这里需要
     * 注意：
     * 如果action为单例模式，第一次放入变量后，
     * 第二次还在，如果需要改变，那么必须明确的再次传入参数
     *
     * @param action action
     */
    public static void copyRequestProperty(Action action) {
        if (action == null) {
            return;
        }
        if (action.getRequest() == null || RequestUtil.isRocRequest(action.getRequest())) {
            return;
        }
        HttpServletRequest request = action.getRequest();
        if (request instanceof MultipartSupport) {
            copyRequestProperty((MultipartSupport) action);
            return;
        }
        Class<?> cls = ClassUtil.getClass(action.getClass());
        String[] requestNames = RequestUtil.getParameterNames(request);
        Method[] methods = ClassUtil.getDeclaredSetMethods(cls);
        for (Method method : methods) {
            if (!Modifier.isPublic(method.getModifiers()) && !Modifier.isProtected(method.getModifiers())) {
                //非公有的不设置
                continue;
            }
            String propertyName = method.getName();
            if (StringUtil.isNull(propertyName) || propertyName.startsWith(ClassUtil.METHOD_NAME_GET) || propertyName.startsWith(ClassUtil.METHOD_NAME_IS) || ArrayUtil.inArray(ACTION_SAFE_METHOD, propertyName, true)) {
                continue;
            }
            if (propertyName.startsWith(ClassUtil.METHOD_NAME_SET) && propertyName.length() > 3) {
                propertyName = StringUtil.uncapitalize(propertyName.substring(3));
            }
            if (!ArrayUtil.inArray(requestNames, propertyName, true)) {
                continue;
            }

            Type[] types = method.getGenericParameterTypes();
            if (types.length < 1) {
                continue;
            }
            Type aType = types[0];
            Object propertyValue;
            if (aType.equals(JSONArray.class)) {
                String[] reqArray = action.getArray(propertyName, false);
                JSONArray array = new JSONArray();
                if (reqArray != null) {
                    Collections.addAll(array, reqArray);
                }
                propertyValue = array;
            } else if (ClassUtil.isArrayType(aType)) {
                if (aType.equals(int[].class)) {
                    propertyValue = action.getIntArray(propertyName);
                } else if (aType.equals(Integer[].class)) {
                    propertyValue = action.getIntegerArray(propertyName);
                } else if (aType.equals(long[].class)) {
                    propertyValue = action.getLongArray(propertyName);
                } else if (aType.equals(Long[].class)) {
                    propertyValue = action.getLongArray(propertyName);
                } else if (aType.equals(float[].class)) {
                    propertyValue = action.getFloatArray(propertyName);
                } else if (aType.equals(Float[].class)) {
                    propertyValue = action.getFloatObjectArray(propertyName);
                } else if (aType.equals(double[].class)) {
                    propertyValue = action.getDoubleArray(propertyName);
                } else if (aType.equals(Double[].class)) {
                    propertyValue = action.getDoubleObjectArray(propertyName);
                } else if (aType.equals(BigDecimal[].class)) {
                    propertyValue = action.getDoubleObjectArray(propertyName);
                } else {
                    propertyValue = action.getArray(propertyName, false);
                }
            } else {
                if (aType.equals(Boolean.class) || aType.equals(boolean.class)) {
                    propertyValue = action.getBoolean(propertyName);
                } else if (aType.equals(int.class) || aType.equals(Integer.class)) {
                    propertyValue = action.getInt(propertyName);
                } else if (aType.equals(long.class) || aType.equals(Long.class)) {
                    propertyValue = action.getLong(propertyName);
                } else if (aType.equals(float.class) || aType.equals(Float.class)) {
                    propertyValue = action.getFloat(propertyName, 0);
                } else if (aType.equals(double.class) || aType.equals(Double.class)) {
                    propertyValue = action.getDouble(propertyName, 0);
                } else if (aType.equals(Date.class)) {
                    propertyValue = action.getDate(propertyName);
                } else if (aType.equals(java.sql.Date.class)) {
                    propertyValue = new java.sql.Date(action.getDate(propertyName).getTime());
                } else if (aType.equals(Timestamp.class)) {
                    propertyValue = new java.sql.Timestamp(action.getDate(propertyName).getTime());
                } else {
                    propertyValue = action.getString(propertyName);
                }
            }
            BeanUtil.setSimpleProperty(action, method.getName(), propertyValue);
        }
    }

    /**
     * 拷贝设置请求参数
     *
     * @param action action bean
     */
    private static void copyRequestProperty(MultipartSupport action) {
        MultipartRequest multipartRequest = (MultipartRequest) action.getRequest();
        String[] requestNames = action.getParameterNames();
        Class<?> cls = ClassUtil.getClass(action.getClass());
        Method[] methods = ClassUtil.getDeclaredSetMethods(cls);
        for (Method method : methods) {
            String propertyName = method.getName();
            if (StringUtil.isNull(propertyName) || propertyName.startsWith(ClassUtil.METHOD_NAME_GET) || propertyName.startsWith(ClassUtil.METHOD_NAME_IS)) {
                continue;
            }
            if (propertyName.startsWith(ClassUtil.METHOD_NAME_SET) && propertyName.length() > 3) {
                propertyName = StringUtil.uncapitalize(propertyName.substring(3));
            }
            if (!ArrayUtil.inArray(requestNames, propertyName, true)) {
                continue;
            }
            Object propertyValue;
            Type[] types = method.getGenericParameterTypes();
            if (types.length < 1) {
                continue;
            }

            Type aType = types[0];
            if (ClassUtil.isArrayType(aType)) {
                if (aType.equals(int[].class)) {
                    propertyValue = ArrayUtil.getIntArray(multipartRequest.getParameterValues(propertyName));
                } else if (aType.equals(Integer[].class)) {
                    propertyValue = ArrayUtil.getIntegerArray(multipartRequest.getParameterValues(propertyName));
                } else if (aType.equals(long[].class)) {
                    propertyValue = ArrayUtil.getLongArray(multipartRequest.getParameterValues(propertyName));
                } else if (aType.equals(Long[].class)) {
                    propertyValue = ArrayUtil.getLongObjectArray(multipartRequest.getParameterValues(propertyName));
                } else if (aType.equals(float[].class)) {
                    propertyValue = ArrayUtil.getFloatArray(multipartRequest.getParameterValues(propertyName));
                } else if (aType.equals(Float[].class)) {
                    propertyValue = ArrayUtil.getFloatObjectArray(multipartRequest.getParameterValues(propertyName));
                } else if (aType.equals(double[].class)) {
                    propertyValue = ArrayUtil.getDoubleArray(multipartRequest.getParameterValues(propertyName));
                } else if (aType.equals(Double[].class)) {
                    propertyValue = ArrayUtil.getDoubleObjectArray(multipartRequest.getParameterValues(propertyName));
                } else {
                    propertyValue = multipartRequest.getParameter(propertyName);
                }
            } else {
                if (aType == Boolean.class) {
                    propertyValue = ObjectUtil.toBoolean(multipartRequest.getParameter(propertyName));
                } else if (aType == boolean.class) {
                    propertyValue = StringUtil.toBoolean(multipartRequest.getParameter(propertyName));
                } else if (aType == int.class) {
                    propertyValue = StringUtil.toInt(multipartRequest.getParameter(propertyName));
                } else if (aType == Integer.class) {
                    propertyValue = ObjectUtil.toInt(multipartRequest.getParameter(propertyName));
                } else if (aType == long.class) {
                    propertyValue = StringUtil.toLong(multipartRequest.getParameter(propertyName));
                } else if (aType == Long.class) {
                    propertyValue = ObjectUtil.toLong(multipartRequest.getParameter(propertyName));
                } else if (aType == float.class) {
                    propertyValue = StringUtil.toFloat(multipartRequest.getParameter(propertyName));
                } else if (aType == Float.class) {
                    propertyValue = ObjectUtil.toFloat(multipartRequest.getParameter(propertyName));
                } else if (aType == double.class) {
                    propertyValue = StringUtil.toDouble(multipartRequest.getParameter(propertyName));
                } else if (aType == Double.class) {
                    propertyValue = ObjectUtil.toDouble(multipartRequest.getParameter(propertyName));
                } else if (aType == Date.class) {
                    propertyValue = StringUtil.getDate(multipartRequest.getParameter(propertyName));
                } else if (aType == java.sql.Date.class) {
                    propertyValue = new java.sql.Date(StringUtil.getDate(multipartRequest.getParameter(propertyName)).getTime());
                } else if (aType == Timestamp.class) {
                    propertyValue = new java.sql.Timestamp(StringUtil.getDate(multipartRequest.getParameter(propertyName)).getTime());
                } else {
                    propertyValue = multipartRequest.getParameter(propertyName);
                }
            }
            BeanUtil.setSimpleProperty(action, method.getName(), propertyValue);
        }
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 这里的主要功能是安全过滤，并且放入参数变量
     *
     * @param action   action对象
     * @param valueMap ROC请求参数
     */
    public static void putValueProperty(Action action, Map<String, Object> valueMap) {
        if (action == null) {
            return;
        }
        Class<?> cls = ClassUtil.getClass(action.getClass());
        Method[] methods = ClassUtil.getDeclaredSetMethods(cls);
        for (Method method : methods) {

            String propertyName = method.getName();
            if (StringUtil.isNull(propertyName) || propertyName.startsWith(ClassUtil.METHOD_NAME_GET) || propertyName.startsWith(ClassUtil.METHOD_NAME_IS) || ArrayUtil.inArray(ACTION_SAFE_METHOD, propertyName, true)) {
                continue;
            }
            if (propertyName.startsWith(ClassUtil.METHOD_NAME_SET) && propertyName.length() > 3) {
                propertyName = StringUtil.uncapitalize(propertyName.substring(3));
            }
            if (valueMap.containsKey(propertyName)) {
                Object propertyValue = valueMap.get(propertyName);
                Param param = method.getAnnotation(Param.class);
                Type[] types = method.getGenericParameterTypes();
                if (param != null) {
                    if (types[0].equals(String[].class)) {
                        if (propertyValue instanceof String[]) {
                            String[] tempArray = (String[]) propertyValue;
                            for (String s : tempArray) {
                                ParamUtil.isSafeParam(action, param, propertyName, s);
                                if (action.hasFieldInfo()) {
                                    return;
                                }
                            }
                            propertyValue = tempArray;
                        }
                        valueMap.put(propertyName, propertyValue);
                    } else {
                        ParamUtil.isSafeParam(action, param, propertyName, propertyValue);
                        if (action.hasFieldInfo()) {
                            return;
                        }
                    }
                }
                BeanUtil.setSimpleProperty(action, method.getName(), propertyValue);
            }

        }
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param cls 类对象
     * @return 需要有继承关系
     */
    public static Map<Operate, Method> getClassOperateList(Class<?> cls) {
        Map<Operate, Method> operateMethodMap = new HashMap<>();
        Class<?> superclass = cls;
        while (!(superclass == null || superclass.equals(Object.class) || superclass.equals(Serializable.class))) {
            Method[] methods = superclass.getDeclaredMethods();
            for (Method method : methods) {
                Operate operate = method.getAnnotation(Operate.class);
                if (operate != null && !operateMethodMap.containsKey(operate)) {
                    operateMethodMap.put(operate, method);
                }
            }
            superclass = superclass.getSuperclass();
            if (superclass == null || superclass.equals(com.caucho.services.server.GenericService.class) || ClassUtil.isStandardProperty(superclass)
                    || superclass.equals(Serializable.class) || superclass.equals(Map.class) || superclass.equals(List.class) || superclass.equals(Runnable.class)
                    || superclass.equals(SoberSupport.class) || superclass.equals(Action.class) || superclass.equals(ActionSupport.class)
                    || superclass.equals(InterceptorSupport.class)
            ) {
                break;
            }
        }
        List<Map.Entry<Operate, Method>> sortList = new ArrayList<>(operateMethodMap.entrySet());

        sortList.sort((s1, s2) -> {
            String st1 = (s1 == null || StringUtil.isNull(s1.getKey().method())) ? StringUtil.empty : s1.getKey().method();
            String st2 = (s2 == null || StringUtil.isNull(s2.getKey().method())) ? StringUtil.empty : s2.getKey().method();
            return Integer.compare(st2.length(), st1.length());//keep
        });

        return sortList.stream().collect(
                Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldVal, currVal) -> oldVal, LinkedHashMap::new)
        );
    }


    /**
     * 步骤: 1:先放入请求参数,2:执行请求方法;3:如果有多个返回，继续执行多个返回,5：最后执行defaultExecute
     * 返回必须位真实的执行结果，不要返回错误提示信息。
     * @param action action对象
     * @param actionContext 上下文
     * @return 返回必须位真实的执行结果，不要返回错误提示信息。
     * @throws Exception 异常
     */
    public static Object invokeJson(Action action, ActionContext actionContext) throws Exception {

        if (actionContext == null) {
            //避免空异常
            return null;
        }
        Method exeMethod = actionContext.getMethod();
        if (exeMethod == null) {
            action.addFieldInfo(Environment.errorInfo, "not found method  " + exeMethod);
            action.setActionResult(ActionSupport.ERROR);
            return null;
        }
        JSONObject callJson = (JSONObject) actionContext.get(ActionEnv.Key_CallRocJsonData);
        //ROC roc Json 根据指定调用返回
        Object[] paramObj;
        Object rocParams = null;
        if (ArrayUtil.inArray(new String[]{RocHandle.NAME, CommandHandle.NAME}, actionContext.getExeType(), true)) {

            if (callJson == null) {
                return null;
            }
            JSONObject methodJson = callJson.getJSONObject(Environment.rocMethod);
            if (methodJson != null && methodJson.containsKey(Environment.rocParams)) {
                rocParams = methodJson.get(Environment.rocParams);
            }

            if (methodJson!=null)
            {
                if (rocParams instanceof JSONObject) {
                    rocParams = methodJson.getJSONObject(Environment.rocParams);
                } else {
                    rocParams = methodJson.getJSONArray(Environment.rocParams);
                }
            }
            if (rocParams instanceof JSONArray) {
                //采用数组方式对于进入
                JSONArray paramsArray = (JSONArray) rocParams;
                paramObj = ParamUtil.getMethodParameter(action, exeMethod, paramsArray);
            } else {
                //参数有三种方式  1:采用数组方式对于进入 2:采用方面名称对应json参数名称
                //3.路径名称作为参数
                //采用数组方式对于进入
                //自动前后
                JSONObject paramsJson = (JSONObject) rocParams;
                paramObj = ParamUtil.getMethodParameter(action, exeMethod, paramsJson);
            }
        } else {
            paramObj = ParamUtil.getMethodParameter(action, exeMethod, actionContext.getCallJson());
        }

        //路径方式载入参数
        Operate operate = exeMethod.getAnnotation(Operate.class);
        if (operate != null && operate.method().contains(ParamUtil.VARIABLE_BEGIN) && operate.method().contains(ParamUtil.VARIABLE_END)) {
            paramObj = ParamUtil.getMethodParameter(action, exeMethod);
            if (action.hasFieldInfo()) {
                return null;
            }
        }

        boolean isVoid = false;
        Object methodResult = null;
        if (!ActionEnv.DEFAULT_EXECUTE.equals(exeMethod.getName())) {
            //载入默认参数,修复方法参数匹配
            if (paramObj == null && exeMethod.getParameterCount() != 0 || (paramObj != null && paramObj.length != exeMethod.getParameterCount())) {
                //一个参数都没有的情况
                paramObj = ParamUtil.getMethodParameter(action, exeMethod);
                if (action.hasFieldInfo()) {
                    return null;
                }
            }
            try {
                if (exeMethod.getGenericReturnType().equals(Void.TYPE)) {
                    //如果不用返回,直接执行
                    isVoid = true;
                    TXWebUtil.invokeFun(action, actionContext, paramObj);
                } else {
                    methodResult = TXWebUtil.invokeFun(action, actionContext, paramObj);
                }
                //执行完后已经执行 execute，确保和form方式一样
            } catch (Exception e) {
                log.error(exeMethod + " params is " + ObjectUtil.toString(paramObj), e);
                //RPC 2.0 标准  返回
                actionContext.setResult(new RocException(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "参数错误," + ObjectUtil.toString(paramObj))));
                actionContext.setActionResult(ActionSupport.ERROR);
                throw (RocException) actionContext.getResult();
            }
        }

        JSONArray resultJson = callJson.getJSONArray(Environment.rocResult);
        if (resultJson == null || resultJson.isEmpty()) {
            //交给后边处理
            return methodResult;
        }
        //多个数据的返回方式

        Class<?> actionClass = ClassUtil.getClass(action.getClass());
        Map<String, Object> resultMap = new HashMap<>();
        //这里目的是放入 多个数据对象
        for (Object v : resultJson.toArray()) {
            String methodName = (String) v;
            //比较，判断是否还有没有返回的对象,避免外部写重复的方法
            if (ActionEnv.DEFAULT_EXECUTE.equals(methodName) || methodName.equals(exeMethod.getName()) || resultMap.containsKey(exeMethod.getName())) {
                continue;
            }
            Method method = ClassUtil.getDeclaredMethod(actionClass, methodName);
            if (method == null) {
                action.addFieldInfo(Environment.warningInfo, methodName + "不存在的方法");
                action.setActionResult(ActionSupport.ERROR);
                return null;
            }
            String methodFiled = ClassUtil.getMethodFiledName(methodName);
            if (StringUtil.isNull(methodFiled)) {
                methodFiled = method.getName();
            }

            Object value = BeanUtil.getProperty(action, methodName);
            if (value == null) {
                resultMap.put(methodFiled, JSONObject.NULL);
            } else if (value instanceof JSONObject) {
                resultMap.put(methodFiled, value);
            } else if (ClassUtil.isStandardProperty(value.getClass())) {
                resultMap.put(methodFiled, value);
            } else {
                resultMap.put(methodFiled, new JSONObject(value, true));
            }
        }
        String methodFiled = ClassUtil.getMethodFiledName(exeMethod.getName());
        if (StringUtil.isNull(methodFiled)) {
            methodFiled = exeMethod.getName();
        }
        //放入执行的结果
        if (!isVoid && !ActionEnv.DEFAULT_EXECUTE.equals(exeMethod.getName())) {
            if (methodResult == null) {
                resultMap.put(methodFiled, new JSONObject());
            } else if (ClassUtil.isStandardProperty(methodResult.getClass()) || ClassUtil.isCollection(methodResult)) {
                resultMap.put(methodFiled, methodResult);
            } else {
                resultMap.put(methodFiled, methodResult);
            }
        }
        //没有返回对象，就不添加了，后被统一返回信息
        return resultMap;

    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 将json 参数放入 action 内部变量
     *
     * @param action   action对象
     * @param callJson json 参数
     */
    public static void putJsonParams(Action action, JSONObject callJson) {
        if (ObjectUtil.isEmpty(callJson)) {
            return;
        }
        //放入请求参数 begin
        Map<String, Object> actionParams = new HashMap<>();
        //参数表示 set全局action参数
        JSONObject paramsJson = callJson.getJSONObject(Environment.rocParams);
        if (paramsJson != null) {
            for (Object key : paramsJson.keySet()) {
                if (key == null) {
                    continue;
                }
                Object objParam = paramsJson.get(key);
                if (objParam instanceof JSONArray) {
                    objParam = ((JSONArray) objParam).toArray();
                }
                actionParams.put((String) key, objParam);
            }
            putValueProperty(action, actionParams);
        }
        //放入请求参数 end

    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param actionProxy action代理
     * @param actionClass 类对象,方便识别
     * @param callJson    参数
     * @param method      方法名称
     * @param namespace   命名空间
     * @return 返回方法
     */
    static public Method getExeMethod(ActionProxy actionProxy, Class<?> actionClass, JSONObject callJson, String method, String namespace) {
        if (actionProxy == null) {
            return null;
        }
        Action action = actionProxy.getAction();
        if (action == null) {
            return null;
        }

        if (!ParamUtil.isSafe(method, 0, 50, SafetyEnumType.LOW)) {
            action.addFieldInfo(Environment.warningInfo, "方法名不符合规范");
            return null;
        }
        //method 有可能是action名称，而这个方法是使用路径方式

        //参数在前边已经放入,这里只需要执行后返回
        //ROC roc Json 根据指定调用返回
        JSONObject rocMethodJson = null;
        if (callJson != null) {
            rocMethodJson = callJson.getJSONObject(Environment.rocMethod);
        }

        boolean isArray = true;
        JSONObject paramsObject = null;
        JSONArray paramsArray = null;
        if (rocMethodJson != null && !rocMethodJson.isEmpty() && rocMethodJson.containsKey(Environment.rocParams)) {
            paramsArray = rocMethodJson.getJSONArray(Environment.rocParams);
            if (paramsArray == null) {
                paramsObject = rocMethodJson.getJSONObject(Environment.rocParams);
                isArray = false;
            }
        }

        int iParam;
        if (isArray) {
            iParam = paramsArray == null ? 0 : paramsArray.length();
        } else {
            iParam = paramsObject == null ? 0 : paramsObject.size();
        }
        //配置了Operate

        String url = URLUtil.deleteUrlSuffix(action.getRequest().getRequestURI());
        Method exeMethod = getExeMethodForOperate(url, actionClass, method, namespace, iParam);

        //这里要添加路径方式
        //先判断映射，在判断方法
        if (exeMethod == null) {
            Method[] methodList = ClassUtil.getDeclaredMethodList(actionClass, method, true);
            if (methodList != null && methodList.length == 1) {
                //只有一个方法匹配
                exeMethod = methodList[0];
            } else if (methodList != null && methodList.length > 1) {
                //这里有继承关系 多个方法匹配
                for (Method theMethod : methodList) {
                    if (ClassUtil.isDeclaredMethod(actionClass, theMethod)) {
                        if (isArray) {
                            if ((paramsArray == null || paramsArray.isEmpty()) && theMethod.getParameterTypes().length == 0) {
                                exeMethod = theMethod;
                                break;
                            } else if ((paramsArray != null && !paramsArray.isEmpty()) && theMethod.getParameterTypes().length == paramsArray.length()) {
                                exeMethod = theMethod;
                                break;
                            }
                        } else {
                            if ((paramsObject == null || paramsObject.isEmpty()) && theMethod.getParameterTypes().length == 0) {
                                exeMethod = theMethod;
                                break;
                            } else if ((paramsObject != null && !paramsObject.isEmpty()) && theMethod.getParameterTypes().length == paramsObject.size()) {
                                exeMethod = theMethod;
                                break;
                            }
                        }
                    }
                }
            }
            if (exeMethod == null && methodList != null && ClassUtil.isProxy(action.getClass())) {
                for (Method met : methodList) {
                    if (met.getName().equals(method)) {
                        exeMethod = met;
                        break;
                    }
                }
            }
        }
        return exeMethod;
    }


    /**
     * @param url         url 路径
     * @param actionClass action类型
     * @param method      方法名称
     * @param namespace   命名空间
     * @param iParam      参数个数
     * @return 方法
     */
    public static Method getExeMethodForOperate(String url, Class<?> actionClass, String method, String namespace, int iParam) {
        //这里只用判断存在于方法命名空间之后就可以了
        Method exeMethod = null;
        Map<Operate, Method> operateMap = getClassOperateList(actionClass);
        for (Operate operate : operateMap.keySet()) {
            //处理通配符情况
            if (TXWebUtil.AT.equals(operate.method())) {
                Method tmpMethod = operateMap.get(operate);
                if (tmpMethod != null && tmpMethod.getName().equalsIgnoreCase(method) && tmpMethod.getParameterCount() == iParam) {
                    exeMethod = operateMap.get(operate);
                    if (exeMethod != null) {
                        break;
                    }
                }
            } else if (operate.method().equalsIgnoreCase(method) || operate.method().equalsIgnoreCase(StringUtil.BACKSLASH + method)) {
                //直接配置
                exeMethod = operateMap.get(operate);
                if (exeMethod != null) {
                    break;
                }
            } else if (operate.method().contains(StringUtil.BACKSLASH)) {
                String urlNamespace = StringUtil.BACKSLASH + namespace;//actionProxy.getNamespace();
                //当前允许执行的方法
                String operateUrl = urlNamespace + (operate.method().startsWith(StringUtil.BACKSLASH) ? operate.method() : (StringUtil.BACKSLASH + operate.method()));
                if (url.startsWith(urlNamespace) && operate.method().contains(ParamUtil.VARIABLE_BEGIN)) {
                    //方法配置的路径 路径参数方式${} {}
                    String methodUrl = StringUtil.substringBefore(operate.method(), ParamUtil.VARIABLE_BEGIN);
                    if (url.toLowerCase().contains(methodUrl.toLowerCase())) {
                        return operateMap.get(operate);
                    }
                    //多级目录情况
                } else if (url.startsWith(operateUrl.toLowerCase())) {
                    //最直接的比对
                    return operateMap.get(operate);
                } else if (url.startsWith(urlNamespace) && StringUtil.getPatternFind(StringUtil.substringAfter(url, urlNamespace), operate.method())) {
                    //通配符方式
                    return operateMap.get(operate);
                }
            }
        }
        return exeMethod;
    }


    /**
     * 这里是执行可返回函数的地方,返回的数据主要是做ROC返回
     * @param action action
     * @param actionContext 上下文
     * @param paramObj 参数对象
     * @return  返回结果
     * @throws Exception 参数
     */
    public static Object invokeFun(Action action, ActionContext actionContext,Object[] paramObj ) throws Exception {
        //安全检查begin
        if (actionContext.getMethod() == null) {
            return null;
        }

        if (action == null) {
            log.error("invoke execute action is Null ,action 不能为空,请检查配置");
            throw new ClassNotFoundException("action is null");
        }
        //有异常就不在执行
        if (actionContext.hasFieldInfo()) {
            return null;
        }

        Method exeMethod =  actionContext.getMethod();
        //判断是否满足执行条件
        log.debug("执行方法:{} 进入参数:{}", exeMethod.getName(), ObjectUtil.toString(paramObj));
        Object result = null;
        try {
            //事务标签处理 begin
            if (paramObj == null) {
                result = exeMethod.invoke(action);
            } else {
                result = exeMethod.invoke(action, paramObj);
            }

            //事务标签处理 end
        } catch (InvocationTargetException exception) {
            action.setActionResult(ActionSupport.ERROR);
            Throwable e = exception.getTargetException();
            if (e instanceof TransactionException) {
                TransactionException transactionException = (TransactionException) e;
                Transaction transaction = transactionException.getTransaction();
                if (transaction != null && !StringUtil.isEmpty(transaction.message())) {
                    String msg = transaction.message();
                    if (msg.contains(ParamUtil.VARIABLE_BEGIN) && msg.contains(ParamUtil.VARIABLE_END)) {
                        Map<String, Object> valueMap = new HashMap<>();
                        valueMap.put(ActionEnv.Key_Language, action.getLanguage());
                        msg = EnvFactory.getPlaceholder().processTemplate(valueMap, msg);
                    }
                    action.addFieldInfo(Environment.errorInfo, msg);
                } else {
                    action.addFieldInfo(Environment.errorInfo, ThrowableUtil.getThrowableMessage(transactionException.getCause()));
                }
            } else if (e instanceof RocException) {
                //--------------------------
                RocException rocException = (RocException) e;
                action.setResult(rocException.getResponse());
            } else {
                Transaction transaction = exeMethod.getAnnotation(Transaction.class);
                if (transaction != null && !StringUtil.isEmpty(transaction.message())) {
                    action.addFieldInfo(Environment.errorInfo, transaction.message());
                } else {
                    action.addFieldInfo(Environment.errorInfo, exeMethod.getName() + " " + ThrowableUtil.getThrowableMessage(e));
                }

            }
        }
        return result;
    }


    /**
     * 标签是否满足操作配置
     *
     * @param action    action
     * @param exeMethod 执行方法
     * @return 是否继续执行这个方法
     */
    public static boolean checkOperate(Action action, Method exeMethod)  {
        //验证 begin
        Operate operate = exeMethod.getAnnotation(Operate.class);
        if (operate == null) {
            log.debug("action:{},exeMethod:{}", action.getClass(), exeMethod);
            action.setActionResult(ActionSupport.ERROR);
            action.addFieldInfo(exeMethod.getName(), "不允许执行的操作");
            return false;
        }
        //没有Operate标记的不允许对外访问
        //注意，档位GET的时候 submit 为 false
        if (operate.post() && TXWeb.httpGET.equalsIgnoreCase(action.getRequest().getMethod())) {
            action.setActionResult(ActionSupport.ERROR);
            action.addFieldInfo(exeMethod.getName(), "错误的请求方式");
            return false;
        }

        //验证防止重复提交 begin
        if (operate.repeat() > 0) {
            String keyValue = EncryptUtil.getMd5(ClassUtil.getClass(action.getClass()).getName() + StringUtil.DOT + exeMethod.getName() + StringUtil.DOT + action.getUserSession().getId());
            String key = String.format(REPEAT_VERIFY_KEY, keyValue);
            Object check = JSCacheManager.get(DefaultCache.class, key);
            if (ObjectUtil.isEmpty(check)) {
                JSCacheManager.put(DefaultCache.class, key, operate.repeat(), operate.repeat());
            } else {
                action.addFieldInfo(exeMethod.getName(), "不运行重复提交," + operate.repeat() + "秒后在来");
                return false;
            }
        }
        //验证防止重复提交 end
        return true;
    }


    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param namespace   命名空间
     * @param actionName  文件名
     * @param classMethod 执行动作
     * @return 得到唯一识别的动作方法ID
     */
    public static String getOperateMethodId(String namespace, String actionName, String classMethod) {
        return EncryptUtil.getBase64EncodeString(StringUtil.replace(namespace + "/" + actionName, "//", "/")) + StringUtil.COLON + classMethod;
    }


    //------------------------------------------------------------------------------------------------------------------
    public static void print(String string, int type, HttpServletResponse response) {
        print(string, type, response, 0);
    }


    /**
     * @param string   字符串
     * @param type     类型
     * @param response 应答
     * @param status   状态
     */
    public static void print(String string, int type, HttpServletResponse response, Integer status) {
        if (response == null) {
            return;
        }
        if (!(response instanceof ResponseFacade) && !(response instanceof CORSResponseWrapper) && response.isCommitted()) {

            StringBuilder sb = new StringBuilder();
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                sb.append(stackTraceElement.getLineNumber()).append(StringUtil.COLON).append(stackTraceElement.getClassName()).append(StringUtil.DOT).append(stackTraceElement.getMethodName()).append(StringUtil.CRLF);
            }
            log.error("response 已经提交并且关闭,又输出信息:{},调用方法:{}", string, sb);
            return;
        }
        String contentType = null;
        if (WebOutEnumType.JSON.getValue() == type) {
            contentType = "application/json";
        } else if (WebOutEnumType.JAVASCRIPT.getValue() == type) {
            contentType = "text/javascript";
        } else if (WebOutEnumType.XML.getValue() == type) {
            contentType = "text/xml";
        } else if (WebOutEnumType.TEXT.getValue() == type) {
            contentType = "text/plain";
        } else if (WebOutEnumType.HTML.getValue() == type) {
            contentType = "text/html";
        } else if (WebOutEnumType.CSS.getValue() == type) {
            contentType = "text/css";
        }
        if (StringUtil.isEmpty(contentType)) {
            contentType = "application/json";
        }
        response.setContentType(contentType + ";charset=" + (StringUtil.isNull(response.getCharacterEncoding()) ? "UTF-8" : response.getCharacterEncoding()));

        if (status != null && status > 0 && status != 200) {
            response.setStatus(status);
        }
        PrintWriter out;
        try {
            out = response.getWriter();
            if (WebOutEnumType.JAVASCRIPT.getValue() == type) {
                out.print("document.write(" + StringUtil.toJavaScriptQuote(string) + ");");
            } else if (string != null) {
                out.print(string);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.error("response writer is close,not out error", e);
        }
    }

    /**
     * 简化调用
     * @param json  json数据
     * @param response 应答
     */
    public static void print(JSONObject json,  HttpServletResponse response)
    {
        print( json, WebOutEnumType.JSON.getValue(),  response, 200);
    }
    /**
     * 格式兼容
     *
     * @param json     json数据
     * @param type     类型
     * @param response 应答
     * @param status   状态
     */
    public static void print(JSONObject json, int type, HttpServletResponse response, Integer status) {
        if (WebOutEnumType.JSON.getValue() == type) {
            print(json.toString(4), type, response, status);
        } else if (WebOutEnumType.XML.getValue() == type) {
            try {
                print(XML.toString(json), WebOutEnumType.XML.getValue(), response, status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            try {
                print(HtmlUtil.deleteHtml(XML.toString(json)), WebOutEnumType.XML.getValue(), response, status);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    public static void errorPrint(String info, Map<String, String> fieldInfo, HttpServletResponse response, int status) {
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        TemplateConfigurable configurable = new TemplateConfigurable();
        configurable.addAutoIncludes(envTemplate.getString(Environment.autoIncludes));
        AbstractSource fileSource = null;
        File f = new File(envTemplate.getString(Environment.templatePath, new File(Dispatcher.getRealPath(), "template").getPath()), envTemplate.getString(Environment.errorInfoPageTemplate, "error.ftl"));
        if (!f.isFile()) {
            f = new File(new File(Dispatcher.getRealPath(), envTemplate.getString(Environment.templatePath, "template")).getPath(), envTemplate.getString(Environment.errorInfoPageTemplate, "error.ftl"));
        }
        if (f.isFile()) {
            fileSource = new FileSource(f, envTemplate.getString(Environment.errorInfoPageTemplate, "error.ftl"), envTemplate.getString(Environment.encode, Environment.defaultEncode));
        } else {
            InputStream inputStream = TXWebUtil.class.getResourceAsStream("/resources/template/" + envTemplate.getString(Environment.errorInfoPageTemplate, "error.ftl"));
            if (inputStream == null) {
                inputStream = TXWebUtil.class.getResourceAsStream("/template/" + envTemplate.getString(Environment.errorInfoPageTemplate, "error.ftl"));
            }
            if (inputStream != null) {
                fileSource = new InputStreamSource(inputStream, envTemplate.getString(Environment.errorInfoPageTemplate, "error.ftl"), envTemplate.getString(Environment.encode, Environment.defaultEncode));
            }

        }
        if (fileSource == null) {
            if (envTemplate.getBoolean(Environment.DEBUG)) {
                TXWebUtil.print("error template page not found,错误信息显示模版没有配置" + f.getPath(), WebOutEnumType.HTML.getValue(), response);
            } else {
                TXWebUtil.print("error template page not found,错误信息显示模版没有配置", WebOutEnumType.HTML.getValue(), response);
            }
            return;
        }
        configurable.setSearchPath(new String[]{envTemplate.getString(Environment.templatePath, "template"), Dispatcher.getRealPath()});
        ScriptMark scriptMark;
        try {
            scriptMark = new ScriptMarkEngine(EncryptUtil.getMd5(f.getPath()), fileSource, configurable);
        } catch (Exception e) {
            log.error("error template page not found" + f.getAbsolutePath(), e);
            TXWebUtil.print("error template page not found,错误信息显示模版没有配置", WebOutEnumType.HTML.getValue(), response);
            return;
        }

        scriptMark.setRootDirectory(Dispatcher.getRealPath());
        scriptMark.setCurrentPath(envTemplate.getString(Environment.templatePath));
        //输出模板数据
        Map<String, Object> valueMap = TXWebUtil.createEnvironment();
        valueMap.put(Environment.message, info);
        valueMap.put(Environment.FieldInfoList, fieldInfo);
        valueMap.put(ActionEnv.Key_Response, RequestUtil.getResponseMap(response));
        try (PrintWriter out = response.getWriter()) {
            response.setStatus(status);
            scriptMark.process(out, valueMap);
        } catch (Exception e) {
            log.error("打印错误信息发生错误", e);
        }
    }

    /**
     * @return 创建默认环境
     */
    public static Map<String, Object> createEnvironment() {
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        Map<String, Object> venParams = new HashMap<>(50);
        venParams.put(Environment.filterSuffix, envTemplate.getString(Environment.filterSuffix));
        venParams.put(Environment.ApiFilterSuffix, envTemplate.getString(Environment.ApiFilterSuffix));
        venParams.put(Environment.templateSuffix, envTemplate.getString(Environment.templateSuffix));
        venParams.put(Environment.encode, Dispatcher.getEncode());
        venParams.put(Environment.remoteHostUrl, envTemplate.getString(Environment.remoteHostUrl, StringUtil.empty));
        venParams.put(Environment.scriptPath, envTemplate.getString(Environment.scriptPath, "/script"));
        venParams.put(Environment.sitePath, envTemplate.getString(Environment.sitePath, "/"));
        venParams.put(Environment.userLoginUrl, envTemplate.getString(Environment.userLoginUrl, "/user/login." + envTemplate.getString(Environment.filterSuffix)));
        venParams.put(Environment.DEBUG, envTemplate.getBoolean(Environment.DEBUG));
        venParams.put("date", new Date());
        return venParams;
    }
}