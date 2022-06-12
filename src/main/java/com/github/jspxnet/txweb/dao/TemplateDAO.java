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
import com.github.jspxnet.txweb.annotation.Param;

import java.io.Serializable;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-11-30
 * Time: 下午3:43
 * 模板DAO提供给普通对象使用,不用每一个对象都建一个DAO
 */
public interface TemplateDAO<T> extends SoberSupport {
    String getNamespace();

    Class<T> getClassType();

    Object load(Serializable id) throws Exception;

    Object get(Serializable id) throws Exception;

    boolean delete(Serializable[] ids) throws Exception;

    boolean updateSortType(Serializable[] ids, int sortType) throws Exception;

    boolean updateSortDate(Serializable[] ids) throws Exception;

    List<T> getList(@Param(caption = "字段") String[] field, @Param(caption = "查询条件") String[] find,
                    @Param(caption = "条件") String term,
                    @Param(caption = "用户id") long uid, @Param(caption = "排序") String sortString, @Param(caption = "页数") int page, @Param(caption = "数量") int count) throws Exception;

    int getCount(@Param(caption = "字段") String[] field, @Param(caption = "查询条件") String[] find,
                 @Param(caption = "条件") String term, @Param(caption = "用户id") long uid) throws Exception;
}