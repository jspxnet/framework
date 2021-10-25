/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex.tag;

import com.github.jspxnet.boot.environment.Environment;

import com.github.jspxnet.utils.ValidUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.util.HashSet;


@Slf4j
public class TagDictionary {

    final private static String FILE_NO_TAG = "/com/github/jspxnet/component/zhex/tag/notag.txt";

    final private static TagDictionary SINGLETON = new TagDictionary();
    private HashSet<String> hsNotag = null;

    static public TagDictionary getInstance() {
        return SINGLETON;
    }

    private TagDictionary() {
        loadNoTag();
    }

    private void loadNoTag() {
        InputStream is = TagDictionary.class.getResourceAsStream(TagDictionary.FILE_NO_TAG);
        this.hsNotag = new HashSet<>();
        String theWord;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Environment.defaultEncode), 512);
            do {
                theWord = br.readLine();
                if (theWord != null) {
                    this.hsNotag.add(theWord.trim());
                }
            } while (theWord != null);
        } catch (IOException ioe) {
            log.error("其它数字载入异常.", ioe);
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * //判断是否有不允许存在的tag字符
     *
     * @param tag 判断是否符合tag
     * @return true 表示可以作为tag
     */
    public boolean isTagFormat(String tag) {
        if (tag == null) {
            return false;
        }
        if (tag.length() < 2) {
            return false;
        }
        if (ValidUtil.isNumber(tag)) {
            return false;
        }
        for (String ta : this.hsNotag) {
            if (ta == null || "".equals(ta)) {
                continue;
            }
            if (tag.contains(ta)) {
                return false;
            }
        }
        return true;
    }
}