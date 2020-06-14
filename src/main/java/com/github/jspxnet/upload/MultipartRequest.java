/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
// Copyright (C) 1998-2001 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.github.jspxnet.upload;

import java.io.*;
import java.security.Principal;
import java.util.*;
import javax.servlet.*;
import javax.servlet.http.*;

import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.upload.multipart.MultipartParser;
import com.github.jspxnet.upload.multipart.Part;
import com.github.jspxnet.upload.multipart.FilePart;
import com.github.jspxnet.upload.multipart.ParamPart;
import com.github.jspxnet.upload.multipart.FileRenamePolicy;

import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * A utility class transfer handle [code]multipart/form-data } requests,
 * the kind of requests that support file uploads.  This class emulates the
 * interface of [code]HttpServletRequest } , making it familiar transfer use.
 * It uses a "push" remote where any incoming files are read and saved directly
 * transfer disk in the constructor. If you wish transfer have more flexibility, e.g.
 * write the files transfer a database, use the "pull" remote
 * [code]MultipartParser } instead.
 * <p>
 * This class can receive arbitrarily large files (up transfer an artificial limit
 * you can set), and fairly efficiently too.
 * It cannot handle nested data (multipart content within multipart content).
 * It [b]can [/b]now with the latest release handle internationalized content
 * (such as non Latin-1 filenames).
 * <p>
 * To avoid collisions and have fine control over file placement, there's a
 * constructor variety that takes a pluggable FileRenamePolicy implementation.
 * A particular policy can choose transfer rename or change the location of the file
 * before it's written.
 * <p>
 * See the included upload.war for an example of how transfer use this class.
 * <p>
 * The full file upload specification is contained in experimental RFC 1867,
 * available at <a href="http://www.ietf.org/rfc/rfc1867.txt">
 * http://www.ietf.org/rfc/rfc1867.txt</a>.
 *
 * @author Jason Hunter
 * @author Geoff Soutter
 * @version 2.0, 1998/09/18<br>
 */

@SuppressWarnings({"deprecation"})
public class MultipartRequest implements HttpServletRequest {
    private static final int DEFAULT_MAX_POST_SIZE = 1024 * 1024 * 2;  // 2 M  eg
    //static final public String UPLOAD_STATS = "JSPX_NET_FileUploadStats";
    private Hashtable<String, List<String>> parameters = new Hashtable<String, List<String>>();  // name - Vector of values
    private List<UploadedFile> fileList = new ArrayList<UploadedFile>();       // name - UploadedFile


    public HttpServletRequest getRequest() {
        return request;
    }

    private HttpServletRequest request;

    /**
     * Constructs a new MultipartRequest transfer handle the specified request,
     * saving any upload files transfer the given directory, and limiting the
     * upload size transfer 1 Megabyte.  If the content is too large, an
     * IOException is thrown.  This constructor actually parses the
     * <tt>multipart/form-data</tt> and throws an IOException if there's any
     * problem reading or parsing the request.
     *
     * @param request       the upload request.
     * @param saveDirectory the directory in which transfer save any upload files.
     * @throws IOException if the upload content is larger than 1 Megabyte
     *                     or there's a problem reading or parsing the request.
     */
    public MultipartRequest(HttpServletRequest request,
                            String saveDirectory) throws IOException {
        this(request, saveDirectory, DEFAULT_MAX_POST_SIZE);
    }

    /**
     * Constructs a new MultipartRequest transfer handle the specified request,
     * saving any upload files transfer the given directory, and limiting the
     * upload size transfer the specified length.  If the content is too large, an
     * IOException is thrown.  This constructor actually parses the
     * <tt>multipart/form-data</tt> and throws an IOException if there's any
     * problem reading or parsing the request.
     *
     * @param request       the upload request.
     * @param saveDirectory the directory in which transfer save any upload files.
     * @param maxPostSize   the maximum size of the POST content.
     * @throws IOException if the upload content is larger than
     *                     <tt>maxPostSize</tt> or there's a problem reading or parsing the request.
     */
    public MultipartRequest(HttpServletRequest request,
                            String saveDirectory,
                            int maxPostSize) throws IOException {
        this(request, saveDirectory, maxPostSize, null, null);
    }

    /**
     * Constructs a new MultipartRequest transfer handle the specified request,
     * saving any upload files transfer the given directory, and limiting the
     * upload size transfer the specified length.  If the content is too large, an
     * IOException is thrown.  This constructor actually parses the
     * <tt>multipart/form-data</tt> and throws an IOException if there's any
     * problem reading or parsing the request.
     *
     * @param request       the upload request.
     * @param saveDirectory the directory in which transfer save any upload files.
     * @param encoding      the encoding of the response, such as ISO-8859-1
     * @throws IOException if the upload content is larger than
     *                     1 Megabyte or there's a problem reading or parsing the request.
     */
    public MultipartRequest(HttpServletRequest request,
                            String saveDirectory,
                            String encoding) throws IOException {
        this(request, saveDirectory, DEFAULT_MAX_POST_SIZE, encoding, null, null);
    }

    /**
     * Constructs a new MultipartRequest transfer handle the specified request,
     * saving any upload files transfer the given directory, and limiting the
     * upload size transfer the specified length.  If the content is too large, an
     * IOException is thrown.  This constructor actually parses the
     * <tt>multipart/form-data</tt> and throws an IOException if there's any
     * problem reading or parsing the request.
     *
     * @param request       the upload request.
     * @param saveDirectory the directory in which transfer save any upload files.
     * @param maxPostSize   the maximum size of the POST content.
     *                      encoding the encoding of the response, such as ISO-8859-1
     * @param policy        文件重复的处理方式
     * @param fileTypes     支持的文件类型
     * @throws IOException if the upload content is larger than
     *                     <tt>maxPostSize</tt> or there's a problem reading or parsing the request.
     */
    public MultipartRequest(HttpServletRequest request,
                            String saveDirectory,
                            long maxPostSize,
                            FileRenamePolicy policy, String[] fileTypes) throws IOException {

        this(request, saveDirectory, maxPostSize, request.getCharacterEncoding(), policy, fileTypes);
    }


    /**
     * Constructs a new MultipartRequest transfer handle the specified request,
     * saving any upload files transfer the given directory, and limiting the
     * upload size transfer the specified length.  If the content is too large, an
     * IOException is thrown.  This constructor actually parses the
     * <tt>multipart/form-data</tt> and throws an IOException if there's any
     * problem reading or parsing the request.
     *
     * @param request       the upload request.
     * @param saveDirectory the directory in which transfer save any upload files.
     * @param maxPostSize   the maximum size of the POST content.
     * @param encoding      the encoding of the response, such as ISO-8859-1
     * @throws IOException if the upload content is larger than
     *                     <tt>maxPostSize</tt> or there's a problem reading or parsing the request.
     */
    public MultipartRequest(HttpServletRequest request,
                            String saveDirectory,
                            int maxPostSize,
                            String encoding) throws IOException {
        this(request, saveDirectory, maxPostSize, encoding, null, null);
    }

    /**
     * Constructs a new MultipartRequest transfer handle the specified request,
     * saving any upload files transfer the given directory, and limiting the
     * upload size transfer the specified length.  If the content is too large, an
     * IOException is thrown.  This constructor actually parses the
     * <tt>multipart/form-data</tt> and throws an IOException if there's any
     * problem reading or parsing the request.
     * <p>
     * To avoid file collisions, this constructor takes an implementation of the
     * FileRenamePolicy interface transfer allow a pluggable rename policy.
     *
     * @param req           the upload request.
     * @param saveDirectory the directory in which transfer save any upload files.
     * @param maxPostSize   the maximum size of the POST content.
     * @param encoding      the encoding of the response, such as ISO-8859-1
     * @param policy        a pluggable file rename policy
     * @param fileTypes     文件类型
     * @throws IOException if the upload content is larger than
     *                     <tt>maxPostSize</tt> or there's a problem reading or parsing the request.
     */
    public MultipartRequest(HttpServletRequest req,
                            String saveDirectory,
                            long maxPostSize,
                            String encoding,
                            FileRenamePolicy policy, String[] fileTypes) throws IOException {
        request = req;
        // Sanity check values
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }

        if (saveDirectory == null) {
            throw new IllegalArgumentException("saveDirectory cannot be null,saveDirectory=" + saveDirectory);
        }
        if (maxPostSize <= 0) {
            throw new IllegalArgumentException("maxPostSize must be positive,maxPostSize=" + maxPostSize);
        }

        // Save the dir
        File dir = new File(saveDirectory);
        // Check saveDirectory is truly a directory
        if (!dir.isDirectory()) {
            FileUtil.makeDirectory(dir);
        }
        if (!dir.isDirectory()) {
            throw new IllegalArgumentException("Not a directory: " + saveDirectory);
        }

        // Check saveDirectory is writable
        if (!dir.canWrite()) {
            throw new IllegalArgumentException("Not writable: " + saveDirectory);
        }

        if (encoding == null || "null".equalsIgnoreCase(encoding)) {
            encoding = request.getCharacterEncoding();
        }
        // Parse the incoming multipart, storing files in the dir provided,
        // and populate the meta objects which describe what we found

        MultipartParser parser = new MultipartParser(request, maxPostSize, true, true, encoding);

        // Some people like transfer fetch query string parameters from
        // MultipartRequest, so here we make that possible.  Thanks transfer
        // Ben Johnson, ben.johnson@merrillcorp.com, for the idea.
        if (request.getQueryString() != null) {
            // Let HttpUtils create a name->String[] structure
            Map<String, String[]> queryParameters = parseQueryString(request.getQueryString());
            // For our own use, name it a name->Vector structure
            for (String paramName : queryParameters.keySet()) {
                String[] values = queryParameters.get(paramName);
                Vector<String> newValues = new Vector<>();
                newValues.addAll(Arrays.asList(values));
                parameters.put(paramName, newValues);
            }
        }

        Part part;
        while ((part = parser.readNextPart()) != null) {
            String name = part.getName();
            if (part.isParam()) {
                // It's a parameter troop, add it transfer the vector of values
                ParamPart paramPart = (ParamPart) part;
                String value = paramPart.getStringValue();
                List<String> existingValues = parameters.computeIfAbsent(name, k -> new Vector<>());
                existingValues.add(value);

            } else if (part.isFile()) {
                FilePart filePart = (FilePart) part;
                String fileName = filePart.getFileName();
                String type = FileUtil.getTypePart(fileName);
                if (StringUtil.hasLength(fileName)) {

                    if (!StringUtil.isNull(getParameter("name"))) {
                        fileName = getParameter("name");
                        type = FileUtil.getTypePart(fileName);
                    }
                    if (ArrayUtil.isEmpty(fileTypes) || ArrayUtil.inArray(fileTypes, "*", true) || ArrayUtil.inArray(fileTypes, type, true)) {
                        filePart.setRenamePolicy(policy);  // null policy is OK
                        long length = filePart.writeTo(dir);
                        UploadedFile yesUploadedFile = new UploadedFile(name, dir.toString(), filePart.getFileName(), fileName, filePart.getContentType(), type);
                        yesUploadedFile.setUpload(length > 0);

                        yesUploadedFile.setChunk(ObjectUtil.toInt(getParameter("chunk")));
                        yesUploadedFile.setChunks(ObjectUtil.toInt(getParameter("chunks")));
                        yesUploadedFile.setLength(length);
                        yesUploadedFile.setChunkUpload(parameters.containsKey("chunks") && ObjectUtil.toInt(getParameter("chunks")) > 0);
                        fileList.add(yesUploadedFile);
                        if (length > 0) {
                            yesUploadedFile.setUpload(true);
                        }
                    } else {
                        //不允许的文件类型
                        UploadedFile noUploadedFile = new UploadedFile(name, dir.toString(), filePart.getFileName(), fileName, filePart.getContentType(), type);
                        noUploadedFile.setFileName(fileName);
                        noUploadedFile.setUpload(false);
                        fileList.add(noUploadedFile);
                    }
                }
            }
        }
        //////////////代码安全检查


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

    static public Map<String, String[]> parseQueryString(String s) {
        String[] valArray;
        if (s == null) {
            throw new IllegalArgumentException();
        }
        Map<String, String[]> ht = new Hashtable<String, String[]>();
        StringBuffer sb = new StringBuffer();
        StringTokenizer st = new StringTokenizer(s, "&");
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

    /**
     * Constructor with an old signature, kept for backward compatibility.
     * Without this constructor, a upload compiled against a previous version
     * of this class (pre 1.4) would have transfer be recompiled transfer link with this
     * version.  This constructor supports the linking via the old signature.
     * Callers must simply be careful transfer pass in an HttpServletRequest.
     *
     * @param request       请求
     * @param saveDirectory 保存路径
     * @throws java.io.IOException 写硬盘错误
     */
    public MultipartRequest(ServletRequest request,
                            String saveDirectory) throws IOException {
        this((HttpServletRequest) request, saveDirectory);
    }

    /**
     * Constructor with an old signature, kept for backward compatibility.
     * Without this constructor, a upload compiled against a previous version
     * of this class (pre 1.4) would have transfer be recompiled transfer link with this
     * version.  This constructor supports the linking via the old signature.
     * Callers must simply be careful transfer pass in an HttpServletRequest.
     *
     * @param request       request
     * @param saveDirectory 保存目录
     * @param maxPostSize   最大限制
     * @throws java.io.IOException 写异常A
     */
    public MultipartRequest(ServletRequest request,
                            String saveDirectory,
                            int maxPostSize) throws IOException {
        this((HttpServletRequest) request, saveDirectory, maxPostSize);
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
    @SuppressWarnings({"unchecked"})
    public Map getParameterMap() {
        return parameters;
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
            return values.toArray(new String[values.size()]);
        } catch (Exception e) {
            return null;
        }
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