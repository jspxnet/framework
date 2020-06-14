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

import com.github.jspxnet.txweb.table.MemberTree;
import com.github.jspxnet.sober.SoberSupport;

import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-11-14
 * Time: 17:54:34
 */
public interface MemberTreeDAO extends SoberSupport {
    Map<String, MemberTree> getMemberTreeMap(long uid);

    void flush();

    List<MemberTree> getMemberTree(long uid);

    String getMemberTreeSplitString(long uid);

    boolean deleteForUid(long uid) throws Exception;

    String[] getMemberTreeArray(long uid);

    boolean deleteAll() throws Exception;

    boolean fixTreeItem(String[] treeItemId) throws Exception;

    String getNamespace();

    void setNamespace(String namespace);

    void setOrganizeId(String organizeId);

    String getOrganizeId();

}