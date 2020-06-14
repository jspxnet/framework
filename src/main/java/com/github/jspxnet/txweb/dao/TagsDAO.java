/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.txweb.table.TagWord;
import com.github.jspxnet.sober.SoberSupport;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-10-19
 * Time: 12:25:06
 */
public interface TagsDAO extends SoberSupport {
    boolean updateTagWord(String words, String parentWord, int correlative) throws Exception;

    List<TagWord> getList(int currentPage, int totalCount) throws Exception;

    List<TagWord> getTagWordChild(String word, int currentPage, int totalCount);
}