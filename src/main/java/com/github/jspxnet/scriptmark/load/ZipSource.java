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
import java.util.zip.ZipFile;
import java.util.zip.ZipException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:33:00
 */
public class ZipSource extends AbstractSource {

    private static final long serialVersionUID = 1L;

    private final File file;

    public ZipSource(File file, String name, String encoding) {
        super(name, encoding);
        this.file = file;
    }

    @Override
    protected InputStream getInputStream() throws IOException {
        // 注：ZipFile与File的设计是不一样的，File相当于C#的FileInfo，只持有信息，
        // 而ZipFile构造时即打开流，所以每次读取数据时，重新new新的实例，而不作为属性字段持有。
        ZipFile zipFile = new ZipFile(file);
        return zipFile.getInputStream(zipFile.getEntry(getName()));
    }

    @Override
    public long getLastModified() {
        try {
            ZipFile zipFile = new ZipFile(file);
            return zipFile.getEntry(getName()).getTime();
        } catch (ZipException e) {
            return -1;
        } catch (IOException e) {
            return -1;
        }
    }

}