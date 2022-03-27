package com.github.jspxnet.txweb.support;

import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.upload.UploadedFile;
import com.github.jspxnet.utils.StringUtil;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUpgradeHandler;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.*;

public abstract class MultipartRequest implements HttpServletRequest {
    public static final int DEFAULT_MAX_POST_SIZE = 1024 * 1024 * 100;  // 100M  eg
    protected final Hashtable<String, List<String>> parameters = new Hashtable<>();  // name - Vector of values
    protected final List<UploadedFile> fileList = new ArrayList<>();       // name - UploadedFile
    protected HttpServletRequest request;
    public HttpServletRequest getRequest() {
        return request;
    }


    /**
     * Returns the names of all the parameters as an Enumeration of
     * Strings.  It returns an empty Enumeration if there are no parameters.
     *
     * @return the names of all the parameters as an Enumeration of Strings.
     */
    @Override
    public Enumeration<String> getParameterNames() {
        return parameters.keys();
    }

    @Override
    public Map<String, String[]>  getParameterMap() {
        Map<String, String[]> result = new HashMap<>();
        for (String name:parameters.keySet())
        {
            List<String> list = parameters.get(name);
            if (list==null)
            {
                continue;
            }
            result.put(name,list.toArray(new String[list.size()]));
        }
        return result;
    }

    /**
     * Returns the names of all the upload files as an Enumeration of
     * Strings.  It returns an empty Enumeration if there are no upload
     * files.  Each file name is the name specified by the form, not by
     * the user.
     *
     * @return the names of all the upload files as an Enumeration of Strings.
     */
    public List<UploadedFile> getFiles() {
        return fileList;
    }

    /**
     * Returns the value of the named parameter as a String, or null if
     * the parameter was not sent or was sent without a value.  The value
     * is guaranteed transfer be in its normal, decoded form.  If the parameter
     * has multiple values, only the last one is returned (for backward
     * compatibility).  For parameters with multiple values, it's possible
     * the last "value" may be null.
     *
     * @param name the parameter name.
     * @return the parameter value.
     */
    @Override
    public String getParameter(String name) {
        try {
            List<String> values = parameters.get(name);
            if (values == null || values.size() == 0) {
                return null;
            }
            return values.get(values.size() - 1);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the values of the named parameter as a String array, or null if
     * the parameter was not sent.  The array has one entry for each parameter
     * field sent.  If any field was sent without a value that entry is stored
     * in the array as a null.  The values are guaranteed transfer be in their
     * normal, decoded form.  A single value is returned as a one-element array.
     *
     * @param name the parameter name.
     * @return the parameter values.
     */
    @Override
    public String[] getParameterValues(String name) {
        try {
            List<String> values = parameters.get(name);
            if (values == null || values.size() == 0) {
                return null;
            }
            return values.toArray(new String[0]);
        } catch (Exception e) {
            return null;
        }

    }


    public void destroy() {
        parameters.clear();
        fileList.clear();
        try {
            ServletInputStream stream = request.getInputStream();
            if (stream!=null)
            {
                stream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    static public Map<String, String[]> parseQueryString(String s) {
        String[] valArray;
        if (s == null) {
            throw new IllegalArgumentException();
        }
        Map<String, String[]> ht = new Hashtable<String, String[]>();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s, StringUtil.AND);
        while (st.hasMoreTokens()) {
            String pair = st.nextToken();
            int pos = pair.indexOf('=');
            if (pos == -1) {
                // XXX
                // should give more detail about the illegal argument
                throw new IllegalArgumentException();
            }
            String key = parseName(pair.substring(0, pos), sb);
            String val = parseName(pair.substring(pos + 1), sb);
            if (ht.containsKey(key)) {
                String[] oldVals = ht.get(key);
                valArray = new String[oldVals.length + 1];
                System.arraycopy(oldVals, 0, valArray, 0, oldVals.length);
                valArray[oldVals.length] = val;
            } else {
                valArray = new String[1];
                valArray[0] = val;
            }
            ht.put(key, valArray);
        }
        return ht;
    }


    static private String parseName(String s, StringBuffer sb) {
        sb.setLength(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '+':
                    sb.append(' ');
                    break;
                case '%':
                    try {
                        sb.append((char) Integer.parseInt(s.substring(i + 1, i + 3), 16));
                        i += 2;
                    } catch (NumberFormatException e) {
                        // XXX
                        // need transfer be more specific about illegal arg
                        throw new IllegalArgumentException();
                    } catch (StringIndexOutOfBoundsException e) {
                        String rest = s.substring(i);
                        sb.append(rest);
                        if (rest.length() == 2) {
                            i++;
                        }
                    }

                    break;
                default:
                    sb.append(c);
                    break;
            }
        }
        return sb.toString();
    }

    //---------------------------------
    @Override
    public String getRequestURI() {
        return request.getRequestURI();
    }

    @Override
    public StringBuffer getRequestURL() {
        return request.getRequestURL();
    }

    @Override
    public String getContextPath() {
        return request.getContextPath();
    }

    @Override
    public String getServletPath() {
        return request.getServletPath();
    }

    @Override
    public String getRequestedSessionId() {
        return request.getRequestedSessionId();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        return request.isRequestedSessionIdValid();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return request.isRequestedSessionIdFromCookie();
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        return request.isRequestedSessionIdFromURL();
    }

    @Override
    public String getAuthType() {
        return request.getAuthType();
    }

    @Override
    public String getRemoteUser() {
        return request.getRemoteUser();
    }

    @Override
    public boolean isUserInRole(String s) {
        return request.isUserInRole(s);
    }

    @Override
    public Principal getUserPrincipal() {
        return request.getUserPrincipal();
    }

    @Override
    @Deprecated
    public boolean isRequestedSessionIdFromUrl() {
        return request.isRequestedSessionIdFromURL();
    }

    @Override
    public String getPathTranslated() {
        return request.getPathTranslated();
    }

    @Override
    public String getQueryString() {
        return request.getQueryString();
    }

    @Override
    public String getPathInfo() {
        return request.getPathInfo();
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getRealPath(String s) {
        return request.getRealPath(s);
    }

    @Override
    public String getProtocol() {
        return request.getProtocol();
    }

    @Override
    public String getScheme() {
        return request.getScheme();
    }

    @Override
    public String getServerName() {
        return request.getServerName();
    }

    @Override
    public int getServerPort() {
        return request.getServerPort();
    }

    @Override
    public String getRemoteAddr() {
        return RequestUtil.getRemoteAddr(request);
    }

    @Override
    public String getRemoteHost() {
        return request.getRemoteHost();
    }

    @Override
    public int getRemotePort() {
        return request.getRemotePort();
    }

    @Override
    public String getLocalAddr() {
        return request.getLocalAddr();
    }

    @Override
    public String getLocalName() {
        return request.getLocalName();
    }

    @Override
    public int getLocalPort() {
        return request.getLocalPort();
    }

    @Override
    public void setCharacterEncoding(String s) throws UnsupportedEncodingException {
        request.setCharacterEncoding(s);
    }

    @Override
    public javax.servlet.http.HttpSession getSession(boolean b) {
        return request.getSession(b);
    }

    @Override
    public javax.servlet.http.HttpSession getSession() {
        return request.getSession();
    }


    @Override
    public String changeSessionId() {
        return request.changeSessionId();
    }

    @Override
    public java.lang.String getHeader(java.lang.String s) {
        return request.getHeader(s);
    }

    @Override
    public Enumeration<String> getHeaders(String s) {
        return request.getHeaders(s);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return request.getHeaderNames();
    }

    @Override
    public int getIntHeader(String s) {
        return request.getIntHeader(s);
    }

    @Override
    public long getDateHeader(String s) {
        return request.getDateHeader(s);
    }

    @Override
    public Cookie[] getCookies() {
        return request.getCookies();
    }

    @Override
    public javax.servlet.ServletInputStream getInputStream() throws java.io.IOException {
        return request.getInputStream();
    }

    @Override
    public BufferedReader getReader() throws IOException, IllegalStateException {
        return request.getReader();
    }

    @Override
    public String getCharacterEncoding() {
        return request.getCharacterEncoding();
    }

    @Override
    public int getContentLength() {
        return request.getContentLength();
    }


    @Override
    public long getContentLengthLong() {
        return request.getContentLengthLong();
    }

    @Override
    public String getContentType() {
        return request.getContentType();
    }

    @Override
    public Locale getLocale() {
        return request.getLocale();
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return request.getLocales();
    }

    @Override
    public boolean isSecure() {
        return request.isSecure();
    }

    @Override
    public Object getAttribute(String s) {
        return request.getAttribute(s);
    }

    @Override
    public void setAttribute(String s, Object o) {
        request.setAttribute(s, o);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return request.getAttributeNames();
    }

    @Override
    public void removeAttribute(String s) {
        request.removeAttribute(s);
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String s) {
        return request.getRequestDispatcher(s);
    }

    @Override
    public AsyncContext getAsyncContext() {
        return request.getAsyncContext();
    }

    @Override
    public ServletContext getServletContext() {
        return request.getServletContext();
    }

    @Override
    public boolean isAsyncStarted() {
        return request.isAsyncStarted();
    }

    @Override
    public boolean isAsyncSupported() {
        return request.isAsyncSupported();
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return request.startAsync();
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
        return request.startAsync(servletRequest, servletResponse);
    }

    @Override
    public DispatcherType getDispatcherType() {
        return request.getDispatcherType();
    }

    @Override
    public boolean authenticate(javax.servlet.http.HttpServletResponse httpServletResponse) throws java.io.IOException, javax.servlet.ServletException {
        return request.authenticate(httpServletResponse);
    }

    @Override
    public void login(String s, String s1) throws ServletException {
        request.login(s, s1);
    }

    @Override
    public void logout() throws ServletException {
        request.logout();
    }

    @Override
    public java.util.Collection<javax.servlet.http.Part> getParts() throws IOException, ServletException {
        return request.getParts();
    }

    @Override
    public javax.servlet.http.Part getPart(String s) throws IOException, ServletException {
        return request.getPart(s);
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> tClass) throws IOException, ServletException {
        return request.upgrade(tClass);
    }

}