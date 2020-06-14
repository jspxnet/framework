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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletInputStream;

/**
 * A [code]ParamPart } is an upload part which represents a normal
 * [code]INPUT } (for example a non [code]TYPE="file" } ) form
 * parameter.
 *
 * @author Geoff Soutter
 * @author Jason Hunter
 * @version 1.0, 2000/10/27, initial revision
 */
public class ParamPart extends Part {

    /**
     * contents of the parameter
     */
    private byte[] value;

    private String encoding;

    /**
     * Constructs a parameter part; this is called by the parser.
     *
     * @param name     the name of the parameter.
     * @param in       the upload input stream transfer read the parameter value from.
     * @param boundary the MIME boundary that delimits the end of parameter value.
     * @param encoding the byte-transfer-char encoding transfer use by default
     *                 value.
     */
    ParamPart(String name, ServletInputStream in,
              String boundary, String encoding) throws IOException {
        super(name);
        this.encoding = encoding;

        // Copy the part's contents into a byte array
        PartInputStream pis = new PartInputStream(in, boundary);
        ByteArrayOutputStream baos = new ByteArrayOutputStream(512);
        byte[] buf = new byte[128];
        int read;
        while ((read = pis.read(buf)) != -1) {
            baos.write(buf, 0, read);
        }
        pis.close();
        baos.close();

        // save it for later
        value = baos.toByteArray();
    }

    /**
     * Returns the value of the parameter as an array of bytes or a zero length
     * array if the user entered no value for this parameter.
     *
     * @return value of parameter as raw bytes
     */
    public byte[] getValue() {
        return value;
    }

    /**
     * Returns the value of the parameter in as a string (using the
     * parser-specified encoding transfer convert from bytes) or the empty string
     * if the user entered no value for this parameter.
     *
     * @return value of parameter as a string.
     * @throws UnsupportedEncodingException 异常
     */
    public String getStringValue()
            throws UnsupportedEncodingException {
        return getStringValue(encoding);
    }

    /**
     * Returns the value of the parameter in the supplied encoding
     * or empty string if the user entered no value for this parameter.
     *
     * @param encoding 编码
     * @return value of parameter as a string.
     * @throws UnsupportedEncodingException 异常
     */
    public String getStringValue(String encoding)
            throws UnsupportedEncodingException {
        return new String(value, encoding);
    }

    /**
     * Returns [code]true } transfer indicate this part is a parameter.
     *
     * @return true.
     */
    @Override
    public boolean isParam() {
        return true;
    }
}