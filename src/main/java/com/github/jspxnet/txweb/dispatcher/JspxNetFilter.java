package com.github.jspxnet.txweb.dispatcher;


import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.sign.HttpStatusType;
import com.github.jspxnet.txweb.evasive.Configuration;
import com.github.jspxnet.txweb.evasive.EvasiveConfiguration;
import com.github.jspxnet.txweb.evasive.EvasiveManager;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.RequestWrapper;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.URLUtil;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

@Slf4j
public class JspxNetFilter implements Filter {

    private static boolean useEvasive = false;
    private static EvasiveManager evasiveManager;

    /**
     * @param servletConfig 配置
     */
    @Override
    public void init(FilterConfig servletConfig) {
        ServletContext servletContext = servletConfig.getServletContext();
        Dispatcher.init(servletContext);
        EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
        useEvasive = envTemplate.getBoolean(Environment.useEvasive);
        if (useEvasive) {
            Configuration evasiveConfiguration = EvasiveConfiguration.getInstance();
            evasiveConfiguration.setFileName(envTemplate.getString(Environment.evasive_config));
            evasiveManager = EvasiveManager.getInstance();
        }
    }

    /**
     * @param servletRequest  请求
     * @param servletResponse 应答
     * @param filterChain     过滤
     * @throws ServletException 异常
     * @throws IOException      异常
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws ServletException, IOException {
        try {
            servletRequest.setCharacterEncoding(Dispatcher.getEncode());
            servletResponse.setCharacterEncoding(Dispatcher.getEncode());
        } catch (UnsupportedEncodingException e) {
            TXWebUtil.errorPrint("系统编码错误", null, (HttpServletResponse)servletResponse, HttpStatusType.HTTP_status_403);
            log.debug("系统编码错误", e);
            return;
        }
        //必须在evasiveManager 前边
        //getParameter()、getInputStream()和getReader() 三个是冲突的,调用一个,其他的会数据错误
        HttpServletRequest request;
        try {
            if (!RequestUtil.isMultipart((HttpServletRequest)servletRequest))
            {
                request = new RequestWrapper((HttpServletRequest)servletRequest);
            } else
            {
                request = (HttpServletRequest)servletRequest;
            }
        } catch (IOException e) {
            e.printStackTrace();
            request = (HttpServletRequest)servletRequest;
        }
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (useEvasive && JspxNetApplication.checkRun() && evasiveManager.execute(request, response)) {
            return;
        }

        String urlName = URLUtil.getFileName(request.getRequestURI());

        //确保系统跳转到正确到后缀名begin
        if (!StringUtil.toBoolean(request.getParameter("t")) && (StringUtil.isNull(urlName) || "".equals(urlName) || "/".equals(urlName))) {
            response.sendRedirect(request.getRequestURI() + "index." + Dispatcher.getFilterSuffix() + "?t=true");
            return;
        }
        //确保系统跳转到正确到后缀名end
        String suffix = URLUtil.getFileType(request.getRequestURI());
        if (suffix != null) {
            suffix = suffix.toLowerCase();
        }

        //防刷功能 begin
        if (useEvasive && JspxNetApplication.checkRun() && evasiveManager.execute(request, response)) {
            return;
        }

        if (Dispatcher.hasSuffix(suffix))
        {
            Dispatcher.getInstance().wrapRequest(request, response);
        } else {
            filterChain.doFilter(servletRequest, servletResponse);
        }
    }
    /**
     * 卸载数据
     */
    @Override
    public void destroy() {
        JspxNetApplication.destroy();
    }
}
