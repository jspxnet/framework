/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex.phrase;

import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: 陈原
 * date: 2007-10-4
 * Time: 15:41:50
 */
public class Phrase {
    public Phrase() {

    }

    private String id = StringUtil.empty;
    private String word = StringUtil.empty;
    private String py = StringUtil.empty;
    private String jp = StringUtil.empty;
    private String explain = StringUtil.empty;
    private String publishing = StringUtil.empty;
    private String example = StringUtil.empty;

    public String getId() {
        if (id == null) {
            return StringUtil.empty;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWord() {
        if (word == null) {
            return StringUtil.empty;
        }
        return word;
    }

    public void setWord(String word) {

        this.word = word;
    }

    public String getPy() {
        if (py == null) {
            return StringUtil.empty;
        }
        return py;
    }

    public void setPy(String py) {
        this.py = py;
    }

    public String getJp() {
        if (jp == null) {
            return StringUtil.empty;
        }
        return jp;
    }

    public void setJp(String jp) {
        this.jp = jp;
    }

    public String getExplain() {
        if (explain == null) {
            return StringUtil.empty;
        }
        return explain;
    }

    public void setExplain(String explain) {

        this.explain = explain;
    }

    public String getPublishing() {
        if (publishing == null) {
            return StringUtil.empty;
        }
        return publishing;
    }

    public void setPublishing(String publishing) {

        this.publishing = publishing;
    }

    public String getExample() {
        if (example == null) {
            return StringUtil.empty;
        }
        return example;
    }

    public void setExample(String example) {
        this.example = example;
    }



/*
 <ID>1</ID>
<成语>哀哀父母</成语>
<拼音>āi  āi  fù  mǔ</拼音>
<解释>可哀呀可哀，我的父母啊！原指古时在暴政下人民终年在外服劳役，对父母病痛、老死不能照料而悲哀。</解释>
<出自>《诗经·小雅·蓼莪》：“蓼蓼者莪，匪莪伊蒿，哀哀父母，生我劬劳。”</出自>
<范例>咱人有子方知不孝娘，岂不问～情肠！（元·无名氏《小张屠》第一折）</范例>
<简拼>aafm</简拼>
</cyzd>
*/

}