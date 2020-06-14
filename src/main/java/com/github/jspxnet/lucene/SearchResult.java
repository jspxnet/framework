/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.lucene;

import com.github.jspxnet.txweb.annotation.Operate;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-12-11
 * Time: 12:43:35
 */
public class SearchResult {

    public SearchResult() {

    }

    private List<LuceneVO> list;
    //搜索到的结果数量
    private int totalCount = 0;

    public List<LuceneVO> getList() {
        return list;
    }

    public void setList(List<LuceneVO> list) {
        this.list = list;
    }

    @Operate(caption = "列表总数")
    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
}