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

import java.util.*;

/**
 * A mapping transfer determine the (somewhat arbitrarily) preferred charset for
 * a given locale.  Supports all locales recognized in JDK 1.1.  This
 * class is used by the LocaleNegotiator.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 1998
 * @version 1.0, 98/09/18
 * @see com.github.jspxnet.upload.LocaleNegotiator
 */
public class LocaleToCharsetMap {

    private static Map<String, String> map = new Hashtable<String, String>();

    static {
        map.put("ar", "ISO-8859-6");
        map.put("be", "ISO-8859-5");
        map.put("bg", "ISO-8859-5");
        map.put("ca", "ISO-8859-1");
        map.put("cs", "ISO-8859-2");
        map.put("da", "ISO-8859-1");
        map.put("de", "ISO-8859-1");
        map.put("el", "ISO-8859-7");
        map.put("en", "ISO-8859-1");
        map.put("es", "ISO-8859-1");
        map.put("et", "ISO-8859-1");
        map.put("fi", "ISO-8859-1");
        map.put("fr", "ISO-8859-1");
        map.put("hr", "ISO-8859-2");
        map.put("hu", "ISO-8859-2");
        map.put("is", "ISO-8859-1");
        map.put("it", "ISO-8859-1");
        map.put("iw", "ISO-8859-8");
        map.put("ja", "Shift_JIS");
        map.put("ko", "EUC-KR");     // Requires JDK 1.1.6
        map.put("lt", "ISO-8859-2");
        map.put("lv", "ISO-8859-2");
        map.put("mk", "ISO-8859-5");
        map.put("nl", "ISO-8859-1");
        map.put("no", "ISO-8859-1");
        map.put("pl", "ISO-8859-2");
        map.put("pt", "ISO-8859-1");
        map.put("ro", "ISO-8859-2");
        map.put("ru", "ISO-8859-5");
        map.put("sh", "ISO-8859-5");
        map.put("sk", "ISO-8859-2");
        map.put("sl", "ISO-8859-2");
        map.put("sq", "ISO-8859-2");
        map.put("sr", "ISO-8859-5");
        map.put("sv", "ISO-8859-1");
        map.put("tr", "ISO-8859-9");
        map.put("uk", "ISO-8859-5");
        map.put("zh", "GB2312");
        map.put("zh", "UTF-8");
        map.put("zh-CN", "UTF-8");
        map.put("zh_TW", "Big5");
        map.put("en_US", "UTF-8");
        map.put("zh-CN_UTF8", "UTF-8");
    }

    /**
     * Gets the preferred charset for the given locale, or null if the locale
     * is not recognized.
     *
     * @param loc the locale
     * @return the preferred charset
     */
    public static String getCharset(Locale loc) {

        String charset;
        charset = map.get(loc.toString());
        if (charset != null) {
            return charset;
        }
        charset = map.get(loc.getLanguage());
        return charset;  // may be null
    }
}