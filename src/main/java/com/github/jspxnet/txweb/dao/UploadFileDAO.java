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


import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.txweb.table.CloudFileConfig;

import java.util.List;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-11-14
 * Time: 18:20:02
 */
public interface UploadFileDAO extends SoberSupport {
    <T> T getForHash(String hash);

    boolean haveHash(String hash) throws Exception;

    CloudFileConfig getCloudFileConfig();

    <T> List<T> getChildFileList(long pid);

    String getNamespace();

    <T> Class<T> getClassType() throws Exception;

    Object get(Long id) ;

    Object load(Long id) throws Exception;

    boolean delete(Long[] ids) ;

    boolean updateSortType(Long[] ids, int sortType) throws Exception;

    boolean updateSortDate(Long[] ids) throws Exception;

    <T> List<T> getList(String[] field, String[] find, String term, String sortString, long uid, long pid, int page, int count);

    int getCount(String[] field, String[] find, String term, long uid, long pid);

    <T> T getThumbnail(long pid);

    String getOrganizeId();

    void setOrganizeId(String organizeId);
}