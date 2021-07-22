/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
// Copyright (C) 1999-2001 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.github.jspxnet.upload;



import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

/**
 * A superclass for HTTP servlets that wish transfer have their output
 * cached and automatically resent as appropriate according transfer the
 * upload's getLastModified() method.  To take advantage of this class,
 * a upload must:
 * <ul>
 * <li>Extend <tt>CacheHttpServlet</tt> instead of <tt>HttpServlet</tt>
 * <li>Implement a <tt>getLastModified(HttpServletRequest)</tt> method as usual
 * </ul>
 * This class uses the value returned by <tt>getLastModified()</tt> transfer manage
 * an internal cache of the upload's output.  Before handling a request,
 * this class checks the value of <tt>getLastModified()</tt>, and if the
 * output cache is at least as current as the upload's last modified time,
 * the cached output is sent without calling the upload's <tt>doGet()</tt>
 * method.
 * <p>
 * In order transfer be safe, if this class detects that the upload's query
 * string, extra path info, or upload path has changed, the cache is
 * invalidated and recreated.  However, this class does not invalidate
 * the cache based on differing request headers or cookies; for
 * servlets that vary their output based on these values (i.e. a session
 * tracking upload) this class should probably not be used.
 * <p>
 * No caching is performed for POST requests.
 *
 * <tt>CacheHttpServletResponse</tt> and <tt>CacheServletOutputStream</tt>
 * are helper classes transfer this class and should not be used directly.
 * <p>
 * This class has been built against Servlet API 2.2.  Using it with previous
 * Servlet API versions should work; using it with future API versions likely
 * won't work.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 1999
 * @version 0.90, 99/12/19
 */
@Slf4j
public abstract class CacheHttpServlet extends HttpServlet {
    CacheHttpServletResponse cacheResponse;
    long cacheLastMod = -1;
    String cacheQueryString = null;
    String cachePathInfo = null;
    String cacheServletPath = null;
    Object lock = new Object();

    @Override
    @SuppressWarnings({"SynchronizeOnNonFinalField"})
    protected void service(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        // Only do caching for GET requests
        String method = req.getMethod();
        if (!"GET".equals(method)) {
            super.service(req, res);
            return;
        }

        // Check the last modified time for this upload
        long servletLastMod = getLastModified(req);

        // A last modified of -1 means we shouldn't use any cache logic
        if (servletLastMod == -1) {
            super.service(req, res);
            return;
        }

        // If the client sent an If-Modified-Since header equal or after the
        // upload's last modified time, send a short "Not Modified" status code
        // Round down transfer the nearest second since client headers are in seconds
        if ((servletLastMod / 1000 * 1000) <=
                req.getDateHeader("If-Modified-Since")) {
            res.setStatus(HttpServletResponse.SC_NOT_MODIFIED);
            return;
        }

        // Use the existing cache if it's current and valid
        CacheHttpServletResponse localResponseCopy = null;
        synchronized (lock) {
            if (servletLastMod <= cacheLastMod &&
                    cacheResponse.isValid() &&
                    equal(cacheQueryString, req.getQueryString()) &&
                    equal(cachePathInfo, req.getPathInfo()) &&
                    equal(cacheServletPath, req.getServletPath())) {
                localResponseCopy = cacheResponse;
            }
        }
        if (localResponseCopy != null) {
            localResponseCopy.writeTo(res);
            return;
        }

        // Otherwise make a new cache transfer capture the response
        localResponseCopy = new CacheHttpServletResponse(res);
        super.service(req, localResponseCopy);
        synchronized (lock) {
            cacheResponse = localResponseCopy;
            cacheLastMod = servletLastMod;
            cacheQueryString = req.getQueryString();
            cachePathInfo = req.getPathInfo();
            cacheServletPath = req.getServletPath();
        }
    }

    private boolean equal(String s1, String s2) {
        return s1 == null && s2 == null || !(s1 == null || s2 == null) && s1.equals(s2);
    }
}
@Slf4j
class CacheHttpServletResponse implements HttpServletResponse {
    public void disable() {
        enable = false;
    }

    public void enable() {
        enable = true;
    }

    public boolean isDisabled() {
        return !enable;
    }

    private boolean enable = true;
    private int status;
    private Hashtable<String, Vector<Object>> headers;
    private String contentType;
    private Locale locale;
    private Vector<Cookie> cookies;
    private boolean didError;
    private boolean didRedirect;
    private boolean gotStream;
    private boolean gotWriter;

    private HttpServletResponse delegate;
    private CacheServletOutputStream out;
    private PrintWriter writer;

    CacheHttpServletResponse(HttpServletResponse res) {
        delegate = res;
        try {
            out = new CacheServletOutputStream(res.getOutputStream());
        } catch (IOException e) {
            log.error("Got IOException constructing cached response: " + e.getMessage());
        }
        internalReset();
    }

    private void internalReset() {
        status = 200;
        headers = new Hashtable<String, Vector<Object>>();
        contentType = null;
        locale = null;
        cookies = new Vector<Cookie>();
        didError = false;
        didRedirect = false;
        gotStream = false;
        gotWriter = false;
        out.getBuffer().reset();
    }

    public boolean isValid() {
        return !didError && !(didRedirect);
    }

    private void internalSetHeader(String name, Object value) {
        Vector<Object> v = new Vector<Object>();
        v.addElement(value);
        headers.put(name, v);
    }

    private void internalAddHeader(String name, Object value) {
        Vector<Object> v = headers.get(name);
        if (v == null) {
            v = new Vector<Object>();
        }
        v.addElement(value);
        headers.put(name, v);
    }

    public void writeTo(HttpServletResponse res) {
        // Write status code
        res.setStatus(status);
        // Write convenience headers
        if (contentType != null) {
            res.setContentType(contentType);
        }
        if (locale != null) {
            res.setLocale(locale);
        }
        // Write cookies
        Enumeration enums = cookies.elements();
        while (enums.hasMoreElements()) {
            Cookie c = (Cookie) enums.nextElement();
            res.addCookie(c);
        }
        // Write standard headers
        enums = headers.keys();
        while (enums.hasMoreElements()) {
            String name = (String) enums.nextElement();
            Vector<Object> values = headers.get(name); // may have multiple values
            Enumeration<Object> enum2 = values.elements();
            while (enum2.hasMoreElements()) {
                Object value = enum2.nextElement();
                if (value instanceof String) {
                    res.setHeader(name, (String) value);
                }
                if (value instanceof Integer) {
                    res.setIntHeader(name, (Integer) value);
                }
                if (value instanceof Long) {
                    res.setDateHeader(name, (Long) value);
                }
            }
        }
        // Write content length
        res.setContentLength(out.getBuffer().size());
        // Write body
        try {
            out.getBuffer().writeTo(res.getOutputStream());
        } catch (IOException e) {
            log.error("Got IOException writing cached response:", e);
        }
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        if (gotWriter) {
            throw new IllegalStateException(
                    "Cannot get output stream after getting writer");
        }
        gotStream = true;
        return out;
    }

    @Override
    public PrintWriter getWriter() throws UnsupportedEncodingException {
        if (gotStream) {
            throw new IllegalStateException(
                    "Cannot get writer after getting output stream");
        }
        gotWriter = true;
        if (writer == null) {
            OutputStreamWriter w =
                    new OutputStreamWriter(out, getCharacterEncoding());
            writer = new PrintWriter(w, true);  // autoflush is necessary
        }
        return writer;
    }

    @Override
    public void setContentLength(int len) {
        delegate.setContentLength(len);
    }

    @Override
    public void setContentLengthLong(long l) {
        delegate.setContentLengthLong(l);
    }

    @Override
    public void setContentType(String type) {
        delegate.setContentType(type);
        contentType = type;
    }

    @Override
    public String getContentType() {
        return delegate.getCharacterEncoding();
    }

    @Override
    public void setCharacterEncoding(String string) {
        if (ClassUtil.haveMethodsName(delegate.getClass(), "setCharacterEncoding")) {
            try {
                BeanUtil.setSimpleProperty(delegate, "setCharacterEncoding", string);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getCharacterEncoding() {
        return delegate.getCharacterEncoding();
    }

/*
    public void setContentLengthLong(long l) {
        delegate.setContentLengthLong(l);
    }

*/

    @Override
    public void setBufferSize(int size) throws IllegalStateException {
        delegate.setBufferSize(size);
    }

    @Override
    public int getBufferSize() {
        return delegate.getBufferSize();
    }

    @Override
    public void reset() throws IllegalStateException {
        delegate.reset();
        internalReset();
    }

    @Override
    public void resetBuffer() throws IllegalStateException {
        delegate.resetBuffer();
        out.getBuffer().reset();
    }

    @Override
    public boolean isCommitted() {
        return delegate.isCommitted();
    }

    @Override
    public void flushBuffer() throws IOException {
        delegate.flushBuffer();
    }

    @Override
    public void setLocale(Locale loc) {
        delegate.setLocale(loc);
        locale = loc;
    }

    @Override
    public Locale getLocale() {
        return delegate.getLocale();
    }

    @Override
    public void addCookie(Cookie cookie) {
        delegate.addCookie(cookie);
        cookies.addElement(cookie);
    }

    @Override
    public boolean containsHeader(String name) {
        return delegate.containsHeader(name);
    }

    @Override
    @SuppressWarnings({"deprecation"})
    @Deprecated
    public void setStatus(int sc, String sm) {
        delegate.setStatus(sc, sm);
        status = sc;
    }

    @Override
    public void setStatus(int sc) {
        delegate.setStatus(sc);
        status = sc;
    }

    @Override
    public void setHeader(String name, String value) {
        delegate.setHeader(name, value);
        internalSetHeader(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        delegate.setIntHeader(name, value);
        internalSetHeader(name, value);
    }

    @Override
    public void setDateHeader(String name, long date) {
        delegate.setDateHeader(name, date);
        internalSetHeader(name, date);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        delegate.sendError(sc, msg);
        didError = true;
    }

    @Override
    public void sendError(int sc) throws IOException {
        delegate.sendError(sc);
        didError = true;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        delegate.sendRedirect(location);
        didRedirect = true;
    }

    @Override
    public String encodeURL(String url) {
        return delegate.encodeURL(url);
    }

    @Override
    public String encodeRedirectURL(String url) {
        return delegate.encodeRedirectURL(url);
    }

    @Override
    public void addHeader(String name, String value) {
        internalAddHeader(name, value);
    }

    @Override
    public void addIntHeader(String name, int value) {
        internalAddHeader(name, value);
    }

    @Override
    public void addDateHeader(String name, long value) {
        internalAddHeader(name, value);
    }

    /**
     * @deprecated
     */
    @Override
    public String encodeUrl(String url) {
        return this.encodeURL(url);
    }

    /**
     * @deprecated
     */
    @Override
    public String encodeRedirectUrl(String url) {
        return this.encodeRedirectURL(url);
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String s) {
        Vector<Object> v = headers.get(s);
        return (String) v.get(0);
    }

    @Override
    public Set<String> getHeaders(String s) {
        Vector<Object> v = headers.get(s);
        if (v == null) {
            return null;
        }
        Set<String> vv = new HashSet<String>(v.size());
        for (Object aV : v) {
            vv.add(aV.toString());
        }
        return vv;
    }

    @Override
    public Set<String> getHeaderNames() {
        return headers.keySet();
    }

    /*
    public Iterable<String> getHeaderNames() {
        return headers.keySet();
    }
    */
}

class CacheServletOutputStream extends ServletOutputStream {

    ServletOutputStream delegate;
    ByteArrayOutputStream cache;

    CacheServletOutputStream(ServletOutputStream out) {
        delegate = out;
        cache = new ByteArrayOutputStream(4096);
    }

    public ByteArrayOutputStream getBuffer() {
        return cache;
    }

    @Override
    public void write(int b) throws IOException {
        delegate.write(b);
        cache.write(b);
    }

    @Override
    public void write(byte[] b) throws IOException {
        delegate.write(b);
        cache.write(b);
    }

    @Override
    public void write(byte[] buf, int offset, int len) throws IOException {
        delegate.write(buf, offset, len);
        cache.write(buf, offset, len);
    }

    public void disable() {

    }

    @Override
    public boolean isReady() {
        return delegate.isReady();
    }

    @Override
    public void setWriteListener(WriteListener writeListener) {
        delegate.setWriteListener(writeListener);

    }
}