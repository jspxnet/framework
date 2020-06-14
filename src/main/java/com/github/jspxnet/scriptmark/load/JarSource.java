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

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.util.jar.JarFile;
import java.util.jar.JarException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:54:46
 */
public class JarSource extends AbstractSource {

    private final File file;

    public JarSource(File file, String name, String encoding) {
        super(name, encoding);
        this.file = file;
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        JarFile jarFile = new JarFile(file);
        return jarFile.getInputStream(jarFile.getEntry(getName()));
    }

    @Override
    public long getLastModified() {
        try {
            JarFile jarFile = new JarFile(file);
            return jarFile.getEntry(getName()).getTime();
        } catch (JarException e) {
            return -1;
        } catch (IOException e) {
            return -1;
        }
    }

}