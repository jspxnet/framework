package com.github.jspxnet.txweb.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sober.enums.ParamModeType;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.enums.TalkEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.tag.BeanModel;
import com.github.jspxnet.sober.annotation.NullClass;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.PathVar;
import com.github.jspxnet.txweb.annotation.Validate;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.model.param.SignParam;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.support.ValidatorAction;
import com.github.jspxnet.txweb.validator.Validator;
import com.github.jspxnet.txweb.view.ValidatorView;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * @author chenyuan
 */
@Slf4j
public class ParamUtil {

    //包含特殊字符的符号,安全过滤掉的符号 / 表示路径会用
    private final static char[] incertitudeChars = {
            '\\', '$', '\'', '!', '\"', '<', '>'
    };

    //保留空格的目的是确保不会和调用方法名称重复，重复就不会调用执行
    private final static String[] safetyFilterKeys = {
            "<script>", " or ", " and ", "update ", " where ", "select ", " from ", "delete ", "insert ", "drop ", "xp_cmdshell", "wscript.shell", "create ", "shell "
    };

    // 危险的javascript:关键字j av a script
    private final static Pattern[] DANGEROUS_TOKENS = new Pattern[]{Pattern.compile("^j\\s*a\\s*v\\s*a\\s*s\\s*c\\s*r\\s*i\\s*p\\s*t\\s*:",
            Pattern.CASE_INSENSITIVE)};

    // javascript:替换字符串（全角中文字符）
    private final static String[] DANGEROUS_TOKEN_REPLACEMENTS = new String[]{"ＪＡＶＡＳＣＲＩＰＴ："};

    final static public String variableBegin = "${";
    final static public  String variableEnd = "}";

    private ParamUtil() {

    }

    /**
     * 自动生成Querystring参数
     *
     * @param request 请求
     * @param paras   参数
     * @param level   安全等级
     * @return 请求字符串
     * @throws Exception 不支持的编码
     */
    public static String getQueryString(HttpServletRequest request, String paras, SafetyEnumType level) throws Exception {
        if (request == null) {
            return StringUtil.empty;
        }
        StringBuilder queryString = new StringBuilder();
        ///////////////载入所有参数 begin
        if (StringUtil.ASTERISK.equals(paras)) {
            String[] names = RequestUtil.getParameterNames(request);
            int i = 0;
            if (names != null) {
                for (String key : names) {
                    String value = request.getParameter(key);
                    boolean isSafe = ParamUtil.isSafe(value, 0, 200, level);
                    if (value != null && isSafe) {
                        if (i != 0) {
                            queryString.append("&");
                        }
                        queryString.append(key).append("=").append(java.net.URLEncoder.encode(ParamUtil.getSafeFilter(value, 200, level), request.getCharacterEncoding()));
                        i++;
                    }
                }
            }
            return queryString.toString();
        }
        ///////////////载入所有参数 end
        String[] paraArray = StringUtil.split(paras, StringUtil.SEMICOLON);
        String[] names = RequestUtil.getParameterNames(request);
        int i = 0;
        if (names != null) {
            for (String key : names) {
                if (ArrayUtil.inArray(paraArray, key, true)) {
                    String value = request.getParameter(key);
                    boolean isSafe = ParamUtil.isSafe(value, 0, 200, level);
                    if (value != null & isSafe) {
                        if (i != 0) {
                            queryString.append("&");
                        }
                        queryString.append(key).append("=").append(java.net.URLEncoder.encode(ParamUtil.getSafeFilter(value, 200, level), request.getCharacterEncoding()));
                        i++;
                    }
                }
            }
        }
        return queryString.toString();
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param str       参数,这里只出了字符串的，数字的直接判断
     * @param minLength 允许最小长度
     * @param maxLength 允许最大长度
     * @param level     安全级别 1:基本到安全，判断是否有特殊符号 IncertitudeChars 2:sql，判断是否有sql符号 sqlKeys 3:url 安全过滤 safetyUrlKeys
     * @return 是否安全
     */
    public static boolean isSafe(String str, int minLength, long maxLength, SafetyEnumType level) {
        if (str == null || SafetyEnumType.NONE.equals(level)) {
            return true;
        }
        if (str.length() > maxLength) {
            return false;
        }


        if (str.length() < Math.max(minLength, 0)) {
            return false;
        }

        //1
        for (char c : incertitudeChars) {
            if (str.indexOf(c) != -1) {
                return false;
            }
        }
        if (SafetyEnumType.LOW.equals(level))
        {
            return true;
        }
        //2
        str = str.toLowerCase();
        for (String safetyUrlKey : safetyFilterKeys) {
            if (str.contains(safetyUrlKey)) {
                return false;
            }
        }

        if (level.getValue() < SafetyEnumType.MIDDLE.getValue()) {
            return true;
        }
        for (String safetyUrlKey : StringUtil.split(HtmlUtil.safeFilter_delAllTags, StringUtil.COMMAS)) {
            if (str.contains(safetyUrlKey)) {
                return false;
            }
        }
        return true;
    }

    /**
     * @param number 数字
     * @param min    最小
     * @param max    最大
     * @return 是否满足,在这个范围类返回 true
     */
    public static boolean isSafe(Number number, int min, long max) {
        if (number == null) {
            return true;
        }
        return min <= number.longValue() && number.longValue() <= max;
    }

    /**
     * 安全过滤字符串
     *
     * @param str       字符串
     * @param maxLength 最大长度
     * @param level     安全等级
     * @return 过滤得到的字符串
     */
    public static String getSafeFilter(String str, int maxLength,  SafetyEnumType level) {
        if (str == null) {
            return StringUtil.empty;
        }
        if (str.length() > maxLength) {
            return StringUtil.empty;
        }
        String result = HtmlUtil.deleteHtml(str);
        //1
        for (char c : incertitudeChars) {
            result = StringUtil.replaceIgnoreCase(result, c + "", StringUtil.empty);
        }
        if (level.getValue() < SafetyEnumType.LOW.getValue()) {
            return result;
        }
        //2
        for (int i = 0; i < DANGEROUS_TOKENS.length; ++i) {
            str = DANGEROUS_TOKENS[i].matcher(str).replaceAll(
                    DANGEROUS_TOKEN_REPLACEMENTS[i]);
        }
        for (String safetyUrlKey : safetyFilterKeys) {
            result = StringUtil.replaceIgnoreCase(result, safetyUrlKey, StringUtil.halfToFull(safetyUrlKey));
        }
        if (level.getValue() < SafetyEnumType.MIDDLE.getValue())
        {
            return result;
        }
        //3
        try {
            result = HtmlUtil.getSafeFilter(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }


    /**
     * 数组方式传入参数
     *
     * @param action      action
     * @param exeMethod   执行的方法
     * @param paramsArray 参数
     * @return 得到要传递的参数
     * @throws Exception 异常
     */
    public static Object[] getMethodParameter(Action action, Method exeMethod, JSONArray paramsArray) throws Exception {
        Type[] pTypes = exeMethod.getGenericParameterTypes();
        if (ArrayUtil.isEmpty(pTypes)) {
            return null;
        }
        Parameter[] parameters = exeMethod.getParameters();
        Object[] paramObj = new Object[pTypes.length];
        //i 表示第几个参数，下边完成参数组装
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] annotations = parameters[i].getDeclaredAnnotations();
            Type pType = pTypes[i];
            String paramName = parameters[i].getName();
            boolean isParam = false;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    isParam = true;
                    Param param = (Param) annotation;
                    if (!ClassUtil.isStandardType(pType) && !ClassUtil.isArrayType(pType)) {
                        paramObj[i] = action.getBean(ClassUtil.loadClass(pType.getTypeName()));
                        //填充类内部的默认值begin
                        isNullSetDefaultValue(action, param, parameters[i].getName(), paramObj[i]);
                        if (action.hasFieldInfo()) {
                            return paramObj;
                        }
                        //填充类内部的默认值end
                    } else if (paramsArray != null && paramsArray.size() > i) {
                        //验证参数
                        paramObj[i] = BeanUtil.getTypeValue(paramsArray.get(i), pType);
                    }
                    isRequired( action,  param,  paramName, paramObj[i]);
                    if (action.hasFieldInfo()) {
                        return paramObj;
                    }
                    //放入默认值
                    if (paramObj[i] == null) {
                        paramObj[i] = BeanUtil.getTypeValue(param.value(), pType);
                    }
                }


                if (annotation instanceof PathVar) {
                    HttpServletRequest request = action.getRequest();
                    if (request == null) {
                        continue;
                    }
                    Operate operate = exeMethod.getAnnotation(Operate.class);
                    PathVar pathVar = (PathVar) annotation;

                    String varName = StringUtil.isNull(pathVar.name()) ? parameters[i].getName() : pathVar.name();
                    if (operate != null && operate.method().contains(varName)) {
                        String urlPath = URLUtil.getURLPath(request.getRequestURI()) + action.getEnv(ActionEnv.Key_ActionName);
                        String tempMethodUrl = StringUtil.substringBefore(operate.method(), StringUtil.BACKSLASH);
                        String checkPath = StringUtil.substringAfter(urlPath, tempMethodUrl);
                        String operatePath = StringUtil.substringAfter(operate.method(), tempMethodUrl);

                        if (checkPath.startsWith(StringUtil.BACKSLASH)) {
                            checkPath = checkPath.substring(1);
                        }
                        if (operatePath.startsWith(StringUtil.BACKSLASH)) {
                            operatePath = operatePath.substring(1);
                        }
                        String[] values = StringUtil.split(checkPath, StringUtil.BACKSLASH);
                        String[] paths = StringUtil.split(operatePath, StringUtil.BACKSLASH);
                        if (values.length != paths.length || ArrayUtil.isEmpty(values) || ArrayUtil.isEmpty(paths)) {
                            action.addFieldInfo(Environment.warningInfo, "错误的访问路径");
                            return null;
                        }
                        for (int p = 0; p < values.length; p++) {
                            if (paths[i].contains("{" + varName + "}")) {
                                if (ClassUtil.isNumberType(pType) && StringUtil.isStandardNumber(values[i])) {
                                    if (isSafe(ObjectUtil.toLong(values[i]), pathVar.min(), pathVar.max())) {
                                        paramObj[i] = BeanUtil.getTypeValue(values[i], pType);
                                    } else
                                    {
                                        String message = StringUtil.isEmpty(pathVar.message())?("参数值域不正确,["+pathVar.min()+"-"+ pathVar.max()+"]"):pathVar.message();
                                        action.addFieldInfo(Environment.errorInfo, message);
                                        return null;
                                    }
                                } else {
                                    long max = pathVar.max() == Integer.MAX_VALUE ? 50000 : pathVar.max();
                                    if (isSafe(values[i], pathVar.min(), max, pathVar.level())) {
                                        paramObj[i] = BeanUtil.getTypeValue(values[i], pType);
                                    }
                                    else
                                    {
                                        String message = StringUtil.isEmpty(pathVar.message())?("参数值域不正确,["+pathVar.min()+"-"+ pathVar.max()+"]"):pathVar.message();
                                        action.addFieldInfo(Environment.errorInfo, message);
                                        return null;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            //处理没有标签单个参数的情况
            if (!isParam && paramsArray != null && paramsArray.size() > i && ClassUtil.isStandardType(pType)) {
                paramObj[i] = BeanUtil.getTypeValue(paramsArray.get(i), pType);
            }
        }
        if (!isMethodParamSafe(action, exeMethod, paramObj)) {
            //参数非法
            return new Object[pTypes.length];
        }
        return paramObj;
    }

    /**
     * 这里参数为 JSONObject 方式
     *
     * @param action     action
     * @param exeMethod  执行方法
     * @param paramsJson 参数json对象
     * @return 这里参数为 JSONObject 方式
     * @throws Exception 异常
     */
    public static Object[] getMethodParameter(Action action, Method exeMethod, JSONObject paramsJson) throws Exception {
        Type[] pTypes = exeMethod.getGenericParameterTypes();
        if (ObjectUtil.isEmpty(pTypes)) {
            return null;
        }
        Object[] paramObj = new Object[pTypes.length];
        //i 表示第几个参数，下边完成参数组装
        Parameter[] parameters = exeMethod.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] annotations = parameters[i].getDeclaredAnnotations();
            Type pType = pTypes[i];
            String paramName = parameters[i].getName();
            boolean isParam = false;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    isParam = true;
                    Param param = (Param) annotation;
                    if (!ClassUtil.isStandardType(pType) && !ClassUtil.isArrayType(pType)) {
                        if (paramsJson != null && paramsJson.containsKey(paramName)) {
                            if (ParamModeType.RocMode.getValue()==param.modeType().getValue())
                            {
                               // paramsJson.containsKey(Environment.rocParams)
                                paramObj[i] = BeanUtil.getTypeValue(paramsJson.get(paramName), pType);
                            }
                            if (ParamModeType.JsonMode.getValue()==param.modeType().getValue()&&pType.equals(JSONObject.class))
                            {
                                JSONObject jsonObject = (JSONObject)action.getEnv().get(ActionEnv.Key_CallRocJsonData);
                                if (jsonObject!=null)
                                {
                                    paramObj[i] = jsonObject;
                                }
                            }
                            if (ParamModeType.SpringMode.getValue()==param.modeType().getValue())
                            {
                                JSONObject jsonObject = (JSONObject)action.getEnv().get(ActionEnv.Key_CallRocJsonData);
                                if (jsonObject!=null)
                                {
                                    paramObj[i] = jsonObject.parseObject(ClassUtil.loadClass(pType.getTypeName()));
                                }
                            }

                        } else {
                            if (ParamModeType.RocMode.getValue()==param.modeType().getValue())
                            {
                                paramObj[i] = action.getBean(ClassUtil.loadClass(pType.getTypeName()));
                            }
                            if (ParamModeType.JsonMode.getValue()==param.modeType().getValue()&&pType.equals(JSONObject.class))
                            {
                                JSONObject jsonObject = (JSONObject)action.getEnv().get(ActionEnv.Key_CallRocJsonData);
                                if (jsonObject!=null)
                                {
                                    paramObj[i] = jsonObject;
                                }
                            }
                            if (ParamModeType.SpringMode.getValue()==param.modeType().getValue())
                            {
                                JSONObject jsonObject = (JSONObject)action.getEnv().get(ActionEnv.Key_CallRocJsonData);
                                if (jsonObject!=null)
                                {
                                    paramObj[i] = jsonObject.parseObject(ClassUtil.loadClass(pType.getTypeName()));
                                }
                            }
                        }
                        //填充类内部的默认值begin
                        isNullSetDefaultValue(action, param, parameters[i].getName(), paramObj[i]);
                        if (action.hasFieldInfo()) {
                            return paramObj;
                        }
                        //填充类内部的默认值end
                    } else if (paramsJson != null && paramsJson.containsKey(paramName)) {
                        //验证参数
                        //没有值的保留为空
                        paramObj[i] = BeanUtil.getTypeValue(paramsJson.get(paramName), pType);
                    }
                    //-------------
                    isRequired( action,  param,  paramName, paramObj[i]);
                    if (action.hasFieldInfo()) {
                        return paramObj;
                    }
                    if (paramObj[i] == null)
                    {
                        //放入默认参数
                        paramObj[i] = BeanUtil.getTypeValue(param.value(), pType);
                    }
                }

                if (annotation instanceof PathVar) {
                    //路径作为参数方式
                    HttpServletRequest request = action.getRequest();
                    if (request == null) {
                        continue;
                    }
                    Operate operate = exeMethod.getAnnotation(Operate.class);
                    PathVar pathVar = (PathVar) annotation;
                    if (operate != null && operate.method().contains(pathVar.name())) {
                        String urlPath = URLUtil.getURLPath(request.getRequestURI()) + action.getEnv(ActionEnv.Key_ActionName);
                        String tempMethodUrl = StringUtil.substringBefore(operate.method(), StringUtil.BACKSLASH);
                        String checkPath = StringUtil.substringAfter(urlPath, tempMethodUrl);
                        String operatePath = StringUtil.substringAfter(operate.method(), tempMethodUrl);
                        if (checkPath.startsWith(StringUtil.BACKSLASH)) {
                            checkPath = checkPath.substring(1);
                        }
                        if (operatePath.startsWith(StringUtil.BACKSLASH)) {
                            operatePath = operatePath.substring(1);
                        }
                        String[] values = StringUtil.split(checkPath, StringUtil.BACKSLASH);
                        String[] paths = StringUtil.split(operatePath, StringUtil.BACKSLASH);
                        if (values.length != paths.length || ArrayUtil.isEmpty(values) || ArrayUtil.isEmpty(paths))
                        {
                            String message = StringUtil.isEmpty(pathVar.message())?("错误的访问路径"):pathVar.message();
                            action.addFieldInfo(Environment.errorInfo, message);
                            return null;
                        }
                        for (int p = 0; p < values.length; p++) {
                            if (paths[i].contains("{" + pathVar.name() + "}")) {
                                paramObj[i] = BeanUtil.getTypeValue(values[i], pType);
                            }
                        }
                    }
                }
            }
            //处理没有标签单个参数的情况
            if (!isParam && paramsJson != null && paramsJson.size() > i && ClassUtil.isStandardType(pType)) {
                paramObj[i] = BeanUtil.getTypeValue(paramsJson.get(paramName), pType);
            }
        }
        if (!isMethodParamSafe(action, exeMethod, paramObj)) {
            //参数非法
            return new Object[pTypes.length];
        }
        return paramObj;
    }
    //-----------------------------------------------------------------------------------------------------

    /**
     * 这里参数为 从请求中得到参数
     * 这里只放入,验证单独方法统一处理
     *
     * @param action    动作
     * @param exeMethod 方法
     * @return 参数对象
     * @throws Exception 异常
     */
    public static Object[] getMethodParameter(Action action, Method exeMethod) throws Exception {
        Type[] pTypes = exeMethod.getGenericParameterTypes();
        if (ObjectUtil.isEmpty(pTypes)) {
            return null;
        }
        Object[] paramObj = new Object[pTypes.length];
        //i 表示第几个参数，下边完成参数组装
        Parameter[] parameters = exeMethod.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] annotations = parameters[i].getDeclaredAnnotations();
            Type pType = pTypes[i];
            String paramName = parameters[i].getName();
            boolean isParam = false;
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    isParam = true;
                    Param param = (Param) annotation;
                    if (!ClassUtil.isStandardType(pType) && !ClassUtil.isArrayType(pType) && !ClassUtil.isCollection(pType)) {
                        if (ParamModeType.RocMode.getValue()==param.modeType().getValue())
                        {
                            JSONObject jsonObject = (JSONObject)action.getEnv().get(ActionEnv.Key_CallRocJsonData);
                            boolean isRoc = false;
                            if (jsonObject!=null&&jsonObject.containsKey(Environment.Protocol))
                            {
                                String protocol = jsonObject.getString(Environment.Protocol);
                                if (protocol!=null&&protocol.contains("roc"))
                                {
                                    isRoc = true;
                                }
                            }
                            if (isRoc)
                            {
                                paramObj[i] = action.getBean(ClassUtil.loadClass(pType.getTypeName()));
                            } else
                            {
                                if (jsonObject!=null)
                                {
                                    paramObj[i] = jsonObject.parseObject(ClassUtil.loadClass(pType.getTypeName()));
                                }
                            }
                        }
                        if (ParamModeType.JsonMode.getValue()==param.modeType().getValue()&&pType.equals(JSONObject.class))
                        {
                            JSONObject jsonObject = (JSONObject)action.getEnv().get(ActionEnv.Key_CallRocJsonData);
                            if (jsonObject!=null)
                            {
                                paramObj[i] = jsonObject;
                            }
                        }
                        if (ParamModeType.SpringMode.getValue()==param.modeType().getValue())
                        {
                            JSONObject jsonObject = (JSONObject)action.getEnv().get(ActionEnv.Key_CallRocJsonData);
                            if (jsonObject!=null)
                            {
                                paramObj[i] = jsonObject.parseObject(ClassUtil.loadClass(pType.getTypeName()));
                            }
                        }
                        //填充类内部的默认值begin
                        isNullSetDefaultValue(action, param, parameters[i].getName(), paramObj[i]);
                        if (action.hasFieldInfo()) {
                            return paramObj;
                        }
                        //填充类内部的默认值end
                    } else {
                        //验证参数
                        if (RequestUtil.isParameter(action.getRequest(), paramName)) {
                            if (ClassUtil.isArrayType(pType) || ClassUtil.isCollection(pType)) {
                                paramObj[i] = BeanUtil.getTypeValue(action.getArray(paramName, false), pType);
                            } else {
                                paramObj[i] = BeanUtil.getTypeValue(action.getString(paramName, false), pType);
                            }
                        }
                        //放入默认参数
                        isRequired( action,  param,  paramName, paramObj[i]);
                        if (action.hasFieldInfo()) {
                            return paramObj;
                        }
                        if (paramObj[i] == null)
                        {
                            //放入默认参数
                            paramObj[i] = BeanUtil.getTypeValue(param.value(), pType);
                        }
                    }
                }


                if (annotation instanceof PathVar) {
                    HttpServletRequest request = action.getRequest();
                    if (request == null) {
                        continue;
                    }
                    isParam = true;
                    Operate operate = exeMethod.getAnnotation(Operate.class);
                    PathVar pathVar = (PathVar) annotation;
                    String pathVarName = paramName;
                    if (!StringUtil.isEmpty(pathVar.name())) {
                        pathVarName = pathVar.name();
                    }

                    if (operate != null && operate.method().contains(pathVarName)) {
                        String urlPath = URLUtil.getURLPath(request.getRequestURI()) + action.getEnv(ActionEnv.Key_ActionName);
                        String tempMethodUrl = StringUtil.substringBefore(operate.method(), variableBegin);
                        String checkPath = StringUtil.substringAfter(urlPath, tempMethodUrl);

                        String operatePath = StringUtil.substringAfter(operate.method(), tempMethodUrl);
                        if (checkPath.startsWith(StringUtil.BACKSLASH)) {
                            checkPath = checkPath.substring(1);
                        }
                        if (operatePath.startsWith(StringUtil.BACKSLASH)) {
                            operatePath = operatePath.substring(1);
                        }
                        String[] values = StringUtil.split(checkPath, StringUtil.BACKSLASH);
                        String[] paths = StringUtil.split(operatePath, StringUtil.BACKSLASH);
                        if (values.length != paths.length || ArrayUtil.isEmpty(values) || ArrayUtil.isEmpty(paths)) {
                            String message = StringUtil.isEmpty(pathVar.message())?("错误的访问路径"):pathVar.message();
                            action.addFieldInfo(Environment.errorInfo, message);
                            return null;
                        }
                        Map<String, Object> valueMap = new HashMap<>();
                        String varName = StringUtil.substringBetween(paths[i], variableBegin, variableEnd);
                        if (!StringUtil.isEmpty(varName) && !varName.contains("+") && !varName.contains("-") && !varName.contains("*") && !varName.contains(StringUtil.BACKSLASH) && !varName.contains("(")) {
                            valueMap.put(varName, values[i]);
                        }
                        paramObj[i] = BeanUtil.getTypeValue(EnvFactory.getPlaceholder().processTemplate(valueMap, paths[i]), pType);
                    }
                }
            }
            //处理没有标签单个参数的情况
            if (!isParam && paramObj[i] == null && ClassUtil.isStandardType(pType)) {
                paramObj[i] = BeanUtil.getTypeValue(action.getString(paramName), pType);
            } else if (!isParam && paramObj[i] == null && (ClassUtil.isArrayType(pType) || ClassUtil.isCollection(pType))) {
                paramObj[i] = BeanUtil.getTypeValue(action.getArray(paramName, false), pType);
            }
        }
        if (isMethodParamSafe(action, exeMethod, paramObj)) {
            return paramObj;
        }
        return new Object[pTypes.length];
    }
    //-----------------------------------------------------------------------------------------------------

    /**
     * 验证参数配置标签
     *
     * @param action    action
     * @param exeMethod 执行方法
     * @param value     参数
     * @return  表示不安全
     */
    public static boolean isMethodParamSafe(Action action, Method exeMethod, Object[] value) {
        if (value == null) {
            return true;
        }
        Type[] pTypes = exeMethod.getGenericParameterTypes();
             //i 表示第几个参数，下边完成参数组装
        Parameter[] parameters = exeMethod.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] annotations = parameters[i].getDeclaredAnnotations();
            Type pType = pTypes[i];
            String paramName = parameters[i].getName();
            for (Annotation annotation : annotations) {
                if (annotation instanceof Param) {
                    Param param = (Param) annotation;
                    if (!ClassUtil.isStandardType(pType) && !ClassUtil.isArrayType(pType) && !ClassUtil.isCollection(pType)) {
                        //参数对象
                        isRequired(action, param, paramName, value[i]);
                        if (action.hasFieldInfo()) {
                            return false;
                        }
                    } else {
                        //验证参数空
                        isRequired(action, param, paramName, value[i]);
                        if (action.hasFieldInfo()) {
                            return false;
                        }
                        //这里是检查参数安全性 begin

                        if (ClassUtil.isArrayType(pType)) {
                            if (value[i] != null && value[i].getClass().isArray()) {
                                if (pType.equals(String[].class)) {
                                    int length = Array.getLength(value[i]);
                                    for (int x = 0; x < length; x++) {
                                        String theParam = (String) Array.get(value[i], x);
                                        isSafeParam(action, param, paramName, theParam);
                                        if (action.hasFieldInfo()) {
                                            return false;
                                        }
                                    }
                                }
                                if ((pType.equals(int[].class) || pType.equals(long[].class) || pType.equals(float[].class) || pType.equals(double[].class) ||
                                        pType.equals(Integer[].class) || pType.equals(Long[].class) || pType.equals(Float[].class) || pType.equals(Double[].class) || pType.equals(BigDecimal[].class))) {
                                    int length = Array.getLength(value[i]);
                                    for (int x = 0; x < length; x++) {
                                        Object theParam = Array.get(value[i], x);
                                        isSafeParam(action, param, paramName, theParam);
                                        if (action.hasFieldInfo()) {
                                            return false;
                                        }
                                    }
                                }
                            }
                        }
                        else
                        {
                            isSafeParam(action,param,paramName,value[i]);
                            if (action.hasFieldInfo()) {
                                return false;
                            }
                        }
                        //这里是检查参数安全性 end
                    }
                }
                if (annotation instanceof Validate) {
                    //对象参数方式验证放这里，就是QO
                    if (!validateObject(action, (Validate) annotation, value[i])) {
                        return false;
                    }
                }

            }
        }
        return true;
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 判断是否必填
     *
     * @param action    action
     * @param param     参数
     * @param paramName  参数名称
     * @param theParam  当前参数
     *  判断是否必填 false, 为不安全
     */
    public static void isRequired(Action action, Param param, String paramName, Object theParam) {
        if (!param.required()) {
            return;
        }
        if (param.required()&&StringUtil.isEmpty(param.value())&&theParam==null)
        {
            if (StringUtil.isNull(param.message())) {
                action.addFieldInfo(Environment.warningInfo, paramName + "参数不允许空");
            } else {
                action.addFieldInfo(Environment.warningInfo, param.message());
            }
        }
    }

    //------------------------------------------------------------------------------------------------

    /**
     *  判断是否安全
     * @param action action
     * @param param 参数
     * @param paramName 参数名称
     * @param theParam 当前参数
     */
    public static void isSafeParam(Action action,Param param, String paramName, Object theParam) {
        if (theParam == null) {
            return;
        }
        if (theParam instanceof String) {
            checkSafe(action, param, paramName, theParam.toString());
        }
        if (ClassUtil.isNumberType(theParam.getClass())||theParam instanceof Number) {
            checkSafe(action, param, paramName, BigDecimal.valueOf(ObjectUtil.toDouble(theParam)));
        }
    }

    /**
     * 参数异常检查
     *
     * @param action    action
     * @param param     安全配置
     * @param paramName      参数名称
     * @param theParam  参数值
     *                  返回是否安全
     */
    private static void checkSafe(Action action,Param param, String paramName, String theParam) {
        //配置不检查

        if (SafetyEnumType.NONE.equals(param.level()))
        {
            return;
        }
        String[] strEnum = null;
        if (!NullClass.class.equals(param.enumType()) && param.enumType().isEnum()) {
            try {
                Object[] objEnums = ClassUtil.getEnumFieldValue(TalkEnumType.class, "value");
                if (objEnums != null) {
                    strEnum = new String[Array.getLength(objEnums)];
                    for (int j = 0; j < strEnum.length; j++) {
                        strEnum[j] = BeanUtil.getTypeValue(objEnums[j], String.class);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!ArrayUtil.isEmpty(strEnum) && !ArrayUtil.contains(strEnum, theParam)) {
            String message = StringUtil.isEmpty(param.message()) ? (paramName + " is unsafe,参数不在允许范围," + param.caption()) : param.message();
            action.addFieldInfo(Environment.warningInfo, message);
            return;
        }

        if (!isSafe(theParam, param.min(), param.max(), param.level())) {
             String message = StringUtil.isEmpty(param.message()) ? (param.caption() + " " + paramName + " parameter is unsafe,参数不合规," + param.caption()) : param.message();
            action.addFieldInfo(Environment.warningInfo, message);
        }
    }


    /**
     * 参数异常检查
     *
     * @param action    action
     * @param param     安全配置
     * @param paramName 参数名称
     * @param theParam  参数值
     *                  返回是否安全
     */
    private static void checkSafe(Action action,  Param param, String paramName, BigDecimal theParam) {
        //配置不检查
        if (SafetyEnumType.NONE.equals(param.level())) {
            return;
        }
        int[] strEnum = null;
        if (!NullClass.class.equals(param.enumType()) && param.enumType().isEnum()) {
            try {
                Object[] objEnums = ClassUtil.getEnumFieldValue(param.enumType(), "value");
                if (objEnums != null) {
                    strEnum = new int[Array.getLength(objEnums)];
                    for (int j = 0; j < strEnum.length; j++) {
                        strEnum[j] = BeanUtil.getTypeValue(objEnums[j], Integer.class);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (!ArrayUtil.isEmpty(strEnum) && !ArrayUtil.contains(strEnum, theParam.intValue())) {
            String message = StringUtil.isEmpty(param.message()) ? (paramName+  " is unsafe,参数不在允许范围") : param.message();
            action.addFieldInfo(Environment.warningInfo, message);
            return;
        }

        if (theParam.intValue() < param.min() || theParam.intValue() > param.max()) {
            String message = StringUtil.isEmpty(param.message()) ? (paramName + " parameter is unsafe,参数不合规") : param.message();
            action.addFieldInfo(Environment.warningInfo, message);
        }
    }


    /**
     * xml 配置方式验证
     *
     * @param action   action对象
     * @param validate 验证配置
     * @param obj      要验证的对象
     * @return 验证参数对象是否通过
     */
    public static boolean validateObject(Action action, Validate validate, Object obj) {
        if (validate == null) {
            //无配置，直接允许执行
            return true;
        }
        String formId = validate.id();
        if (StringUtil.isNull(formId) && obj != null && !(obj instanceof ActionSupport) && !ClassUtil.isStandardProperty(obj.getClass())) {
            formId = obj.getClass().getName();
        }

        if (StringUtil.isNull(formId)) {
            log.debug("没有找到 Validate formId class:{}", action.getClass().getName());
            return true;
        }

        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        String namespace = action.getRootNamespace();
        ValidatorAction validatorAction = beanFactory.getBean(ValidatorView.class, namespace);
        if (validatorAction == null) {
            //添加自动载入，减少配置
            BeanModel beanModel = new BeanModel();
            beanModel.setId(ValidatorView.class.getName());
            beanModel.setClassName(ValidatorView.class.getName());
            beanModel.setNamespace(namespace);
            beanModel.setSingleton(true);
            if (beanFactory.registerBean(beanModel)) {
                validatorAction = beanFactory.getBean(ValidatorView.class, namespace);
                if (validatorAction != null) {
                    String fileName = action.getRootNamespace() + ".validator.xml";
                    validatorAction.setConfigFile(fileName);
                }
            }
        }

        if (validatorAction == null) {
            String msg = "validate config no find 验证器没有配置:bean name " + ValidatorView.class.getName() + "     namespace=" + HtmlUtil.escapeEncoderHTML(namespace);
            action.addFieldInfo(Environment.errorInfo, msg);
            return false;
        }

        validatorAction.setRequest(action.getRequest());
        validatorAction.setResponse(action.getResponse());
        Validator dataTypeValidator = validatorAction.getProcessor();
        dataTypeValidator.setCheckObject(obj);

        if (StringUtil.isNull(formId) || ActionSupport.NONE.equals(formId)) {
            String msg = "data type validator " + action.getClass().getName() + " not find form id ";
            action.addFieldInfo(Environment.errorInfo, msg);
            return false;
        } else {
            dataTypeValidator.setId(formId);
            action.addFieldInfo(dataTypeValidator.getInformation());
        }
        //验证 end
        return !action.hasFieldInfo();
    }

    /**
     * @param action    action对象
     * @param param     参数描述
     * @param paramName 参数名称
     * @param obj       验证对象
     *                  是否继续执行  用错误信息判断
     */
    public static void isNullSetDefaultValue(Action action, Param param, String paramName, Object obj) {
        if (param.required() && (obj == null||obj.getClass().equals(NullClass.class))) {
            String message = StringUtil.isEmpty(param.message()) ? (paramName + "不允许为空") : param.message();
            action.addFieldInfo(Environment.warningInfo, message);
            return;
        }

        Field[] fields = ClassUtil.getDeclaredFields(obj.getClass());
        for (Field field : fields) {
            Param classParam = field.getAnnotation(Param.class);
            if (classParam == null) {
                continue;
            }
            field.setAccessible(true);
            Object value = null;
            try {
                value = field.get(obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            Class<?> pType = field.getType();
            if (!StringUtil.isEmpty(classParam.value()) && (value == null || ClassUtil.isBaseNumberType(pType)&&0==((Number)value).intValue())) {
                value = BeanUtil.getTypeValue(classParam.value(),pType);
                BeanUtil.setFieldValue(obj, field.getName(), value);
            }

            if (value!=null)
            {
                if (!ClassUtil.isStandardType(pType) && !ClassUtil.isArrayType(pType) && !ClassUtil.isCollection(pType))
                {
                    isNullSetDefaultValue( action,  classParam,  paramName, value);
                    if (action.hasFieldInfo())
                    {
                        return;
                    }
                } else
                if (ClassUtil.isArrayType(pType))
                {
                    int len = Array.getLength(value);
                    for (int i=0;i<len;i++)
                    {
                        Object v = Array.get(value,i);
                        isNullSetDefaultValue( action,  classParam,  paramName, v);
                        if (action.hasFieldInfo())
                        {
                            return;
                        }
                    }
                } else
                if (ClassUtil.isCollection(pType))
                {
                    Collection collection = (Collection)value;
                    for (Object v:collection)
                    {
                        isNullSetDefaultValue( action,  classParam,  paramName, v);
                        if (action.hasFieldInfo())
                        {
                            return;
                        }
                    }
                }
                else
                {
                    isSafeParam(action,classParam,field.getName(),value);
                    if (action.hasFieldInfo()) {
                        return;
                    }
                }
            } else
            {
                isRequired(action,classParam,field.getName(),value);
            }
        }
    }
    //-----------------------------------------------------------------------------------------------------------
    //签名参数封装

    final static public String[] SIGN_TYPE = new String[]{"Md5", "Sha", "Sm3", "sha256"};
    final static public Map<String, String> SIGN_TYPE_MAP = new HashMap<>();

    static {
        SIGN_TYPE_MAP.put("md5", "Md5");
        SIGN_TYPE_MAP.put("Sha", "Sha");
        SIGN_TYPE_MAP.put("Sm3", "Sm3");
        SIGN_TYPE_MAP.put("sha256", "sha256");
    }


    /**
     * 创建带参签名的参数字符串
     *
     * @param objectTO 要传递的对象
     * @param key      密钥
     * @param signType 签名方式
     * @return SignParam对象
     */
    public static SignParam createSignParam(Object objectTO, String key, String signType) {
        if (objectTO == null || signType == null) {
            return null;
        }
        signType = SIGN_TYPE_MAP.get(signType.toLowerCase());

        // 签名
        SignParam signParam = new SignParam();
        String data = new JSONObject(objectTO).toString();
        String encodeData = ZipUtil.getZipBase64Encode(data);
        signParam.setData(encodeData);
        signParam.setClassName(objectTO.getClass().getName());
        signParam.setSignType(signType);
        if (ArrayUtil.inArray(SIGN_TYPE, signParam.getSignType(), true)) {
            signParam.setSign(EncryptUtil.getHashEncode(signParam.getData() + key, signParam.getSignType()));
        }
        return signParam;
    }

    /**
     * 签名验证
     *
     * @param signParam 参数对象
     * @param key       密钥
     * @return 验证是否通过
     */
    public static boolean verifySignParam(SignParam signParam, String key) {
        if (signParam.getData() == null || StringUtil.isNull(signParam.getSignType())) {
            return false;
        }
        if (ArrayUtil.inArray(SIGN_TYPE, signParam.getSignType(), true)) {
            return signParam.getSign().equalsIgnoreCase(EncryptUtil.getHashEncode(signParam.getData() + key, signParam.getSignType()));
        }
        return false;
    }

    /**
     * 判断是否为Roc请求方式
     * @param json json
     * @return  判断是否为Roc请求方式
     */
    public static boolean isRocRequest(JSONObject json)
    {
        if (ObjectUtil.isEmpty(json))
        {
            return false;
        }
        if (json.containsKey(Environment.Protocol))
        {
            String protocol = json.getString(Environment.Protocol);
            return protocol != null && protocol.contains("roc");
        }
        return false;
    }
}
