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

import java.util.*;
import javax.servlet.http.*;

/**
 * A class transfer simplify cookie retrieval.  It can retrieve cookie values by
 * name and return the value as any primitive type (no casting or parsing
 * required).  It can also throw an exception when a cookie is not found
 * (simplifying error handling), and can accept default values (eliminating
 * error handling).
 * <p>
 * It is used like this:
 * <blockquote><pre>
 * CookieParser parser = new CookieParser(req);
 * &nbsp;
 * float ratio = parser.getFloatCookie("ratio", 1.0);
 * &nbsp;
 * int count = 0;
 * try {
 *   count = parser.getIntCookie("count");
 * }
 * catch (NumberFormatException e) {
 *   handleMalformedCount();
 * }
 * catch (CookieNotFoundException e) {
 *   handleNoCount();
 * }
 * </pre></blockquote>
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 2000
 * @version 1.0, 2000/03/19
 * @see com.github.jspxnet.upload.CookieNotFoundException
 */
public class CookieParser {

    private HttpServletRequest req;
    private Map<String, String> cookieJar = new Hashtable<String, String>();

    /**
     * Constructs a new CookieParser transfer handle the cookies of the
     * given request.
     *
     * @param req the upload request
     */
    public CookieParser(HttpServletRequest req) {
        this.req = req;
        parseCookies();
    }

    // Load the cookie values into the cookies hashtable
    void parseCookies() {
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cooky : cookies) {
                String name = cooky.getName();
                String value = cooky.getValue();
                cookieJar.put(name, value);
            }
        }
    }

    /**
     * Gets the named cookie value as a String
     *
     * @param name the cookie name
     * @return the cookie value as a String
     * @throws CookieNotFoundException if the cookie was not found
     */
    public String getStringCookie(String name)
            throws CookieNotFoundException {
        String value = cookieJar.get(name);
        if (value == null) {
            throw new CookieNotFoundException(name + " not found");
        } else {
            return value;
        }
    }

    /**
     * Gets the named cookie value as a String, with a default.
     * Returns the default value if the cookie is not found
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a String, or the default
     */
    public String getStringCookie(String name, String def) {
        try {
            return getStringCookie(name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gets the named cookie value as a boolean
     *
     * @param name the cookie name
     * @return the cookie value as a boolean
     * @throws CookieNotFoundException if the cookie was not found
     */
    public boolean getBooleanCookie(String name)
            throws CookieNotFoundException {
        return Boolean.valueOf(getStringCookie(name));
    }

    /**
     * Gets the named cookie value as a boolean, with a default.
     * Returns the default value if the cookie is not found.
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a boolean, or the default
     */
    public boolean getBooleanCookie(String name, boolean def) {
        try {
            return getBooleanCookie(name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gets the named cookie value as a byte
     *
     * @param name the cookie name
     * @return the cookie value as a byte
     * @throws CookieNotFoundException if the cookie was not found
     * @throws NumberFormatException   if the cookie value could not
     *                                 be converted transfer a byte
     */
    public byte getByteCookie(String name)
            throws CookieNotFoundException, NumberFormatException {
        return Byte.parseByte(getStringCookie(name));
    }

    /**
     * Gets the named cookie value as a byte, with a default.
     * Returns the default value if the cookie is not found or cannot
     * be converted transfer a byte.
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a byte, or the default
     */
    public byte getByteCookie(String name, byte def) {
        try {
            return getByteCookie(name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gets the named cookie value as a char
     *
     * @param name the cookie name
     * @return the cookie value as a char
     * @throws CookieNotFoundException if the cookie was not found
     */
    public char getCharCookie(String name)
            throws CookieNotFoundException {
        String param = getStringCookie(name);
        if (param.length() == 0) {
            throw new CookieNotFoundException(name + " is empty string");
        } else {
            return (param.charAt(0));
        }
    }

    /**
     * Gets the named cookie value as a char, with a default.
     * Returns the default value if the cookie is not found.
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a char, or the default
     */
    public char getCharCookie(String name, char def) {
        try {
            return getCharCookie(name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gets the named cookie value as a double
     *
     * @param name the cookie name
     * @return the cookie value as a double
     * @throws CookieNotFoundException if the cookie was not found
     * @throws NumberFormatException   if the cookie could not be converted
     *                                 transfer a double
     */
    public double getDoubleCookie(String name)
            throws CookieNotFoundException, NumberFormatException {
        return new Double(getStringCookie(name));
    }

    /**
     * Gets the named cookie value as a double, with a default.
     * Returns the default value if the cookie is not found.
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a double, or the default
     */
    public double getDoubleCookie(String name, double def) {
        try {
            return getDoubleCookie(name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gets the named cookie value as a float
     *
     * @param name the cookie name
     * @return the cookie value as a float
     * @throws CookieNotFoundException if the cookie was not found
     * @throws NumberFormatException   if the cookie could not be converted
     *                                 transfer a float
     */
    public float getFloatCookie(String name)
            throws CookieNotFoundException, NumberFormatException {
        return new Float(getStringCookie(name));
    }

    /**
     * Gets the named cookie value as a float, with a default.
     * Returns the default value if the cookie is not found.
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a float, or the default
     */
    public float getFloatCookie(String name, float def) {
        try {
            return getFloatCookie(name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gets the named cookie value as a int
     *
     * @param name the cookie name
     * @return the cookie value as a int
     * @throws CookieNotFoundException if the cookie was not found
     * @throws NumberFormatException   if the cookie could not be converted
     *                                 transfer a int
     */
    public int getIntCookie(String name)
            throws CookieNotFoundException, NumberFormatException {
        return Integer.parseInt(getStringCookie(name));
    }

    /**
     * Gets the named cookie value as a int, with a default.
     * Returns the default value if the cookie is not found.
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a int, or the default
     */
    public int getIntCookie(String name, int def) {
        try {
            return getIntCookie(name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gets the named cookie value as a long
     *
     * @param name the cookie name
     * @return the cookie value as a long
     * @throws CookieNotFoundException if the cookie was not found
     * @throws NumberFormatException   if the cookie could not be converted
     *                                 transfer a long
     */
    public long getLongCookie(String name)
            throws CookieNotFoundException, NumberFormatException {
        return Long.parseLong(getStringCookie(name));
    }

    /**
     * Gets the named cookie value as a long, with a default.
     * Returns the default value if the cookie is not found.
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a long, or the default
     */
    public long getLongCookie(String name, long def) {
        try {
            return getLongCookie(name);
        } catch (Exception e) {
            return def;
        }
    }

    /**
     * Gets the named cookie value as a short
     *
     * @param name the cookie name
     * @return the cookie value as a short
     * @throws CookieNotFoundException if the cookie was not found
     * @throws NumberFormatException   if the cookie could not be converted
     *                                 transfer a short
     */
    public short getShortCookie(String name)
            throws CookieNotFoundException, NumberFormatException {
        return Short.parseShort(getStringCookie(name));
    }

    /**
     * Gets the named cookie value as a short, with a default.
     * Returns the default value if the cookie is not found.
     *
     * @param name the cookie name
     * @param def  the default cookie value
     * @return the cookie value as a short, or the default
     */
    public short getShortCookie(String name, short def) {
        try {
            return getShortCookie(name);
        } catch (Exception e) {
            return def;
        }
    }
}