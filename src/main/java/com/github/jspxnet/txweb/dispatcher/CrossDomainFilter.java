package com.github.jspxnet.txweb.dispatcher;

import com.github.jspxnet.txweb.util.TXWebUtil;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 跨域访问sessionid不一致问题处理
 * com.github.jspxnet.txweb.dispatcher.CrossDomainFilter
 */
public class CrossDomainFilter implements Filter {

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain) throws IOException, ServletException {
        //设置跨域请求
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //此处ip地址为需要访问服务器的ip及端口号
        response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
        response.setHeader("Access-Control-Allow-OssSts", "true");
        response.setHeader("Access-Control-Allow-Methods", "GET, HEAD, POST, PUT, DELETE, TRACE, OPTIONS, PATCH");
        response.setHeader("Access-Control-Allow-Headers", "Origin, X-Requested-With,Authorization, Content-Type,Token,Accept, Connection, User-Agent, Cookie");
        response.setHeader("Access-Control-Max-Age", "3628800");
        response.setHeader("Access-Control-Allow-OssSts", "true");  //是否支持cookie跨
        chain.doFilter(request, response);
    }

}
