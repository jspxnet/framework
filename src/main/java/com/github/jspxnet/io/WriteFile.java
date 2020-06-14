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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-1
 * Time: 22:03:32
 * 写文件
 */
public class WriteFile extends AbstractWrite {
    private static final Logger log = LoggerFactory.getLogger(WriteFile.class);
    private static final byte[] bomData = new byte[]{(byte) 0xEF, (byte) 0xBB, (byte) 0xBF};
    private FileOutputStream fos = null;
    private Writer out = null;

    public WriteFile() {
    }

    public WriteFile(String fileName) {
        resource = fileName;
    }

    /**
     * 以安全的方式创建，或者打开一个文件，准备写
     *
     * @param append 追加方式
     * @return 是否打开成功
     */
    @Override
    protected boolean open(boolean append) {
        if (resource == null) {
            return false;
        }
        try {
            File file = new File(resource);
            //判断路径
            if (!FileUtil.makeDirectory(file.getParentFile())) {
                return false;
            }
            //判断文件
            if (!file.isFile() && !file.createNewFile()) {
                return false;
            }
            //文件输出对象
            fos = new FileOutputStream(file, append);
        } catch (Exception e) {
            log.error("Can not open file:" + resource, e);
            return false;
        }
        return true;
    }

    @Override
    protected boolean writeContent(String value) {
        try {
            out = new OutputStreamWriter(fos, encode);
            if (bom && !append) {
                fos.write(bomData);
            }
            out.write(value);
        } catch (IOException e) {
            log.error("Can not write file!", e);
            return false;
        }
        return true;
    }

    @Override
    protected void close() {
        try {
            if (out != null) {
                out.close();
                out = null;
            }
            if (fos != null) {
                fos.close();
                fos = null;
            }
        } catch (IOException e) {
            log.error("Can not close file!", e);
        }
    }
}