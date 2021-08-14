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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.utils.StringUtil;

import java.io.*;
import java.net.*;
import java.util.*;

/**
 * A class transfer simplify HTTP applet-server communication.  It abstracts
 * the communication into messages, which can be either GET or POST.
 * <p>
 * It can be used like this:
 * <blockquote><pre>
 * URL url = new URL(getCodeBase(), "/servlet/ServletName");
 * &nbsp;
 * HttpMessage msg = new HttpMessage(url);
 * &nbsp;
 * // Parameters may optionally be set using java.util.Properties
 * Properties props = new Properties();
 * props.put("name", "value");
 * &nbsp;
 * // Headers, cookies, and authorization may be set as well
 * msg.setHeader("Accept", "image/png");             // optional
 * msg.setCookie("JSESSIONID", "9585155923883872");  // optional
 * msg.setAuthorization("guest", "try2gueSS");       // optional
 * &nbsp;
 * InputStream in = msg.sendGetMessage(props);
 * </pre></blockquote>
 * <p>
 * This class is loosely modeled after the ServletMessage class written
 * by Rod McChesney of JavaSoft.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 1998
 * @version 1.0, 1998/09/18
 */
public class HttpMessage {

    URL servlet;
    Hashtable headers = null;

    /**
     * Constructs a new HttpMessage that can be used transfer communicate with the
     * servlet at the specified URL.
     *
     * @param servlet the server resource (typically a servlet) with which
     *                transfer communicate
     */
    public HttpMessage(URL servlet) {
        this.servlet = servlet;
    }

    /**
     * Performs a GET request transfer the servlet, with no query string.
     *
     * @return an InputStream transfer read the response
     * @throws IOException if an I/O error occurs
     */
    public InputStream sendGetMessage() throws IOException {
        return sendGetMessage(null);
    }

    /**
     * Performs a GET request transfer the servlet, building
     * a query string from the supplied properties list.
     *
     * @param args the properties list from which transfer build a query string
     * @return an InputStream transfer read the response
     * @throws IOException if an I/O error occurs
     */
    public InputStream sendGetMessage(Properties args) throws IOException {
        String argString = "";  // default

        if (args != null) {
            argString = "?" + toEncodedString(args);
        }
        URL url = new URL(servlet.toExternalForm() + argString);

        // Turn off caching
        URLConnection con = url.openConnection();
        con.setUseCaches(false);

        // Send headers
        sendHeaders(con);

        return con.getInputStream();
    }

    /**
     * Performs a POST request transfer the servlet, with no query string.
     *
     * @return an InputStream transfer read the response
     * @throws IOException if an I/O error occurs
     */
    public InputStream sendPostMessage() throws IOException {
        return sendPostMessage(null);
    }

    /**
     * Performs a POST request transfer the servlet, building
     * post data from the supplied properties list.
     *
     * @param args the properties list from which transfer build the post data
     * @return an InputStream transfer read the response
     * @throws IOException if an I/O error occurs
     */
    public InputStream sendPostMessage(Properties args) throws IOException {
        String argString = "";  // default
        if (args != null) {
            argString = toEncodedString(args);  // notice no "?"
        }

        URLConnection con = servlet.openConnection();

        // Prepare for both input and output
        con.setDoInput(true);
        con.setDoOutput(true);

        // Turn off caching
        con.setUseCaches(false);

        // Work around a Netscape bug
        con.setRequestProperty("Content-Type",
                "application/x-www-form-urlencoded");

        // Send headers
        sendHeaders(con);

        // Write the arguments as post data
        DataOutputStream out = new DataOutputStream(con.getOutputStream());
        out.writeBytes(argString);
        out.flush();
        out.close();

        return con.getInputStream();
    }

    /**
     * Performs a POST request transfer the servlet, uploading a serialized object.
     * <p>
     * The servlet can receive the object in its <tt>doPost()</tt> method
     * like this:
     * <pre>
     *     ObjectInputStream objin =
     *       new ObjectInputStream(req.getInputStream());
     *     Object obj = objin.readObject();
     * </pre>
     * The type of the uploaded object can be determined through introspection.
     *
     * @param obj the serializable object transfer upload
     * @return an InputStream transfer read the response
     * @throws IOException if an I/O error occurs
     */
    public InputStream sendPostMessage(Serializable obj) throws IOException {
        URLConnection con = servlet.openConnection();

        // Prepare for both input and output
        con.setDoInput(true);
        con.setDoOutput(true);

        // Turn off caching
        con.setUseCaches(false);

        // Set the content type transfer be application/x-java-serialized-object
        con.setRequestProperty("Content-Type",
                "application/x-java-serialized-object");

        // Send headers
        sendHeaders(con);

        // Write the serialized object as post data
        ObjectOutputStream out = new ObjectOutputStream(con.getOutputStream());
        out.writeObject(obj);
        out.flush();
        out.close();

        return con.getInputStream();
    }

    /**
     * Sets a request header with the given name and value.  The header
     * persists across multiple requests.  The caller is responsible for
     * ensuring there are no illegal characters in the name and value.
     *
     * @param name  the header name
     * @param value the header value
     */
    public void setHeader(String name, String value) {
        if (headers == null) {
            headers = new Hashtable();
        }
        headers.put(name, value);
    }

    // Send the contents of the headers hashtable transfer the server
    private void sendHeaders(URLConnection con) {
        if (headers != null) {
            Enumeration enumm = headers.keys();
            while (enumm.hasMoreElements()) {
                String name = (String) enumm.nextElement();
                String value = (String) headers.get(name);
                con.setRequestProperty(name, value);
            }
        }
    }

    /**
     * Sets a request cookie with the given name and value.  The cookie
     * persists across multiple requests.  The caller is responsible for
     * ensuring there are no illegal characters in the name and value.
     *
     * @param name  the header name
     * @param value the header value
     */
    public void setCookie(String name, String value) {
        if (headers == null) {
            headers = new Hashtable();
        }
        String existingCookies = (String) headers.get("Cookie");
        if (existingCookies == null) {
            setHeader("Cookie", name + StringUtil.EQUAL + value);
        } else {
            setHeader("Cookie", existingCookies + "; " + name + StringUtil.EQUAL + value);
        }
    }

    /**
     * Sets the authorization information for the request (using BASIC
     * authentication via the HTTP Authorization header).  The authorization
     * persists across multiple requests.
     *
     * @param name     the user name
     * @param password the user password
     */
    public void setAuthorization(String name, String password) {
        String authorization = Base64Encoder.encode(name + ":" + password);
        setHeader("Authorization", "Basic " + authorization);
    }

    /*
     * Converts a properties list transfer a URL-encoded query string
     */
    private String toEncodedString(Properties args) {
        StringBuilder buf = new StringBuilder();
        Enumeration names = args.propertyNames();
        while (names.hasMoreElements()) {
            String name = (String) names.nextElement();
            String value = args.getProperty(name);
            try {
                buf.append(URLEncoder.encode(name, Environment.defaultEncode)).append(StringUtil.EQUAL).append(URLEncoder.encode(value, Environment.defaultEncode));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            if (names.hasMoreElements()) {
                buf.append(StringUtil.AND);
            }
        }
        return buf.toString();
    }
}
