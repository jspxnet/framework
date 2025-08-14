/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.util;

import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.SystemUtil;

import java.io.*;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-10-21
 * Time: 下午8:37
 */
public class JShell {
    private Process child = null;    //child process
    private final StringBuilder out = new StringBuilder();//handle error output of child process

    public JShell(String shellCommand, String encode, long sleep) throws UnsupportedEncodingException {
        try {
            child = Runtime.getRuntime().exec(shellCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (child == null) {
            return;
        }

        final InputStream inputStream = child.getInputStream();
        final BufferedReader brOut = new BufferedReader(new InputStreamReader(inputStream, encode == null ? SystemUtil.encode : encode));
        new Thread() {

            @Override
            public void run() {
                try {
                    String line;
                    while ((line = brOut.readLine()) != null) {
                        out.append(line).append(StringUtil.CRLF);
                    }
                    brOut.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    interrupt();
                }
            }
        }.start();


        final InputStream errorStream = child.getErrorStream();
        final BufferedReader brErr = new BufferedReader(new InputStreamReader(errorStream, encode == null ? SystemUtil.encode : encode));
        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    String line;
                    while ((line = brErr.readLine()) != null) {
                        out.append(line).append("\r\n");
                    }
                    brErr.close();
                } catch (Exception e) {
                    e.printStackTrace();
                    interrupt();
                }
            }
        };
        thread.setDaemon(true);
        thread.start();

        try {
            Thread.sleep(sleep);
            child.waitFor();
            out.append("exit value:").append(child.exitValue()).append("\r\n");
            inputStream.close();
            errorStream.close();
            child.getOutputStream().close();
            if (child != null) {
                child.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        if (child != null) {
            child.destroy();
        }
        out.setLength(0);
    }

    public String getOut() {
        return out.toString();
    }

}