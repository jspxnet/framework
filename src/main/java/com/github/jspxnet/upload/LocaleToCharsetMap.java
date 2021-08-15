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

import java.nio.charset.StandardCharsets;
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

    private static final Map<String, String> MAP = new Hashtable<String, String>();

    static {
        MAP.put("ar", "ISO-8859-6");
        MAP.put("be", "ISO-8859-5");
        MAP.put("bg", "ISO-8859-5");
        MAP.put("ca", "ISO-8859-1");
        MAP.put("cs", "ISO-8859-2");
        MAP.put("da", "ISO-8859-1");
        MAP.put("de", "ISO-8859-1");
        MAP.put("el", "ISO-8859-7");
        MAP.put("en", "ISO-8859-1");
        MAP.put("es", "ISO-8859-1");
        MAP.put("et", "ISO-8859-1");
        MAP.put("fi", "ISO-8859-1");
        MAP.put("fr", "ISO-8859-1");
        MAP.put("hr", "ISO-8859-2");
        MAP.put("hu", "ISO-8859-2");
        MAP.put("is", "ISO-8859-1");
        MAP.put("it", "ISO-8859-1");
        MAP.put("iw", "ISO-8859-8");
        MAP.put("ja", "Shift_JIS");
        MAP.put("ko", "EUC-KR");     // Requires JDK 1.1.6
        MAP.put("lt", "ISO-8859-2");
        MAP.put("lv", "ISO-8859-2");
        MAP.put("mk", "ISO-8859-5");
        MAP.put("nl", "ISO-8859-1");
        MAP.put("no", "ISO-8859-1");
        MAP.put("pl", "ISO-8859-2");
        MAP.put("pt", "ISO-8859-1");
        MAP.put("ro", "ISO-8859-2");
        MAP.put("ru", "ISO-8859-5");
        MAP.put("sh", "ISO-8859-5");
        MAP.put("sk", "ISO-8859-2");
        MAP.put("sl", "ISO-8859-2");
        MAP.put("sq", "ISO-8859-2");
        MAP.put("sr", "ISO-8859-5");
        MAP.put("sv", "ISO-8859-1");
        MAP.put("tr", "ISO-8859-9");
        MAP.put("uk", "ISO-8859-5");
        MAP.put("zh_GB2312", "GB2312");
        MAP.put("zh_GBK", "GBK");
        MAP.put("zh", StandardCharsets.UTF_8.name());
        MAP.put("zh-CN", StandardCharsets.UTF_8.name());
        MAP.put("zh_TW", "Big5");
        MAP.put("en_US", StandardCharsets.UTF_8.name());
        MAP.put("zh-CN_UTF8", StandardCharsets.UTF_8.name());
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
        charset = MAP.get(loc.toString());
        if (charset != null) {
            return charset;
        }
        charset = MAP.get(loc.getLanguage());
        return charset;  // may be null
    }
}