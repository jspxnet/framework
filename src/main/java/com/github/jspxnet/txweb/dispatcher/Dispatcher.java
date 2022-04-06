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
import com.github.jspxnet.txweb.dispatcher.handle.*;
import com.github.jspxnet.txweb.enums.WebOutEnumType;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import com.thetransactioncompany.cors.CORSResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ResponseFacade;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
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
public final class Dispatcher {
    private static final Map<String, Class<?>> ACTION_HANDLE_MAP = new HashMap<String, Class<?>>(7)
    {
        {
            put(RocHandle.NAME, RocHandle.class);
            put(RsaRocHandle.NAME, RsaRocHandle.class);
            put(ActionHandle.NAME, ActionHandle.class);
            put(MarkdownHandle.NAME, MarkdownHandle.class);
            put(HessianHandle.NAME, HessianHandle.class);
            put(CommandHandle.NAME, CommandHandle.class);
        }
    };

    //根路径
    private static String realPath;



    private static String encode;
    final private static String COMMAND_SUFFIX = "cmd";
    private static String MARKDOWN_SUFFIX = "md";
    private static String FILTER_SUFFIX = "jhtml";
    private static String API_FILTER_SUFFIX = "jwc";
    private static String TEMPLATE_SUFFIX = "ftl";


    private static boolean accessAllowOrigin = true;

    private static final Dispatcher DISPATCHER = new Dispatcher();
    private Dispatcher() {

    }

    public static String getMarkdownSuffix() {
        return MARKDOWN_SUFFIX;
    }

    public static String getEncode() {
        return StringUtil.isNull(encode)?Environment.defaultEncode:encode;
    }

    public static Dispatcher getInstance() {
        return DISPATCHER;
    }


    /**
     * 主要是防止关闭异常
     */
    static public void shutdown() {

        //ACTION_HANDLE_MAP_INSTANCE.remove();
    }

    public static String getRealPath() {
        return realPath;
    }

    public static void setRealPath(String realPath) {
        Dispatcher.realPath = realPath;
    }


    public static String getFilterSuffix() {
        return FILTER_SUFFIX;
    }

    public static String getTemplateSuffix() {
        return TEMPLATE_SUFFIX;
    }


    public static boolean hasSuffix(String suffix)
    {
        return ArrayUtil.inArray(new String[]{COMMAND_SUFFIX,MARKDOWN_SUFFIX,FILTER_SUFFIX,API_FILTER_SUFFIX,TEMPLATE_SUFFIX},suffix,true);
    }
    /**
     * @param request  请求
     * @param response 应答
     */
    void wrapRequest(final HttpServletRequest request, final HttpServletResponse response) {


        //防刷功能 end
        if (accessAllowOrigin) {
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With, Authorization, Content-Type, Accept, If-Modified-Since");
            response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
            response.setHeader("Access-Control-Max-Age", "3600");
            response.addHeader("Access-Control-Allow-OssSts", "true");
        }


        // 正常到执行
        String urlName = URLUtil.getFileName(request.getRequestURI());
        String namespace = URLUtil.getNamespace(request.getServletPath());


        //X-Requested-With
        //这里放宽了条件，飞加密方式可以不需要 X-Requested-With

        String urlSuffixType = URLUtil.getFileType(request.getRequestURI());
        String suffix;
        //到要执行的方式begin
        if (MARKDOWN_SUFFIX.equalsIgnoreCase(urlSuffixType)) {
            suffix = MARKDOWN_SUFFIX;
        } else
        if (COMMAND_SUFFIX.equalsIgnoreCase(urlSuffixType)) {
            suffix = COMMAND_SUFFIX;
        }
        else {
            String requestedWith = RequestUtil.getHeader(request, RequestUtil.REQUEST_X_REQUESTED_WITH);
            String contentType = RequestUtil.getHeader(request, RequestUtil.requestContentType);
            requestedWith = requestedWith.toLowerCase();
            contentType = contentType.toLowerCase();
            if (requestedWith.contains(Environment.rocSecret)) {
                suffix = RsaRocHandle.NAME;
            } else if (contentType.contains(HessianHandle.HTTP_HEARD_NAME)) {
                suffix = HessianHandle.NAME;
            } else if (requestedWith.contains(RocHandle.NAME) || contentType.contains(RocHandle.HTTP_HEAND_NAME) || API_FILTER_SUFFIX.equalsIgnoreCase(urlSuffixType)) {
                suffix = RocHandle.NAME;
            } else {
                suffix = ActionHandle.NAME;
            }
        }
        try {
            WebHandle actionHandle = (WebHandle)ACTION_HANDLE_MAP.get(suffix).newInstance();
            actionHandle.doing(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            log.info(namespace + "/" + urlName, e);
            printException(e, response, WebOutEnumType.HTML.getValue());
        }
    }


    private static void printException(Exception e, HttpServletResponse response, int type) {
        if (!(response instanceof ResponseFacade) && !(response instanceof CORSResponseWrapper) && response.isCommitted()) {
            return;
        }
        TXWebUtil.print(new JSONObject(RocResponse.error(-320021, ThrowableUtil.getThrowableMessage(e))).toString(), type, response, HttpStatusType.HTTP_status_405);
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
            if (!StringUtil.isNull(filePath) && FileUtil.isDirectory(filePath) && FileUtil.isFileExist(new File(filePath,Environment.jspx_properties_file))) {
                JspxNetApplication.autoRun(FileUtil.mendPath(filePath));
            } else {
                JspxNetApplication.autoRun();
            }
        }
        String realPath = servletContext.getInitParameter(Environment.realPath);
        if (StringUtil.isNull(realPath)) {
            realPath = (String)servletContext.getAttribute(Environment.realPath);
        }
        if (StringUtil.isNull(realPath)) {
            realPath = servletContext.getRealPath("/");
        }
        if (!StringUtil.isNull(realPath)) {
            Dispatcher.realPath = FileUtil.mendPath(realPath);
        }

        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        MARKDOWN_SUFFIX = envTemplate.getString(Environment.markdownSuffix, "md");
        FILTER_SUFFIX = envTemplate.getString(Environment.filterSuffix);
        API_FILTER_SUFFIX = envTemplate.getString(Environment.ApiFilterSuffix,"jwc");
        TEMPLATE_SUFFIX = envTemplate.getString(Environment.templateSuffix);
        encode = envTemplate.getString(Environment.encode, Environment.defaultEncode);
        accessAllowOrigin = envTemplate.getBoolean(Environment.ACCESS_ALLOW_ORIGIN);
    }


}