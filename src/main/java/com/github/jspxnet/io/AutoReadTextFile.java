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


import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.utils.CharacterUtil;
import com.github.jspxnet.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.jar.JarInputStream;
import java.util.zip.Adler32;
import java.util.zip.CheckedInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-6
 * Time: 14:11:46
 */
@Slf4j
public class AutoReadTextFile extends AbstractRead {

    private Reader isr = null;
    private ZipInputStream zis = null;

    public AutoReadTextFile() {

    }

    @Override
    protected boolean open() throws IOException {
        if (resource == null) {
            return false;
        }
        resource = FileUtil.mendFile(resource);
        /////////不在jar文件中
        if (FileUtil.isZipFile(resource)) {
            String jarFileName = null;
            if (resource.contains(".jar!")) {
                jarFileName = resource.substring(0, resource.indexOf(".jar!") + 4);
            }
            if (resource.contains(".zip!")) {
                jarFileName = resource.substring(0, resource.indexOf(".zip!") + 4);
            }
            if (resource.contains(".jzb!")) {
                jarFileName = resource.substring(0, resource.indexOf(".jzb!") + 4);
            }
            if (resource.contains(".apk!")) {
                jarFileName = resource.substring(0, resource.indexOf(".apk!") + 4);
            }
            if (resource.contains(".war!")) {
                jarFileName = resource.substring(0, resource.indexOf(".war!") + 4);
            }

            if (jarFileName == null) {
                return false;
            }

            String entryName = resource.substring(jarFileName.length() + 2);
            zis = new JarInputStream(new FileInputStream(jarFileName));
            ZipEntry e;
            while ((e = zis.getNextEntry()) != null) {
                if (e.isDirectory()) {
                    continue;
                }
                if (e.getName().equalsIgnoreCase(entryName)) {
                    break;
                }
            }
            isr = new InputStreamReader(zis, encode);
            return true;
        }
        if (FileUtil.isZipFile(resource) && FileUtil.isRead(resource)) {
            zis = new ZipInputStream(new CheckedInputStream(new FileInputStream(resource), new Adler32()));
            zis.getNextEntry();
            isr = new InputStreamReader(zis, encode);
            return true;
        }

        if (resource.startsWith("http:") || resource.startsWith("https:")) {
            HttpClient httpClient = HttpClientFactory.createHttpClient(resource);
            try {
                isr = new StringReader(httpClient.getString(resource));
            } catch (Exception e) {
                log.error("not reader file:{}", resource);
                return false;
            }
            return true;
        }

        File file = new File(resource);
        if (file.exists() && file.canRead()) {

            if (encode != null && (encode.toLowerCase().startsWith("utf") || encode.toLowerCase().startsWith("unicode"))) {
                UnicodeReader reader = new UnicodeReader(new FileInputStream(file), encode);
                encode = reader.getEncoding();
                isr = reader;
            } else {
                encode = CharacterUtil.getFileCharacterEnding(file, encode);
                isr = new InputStreamReader(new FileInputStream(file), encode);
            }
            return true;
        }


        return false;
    }

    @Override
    protected void readContent() {
        result.setLength(0);
        try {
            int ch;
            while ((ch = isr.read()) > -1) {
                result.append((char) ch);
            }
        } catch (IOException e) {
            log.error("Can not open read file!", e);
        }

    }

    @Override
    protected void close() {
        try {
            if (zis != null) {
                zis.closeEntry();
                zis.close();
            }
            if (isr != null) {
                isr.close();
            }
        } catch (IOException e) {
            log.error("IO error ! file:" + resource, e);
        }
    }
}