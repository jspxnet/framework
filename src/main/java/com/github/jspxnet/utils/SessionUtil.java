package com.github.jspxnet.utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

public final class SessionUtil {
    private SessionUtil()
    {

    }
    public static void add(HttpServletRequest request, String key, Object value) {
        request.getSession().setAttribute(key, value);
    }

    public static void remove(HttpServletRequest request, String key) {
        request.getSession().removeAttribute(key);
    }

    public static String getAtt(HttpServletRequest request, String key) {
        return (String) request.getSession().getAttribute(key);
    }

    public static Object getAttObj(HttpServletRequest request, String key) {
        return request.getSession().getAttribute(key);
    }

    public static String optAtt(HttpServletRequest request, String key, String value) {
        String r = (String) request.getSession().getAttribute(key);
        if (r == null) {
            r = value;
        }
        return r;
    }

    public static void cleanAll(HttpServletRequest request) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        Enumeration<String> enumeration = session.getAttributeNames();
        while (enumeration.hasMoreElements()) {
            session.removeAttribute(enumeration.nextElement());
        }
        session.setMaxInactiveInterval(0);
    }


    public static String getSessionId(HttpSession session) {
        if (session==null)
        {
            return null;
        }
        String str = session.getId();
        if (str!=null&&str.contains(StringUtil.DOT))
        {
            return StringUtil.substringBefore(str,StringUtil.DOT);
        }
        return str;
    }

}
