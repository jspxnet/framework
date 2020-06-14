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
 * User: chenYuan
 * date: 2004-4-1
 * Time: 19:54:04
 * <p>
 * A concrete class extends AbstractRead
 * This class can read HTML from a HTTP URL
 * com.github.jspxnet.io.ReadHtml
 */

import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import org.slf4j.Logger;
import com.github.jspxnet.boot.environment.Environment;
import org.slf4j.LoggerFactory;


public class ReadHtml extends AbstractRead {
    private static final Logger log = LoggerFactory.getLogger(ReadHtml.class);
    private HttpClient httpClient = null;

    public ReadHtml() {
        encode = Environment.defaultEncode;
    }

    public ReadHtml(String s) {
        resource = s;
    }

    @Override
    public boolean open() {
        httpClient = HttpClientFactory.createHttpClient(resource);
        return true;
    }

    @Override
    protected void readContent() {

        result.setLength(0);
        try {
            result.append(httpClient.getString(resource));
        } catch (Exception e) {
            e.printStackTrace();
            result.setLength(0);
            log.error("read rul error :" + resource, e);
        }
        encode = httpClient.getEncode();
    }

    @Override
    protected void close() {

    }
}