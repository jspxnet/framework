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
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:54:23
 */
public class JarSourceLoader extends AbstractSourceLoader {

    private File jarFile;

    public JarSourceLoader() {

    }

    public JarSourceLoader(String jarFilePath) throws FileNotFoundException {
        this(new File(jarFilePath));
    }

    public JarSourceLoader(File jarFile) throws FileNotFoundException {
        this.jarFile = jarFile;
    }

    public void setJarFilePath(String jarFilePath) {
        this.jarFile = new File(jarFilePath);
    }

    @Override
    public Source loadResource(String path, String name, String encoding)
            throws FileNotFoundException {
        return new JarSource(jarFile, name, encoding);
    }

}