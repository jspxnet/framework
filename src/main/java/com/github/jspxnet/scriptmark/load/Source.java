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

import com.github.jspxnet.scriptmark.util.ReadFileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:23:09
 */
public abstract class Source {

    /**
     * 获取模板读取器, 此方法可重复调用.
     *
     * @return 模板读取器
     * @throws java.io.IOException 模板不存在或读取失败时抛出
     */
    public abstract Reader getReader() throws IOException;

    /**
     * 获取模板的编码方式
     *
     * @return 编码方式
     */
    public abstract String getEncoding();

    /**
     * 未知内容长度
     */
    public static final long UNKOWN_LENGTH = -1;

    /**
     * 获取模板的内容长度
     *
     * @return 内容长度
     */
    public long getLength() {
        return UNKOWN_LENGTH;
    }

    /**
     * 未知修改时间
     */
    public static final long UNKOWN_MODIFIED = -1;

    /**
     * 获取模板的最后修改时间
     *
     * @return 模板的最后修改时间, 未知时返回  {@code UNKOWN_MODIFIED } , 应总是不小于-1
     */
    public long getLastModified() {
        return UNKOWN_MODIFIED;
    }

    /**
     * 获取原始内容
     *
     * @return 原始内容
     * @throws IOException 读取内容出错时抛出
     */
    public String getSource() throws IOException {
        return ReadFileUtil.readToString(getReader());
    }

    /**
     * 获取指定位置的模板源代码
     *
     * @param beginIndex 模板起始位置
     * @param endIndex   模板结束位置
     * @return 模板源代码
     * @throws java.io.IOException 读取错误
     */
    public String getSource(int beginIndex, int endIndex) throws IOException {
        if (beginIndex < 0) {
            beginIndex = 0;
        }
        if (endIndex <= beginIndex) {
            return StringUtil.empty;
        }
        int offset = endIndex - beginIndex;
        Reader reader = null;
        StringBuilder buf = new StringBuilder();
        try {
            reader = getReader();
            reader.skip(beginIndex);
            char[] cBuf = new char[ReadFileUtil.BUFFER_SIZE];
            int len;
            while ((len = reader.read(cBuf)) != -1) {
                if (len >= offset - buf.length()) {
                    buf.append(cBuf, 0, offset - buf.length());
                    break;
                } else {
                    buf.append(cBuf, 0, len);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return buf.toString();
    }

    /**
     * 获取位置所在行的源代码
     *
     * @param beginLine 模板起始位置
     * @param endLine   模板结束位置
     * @return 模板源代码
     * @throws java.io.IOException 读取错误
     */
    public String getLineSource(int beginLine, int endLine) throws IOException {
        if (beginLine < 0) {
            beginLine = 0;
        }
        if (endLine <= beginLine) {
            return StringUtil.empty;
        }
        try (Reader rd = getReader();BufferedReader reader = new BufferedReader(rd)){
            StringBuilder buf = new StringBuilder();
            String str;
            int line = 0;
            do {
                str = reader.readLine();
                line++;
                if (line > endLine) {
                    break;
                }
                if (line >= beginLine) {
                    buf.append(str);
                }
            } while (str != null);
            return buf.toString();
        }
    }

    /**
     * 将模板源内容读取为字符串
     *
     * @return 模板源内容
     */
    @Override
    public String toString() {
        try {
            return getSource();
        } catch (IOException e) {
            return StringUtil.empty;
        }
    }

}