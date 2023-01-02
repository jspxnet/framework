/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.load;

import com.github.jspxnet.utils.StringUtil;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-6-3
 * Time: 11:37:12
 */
public class InputStreamSource extends AbstractSource {
    final private InputStream inputStream;

    public InputStreamSource(InputStream inputStream, String name, String encoding) {
        super(name, encoding);
        this.inputStream = inputStream;
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return inputStream;
    }

    @Override
    public boolean isFile()
    {
        return inputStream!=null;
    }
}