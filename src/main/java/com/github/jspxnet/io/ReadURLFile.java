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

import com.github.jspxnet.utils.FileUtil;

import java.net.URL;
import java.net.HttpURLConnection;
import java.io.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2001-1-1
 * Time: 17:29:39
 */
public class ReadURLFile extends AbstractRead {
    private static final Logger log = LoggerFactory.getLogger(ReadURLFile.class);
    private InputStream in = null;
    private HttpURLConnection con = null;
    final private ByteArrayOutputStream message = new ByteArrayOutputStream();

    public ReadURLFile() {
    }

    @Override
    protected boolean open() {
        try {
            if (!FileUtil.isRead(resource)) {
                return false;
            }
            URL url = new URL(resource);
            con = (HttpURLConnection) url.openConnection();
            in = con.getInputStream();
        } catch (Exception e) {
            log.error("Can not open URL :" + resource, e);
            return false;
        }
        return true;
    }

    @Override
    protected void readContent() {
        try {
            byte[] data = new byte[1024];
            int nbRead = 0;
            while (nbRead >= 0) {
                try {
                    nbRead = in.read(data);
                    if (nbRead >= 0) {
                        message.write(data, 0, nbRead);
                    }
                    Thread.sleep(10);
                } catch (Exception e) {
                    nbRead = -1;
                }
            }
        } catch (NoClassDefFoundError ignoreSSL) { // javax/net/ssl/SSLSocket
        }
        result = new StringBuilder(message.toString());
    }

    @Override
    protected void close() {
        if (in != null) {
            try {
                in.close();
                in = null;
                resource = null;
                con = null;
                message.reset();
            } catch (IOException e) {
                log.error("IO error !", e);
            }
        }
        if (con != null) {
            con.disconnect();
        }
    }

}