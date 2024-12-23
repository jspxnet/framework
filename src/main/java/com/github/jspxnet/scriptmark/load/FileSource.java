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

import java.io.*;
import java.nio.file.Files;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:35:44
 */
public class FileSource extends AbstractSource {
    private final File file;


    public FileSource(File file, String name, String encoding) {
        super(name, encoding);
        this.file = file;
    }

    @Override
    public long getLastModified() {
        return file.lastModified();
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        return Files.newInputStream(file.toPath());
    }

    @Override
    public boolean isFile()
    {

        return file.isFile();
    }
}