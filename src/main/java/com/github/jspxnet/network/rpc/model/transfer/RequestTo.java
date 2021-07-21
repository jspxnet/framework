package com.github.jspxnet.network.rpc.model.transfer;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.rpc.model.cmd.INetCommand;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.ObjectUtil;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/21 19:48
 * description: request 请求方式序列化
 **/
public class RequestTo extends HashMap<String,Object> implements  HttpServletRequest, Serializable {

    public RequestTo(Map<String,Object> map)
    {
        if (map!=null)
        {
            super.putAll(map);
        }
    }

    public RequestTo(HttpServletRequest request)
    {
        super.putAll(RequestUtil.getTransferMap(request));
    }

    @Override
    public String getAuthType() {
        return INetCommand.RPC;
    }

    @Override
    public Cookie[] getCookies() {
        return new Cookie[0];
    }

    @Override
    public long getDateHeader(String s) {
        return 0;
    }

    @Override
    public String getHeader(String s) {
        Object value = super.get(RequestUtil.HEADER + "."+s.toLowerCase());
        if (value==null)
        {
            return null;
        }
        return value.toString();
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        Vector<String> names = new Vector<String>();
        for (String key:super.keySet())
        {
            if (key==null)
            {
                continue;
            }
            if (key.startsWith(RequestUtil.HEADER))
            {
                names.add(key);
            }
        }
        return names.elements();
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        Vector<String> names = new Vector<String>();
        for (String key:super.keySet())
        {
            if (key==null)
            {
                continue;
            }
            if (key.startsWith(RequestUtil.HEADER))
            {
                names.add(key);
            }
        }
        return names.elements();
    }

    @Override
    public int getIntHeader(String s) {
        return ObjectUtil.toInt(getHeader(s));
    }

    @Override
    public String getMethod() {
        return TXWeb.httpPOST;
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return null;
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String s) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return null;
    }

    @Override
    public StringBuffer getRequestURL() {
        return null;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public HttpSession getSession(boolean b) {
        return new SessionTo(this);
    }

    @Override
    public HttpSession getSession() {
        return new SessionTo(this);
    }

    @Override
    public String changeSessionId() {
        return null;
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromUrl() {
        return false;
    }

    @Override
    public boolean authenticate(HttpServletResponse httpServletResponse) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String s, String s1) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String s) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> aClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String s) {
        return get(s);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Vector<String> names = new Vector<String>();
        names.addAll(super.keySet());
        return names.elements();
    }

    @Override
    public String getCharacterEncoding() {
        return Environment.defaultEncode;
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return ObjectUtil.toInt(super.get(RequestUtil.HEADER+".contentlength"));
    }

    @Override
    public long getContentLengthLong() {
        return ObjectUtil.toLong(super.get(RequestUtil.HEADER+".contentlength"));
    }


    @Override
    public String getContentType() {
        return (String)super.get(RequestUtil.HEADER+".contenttype");
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public String getParameter(String s) {
        return (String)super.get(s);
    }

    @Override
    public Enumeration<String> getParameterNames() {
        Vector<String> names = new Vector<String>();
        for (String key:super.keySet())
        {
            if (key==null)
            {
                continue;
            }
            if (!key.startsWith(RequestUtil.HEADER))
            {
                names.add(key);
            }
        }
        return names.elements();

    }

    @Override
    public String[] getParameterValues(String s) {
        return (String[])super.get(s);
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        Map result = new HashMap();
        result.putAll(this);
        return result;

    }

    @Override
    public String getProtocol() {
        return getHeader("protocol");
    }

    @Override
    public String getScheme() {
        return null;
    }

    @Override
    public String getServerName() {
        return null;
    }

    @Override
    public int getServerPort() {
        return 0;
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return getHeader("remoteaddr");
    }

    @Override
    public String getRemoteHost() {
        return getHeader("remotehost");
    }

    @Override
    public void setAttribute(String s, Object o) {
        super.put(s,o);
    }

    @Override
    public void removeAttribute(String s) {
        super.remove(s);
    }

    @Override
    public Locale getLocale() {
        return new Locale("zh","CN");
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return null;
    }

    @Override
    public boolean isSecure() {
        return false;
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return null;
    }

    @Override
    public String getRealPath(String s) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return 0;
    }

    @Override
    public String getLocalName() {
        return "zh_CN";
    }

    @Override
    public String getLocalAddr() {
        return (String)super.get(RequestUtil.HEADER+".remoteaddr");
    }

    @Override
    public int getLocalPort() {
        return 0;
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }


}
