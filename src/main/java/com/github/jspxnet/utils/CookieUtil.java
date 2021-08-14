/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-12
 * Time: 10:07:36
 */
public final  class CookieUtil {
    private CookieUtil() {

    }

    static public boolean testCookie(HttpServletRequest request, HttpServletResponse response) {
        try {
            setCookie(response, "jspx/test", "jspx.net");
            String test = getCookieString(request, "jspx/test", "");
            if (StringUtil.isNull(test)) {
                return false;
            }
            if ("jspx/test".equals(test)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    static public void cookieClear(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null) {
            return;
        }
        for (Cookie cook : cookieList) {
            if (cook == null) {
                continue;
            }
            cook.setMaxAge(0);
            response.addCookie(cook);
        }
    }

    static public String getCookieString(HttpServletRequest request, String cookieName, String defaultValue) {
        Cookie[] cookieList = request.getCookies();
        if (cookieList == null || cookieName == null) {
            return StringUtil.empty;
        }
        String result = null;
        for (Cookie aCookieList : cookieList) {
            if (aCookieList.getName().equals(cookieName)) {
                result = aCookieList.getValue();
            }
        }
        if (result == null) {
            return defaultValue;
        } else {
            return result;
        }
    }

    static public int getCookieInt(HttpServletRequest request, String cookieName, int defaultValue) {
        return StringUtil.toInt(getCookieString(request, cookieName, "0"), defaultValue);
    }

    static public Date getCookieDate(HttpServletRequest request, String cookieName) {

        String stmp = getCookieString(request, cookieName, "");
        if (StringUtil.isNull(stmp)) {
            return new Date();
        }
        try {
            return StringUtil.getDate(stmp);
        } catch (Exception e) {
            return new Date();
        }
    }

    static public boolean getCookieBoolean(HttpServletRequest request, String cookieName) {
        return StringUtil.toBoolean(getCookieString(request, cookieName, ""));
    }

    static public void setCookieBoolean(HttpServletResponse response, String cookieName, boolean cookieValue) {
        Cookie cookie = new Cookie(cookieName, BooleanUtil.toString(cookieValue));
        response.addCookie(cookie);
    }

    static public void setCookieDate(HttpServletResponse response, String cookieName, Date cookieValue) {
        Cookie cookie = new Cookie(cookieName, DateUtil.toString(cookieValue, DateUtil.FULL_ST_FORMAT));
        response.addCookie(cookie);
    }

    static public void setCookie(HttpServletResponse response, String cookieName, String cookieValue) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        response.addCookie(cookie);
    }

    static public void setCookie(HttpServletResponse response, String cookieName, int cookieValue) {
        Cookie cookie = new Cookie(cookieName, NumberUtil.toString(cookieValue));
        response.addCookie(cookie);
    }

    static public void setCookie(HttpServletResponse response, String cookieName, String cookieValue, int cookieMaxage) {
        Cookie cookie = new Cookie(cookieName, cookieValue);
        cookie.setMaxAge(cookieMaxage);
        response.addCookie(cookie);
    }

    static public void setCookie(HttpServletResponse response, String cookieName, int[] cookieValue) {
        for (int i = 0; i < cookieValue.length; i++) {
            Cookie cookie = new Cookie(cookieName + i, NumberUtil.toString(cookieValue[i]));
            response.addCookie(cookie);
        }
    }

    static public void setCookie(HttpServletResponse response, String cookieName, String[] cookieValue) {
        for (int i = 0; i < cookieValue.length; i++) {
            Cookie cookie = new Cookie(cookieName + i, cookieValue[i]);
            response.addCookie(cookie);
        }
    }

    static public String cookieToString(HttpServletRequest request) {

        Cookie[] cookieList = request.getCookies();
        StringBuilder sb = new StringBuilder();
        if (cookieList == null) {
            return StringUtil.empty;
        }
        for (Cookie aCookieList : cookieList) {
            if (aCookieList == null) {
                continue;
            }
            sb.append(aCookieList.getName()).append(StringUtil.EQUAL).append(aCookieList.getValue()).append("<br/>");
        }
        return sb.toString();
    }
}