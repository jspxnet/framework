/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dispatcher;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.txweb.InterceptorUrl;
import com.github.jspxnet.txweb.WebConfigManager;
import com.github.jspxnet.txweb.config.TXWebConfigManager;
import com.github.jspxnet.txweb.dispatcher.handle.*;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.evasive.Configuration;
import com.github.jspxnet.txweb.evasive.EvasiveConfiguration;
import com.github.jspxnet.txweb.evasive.EvasiveManager;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ThrowableUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-3-20
 * Time: 1:13:07
 * com.github.jspxnet.txweb.dispatcher.DispatcherSupport
 * DispatcherInterceptor
 * 转发器，转交action动作
 */
@Slf4j
public class Dispatcher {
    //private static final ThreadLocal<Dispatcher> INSTANCE = new ThreadLocal<>();

    private static Dispatcher instance;

    private static final Map<String, ActionHandle> HANDLE_LIST = new HashMap<>(5);

    static {
        HANDLE_LIST.put(RocHandle.NAME, new RocHandle());
        HANDLE_LIST.put(RsaRocHandle.NAME, new RsaRocHandle());
        HANDLE_LIST.put(ActionHandle.NAME, new ActionHandle());
        HANDLE_LIST.put(MarkdownHandle.NAME, new MarkdownHandle());
        HANDLE_LIST.put(HessianHandle.NAME, new HessianHandle());
    }

    //根路径
    private static String realPath;


    //使用回避拦截功能
    static private boolean useEvasive;

    //转发次数
    private static EvasiveManager evasiveManager = null;
    private static String encode;
    private static String markdownSuffix = "md";
    private static String filterSuffix = "jhtml";
    private static String apiFilterSuffix = "jwc";
    private static String templateSuffix = "ftl";

    private static boolean accessAllowOrigin = true;

    private Dispatcher() {

    }

    public static String getMarkdownSuffix() {
        return markdownSuffix;
    }

    public static String getEncode() {
        return encode;
    }

    public static Dispatcher getInstance() {
        if (instance == null) {
            synchronized (Dispatcher.class) {
                instance = new Dispatcher();
            }
        }
        return instance;
    }
/*
    public static Dispatcher getInstance() {
        Dispatcher du = Dispatcher.INSTANCE.get();
        if (du == null) {
            Dispatcher.INSTANCE.set(new Dispatcher());
        }
        return Dispatcher.INSTANCE.get();
    }*/

    /**
     * 主要是防止关闭异常
     */
    static public void shutdown() {
        //   INSTANCE.set(null);
        //   INSTANCE.remove();
        HANDLE_LIST.clear();
    }

    public static String getRealPath() {
        return realPath;
    }

    public static void setRealPath(String realPath) {
        Dispatcher.realPath = realPath;
    }


    public static String getFilterSuffix() {
        return filterSuffix;
    }

    public static String getTemplateSuffix() {
        return templateSuffix;
    }

    /**
     * @param request  请求
     * @param response 应答
     */
    void wrapRequest(final HttpServletRequest request, final HttpServletResponse response) {
        try {
            request.setCharacterEncoding(Dispatcher.getEncode());
            response.setCharacterEncoding(Dispatcher.getEncode());
        } catch (UnsupportedEncodingException e) {
            TXWebUtil.errorPrint("系统编码错误", null, response, HttpStatusType.HTTP_status_403);
            log.debug("系统编码错误", e);
            return;
        }
        if (HANDLE_LIST.isEmpty()) {
            TXWebUtil.errorPrint("系统在启动中,请稍后", null, response, HttpStatusType.HTTP_status_403);
            return;
        }

        //防刷功能 begin
        if (useEvasive && JspxNetApplication.checkRun() && evasiveManager.execute(request, response)) {
            return;
        }
        //防刷功能 end
        if (accessAllowOrigin) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Authorization, Content-Type, Accept, If-Modified-Since");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.addHeader("Access-Control-Allow-OssSts", "true");
        }

        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        // 正常到执行
        String urlName = URLUtil.getFileName(request.getRequestURI());
        String namespace = TXWebUtil.getNamespace(request.getServletPath());
        WebConfigManager webConfigManager = TXWebConfigManager.getInstance();
        List<String> pageInterceptors = webConfigManager.getPageDefaultInterceptors(namespace);
        if (pageInterceptors != null) {
            for (String name : pageInterceptors) {
                InterceptorUrl dispatcherSupport = (InterceptorUrl) beanFactory.getBean(name, namespace);
                if (dispatcherSupport != null) {
                    if (!dispatcherSupport.dispatcherBefore(request, response)) {
                        return;
                    }
                }
            }
        }

        //X-Requested-With
        //这里放宽了条件，飞加密方式可以不需要 X-Requested-With

        String urlSuffixType = URLUtil.getFileType(request.getRequestURI());
        String suffix;
        //到要执行的方式begin
        if (urlSuffixType.equalsIgnoreCase(markdownSuffix)) {
            suffix = markdownSuffix;
        } else {
            String requestedWith = RequestUtil.getHeader(request, RequestUtil.REQUEST_X_REQUESTED_WITH);
            String contentType = RequestUtil.getHeader(request, RequestUtil.requestContentType);
            requestedWith = requestedWith.toLowerCase();
            contentType = contentType.toLowerCase();
            if (requestedWith.contains(Environment.rocSecret)) {
                suffix = RsaRocHandle.NAME;
            } else if (contentType.contains(HessianHandle.HTTP_HEAND_NAME)) {
                suffix = HessianHandle.NAME;
            } else if (requestedWith.contains(RocHandle.NAME) || contentType.contains(RocHandle.HTTP_HEAND_NAME) || apiFilterSuffix.equalsIgnoreCase(urlSuffixType)) {
                suffix = RocHandle.NAME;
            } else {
                suffix = ActionHandle.NAME;
            }
        }

        //到要执行的方式end
        //执行begin

        try {
            WebHandle actionHandle = HANDLE_LIST.get(suffix);
            actionHandle.doing(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(namespace + "/" + urlName, e);
            printException(e, response, WebOutEnumType.HTML.getValue());
        }

        //执行end
        if (pageInterceptors != null) {
            for (String pageName : pageInterceptors) {
                InterceptorUrl dispatcherSupport = (InterceptorUrl) beanFactory.getBean(pageName, namespace);
                if (dispatcherSupport != null) {
                    dispatcherSupport.dispatcherAfter(request, response);
                }
            }
        }
    }

    private static void printException(Exception e, HttpServletResponse response, int type) {
        if (response.isCommitted()) {
            return;
        }
        JSONObject errorResult = new JSONObject(RocResponse.error(-320021, ThrowableUtil.getThrowableMessage(e)));
        TXWebUtil.print(errorResult.toString(), type, response, HttpStatusType.HTTP_status_405);
    }

    static public void init(ServletContext servletContext) {
        String defaultPath = servletContext.getInitParameter(Environment.defaultPath);
        if (!StringUtil.isNull(defaultPath)) {
            JspxNetApplication.autoRun(defaultPath);
        } else {
            String filePath = servletContext.getRealPath("/");
            if (!StringUtil.isNull(filePath)) {
                filePath = FileUtil.mendPath(filePath + "WEB-INF/classes");
            }
            if (!StringUtil.isNull(filePath) && FileUtil.isDirectory(filePath)) {
                JspxNetApplication.autoRun(FileUtil.mendPath(filePath));
            } else {

                JspxNetApplication.autoRun();
            }
        }
        String realPath = servletContext.getInitParameter(Environment.realPath);
        if (StringUtil.isNull(realPath)) {
            realPath = servletContext.getRealPath("/");
        }
        Dispatcher.realPath = FileUtil.mendPath(realPath);

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        markdownSuffix = envTemplate.getString(Environment.markdownSuffix, "md");
        filterSuffix = envTemplate.getString(Environment.filterSuffix);
        apiFilterSuffix = envTemplate.getString(Environment.ApiFilterSuffix,"jwc");

        templateSuffix = envTemplate.getString(Environment.templateSuffix);
        encode = envTemplate.getString(Environment.encode, Environment.defaultEncode);
        useEvasive = envTemplate.getBoolean(Environment.useEvasive);
        accessAllowOrigin = envTemplate.getBoolean(Environment.ACCESS_ALLOW_ORIGIN);
        if (useEvasive) {
            Configuration evasiveConfiguration = EvasiveConfiguration.getInstance();
            evasiveConfiguration.setFileName(envTemplate.getString(Environment.evasive_config));
            evasiveManager = EvasiveManager.getInstance();
        }


    }


}