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
import com.github.jspxnet.network.rpc.model.transfer.RequestTo;
import com.github.jspxnet.network.rpc.model.transfer.ResponseTo;
import com.github.jspxnet.sober.model.container.PropertyContainer;
import com.github.jspxnet.txweb.dispatcher.Dispatcher;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-5
 * Time: 11:31:26
 * HttpServletRequest,HttpServletRequest cookie 扩展类
 */
@Slf4j
public class RequestUtil {
    final public static String AUTHORIZATION_KEY = "Authorization";
    final public static String HEADER = "header";
    final public static String SESSION = "session";
    final public static String requestUserAgent = "User-Agent";
    final public static String requestAcceptLanguage = "Accept-Language";
    final public static String requestReferer = "Referer";
    final public static String requestAcceptRanges = "Accept-Ranges";
    final public static String requestContentLength = "Content-Length";
    final public static String requestContentRange = "Content-Range";
    final public static String requestContentType = "Content-Type";
    final public static String REQUEST_X_REQUESTED_WITH = "X-Requested-With";
    final public static String requestContentDisposition = "Content-Disposition";
/*
    //微信小程序
    final public static String BROWSER_MINIPROGRAM = "miniprogram";
    //微信浏览器
    final public static String BROWSER_MICROMESSENGER = "micromessenger";*/

    private static final EnvironmentTemplate ENV_TEMPLATE = EnvFactory.getEnvironmentTemplate();
    private static final boolean REPAIR_ENCODE = ENV_TEMPLATE.getBoolean(Environment.repairEncode);
    private static final String REPAIR_REQUEST_METHOD = ENV_TEMPLATE.getString(Environment.repairRequestMethod, StringUtil.ASTERISK);
    private static final String[] mobileKeywords = new String[]{"mobile",
            "android", "symbianos", "iphone", "wp\\d*", "windows phone", "mqqbrowser", "nokia", "midp-2", "untrusted/1.0", "windows ce", "blackberry", "ucweb", "brew", "j2me", "yulong",
            "coolpad", "tianyu", "ty-", "k-touch", "haier", "dopod", "lenovo", "huaqin", "aigo-", "ctc/1.0", "ctc/2.0", "cmcc", "daxian", "mot-", "sonyericsson", "gionee", "htc",
            "zte", "lg", "sony", "samsung", "sharp", "huawei", "webos", "gobrowser", "khtml", "ucbrowser", "iemobile", "operamobi", "opera mobi", "wap2.0", "wapi", "openwave", "nexusone"};

    public static final Map<String, String> systemKeywords = new LinkedHashMap<>();

    public static final Map<String, String> browserKeywords = new LinkedHashMap<>();

    final public static int paramMaxLength = 50000;

    static {
        systemKeywords.put("android", "Android");
        systemKeywords.put("nt4", "Windows NT4");
        systemKeywords.put("unix", "Unix");
        systemKeywords.put("windows 98", "Windows 98");
        systemKeywords.put("nt 5.0", "Windows 2000");
        systemKeywords.put("nt 5.1", "Windows XP");
        systemKeywords.put("nt 5.2", "Windows 2003");
        systemKeywords.put("nt 6.0", "Windows 2008");
        systemKeywords.put("nt 6.1", "Windows 7");
        systemKeywords.put("nt 6.2", "Windows 8");
        systemKeywords.put("vista", "Windows vista");
        systemKeywords.put("nt 10.0", "Windows 10");
        systemKeywords.put("nt 11", "Windows 11");
        systemKeywords.put("nt 12", "Windows 12");
        systemKeywords.put("iphone os", "iPhone OS");
        systemKeywords.put("mac os", "Mac OS");
        systemKeywords.put("tel", "Telport");
        systemKeywords.put("linux", "Linux");
        systemKeywords.put("sunos", "SunOS");
        //--------------------------------------------------------------------------------------------------------------
        browserKeywords.put("micromessenger", "MicroMessenger"); //微信
        browserKeywords.put("miniprogram", "miniprogram"); //微信 小程序

        browserKeywords.put("netcaptor", "NetCaptor");
        browserKeywords.put("mozilla", "Mozilla");
        browserKeywords.put("myie", "MyIe");
        browserKeywords.put("myie2", "MyIE2");
        browserKeywords.put("maxthon", "Maxthon");
        browserKeywords.put("firefox", "Firefox");
        browserKeywords.put("msie 5.5", "Internet Explorer 5.5");
        browserKeywords.put("msie 6.0", "Internet Explorer 6.0");
        browserKeywords.put("msie 7.0", "Internet Explorer 7.0");
        browserKeywords.put("msie 8.0", "Internet Explorer 8.0");
        browserKeywords.put("msie 9.0", "Internet Explorer 9.0");
        browserKeywords.put("msie 10.0", "Internet Explorer 10.0");
        browserKeywords.put("msie 11.0", "Internet Explorer 11.0");
        browserKeywords.put("msie 12.0", "Internet Explorer 12.0");
        browserKeywords.put("msie 13.0", "Internet Explorer 13.0");
        browserKeywords.put("msie 14.0", "Internet Explorer 14.0");
        browserKeywords.put("msie 15.0", "Internet Explorer 15.0");
        browserKeywords.put("msie 16.0", "Internet Explorer 16.0");
        browserKeywords.put("msie", "Internet Explorer");
        browserKeywords.put("chrome", "Chrome");
        browserKeywords.put("safari", "Safari");
        browserKeywords.put("opera", "Opera");
        browserKeywords.put("360", "360 Browser");
        browserKeywords.put("mqqbrowser", "MQQBrowser");
        browserKeywords.put("qqbrowser", "QQBrowser");
        browserKeywords.put("googlebot", "Google Spider"); //网络爬虫
        browserKeywords.put("baiduspider", "Baidu Spider"); //网络爬虫
        browserKeywords.put("yahoo! slurp", "YaHoo Spider"); //雅虎 网络爬虫
        browserKeywords.put("msnbot", "MSN Spider"); //网络爬虫
        browserKeywords.put("sosospider", "SoSo Spider"); //网络爬虫
        browserKeywords.put("yodaobot", "YouDao Spider"); //有道 网络爬虫
        browserKeywords.put("sogou", "Sogou Spider"); //搜狗 网络爬虫
        browserKeywords.put("fast-webcrawler", "Alltheweb Spider"); //网络爬虫
        browserKeywords.put("lycos_spider", "Lycos Spider"); //网络爬虫
        browserKeywords.put("inktomi", "Inktomi Spider"); //网络爬虫
        browserKeywords.put("Java", "Java Server"); //网络爬虫
        browserKeywords.put("download", "Download Application"); //网络爬虫
    }


    private RequestUtil() {

    }

    /**
     * @param request 请求   请求方式为自定义的       X-Requested-With=remote
     * @return 判断是否为ROC请求
     */
    public static boolean isRocRequest(HttpServletRequest request) {
        if (request==null)
        {
            return false;
        }
        String requestedWith = request.getHeader("Content-Type");
        return requestedWith != null && requestedWith.toLowerCase().contains("application/json");
    }

    /**
     * @param request 请求
     * @return 判断是否为盗用
     */
    public static boolean isPirated(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String serverName = URLUtil.getTopDomain(request.getServerName());
        if (serverName == null) {
            return false;
        }
        String referer = request.getHeader(requestReferer);
        if (!StringUtil.hasLength(referer)) {
            return false;
        }
        //排除二级域名
        return !serverName.equalsIgnoreCase(URLUtil.getTopDomain(referer));
    }

    public static boolean isMultipart(HttpServletRequest request) {
        if (request == null || !"POST".equalsIgnoreCase(request.getMethod())) {
            return false;
        }
        String type = null;
        String type1 = request.getHeader(requestContentType);
        String type2 = request.getContentType();
        if (type1 == null && type2 != null) {
            type = type2;
        } else if (type2 == null && type1 != null) {
            type = type1;
        } else if (type1 != null) {
            type = (type1.length() > type2.length() ? type1 : type2);
        }
        if (type == null) {
            type = request.getHeader(requestContentType);
        }
        if (type == null) {
            return false;
        } else {
            return type.toLowerCase().startsWith("multipart");
        }
    }

    /**
     * @param response 应答
     * @return 参数列表
     */
    public static Map<String, Object> getResponseMap(HttpServletResponse response) {
        if (response == null) {
            return new HashMap<>(0);
        }
        if (response instanceof ResponseTo)
        {
            return (ResponseTo)response;
        }
        Map<String, Object> result = new HashMap<>();
        result.put("bufferSize", response.getBufferSize());
        result.put("characterEncoding", response.getCharacterEncoding());
        result.put("locale", response.getLocale());
        result.put("contentType", response.getContentType());
        return result;
    }


    /**
     * @param request 请求
     * @return 参数map, 不错误默认页面执行过程，只在传递参数的时候使用,这样能提高速度
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getRequestMap(HttpServletRequest request)  {
        Map<String,Object> result = new RequestMap(request);
        if (request == null || isRocRequest(request)) {
            return result;
        }
        Enumeration<String> enu = (Enumeration<String>) BeanUtil.getProperty(request, "getParameterNames");
        if (enu==null)
        {
            return null;
        }
        while (enu.hasMoreElements()) {
            String name = enu.nextElement();
            Object values = BeanUtil.getProperty(request, "getParameterValues", new Object[]{name}, false);
            if (values != null && values.getClass().isArray() && ArrayUtil.getLength(values) > 1) {
                if (REPAIR_ENCODE && (StringUtil.ASTERISK.equals(REPAIR_REQUEST_METHOD) || StringUtil.indexIgnoreCaseOf(REPAIR_REQUEST_METHOD, request.getMethod()) != -1)) {
                    int max = ArrayUtil.getLength(values);
                    String[] tempArray = new String[max];
                    for (int i = 0; i < max; i++) {
                        Object o = Array.get(values, i);
                        if (o != null) {
                            try {
                                tempArray[i] = new String(((String) o).getBytes(StandardCharsets.ISO_8859_1), Dispatcher.getEncode());
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                                tempArray[i] = null;
                            }
                        }
                    }
                    result.put(name, tempArray);
                } else {
                    result.put(name, values);
                }
            } else {
                result.put(name, getString(request, name, false));
            }
        }

        return result;
    }


    /**
     * @param request    请求
     * @param name       命令名称
     * @param defaultVal 默认
     * @return 变量
     */
    public static int getInt(HttpServletRequest request, String name, int defaultVal) {
        String value = getString(request, name, true);
        if (StringUtil.isNull(value)) {
            return defaultVal;
        }
        return StringUtil.toInt(value);
    }

    /**
     * @param request 请求
     * @param name    命令名称
     * @return 变量
     */
    public static int getInt(HttpServletRequest request, String name) {
        return StringUtil.toInt(getString(request, name, true));
    }

    /**
     * @param request    请求
     * @param name       命令名称
     * @param defaultVal 默认
     * @return 变量
     */
    public static long getLong(HttpServletRequest request, String name, long defaultVal) {
        String value = getString(request, name, true);
        if (StringUtil.isNull(value)) {
            return defaultVal;
        }
        return StringUtil.toLong(value);
    }

    /**
     * @param request 请求
     * @param name    命令名称
     * @return 变量
     */
    public static long getLong(HttpServletRequest request, String name) {
        return getLong(request, name, 0);
    }

    /**
     * @param request    请求
     * @param name       命令名称
     * @param defaultVal 默认
     * @return 变量
     */
    public static float getFloat(HttpServletRequest request, String name, float defaultVal) {
        String value = getString(request, name, true);
        if (StringUtil.isNull(value)) {
            return defaultVal;
        }
        return StringUtil.toFloat(value);
    }

    /**
     * @param request 请求
     * @param name    变量名
     * @return 返回单精度
     */
    public static float getFloat(HttpServletRequest request, String name) {
        return getFloat(request, name, 0);
    }

    /**
     * @param request    请求
     * @param name       名称
     * @param defaultVal 默认
     * @return 变量
     */
    public static double getDouble(HttpServletRequest request, String name, double defaultVal) {
        String value = getString(request, name, true);
        if (StringUtil.isNull(value)) {
            return defaultVal;
        }
        return StringUtil.toDouble(value);
    }


    /**
     * @param request 请求
     * @param name    名称
     * @return double 类型
     */
    public static double getDouble(HttpServletRequest request, String name) {
        return getDouble(request, name, 0);
    }

    /**
     * @param request 请求
     * @param name    名称
     * @return bool 类型
     */
    public static boolean getBoolean(HttpServletRequest request, String name) {
        String value = getString(request, name, true);
        return StringUtil.toBoolean(value);
    }


    /**
     * 得到字符请求串参数
     *
     * @param request  请求
     * @param name     名称
     * @param def      默认
     * @param checkSql 安全过滤
     * @return 数据
     */
    public static String getString(HttpServletRequest request, String name, String def, boolean checkSql) {
        if (request == null) {
            return def;
        }
        if (request instanceof  Map)
        {
            return (String)((Map)request).get(name);
        }
        String s = null;
        try {
            s = request.getParameter(name);
            if (!StringUtil.hasLength(s))
            {
                return def;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (checkSql) {
            return ParamUtil.getSafeFilter(s, paramMaxLength, SafetyEnumType.MIDDLE);
        }
        return s;
    }

    /**
     * @param request  请求
     * @param name     名称
     * @param checkSql 安全过滤
     * @return 得到参数
     */
    public static String getString(HttpServletRequest request, String name, boolean checkSql) {
        return getString(request, name, StringUtil.empty, checkSql);
    }

    /**
     * @param request  请求
     * @param name     变量名称
     * @param checkSql sql安全方式
     * @return 变量数组
     */
    public static String[] getArray(HttpServletRequest request, String name, boolean checkSql) {
        if (request == null) {
            return null;
        }
        String[] s = null;
        try {
            s = request.getParameterValues(name);
            if (REPAIR_ENCODE && (StringUtil.ASTERISK.equals(REPAIR_REQUEST_METHOD) || StringUtil.indexIgnoreCaseOf(REPAIR_REQUEST_METHOD, request.getMethod()) != -1)) {
                int max = ArrayUtil.getLength(s);
                String[] tempArray = new String[max];
                for (int i = 0; i < max; i++) {
                    String o = (String) Array.get(s, i);
                    if (o != null) {
                        tempArray[i] = new String(o.getBytes(StandardCharsets.ISO_8859_1), Dispatcher.getEncode());
                    }
                }
                s = tempArray;
            }
            if (ArrayUtil.isEmpty(s) && request.getParameter(name) != null) {
                s = new String[]{request.getParameter(name)};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (s == null) {
            return new String[0];
        }
        if (checkSql) {
            for (int i = 0; i < s.length; i++) {
                s[i] = ParamUtil.getSafeFilter(s[i], paramMaxLength, SafetyEnumType.MIDDLE);
            }
        }
        return s;
    }

    /**
     * @param request 请求
     * @param name    名称
     * @return 得到整型数组
     */
    public static Integer[] getIntegerArray(HttpServletRequest request, String name) {
        if (request == null) {
            return null;
        }
        String[] s = getArray(request, name, true);
        if (s == null) {
            return new Integer[0];
        }
        Integer[] result = new Integer[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = StringUtil.toInt(s[i], 0);
        }
        return result;
    }

    public static int[] getIntArray(HttpServletRequest request, String name) {
        if (request == null) {
            return null;
        }
        return ArrayUtil.getIntArray(getArray(request, name, true));
    }

    /**
     * @param request 请求
     * @param name    名称
     * @return 得到整型数组
     */
    public static Long[] getLongArray(HttpServletRequest request, String name) {
        if (request == null) {
            return null;
        }
        String[] s = getArray(request, name, true);
        if (s == null) {
            return new Long[0];
        }
        Long[] result = new Long[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = StringUtil.toLong(s[i]);
        }
        return result;
    }

    /**
     * @param request 请求
     * @param name    名称
     * @return 得到float数组
     */
    public static float[] getFloatArray(HttpServletRequest request, String name) {
        if (request == null) {
            return null;
        }
        String[] s = getArray(request, name, true);
        if (s == null) {
            return new float[0];
        }
        float[] result = new float[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = StringUtil.toFloat(s[i]);
        }
        return result;
    }

    /**
     * 只是为了类型
     *
     * @param request 请求
     * @param name    名称
     * @return 类型转换
     */
    public static Float[] getFloatObjectArray(HttpServletRequest request, String name) {
        if (request == null) {
            return null;
        }
        String[] s = getArray(request, name, true);
        if (s == null) {
            return new Float[0];
        }
        Float[] result = new Float[s.length];
        for (int i = 0; i < s.length; i++) {
            result[i] = StringUtil.toFloat(s[i]);
        }
        return result;
    }

    /**
     * @param request 请求
     * @param name    名称
     * @return 得到double数组
     */
    public static double[] getDoubleArray(HttpServletRequest request, String name) {
        if (request == null) {
            return null;
        }
        String[] s = getArray(request, name, true);
        if (s == null) {
            return new double[0];
        }
        double[] iResult = new double[s.length];
        for (int i = 0; i < s.length; i++) {
            iResult[i] = StringUtil.toDouble(s[i]);
        }
        return iResult;
    }

    /**
     * 只是为了类型
     *
     * @param request 请求
     * @param name    名称
     * @return 类型转换
     */
    public static Double[] getDoubleObjectArray(HttpServletRequest request, String name) {
        if (request == null) {
            return null;
        }
        String[] s = getArray(request, name, true);
        if (s == null) {
            return new Double[0];
        }
        Double[] iResult = new Double[s.length];
        for (int i = 0; i < s.length; i++) {
            iResult[i] = StringUtil.toDouble(s[i]);
        }
        return iResult;
    }


    /**
     * 得到日期
     *
     * @param request 请求
     * @param name    变量名称
     * @param date    默认日期
     * @return 参数
     */
    public static Date getDate(HttpServletRequest request, String name, Date date) {
        String sTmp = getString(request, name, true);
        if (StringUtil.isNull(sTmp)) {
            return date;
        }
        try {
            return StringUtil.getDate(sTmp);
        } catch (Exception e) {
            e.printStackTrace();
            return DateUtil.empty;
        }
    }

    /**
     * 得到日期
     *
     * @param request 请求
     * @param name    变量名称
     * @param format  日期格式
     * @param date    默认日期
     * @return 参数
     */
    public static Date getDate(HttpServletRequest request, String name, String format, Date date) {
        String tmp = getString(request, name, true);
        if (StringUtil.isNull(tmp)) {
            return date;
        }
        try {
            return StringUtil.getDate(tmp, format);
        } catch (Exception e) {
            return date;
        }
    }

    /**
     * 得到请求的参数名称参数对象
     *
     * @param request 请求
     * @return 参数字符串
     */
    public static String[] getParameterNames(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        if (isRocRequest(request)) {
            return null;
        }
        String[] result = null;
        Enumeration<String> enm = request.getParameterNames();
        while (enm.hasMoreElements()) {
            result = ArrayUtil.add(result, enm.nextElement());
        }
        return result;
    }

    /**
     * 判断是否是一个请求参数
     * @param request 请求
     * @param name 参数名称
     * @return 是否
     */
    public static boolean isParameter(HttpServletRequest request,String name)
    {
        if (request == null||name==null) {
            return false;
        }
        Enumeration<String> enm = request.getParameterNames();
        while (enm.hasMoreElements()) {
            if (name.equals(enm.nextElement()))
            {
                return true;
            }
        }
        return false;
    }
    /**
     * @param request 请求
     * @param param   参数
     * @return 继承方式重新组合 参数
     */
    static public String toQueryString(HttpServletRequest request, Map<String, String> param) {
        if (isRocRequest(request)) {
            return StringUtil.empty;
        }
        Enumeration<String> parameterNames = request.getParameterNames();
        while (parameterNames.hasMoreElements()) {
            String name = parameterNames.nextElement();
            if (!param.containsKey(name)) {
                param.put(name, getString(request, name, false));
            }
        }

        StringBuilder result = new StringBuilder();
        for (String key : param.keySet()) {
            result.append(key).append(StringUtil.EQUAL).append(URLUtil.getUrlEncoder(param.get(key), Environment.defaultEncode)).append(StringUtil.AND);
        }

        if (result.toString().endsWith(StringUtil.AND)) {
            result.setLength(result.length() - 1);
        }
        return result.toString();
    }

    /**
     * 得到请求属性名称列表
     *
     * @param request 请求
     * @return 返回
     */
    public static String[] getAttributeNames(HttpServletRequest request) {
        if (request == null) {
            return null;
        }
        String[] result = null;
        Enumeration<String> enm = request.getAttributeNames();
        while (enm.hasMoreElements()) {
            result = ArrayUtil.add(result, enm.nextElement());
        }
        return result;
    }


    /**
     * request 从参数传入Bean对象  MultipartRequest HttpServletRequest 两种情况
     *
     * @param request 请求
     * @param cla     类
     * @param safe    安全
     * @param <T>     泛型
     * @return 实体bean
     */
    public static <T> T getBean(HttpServletRequest request, Class<T> cla, boolean safe) {
        if (cla == null) {
            return null;
        }
        T result = null;
        try {
            result = (T) ClassUtil.newInstance(cla.getName());
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (Exception e) {
            log.error("newInstance " + cla, e);
        }
        if (request == null) {
            return result;
        }
        if (!isRocRequest(request)) {
            String[] names = getParameterNames(request);
            if (names == null) {
                return result;
            }

            List<String> propertyList = new ArrayList<>();
            Collections.addAll(propertyList, names);
            Method[] methods = ClassUtil.getDeclaredSetMethods(cla);
            for (Method method : methods) {
                String propertyName = method.getName();
                propertyList.remove(propertyName);
                if (StringUtil.isNull(propertyName) || propertyName.startsWith("get") || propertyName.startsWith("is")) {
                    continue;
                }
                if (propertyName.startsWith("set") && propertyName.length() > 3) {
                    //得到set方法名称
                    propertyName = StringUtil.uncapitalize(propertyName.substring(3));
                }
                if (!ArrayUtil.inArray(names, propertyName, true)) {
                    //判断方法是否存在
                    continue;
                }
                Type[] type = method.getParameterTypes();
                if (type.length > 0) {
                    if (ClassUtil.isArrayType(type[0])) {
                        BeanUtil.setSimpleProperty(result, method.getName(), getArray(request, propertyName, safe));
                    } else if (type[0].equals(java.util.Date.class)) {
                        BeanUtil.setSimpleProperty(result, method.getName(), getDate(request, propertyName, new Date()));
                    } else {
                        BeanUtil.setSimpleProperty(result, method.getName(),BeanUtil.getTypeValue( getString(request, propertyName, safe),type[0]));
                    }
                }

            }

            //扩展方式支持 begin
            if (result!=null&&!ObjectUtil.isEmpty(propertyList)&& PropertyContainer.class.isAssignableFrom(cla))
            {
                PropertyContainer p = (PropertyContainer)result;
                for (String key:propertyList)
                {
                    //判断名称是正常的,又是上边没有放入的,统一在这里放入
                    if (ValidUtil.isGoodName(key))
                    {
                        p.put(key,getString(request, key, safe));
                    }
                }
            }
            //扩展方式支持 end
        }
        return result;
    }


    /**
     * @param request 请求
     * @return String 得到浏览器名称
     */
    static public String getBrowser(HttpServletRequest request) {
        if (request == null) {
            return "other";
        }
        String s = request.getHeader(requestUserAgent);
        if (StringUtil.isNull(s)) {
            return StringUtil.empty;
        }
        s = s.toLowerCase();
        for (String key : browserKeywords.keySet()) {
            if (s.contains(key)) {
                return browserKeywords.get(key);
            }
        }
        return "other";
    }

    /**
     *
     * @param request 请求
     * @return 判断是否为ie浏览器
     */
    static public boolean isIeBrowser(HttpServletRequest request) {
        String s = getBrowser(request);
        return s.contains("msie")||s.contains("Internet Explorer");
    }

    /**
     * 判断是否为低版本IE
     * @param request 请求
     * @return 判断是否小于IE9 版本
     */
    static public boolean isLowIe(HttpServletRequest request) {
        if (!isIeBrowser(request))
        {
            return false;
        }
        String s = getBrowser(request);
        return s.contains(" 6") || s.contains(" 7") || s.contains(" 8") || s.contains(" 9");
    }
    /**
     * @param request 请求
     * @return String 得到远程用户操作系统
     */
    static public String getSystem(HttpServletRequest request) {
        String s = request.getHeader(requestUserAgent);
        return getSystem(s);
    }

    static public String getSystem(String agent) {
        if (StringUtil.isNull(agent)) {
            return StringUtil.empty;
        }
        agent = agent.toLowerCase();
        for (String key : systemKeywords.keySet()) {
            if (agent.contains(key)) {
                return systemKeywords.get(key);
            }
        }
        if (agent.contains("windows")) {
            return "windows";
        }
        return "other";
    }

    /**
     * @param request 请求
     * @return String   得到用户国家信息
     */
    static public String getLocale(HttpServletRequest request) {
        if (request == null) {
            return "zh_CN";
        }
        //从HTTP请求的头信息中获取客户端的语言设置
        String clientLanguage = request.getHeader(requestAcceptLanguage);
        if (StringUtil.isNull(clientLanguage)) {
            return "zh_CN";
        }
        clientLanguage = clientLanguage.toLowerCase();
        //简体中文浏览器
        if (clientLanguage.contains("zh")) {
            return "zh_CN";
        }        //繁体中文浏览器
        else if ("zh-tw".equalsIgnoreCase(clientLanguage)) {
            return "zh_TW";
        }        //日文浏览器
        else if ("jp".equalsIgnoreCase(clientLanguage)) {
            return "jp";
        }     //缺省认为是英文浏览器
        else if (clientLanguage.contains("en") || clientLanguage.contains("us")) {
            return "en_US";
        } else {
            return request.getLocalName();
        }
    }

    /**
     * 返回系统支持的语言,不是识别语言
     *
     * @param request 请求
     * @return 语言
     */
    static public String getLanguage(HttpServletRequest request) {
        if (request == null) {
            return "zh";
        }
        //从HTTP请求的头信息中获取客户端的语言设置
        String clientLanguage = request.getHeader(requestAcceptLanguage);
        if (StringUtil.isNull(clientLanguage)) {
            return "zh";
        }
        clientLanguage = clientLanguage.toLowerCase();
        //简体中文浏览器
        if (clientLanguage.contains("zh")) {
            return "zh";
        }        //繁体中文浏览器
        else if ("zh".equalsIgnoreCase(clientLanguage)) {
            return "zh";
        }        //日文浏览器
        //缺省认为是英文浏览器
        else if (clientLanguage.contains("en") || clientLanguage.contains("us")) {
            return "en";
        } else {

            return "zh";
        }
    }

    static public boolean isMobileBrowser(HttpServletRequest request) {
        if (request == null) {
            return false;
        }
        String agent = request.getHeader(requestUserAgent);
        return isMobileBrowser(agent);
    }

    static public boolean isMobileBrowser(String agent) {
        if (!StringUtil.hasLength(agent)) {
            return false;
        }
        String agentCheck = agent.trim().toLowerCase();
        if (agentCheck.contains("windows xp") || agentCheck.contains("windows nt") || agentCheck.contains("ubuntu") || agentCheck.contains("center os")) {
            return false;
        }
        for (String keyword : mobileKeywords) {
            Pattern p = Pattern.compile(keyword);
            Matcher m = p.matcher(agentCheck);
            boolean find = m.find();
            if (find) {
                return (!agentCheck.contains("ipad")
                        && !agentCheck.contains("ipod")
                        && !agentCheck.contains("macintosh"));
            }
        }
        return false;
    }


    static public String getNetType(HttpServletRequest request) {
        String agent = request.getHeader(requestUserAgent);
        return getNetType(agent);
    }

    static public String getUserAgent(HttpServletRequest request) {
        return request.getHeader(requestUserAgent);
    }

    static public String getNetType(String agent) {
        if (!StringUtil.hasLength(agent)) {
            return "unknown";
        }
        String agentCheck = agent.trim().toLowerCase();
        if (agentCheck.contains("nettype/")) {
            String temp = StringUtil.substringAfter(agentCheck, "nettype/");
            if (temp.length() > 5) {
                temp = StringUtil.cut(temp, 5, StringUtil.empty);
            }
            return temp;
        }
        return "unknown";
    }

    /**
     * @param request 请求
     * @return 排除代理, 得到真实的IP地址
     */
    static public String getRemoteAddr(HttpServletRequest request) {
        if (request==null)
        {
            return "127.0.0.1";
        }
        String clientIp = request.getHeader("x-forwarded-for");
        if (StringUtil.isNull(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtil.isNull(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtil.isNull(clientIp) || "unknown".equalsIgnoreCase(clientIp)) {
            clientIp = request.getRemoteAddr();
        }
        if (clientIp!=null&&clientIp.contains(",")) {
            clientIp = StringUtil.substringBefore(clientIp, ",");
        }
        if (clientIp==null)
        {
            return "127.0.0.1";
        }
        return clientIp;
    }

    /**
     * 得到ajax请求的字符串
     *
     * @param request 请求
     * @return 得到ajax请求的字符串
     * @throws IOException 异常
     */
    static public String getReader(HttpServletRequest request) throws Exception {
        ///////////////////读取ajax请求 begin
        if (isMultipart(request))
        {
            return null;
        }

       try (BufferedReader buffer = request.getReader()){
            StringBuilder call = new StringBuilder();
            String str;
            while ((str = buffer.readLine()) != null) {
                call.append(str);
            }
            return call.toString();
        }

    }

    /**
     * @param request 请求
     * @return 返回域名名称部分代http的 例如:http://www.jspx.net
     */
    static public String getHostUrl(HttpServletRequest request) {
        if (request == null) {
            return StringUtil.empty;
        }
        StringBuffer sb =  request.getRequestURL();
        if (sb==null)
        {
            return StringUtil.empty;
        }
        return URLUtil.getHostUrl(sb.toString());
    }

    /**
     * 返回域名
     * @param request 请求
     * @return  返回域名名称部分代http的 例如:www.jspx.net:8080
     */
    static public String getDomain(HttpServletRequest request) {
        if (request == null) {
            return StringUtil.empty;
        }
        return StringUtil.substringAfter(getHostUrl(request),"//");
    }


    static public String getHeader(HttpServletRequest request, String key) {
        if (request == null) {
            return StringUtil.empty;
        }
        if (request instanceof  Map)
        {
            return (String)((Map)request).get((HEADER + StringUtil.DOT + key).toLowerCase());
        }
        String value = request.getHeader(key);
        if (value == null) {
            return StringUtil.empty;
        }
        return value;
    }

    /**
     * 得到认证token
     * @param request 请求
     * @return token
     */
    static public String getToken(HttpServletRequest request) {
        if (request == null) {
            return StringUtil.empty;
        }
        String token = getHeader(request, AUTHORIZATION_KEY);
        if (token!=null&&token.contains(" ")) {
            token = StringUtil.substringAfter(token, " ");
            if (StringUtil.getLength(token) < 20) {
                token = null;
            }
        }
        return token;
    }

    /**
     * 得到Map方式的参数表，但是排除 签名，使用在支付等加密传输
     *
     * @param request 请求
     * @return 得到Map方式的参数表
     */
    public static StringMap<String, String> getSignSortMap(HttpServletRequest request) {
        StringMap<String,String> valueMap = new StringMap<>();
        valueMap.setKeySplit(StringUtil.EQUAL);
        valueMap.setLineSplit(StringUtil.CRLF);
        Enumeration<String> env = request.getParameterNames();
        while (env.hasMoreElements()) {
            String name = env.nextElement();
            if (name==null)
            {
                continue;
            }
            if (Environment.sign.equals(name)) {
                continue;
            }
            if (Environment.signType.equals(name)) {
                continue;
            }
            valueMap.put(name, ParamUtil.getSafeFilter(RequestUtil.getString(request, name, false), paramMaxLength, SafetyEnumType.LOW));
        }
        valueMap.sortByKey(true);
        return valueMap;
    }


    /**
     *  Map中参数排序
     * @param request 请求
     * @return 参数
     */
    public static Map<String, String> getSortMap(HttpServletRequest request) {

        Map<String, String> treeMap = new TreeMap<>();
        Enumeration<String> requestParams = request.getParameterNames();
        while (requestParams.hasMoreElements()) {
            String key = requestParams.nextElement();
            if (key==null)
            {
                continue;
            }
            String value = request.getParameter(key);
            treeMap.put(key, value);
        }
        return treeMap;
    }

    /**
     * 转换为可传输的map对象
     * @param request 请求
     * @return map
     */
    public static Map<String, Object> getTransferMap(HttpServletRequest request) {
        if (request instanceof RequestTo)
        {
            return (RequestTo) request;
        }
        Map<String, Object> resultMap = new HashMap<>();
        if (request==null)
        {
            return resultMap;
        }
        Enumeration<String> requestParams = request.getParameterNames();
        while (requestParams.hasMoreElements()) {
            String key = requestParams.nextElement();
            if (key==null)
            {
                continue;
            }
            String value = request.getParameter(key);
            resultMap.put(key, value);
        }
        Enumeration<String> enumeration = request.getHeaderNames();
        while (enumeration.hasMoreElements())
        {
            String key = enumeration.nextElement();
            if (key==null)
            {
                continue;
            }
            resultMap.put(HEADER+StringUtil.DOT+key.toLowerCase(),request.getHeader(key));
        }
        //header 中key 全部小写
        String token = getToken(request);
        resultMap.put(HEADER+".authorization", token);
        resultMap.put(HEADER+".contentlength",request.getContentLength());
        resultMap.put(HEADER+".contenttype",request.getContentType());
        resultMap.put(HEADER+".protocol",request.getProtocol());
        resultMap.put(HEADER+".remoteaddr",request.getRemoteAddr());
        resultMap.put(HEADER+".remotehost",request.getRemoteHost());
        resultMap.put(HEADER+".authtype",request.getAuthType());
        resultMap.put(HEADER+".querystring",request.getQueryString());
        resultMap.put(HEADER+".remoteuser",request.getRemoteUser());
        resultMap.put(HEADER+".requestedsessionid",request.getRequestedSessionId());
        resultMap.put(HEADER+".requesturi",StringUtil.empty);
        resultMap.put(HEADER+".requesturl",StringUtil.empty);
        /*
        if (request instanceof RequestFacade)
        {

        } else
        {
            resultMap.put(HEADER+".requesturi",StringUtil.empty);
            resultMap.put(HEADER+".requesturl",StringUtil.empty);

            try {
                resultMap.put(HEADER+".requesturi",request.getRequestURI());
                resultMap.put(HEADER+".requesturl",request.getRequestURL()==null?StringUtil.empty:request.getRequestURL().toString());
            } catch (Exception e) {
                resultMap.put(HEADER+".requesturi",StringUtil.empty);
                resultMap.put(HEADER+".requesturl",StringUtil.empty);
            }
        }*/
        resultMap.put(HEADER+".pathinfo",request.getPathInfo());
        resultMap.put(HEADER+".pathtranslated",request.getPathTranslated());
        resultMap.put(HEADER+".servletpath",Dispatcher.getRealPath());
        resultMap.put(HEADER+".contextpath",Dispatcher.getRealPath());
        resultMap.put(HEADER+".servername",request.getServerName());
        resultMap.put(HEADER+".serverport",request.getServerPort());
        resultMap.put(HEADER+".localport",request.getLocalPort());
        resultMap.put(HEADER+".remoteport",request.getRemotePort());
        resultMap.put(HEADER+".characterencoding",request.getCharacterEncoding());
        try {
            resultMap.put(HEADER+".scheme",request.getScheme());
        } catch (Exception e)
        {
            resultMap.put(HEADER+".scheme","http");
        }
        HttpSession httpSession = request.getSession();
        if (httpSession==null)
        {
            httpSession = request.getSession(true);
        }
        if (httpSession!=null)
        {
            resultMap.put(SESSION+".id",httpSession.getId());
        }
        return resultMap;
    }

}