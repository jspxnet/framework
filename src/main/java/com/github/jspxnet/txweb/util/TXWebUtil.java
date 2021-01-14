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
import com.github.jspxnet.cache.redis.RedissonClientConfig;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.scriptmark.ScriptMark;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.config.TemplateConfigurable;
import com.github.jspxnet.scriptmark.core.ScriptMarkEngine;
import com.github.jspxnet.scriptmark.load.AbstractSource;
import com.github.jspxnet.scriptmark.load.FileSource;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.exception.TransactionException;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.annotation.*;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.interceptor.InterceptorSupport;
import com.github.jspxnet.txweb.result.RocException;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.support.MultipartSupport;
import com.github.jspxnet.txweb.turnpage.TurnPageButton;
import com.github.jspxnet.txweb.turnpage.impl.TurnPageButtonImpl;
import com.github.jspxnet.upload.MultipartRequest;
import com.github.jspxnet.upload.multipart.CoveringsFileRenamePolicy;
import com.github.jspxnet.upload.multipart.FileRenamePolicy;
import com.github.jspxnet.upload.multipart.JspxNetFileRenamePolicy;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RBucket;
import org.redisson.api.RedissonClient;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-27
 * Time: 15:16:56
 * LifecycleManager
 */
@Slf4j
public class TXWebUtil {
    public final static String chainType = "chain";
    public final static String redirectType = "redirect";
    public final static String defaultExecute = "execute";
    public static final String defMethod = "method";
    public final static String REPEAT_VERIFY_KEY = "jspx:operate:repeat:verify:%s";
    public final static String AT = "@";
    //安全跳过,这些方法不接受请求发送的参数
    private final static String[] ACTION_SAFE_METHOD = new String[]{"setActionLogTitle", "setActionLogContent", "setActionResult", "isRepeatPost"};
    private final static BeanFactory beanFactory = EnvFactory.getBeanFactory();

    private TXWebUtil() {

    }

    /**
     * 上传命名方式
     *
     * @param covering 是否覆盖
     * @return cos 上传的命名方式
     */
    public static FileRenamePolicy getFileRenamePolicy(final String covering) {
        if ("CoveringsFileRenamePolicy".equalsIgnoreCase(covering)) {
            return new CoveringsFileRenamePolicy();
        } else if ("DateRandomNamePolicy".equalsIgnoreCase(covering)) {
            return new CoveringsFileRenamePolicy();
        } else if ("DefaultFileRenamePolicy".equalsIgnoreCase(covering)) {
            return new CoveringsFileRenamePolicy();
        } else {
            return new JspxNetFileRenamePolicy();
        }
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 上传请求
     *
     * @param action 设置上传方式的请求
     * @throws Exception 异常 错误
     */
    public static void setMulRequest(final ActionSupport action) throws Exception {
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

            String covering = mulRequest.covering();
            if (covering.startsWith(AT)) {
                covering = covering.substring(1);
                covering = BeanUtil.getProperty(action, covering).toString();
            }

            String maxPostSize = mulRequest.maxPostSize();
            if (maxPostSize.startsWith(AT)) {
                maxPostSize = maxPostSize.substring(1);
                maxPostSize = BeanUtil.getProperty(action, maxPostSize).toString();
            }
            int iMaxPostSize = StringUtil.toInt(maxPostSize);

            String[] fileTypes = null;
            if (!StringUtil.isNull(fileType) && !"*".equals(fileType)) {
                fileTypes = StringUtil.split(StringUtil.replace(fileType, StringUtil.COMMAS, StringUtil.SEMICOLON), StringUtil.SEMICOLON);
            }
            MultipartRequest multipartRequest = new MultipartRequest(action.getRequest(), saveDirectory, iMaxPostSize, getFileRenamePolicy(covering), fileTypes);
            BeanUtil.setSimpleProperty(action, method.getName(), multipartRequest);
            action.setRequest(multipartRequest);

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
    public static void copyRequestProperty(Action action)  {
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
            if (ClassUtil.isArrayType(aType)) {
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
        MultipartRequest multipartRequest = action.getMultipartRequest();
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
    private static void putJsonProperty(Action action, Map<String, Object> valueMap)  {
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

        Collections.sort(sortList, (s1, s2) -> {
            String st1 = (s1 == null || StringUtil.isNull(s1.getKey().method())) ? StringUtil.empty : s1.getKey().method();
            String st2 = (s2 == null || StringUtil.isNull(s2.getKey().method())) ? StringUtil.empty : s2.getKey().method();
            return Integer.compare(st2.length(), st1.length());//keep
        });

        return sortList.stream().collect(
                Collectors.toMap(item -> item.getKey(), item -> item.getValue(), (oldVal, currVal) -> oldVal, LinkedHashMap::new)
        );
    }

    /**
     * 步骤: 1:先放入请求参数,2:执行请求方法;3:如果有多个返回，继续执行多个返回,5：最后执行defaultExecute
     * 返回必须位真实的执行结果，不要返回错误提示信息。
     *
     * @param actionProxy action对象
     * @return 返回必须位真实的执行结果，不要返回错误提示信息。
     * @throws Exception 异常
     */
    public static Object invokeJson(ActionProxy actionProxy) throws Exception {
        ActionSupport action = actionProxy.getAction();
        JSONObject callJson = actionProxy.getCallJson();
        if (callJson == null) {
            //避免空异常
            callJson = new JSONObject();
        }

        Method exeMethod = actionProxy.getMethod();
        if (exeMethod == null) {
            action.addFieldInfo(Environment.errorInfo, "not found method  " + actionProxy.getMethod());
            return null;
        }
        JSONArray resultJson = callJson.getJSONArray(Environment.rocResult);
        if (StringUtil.isNull(action.getActionResult())) {
            action.setActionResult(ActionSupport.ROC);
        }

        //ROC roc Json 根据指定调用返回
        Object[] paramObj = null;
        Object rocParams = null;
        if (ParamUtil.isRocRequest(callJson))
        {
            JSONObject methodJson = callJson.getJSONObject(Environment.rocMethod);
            if (methodJson!=null&&methodJson.containsKey(Environment.rocParams))
            {
                rocParams = methodJson.get(Environment.rocParams);
            } else
            {
                rocParams = callJson.get(Environment.rocParams);
            }
            if (methodJson != null &&  (rocParams instanceof JSONObject)) {
                //参数有三种方式  1:采用数组方式对于进入 2:采用方面名称对应json参数名称
                //3.路径名称作为参数
                //采用数组方式对于进入
                //自动前后
                JSONObject paramsJson = (JSONObject)rocParams;
                paramObj = ParamUtil.getMethodParameter(action, exeMethod, paramsJson);

            }  else if (rocParams instanceof JSONArray) {
                //采用数组方式对于进入
                JSONArray paramsArray = (JSONArray)rocParams;

                paramObj = ParamUtil.getMethodParameter(action, exeMethod, paramsArray);
            }
        } else {
            paramObj = ParamUtil.getMethodParameter(action, exeMethod, callJson);
        }

        //路径方式载入参数
        Operate operate = exeMethod.getAnnotation(Operate.class);
        if (operate != null && operate.method().contains(ParamUtil.variableBegin) && operate.method().contains(ParamUtil.variableEnd)) {
            paramObj = ParamUtil.getMethodParameter(action, exeMethod,actionProxy.getExeType());
            if (action.hasFieldInfo())
            {
                return null;
            }
        }

        boolean isVoid = false;
        Object methodResult = null;
        if (!TXWebUtil.defaultExecute.equals(exeMethod.getName())) {
            //载入默认参数,修复方法参数匹配
            if (paramObj == null && exeMethod.getParameterCount() != 0 || (paramObj != null && paramObj.length != exeMethod.getParameterCount())) {
                //一个参数都没有的情况
                paramObj = ParamUtil.getMethodParameter(action, exeMethod ,actionProxy.getExeType());
                if (action.hasFieldInfo())
                {
                    return null;
                }
            }

            try {
                if (exeMethod.getGenericReturnType().equals(Void.TYPE)) {
                    //如果不用返回,直接执行
                    isVoid = true;
                    TXWebUtil.invokeFun(action, exeMethod, paramObj);
                } else {
                    methodResult = TXWebUtil.invokeFun(action, exeMethod, paramObj);
                }
                //执行完后已经执行 execute，确保和form方式一样
            } catch (Exception e) {
                log.error(actionProxy.getMethod() + " params is " + ObjectUtil.toString(paramObj), e);
                //RPC 2.0 标准  返回
                action.addFieldInfo(Environment.errorInfo, e.getMessage());
                return null;
            }
        }

        if (action.hasFieldInfo()||resultJson == null || resultJson.isEmpty()) {
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
            if (TXWebUtil.defaultExecute.equals(methodName) || methodName.equals(exeMethod.getName()) || resultMap.containsKey(exeMethod.getName())) {
                continue;
            }
            Method method = ClassUtil.getDeclaredMethod(actionClass, methodName);
            if (method == null) {
                action.addFieldInfo(Environment.warningInfo, methodName + "不存在的方法");
                continue;
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
        if (!isVoid && !TXWebUtil.defaultExecute.equals(exeMethod.getName())) {
            if (methodResult == null) {
                resultMap.put(methodFiled, new JSONObject());
            } else if (ClassUtil.isStandardProperty(methodResult.getClass()) || ClassUtil.isCollection(methodResult)) {
                resultMap.put(methodFiled, methodResult);
            } else {
                resultMap.put(methodFiled, methodResult);
            }
        }
        //没有返回对象，就不添加了，后被统一返回信息
        if (resultMap.isEmpty()) {
            return null;
        }
        return resultMap;
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 步骤: 1:先放入请求参数,2:执行请求方法;3:如果有多个返回，继续执行多个返回,5：最后执行defaultExecute
     *
     * @param actionProxy action对象
     */

    public static void putJsonParams(ActionProxy actionProxy)  {
        //放入请求参数 begin
        Map<String, Object> actionParams = new HashMap<>();
        JSONObject callJson = actionProxy.getCallJson();
        ActionSupport action = actionProxy.getAction();
        //参数表示 set全局action参数
        JSONObject paramsJson = callJson.getJSONObject(Environment.rocParams);
        if (paramsJson != null) {
            for (Object key : paramsJson.keySet()) {
                Object objParam = paramsJson.get(key);
                if (objParam instanceof JSONArray) {
                    objParam = ((JSONArray) objParam).toArray();
                }
                actionParams.put((String) key, objParam);
            }
            putJsonProperty(action, actionParams);
        }
        //放入请求参数 end

    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 得到要执行的方法，多种匹配方式
     *
     * @param actionProxy action代理
     * @param actionClass 类对象,方便识别
     * @param method      方法名称
     * @return 返回方法
     */
    static public Method getExeMethod(ActionProxy actionProxy,Class<?> actionClass, String method) {
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
        //默认能直接得到的
        Method exeMethod = null;
        if (method.startsWith(TXWebUtil.AT) && method.length() > 1) {
            try {
                method = action.getString(method.substring(1), true);
            } catch (Exception e) {
                method = StringUtil.empty;
            }
        }

        //参数在前边已经放入,这里只需要执行后返回
        JSONObject callJson = actionProxy.getCallJson();
        //ROC roc Json 根据指定调用返回
        JSONObject rocMethodJson = null;
        if (callJson != null) {
            rocMethodJson = callJson.getJSONObject(Environment.rocMethod);
        }

        boolean isArray = true;
        JSONObject paramsObject = null;
        JSONArray paramsArray = null;
        if (rocMethodJson != null && rocMethodJson.containsKey(Environment.rocParams)) {
            paramsArray = rocMethodJson.getJSONArray(Environment.rocParams);
            if (paramsArray == null) {
                paramsObject = rocMethodJson.getJSONObject(Environment.rocParams);
                isArray = false;
            }
        }


        int iParam = 0;
        if (isArray) {
            iParam = paramsArray == null ? 0 : paramsArray.length();
        } else {
            iParam = paramsObject == null ? 0 : paramsObject.size();
        }
        //配置了Operate

        //这里只用判断存在于方法命名空间之后就可以了
        Map<Operate, Method> operateMap = getClassOperateList(actionClass);
        for (Operate operate : operateMap.keySet()) {
            //处理通配符情况
            if (TXWebUtil.AT.equals(operate.method())) {
                Method tmpMethod = operateMap.get(operate);
                if (tmpMethod != null && tmpMethod.getName().equalsIgnoreCase(method) && tmpMethod.getParameterCount() == iParam)
                {
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
                String urlNamespace = StringUtil.BACKSLASH + actionProxy.getNamespace();
                //URL外部路径
                String url = URLUtil.getURLPath(action.getRequest().getRequestURI()) + action.getEnv(ActionEnv.Key_ActionName);
                //当前允许执行的方法
                String operateUrl = urlNamespace + (operate.method().startsWith(StringUtil.BACKSLASH) ? operate.method() : (StringUtil.BACKSLASH + operate.method()));
                if (url.startsWith(urlNamespace) && operate.method().contains(ParamUtil.variableBegin)) {
                    //方法配置的路径 路径参数方式${} {}
                    String methodUrl = StringUtil.substringBefore(operate.method(), ParamUtil.variableBegin);
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

        //这里要添加路径方式
        //先判断映射，在判断方法
        if (exeMethod == null) {
            Method[] methodList = ClassUtil.getDeclaredMethodList(actionClass, method);

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
                    if (met.getName().equals(method))
                    {
                        exeMethod = met;
                        break;
                    }
                }
            }
        }

        return exeMethod;
    }


    /**
     * 这里是执行可返回函数的地方,返回的数据主要是做ROC返回
     * {@code Object[] paramObj = getMethodParameter(action, exeMethod, paramsArray); }
     *
     * @param action    action
     * @param exeMethod 调用方法
     * @param paramObj  参数
     * @return 返回结果
     * @throws Exception 异常
     */
    public static Object invokeFun(Action action, Method exeMethod, Object[] paramObj) throws Exception {
        //安全检查begin
        //执行方法为空
        if (exeMethod == null) {
            return null;
        }

        if (action == null) {
            log.error("invoke execute action is Null ,action 不能为空,请检查配置");
            throw new ClassNotFoundException("action is null");
        }

        //有异常就不在执行
        if (action.hasFieldInfo()) {
            return null;
        }

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
            Throwable e = exception.getTargetException();
            if (e instanceof TransactionException) {
                TransactionException transactionException = (TransactionException) e;
                Transaction transaction = transactionException.getTransaction();
                if (transaction != null&&!StringUtil.isEmpty(transaction.message()))
                {
                    String msg = transaction.message();
                    if (msg.contains(ParamUtil.variableBegin) && msg.contains(ParamUtil.variableEnd)) {
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
            } else if (e instanceof Exception) {
                //--------------------------
                Exception err = (Exception) e;
                action.addFieldInfo(Environment.errorInfo, ThrowableUtil.getThrowableMessage(err));
            }
            else {
                action.addFieldInfo(Environment.errorInfo, ThrowableUtil.getThrowableMessage(e));
            }
        } finally {
            action.put(ActionEnv.Key_CallMethodName, exeMethod);
        }
        return result;
    }


    /**
     * 标签是否满足操作配置
     *
     * @param action    action
     * @param exeMethod 执行方法
     * @return 是否继续执行这个方法
     * @throws Exception 异常
     */
    public static boolean checkOperate(Action action, Method exeMethod) throws Exception {
         //验证 begin
        Operate operate = exeMethod.getAnnotation(Operate.class);
        if (operate == null) {
            log.debug("action:{},exeMethod:{}", action.getClass(), exeMethod);
            throw new Exception("不允许执行的操作," + exeMethod.getName());
        }
        //没有Operate标记的不允许对外访问
        //注意，档位GET的时候 submit 为 false
        if (operate.post() && TXWeb.httpGET.equalsIgnoreCase(action.getRequest().getMethod())) {
            return false;
            //throw new Exception("错误的请求方式,方法名称:" + exeMethod.getName());
        }

        //验证防止重复提交 begin
        if (operate.repeat() > 0) {
            String keyValue = ClassUtil.getClass(action.getClass()).getName() + "." + exeMethod.getName() + "." + action.getUserSession().getId();
            keyValue = EncryptUtil.getMd5(keyValue);
            String key = String.format(REPEAT_VERIFY_KEY, keyValue);
            RedissonClient redissonClient = (RedissonClient) beanFactory.getBean(RedissonClientConfig.class);
            if (redissonClient != null) {
                RBucket<String> bucket = redissonClient.getBucket(key);
                if (bucket.isExists()) {
                    throw new Exception(operate.repeat() + "秒后再试");
                } else {
                    bucket.set(key, operate.repeat(), TimeUnit.SECONDS);
                }
            }
        }
        //验证防止重复提交 end
        return true;
    }


    /**
     * 得到namespace
     *
     * @param servletPath 传入 request.servletPath
     * @return namespace 命名空间
     */
    public static String getNamespace(String servletPath) {
        String namespace = URLUtil.getURLPath(servletPath);
        if (namespace.endsWith(StringUtil.ASTERISK)) {
            namespace = namespace.substring(0, namespace.length() - 1);
        }
        if (namespace.endsWith(StringUtil.BACKSLASH)) {
            namespace = namespace.substring(0, namespace.length() - 1);
        }
        if (namespace.startsWith(StringUtil.BACKSLASH)) {
            namespace = namespace.substring(1);
        }
        if (StringUtil.BACKSLASH.equals(namespace)) {
            return StringUtil.empty;
        }
        return namespace;
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param servletPath 得到方法
     * @return 得到更目录
     */
    public static String getRootNamespace(String servletPath) {
        if (servletPath == null) {
            return StringUtil.empty;
        }
        if (!servletPath.contains("/")) {
            return servletPath;
        }
        if (servletPath.startsWith("http")) {
            servletPath = StringUtil.substringAfter(servletPath, URLUtil.getHostUrl(servletPath));
        }
        String namespace = URLUtil.getURLPath(servletPath);
        if (namespace.startsWith("/")) {
            namespace = namespace.substring(1);
        }
        if (namespace.contains("/")) {
            return StringUtil.substringBefore(namespace, "/");
        }
        return namespace;
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
    public static void print(String string, int type, HttpServletResponse response, final int status) {
        if (response == null) {
            return;
        }
        if (response.isCommitted()) {

            StringBuilder sb = new StringBuilder();
            for (StackTraceElement stackTraceElement : Thread.currentThread().getStackTrace()) {
                sb.append(stackTraceElement.getLineNumber()).append(StringUtil.COLON).append(stackTraceElement.getClassName()).append(".").append(stackTraceElement.getMethodName()).append(StringUtil.CRLF);
            }
            log.error("response 已经提交并且关闭,又输出信息:{},调用方法:{}", string, sb.toString());
            return;
        }
        String contentType = "text/html";
        if (WebOutEnumType.JSON.getValue() == type) {
            contentType = "application/json";
        }
        if (WebOutEnumType.JAVASCRIPT.getValue() == type) {
            contentType = "text/javascript; charset=" + response.getCharacterEncoding();
        }
        if (WebOutEnumType.XML.getValue() == type) {
            contentType = "text/xml";
        }
        if (WebOutEnumType.TEXT.getValue() == type) {
            contentType = "text/plain";
        }
        if (WebOutEnumType.HTML.getValue() == type) {
            contentType = "text/html";
        }
        AtomicReference<StringBuilder> sb = new AtomicReference<>(new StringBuilder());
        sb.get().append(contentType).append(";charset=").append(response.getCharacterEncoding());
        response.setContentType(sb.toString());
        if (status > 0) {
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
            //if (out != null) out.close();
        } catch (Exception e) {
            e.printStackTrace();
            log.error("response writer is close,not out error", e);
        }
    }

    /**
     * 格式兼容
     *
     * @param json     json数据
     * @param type     类型
     * @param response 应答
     * @param status   状态
     */
    public static void print(JSONObject json, int type, HttpServletResponse response, int status) {
        if (WebOutEnumType.JSON.getValue() == type)
        {
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



    /**
     * @return 创建默认环境
     */
    public static Map<String, Object> createEnvironment() {
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        Map<String, Object> venParams = new HashMap<>();
        venParams.put(Environment.filterSuffix, envTemplate.getString(Environment.filterSuffix));
        venParams.put(Environment.ApiFilterSuffix, envTemplate.getString(Environment.ApiFilterSuffix));
        venParams.put(Environment.templateSuffix, envTemplate.getString(Environment.templateSuffix));
        venParams.put(Environment.encode, Dispatcher.getEncode());
        venParams.put(Environment.remoteHostUrl, envTemplate.getString(Environment.remoteHostUrl, StringUtil.empty));
        venParams.put(Environment.scriptPath, envTemplate.getString(Environment.scriptPath, "/script"));
        venParams.put(Environment.sitePath, envTemplate.getString(Environment.sitePath, "/"));
        venParams.put(Environment.userLoginUrl, envTemplate.getString(Environment.userLoginUrl, "/user/login." + envTemplate.getString(Environment.filterSuffix)));
        venParams.put(Environment.logJspxDebug, envTemplate.getBoolean(Environment.logJspxDebug));
        venParams.put("date", new Date());
        return venParams;
    }


    public static void errorPrint(String info, Map<String, String> fieldInfo, HttpServletResponse response, int status) {
        if (response.isCommitted()) {
            log.error("response 已经提交并且关闭,又输出错误信息:" + info + ",检查执行代码，之前错误信息");
            return;
        }
        if (response.getStatus() != 200) {
            return;
        }
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        TemplateConfigurable configurable = new TemplateConfigurable();
        configurable.addAutoIncludes(envTemplate.getString(Environment.autoIncludes));
        File f = new File(envTemplate.getString(Environment.templatePath), envTemplate.getString(Environment.errorInfoPageTemplate, "error.ftl"));
        AbstractSource fileSource = new FileSource(f, envTemplate.getString(Environment.templatePath), envTemplate.getString(Environment.encode, Environment.defaultEncode));
        configurable.setSearchPath(new String[]{envTemplate.getString(Environment.templatePath), Dispatcher.getRealPath()});
        ScriptMark scriptMark;
        try {
            scriptMark = new ScriptMarkEngine(ScriptmarkEnv.noCache, fileSource, configurable);
        } catch (Exception e) {
            log.error("error template page not found" + f.getAbsolutePath(), e);
            TXWebUtil.print("error template page not found,错误信息显示模版没有配置", WebOutEnumType.HTML.getValue(), response);
            return;
        }

        scriptMark.setRootDirectory(Dispatcher.getRealPath());
        scriptMark.setCurrentPath(envTemplate.getString(Environment.templatePath));
        //输出模板数据
        Map<String, Object> valueMap = createEnvironment();
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
}