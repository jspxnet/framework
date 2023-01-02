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
import com.github.jspxnet.txweb.model.param.PageParam;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-2-4
 * Time: 下午10:54
 */
public interface GenericDAO extends SoberSupport {

    /**
     * @param cls        对象类型
     * @param field      字段
     * @param find       查询条件
     * @param term       条件
     * @param uid        用户
     * @param sortString 排序
     * @param page       页数
     * @param count      行数
     * @param load       载入关联
     * @param <T>        类型
     * @return 返回列表
     */
    <T> List<T> getList(
            Class<T> cls,
            String[] field,
            String[] find,
            String term,
            long uid,
            String sortString,
            int page, int count, boolean load);

    /**
     * @param cls     类
     * @param field 字段
     * @param find  查询
     * @param term  条件
     * @param uid   用户id
     * @return 得到长度
     */
    int getCount(
            Class<?> cls,
            String[] field,
            String[] find,
            String term,
            long uid
    );
    /**
     *
     * @param cls 对象类型
     * @param param 翻页参数
     * @param <T> 类型
     * @return 返回列表
     */
    <T> List<T> getList(
            Class<T> cls, PageParam param);
    /**
     *
     * @param cls 对象类型
     * @param param 翻页参数
     * @return 得到长度
     */
    int getCount(Class<?> cls, PageParam param) ;
}