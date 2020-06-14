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
import java.io.IOException;
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 14:31:58
 */
public class ZipSourceLoader extends AbstractSourceLoader {

    private File zipFile;

    public ZipSourceLoader() {

    }

    public ZipSourceLoader(String zipFilePath) throws IOException {
        this(new File(zipFilePath));
    }

    public ZipSourceLoader(File zipFile) throws IOException {
        this.zipFile = zipFile;
    }

    public void setZipFilePath(String zipFilePath) {
        this.zipFile = new File(zipFilePath);
    }

    @Override
    public Source loadResource(String path, String name, String encoding)
            throws FileNotFoundException {
        return new ZipSource(zipFile, name, encoding);
    }

}