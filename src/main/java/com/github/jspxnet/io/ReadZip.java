/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.io;

/*
 * Created by IntelliJ IDEA.
 * author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-1
 * Time: 20:53:41
 */

import com.github.jspxnet.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import javax.swing.*;
import java.util.HashMap;
import java.util.Enumeration;
import java.util.Map;
import java.util.zip.ZipFile;
import java.util.zip.ZipEntry;
import java.io.InputStream;
import java.io.IOException;
import java.awt.*;
import java.net.URL;
import java.net.MalformedURLException;

@Slf4j
public class ReadZip extends AbstractRead {
    private Map<String,ZipEntry> entries = new HashMap<>();
    private Map<String,String> names = new HashMap<>();
    private String fileName;
    private ZipFile file;

    /**
     * 根据指定的文件名创建 JarResource。 可以是 zip  or gz or jar
     *
     * @param fileName 文件名
     * @since 0.4
     */
    public ReadZip(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 初始化内部的资源项HashMap。
     */

    @Override

    protected boolean open() {
        try {
            file = new ZipFile(fileName);
            Enumeration<?> enumeration = file.entries();
            while (enumeration.hasMoreElements()) {
                ZipEntry entry = (ZipEntry) enumeration.nextElement();
                if (!entry.isDirectory()) {
                    entries.put(entry.getName(), entry);
                    names.put(FileUtil.mendPath(entry.getName()), entry.getName());
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
            return false;
        }
        return true;
    }

    /**
     * 根据文件名得到在压缩包中的ZipEntry。
     * 可能一个文件名会有多个可能的对应项，具体对应那一项不能确定。对于这样的情况请使用全限定路径。
     *
     * @param fileName 文件名
     * @return 对应的压缩包中的ZipEntry，不存在时返回null
     */
    private ZipEntry getEntry(String fileName) {
        ZipEntry entry = (ZipEntry) entries.get(fileName);
        if (entry == null) {
            String entryName = (String) names.get(fileName);
            if (entryName != null) {
                return (ZipEntry) entries.get(entryName);
            } else {
                return null;
            }
        } else {
            return entry;
        }

    }

    /**
     * 提取指定的文件内容并返回一个字节数组。
     *
     * @param fileName 资源的文件名
     * @return 指定的文件内容的字节数组
     * @since 0.4
     */
    public byte[] getResource(String fileName) {
        ZipEntry entry = getEntry(fileName);
        if (entry != null) {
            try (InputStream inputStream = file.getInputStream(entry)){
                int length = inputStream.available();
                byte[] contents = new byte[length];
                inputStream.read(contents);
                return contents;
            } catch (IOException e) {
                log.error(e.getMessage());
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 提取指定的文件所代表的图像。
     *
     * @param fileName 资源的文件名
     * @return 指定的文件所代表的图像
     * @since 0.4
     */
    public Image getImage(String fileName) {
        ZipEntry entry = getEntry(fileName);
        if (entry != null) {
            StringBuilder url = new StringBuilder("jar:file:/");
            url.append(FileUtil.mendPath(fileName));
            url.append("!/");
            url.append(entry.getName());
            try {
                URL fileURL = new URL(url.toString());
                return new ImageIcon(fileURL).getImage();
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * 提取指定的文件所代表的字符串。
     *
     * @param fileName 资源的文件名
     * @return 指定的文件所代表的字符串
     * @since 0.4
     */
    public String getString(String fileName) {
        byte[] contents = getResource(fileName);
        if (contents != null) {
            return new String(contents);
        } else {
            return null;
        }
    }

    @Override
    protected void readContent() {
        result.append(getString(resource));
    }

    @Override
    protected void close() {
        try {
            file.close();
            file = null;
        } catch (IOException e) {
            log.error(e.getMessage());
        }
        if (entries != null) {
            entries.clear();
            entries = null;
        }
        if (names != null) {
            names.clear();
            names = null;
        }
        fileName = null;
    }
}