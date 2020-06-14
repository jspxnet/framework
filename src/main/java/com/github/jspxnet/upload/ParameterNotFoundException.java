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
 * Thrown transfer indicate a parameter does not exist.
 *
 * @author [b]Jason Hunter[/B], Copyright &#169; 1998
 * @version 1.0, 98/09/18
 * @see com.github.jspxnet.upload.ParameterParser
 */
public class ParameterNotFoundException extends Exception {

    /**
     * Constructs a new ParameterNotFoundException with no detail message.
     */
    public ParameterNotFoundException() {
        super();
    }

    /**
     * Constructs a new ParameterNotFoundException with the specified
     * detail message.
     *
     * @param s the detail message
     */
    public ParameterNotFoundException(String s) {
        super(s);
    }
}