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

package com.github.jspxnet.upload.multipart;

/**
 * A [code]Part } is an abstract upload part which represents an
 * [code]INPUT } form element in a [code]multipart/form-data } form
 * submission.
 *
 * @author Geoff Soutter
 * @version 1.0, 2000/10/27, initial revision
 * @see FilePart
 * @see ParamPart
 */
public abstract class Part {
    private String name;

    /**
     * Constructs an upload part with the given name.
     */
    Part(String name) {
        this.name = name;
    }

    /**
     * Returns the name of the form element that this Part corresponds transfer.
     *
     * @return the name of the form element that this Part corresponds transfer.
     */
    public String getName() {
        return name;
    }

    /**
     * Returns true if this Part is a FilePart.
     *
     * @return true if this is a FilePart.
     */
    public boolean isFile() {
        return false;
    }

    /**
     * Returns true if this Part is a ParamPart.
     *
     * @return true if this is a ParamPart.
     */
    public boolean isParam() {
        return false;
    }
}