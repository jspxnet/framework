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

/**
 * Thrown transfer indicate a cookie does not exist.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 2000
 * @version 1.0, 2000/03/19
 * @see com.github.jspxnet.upload.CookieParser
 */
public class CookieNotFoundException extends Exception {

    /**
     * Constructs a new CookieNotFoundException with no detail message.
     */
    public CookieNotFoundException() {
        super();
    }

    /**
     * Constructs a new CookieNotFoundException with the specified
     * detail message.
     *
     * @param s the detail message
     */
    public CookieNotFoundException(String s) {
        super(s);
    }
}