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
import com.github.jspxnet.io.AbstractWrite;
import com.github.jspxnet.io.WriteFile;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AutoReadTextFile;
import java.util.LinkedList;
import java.io.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-4-26
 * Time: 16:38:32
 * 读取字符串，按照行数 作为list来使用
 */
public class StringList extends LinkedList<String> implements Serializable {
    public StringList() {

    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) throws Exception {
        this.fileName = fileName;
        loadFile();
    }

    private String fileName;

    public StringList(String text) {
        setString(text);
    }


    public void setString(String text) {
        clear();
        if (text == null) {
            return;
        }
        String[] textArray;
        if (text.contains(StringUtil.SEMICOLON)) {
            textArray = StringUtil.split(text, StringUtil.SEMICOLON);
        } else {
            textArray = StringUtil.split(StringUtil.convertCR(text), StringUtil.CR);
        }
        for (String str : textArray) {
            if (StringUtil.isNull(str)) {
                continue;
            }
            super.add(str);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String s : this) {
            if (StringUtil.isNull(s)) {
                continue;
            }
            sb.append(s).append(StringUtil.CRLF);
        }
        return sb.toString();
    }

    public void save(String file) {
        AbstractWrite aw = new WriteFile();
        aw.setFile(file);
        aw.setContent(toString(), false);
    }

    private void loadFile() throws Exception {
        clear();
        AbstractRead ar = new AutoReadTextFile();
        ar.setFile(fileName);
        LineNumberReader reader = new LineNumberReader(new StringReader(ar.getContent()));
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                if (StringUtil.isNull(line)) {
                    continue;
                }
                if (line.startsWith("#")) {
                    continue;
                }
                add(line);
            }
        } finally {
            reader.close();
        }
    }


    @Override
    public String[] toArray() {
        String[] result = new String[this.size()];
        for (int i = 0; i < size(); i++) {
            result[i] = get(i);
        }
        return result;
    }


}