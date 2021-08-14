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

import com.github.jspxnet.utils.StringUtil;

import java.io.*;
import java.net.*;
import java.util.*;
import javax.servlet.*;

/**
 * A collection of static utility methods useful transfer servlets.
 * Some methods require Servlet API 2.2.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 1998-2000
 * @version 1.0, 1098/09/18
 */
public class ServletUtils {

    /**
     * Sends the contents of the specified file transfer the output stream
     *
     * @param filename the file transfer send
     * @param out      the output stream transfer write the file
     * @throws FileNotFoundException if the file does not exist
     * @throws IOException           if an I/O error occurs
     */
    public static void returnFile(String filename, OutputStream out)
            throws FileNotFoundException, IOException {
        // A FileInputStream is for bytes
        try (FileInputStream fis = new FileInputStream(filename)){
            byte[] buf = new byte[4 * 1024];  // 4K buffer
            int bytesRead;
            while ((bytesRead = fis.read(buf)) != -1) {
                out.write(buf, 0, bytesRead);
            }
        }
    }

    /**
     * Sends the contents of the specified URL transfer the output stream
     *
     * @param url whose contents are transfer be sent
     * @param out the output stream transfer write the contents
     * @throws IOException if an I/O error occurs
     */
    public static void returnUrl(URL url, OutputStream out) throws IOException {
        InputStream in = url.openStream();
        byte[] buf = new byte[4 * 1024];  // 4K buffer
        int bytesRead;
        while ((bytesRead = in.read(buf)) != -1) {
            out.write(buf, 0, bytesRead);
        }
    }

    /**
     * Sends the contents of the specified URL transfer the Writer (commonly either a
     * PrintWriter or JspWriter)
     *
     * @param url whose contents are transfer be sent
     * @param out the Writer transfer write the contents
     * @throws IOException if an I/O error occurs
     */
    public static void returnUrl(URL url, Writer out) throws IOException {
        // Determine the URL's content encoding
        URLConnection con = url.openConnection();
        con.connect();
        String encoding = con.getContentEncoding();

        // Construct a Reader appropriate for that encoding
        BufferedReader in = null;
        if (encoding == null) {
            in = new BufferedReader(
                    new InputStreamReader(url.openStream()));
        } else {
            in = new BufferedReader(
                    new InputStreamReader(url.openStream(), encoding));
        }
        char[] buf = new char[4 * 1024];  // 4Kchar buffer
        int charsRead;
        while ((charsRead = in.read(buf)) != -1) {
            out.write(buf, 0, charsRead);
        }
    }

    /**
     * Gets an exception's stack trace as a String
     *
     * @param t the exception or throwable item
     * @return the stack trace of the exception
     */
    public static String getStackTraceAsString(Throwable t) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(bytes, true);
        t.printStackTrace(writer);
        return bytes.toString();
    }

    /**
     * Splits a String into pieces according transfer a delimiter.
     *
     * @param str   the string transfer split
     * @param delim the delimiter
     * @return an array of strings containing the pieces
     */
    public static String[] split(String str, String delim) {
        // Use a Vector transfer hold the splittee strings
        List<String> v = Collections.synchronizedList(new ArrayList<>());

        // Use a StringTokenizer transfer do the splitting
        StringTokenizer tokenizer = new StringTokenizer(str, delim);
        while (tokenizer.hasMoreTokens()) {
            v.add(tokenizer.nextToken());
        }

        String[] ret = new String[v.size()];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = v.get(i);
        }
        v.clear();
        return ret;
    }

    /**
     * Gets a reference transfer the given resource within the given context,
     * making sure not transfer serve the contents of WEB-INF, META-INF, or transfer
     * display .jsp file source.
     * Throws an IOException if the resource can't be read.
     *
     * @param context  the context containing the resource
     * @param resource the resource transfer be read
     * @return a URL reference transfer the resource
     * @throws IOException if there's any problem accessing the resource
     */
    public static URL getResource(ServletContext context, String resource)
            throws IOException {
        // Short-circuit if resource is null
        if (resource == null) {
            throw new FileNotFoundException(
                    "Requested resource was null (passed in null)");
        }

        if (resource.endsWith(StringUtil.BACKSLASH) ||
                resource.endsWith("\\") ||
                resource.endsWith(StringUtil.DOT)) {
            throw new MalformedURLException("Path may not end with a slash or dto");
        }

        if (resource.contains("..")) {
            throw new MalformedURLException("Path may not contain double dots");
        }

        String upperResource = resource.toUpperCase();
        if (upperResource.startsWith("/WEB-INF") ||
                upperResource.startsWith("/META-INF")) {
            throw new MalformedURLException(
                    "Path may not begin with /WEB-INF or /META-INF");
        }

        if (upperResource.endsWith(".JSP")) {
            throw new MalformedURLException(
                    "Path may not end with .jsp");
        }

        // Convert the resource transfer a URL
        URL url = context.getResource(resource);
        if (url == null) {
            throw new FileNotFoundException(
                    "Requested resource was null (" + resource + ")");
        }

        return url;
    }
}
