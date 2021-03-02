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
    /**
     * @param hash hash值
     * @param <T> 类型
     * @return 返回对象
     */
    <T> T getForHash(String hash);
    /**
     *
     * @param hash hash值
     * @return 是否存在
     * @throws Exception 异常
     */
    boolean haveHash(String hash) throws Exception;



    /**
     *
     * @return 得到云配配置
     */
    CloudFileConfig getCloudFileConfig();

    /**
     * 得到子图列表
     * @param pid 父id
     * @param <T> 类型
     * @return 返回子文件列表
     */
    <T> List<T> getChildFileList(long pid);
    /**
     * @return 类对象
     */
    Class<?> getClassType();

    /**
     *
     * @return 命名空间
     */
    String getNamespace();

    /**
     *
     * @param id id
     * @return 查询
     */
    Object get(Long id) ;
    /**
     * @param id id
     * @return 载入
     */
    Object load(Long id) throws Exception;
    /**
     * @param ids id
     * @return 删除
     */
    boolean delete(Long[] ids) ;
    /**
     *
     * @param ids id
     * @param sortType 排序
     * @return 更新排序
     * @throws Exception 异常
     */
    boolean updateSortType(Long[] ids, int sortType) throws Exception;
    /**
     * @param ids id
     * @return 更新排序日期
     */
    boolean updateSortDate(Long[] ids) throws Exception;
    /**
     * @param field      字段
     * @param find       查询条件
     * @param term       条件
     * @param sortString 排序
     * @param uid        用户id
     * @param page       页数
     * @param count      返回数量
     * @param <T> 类型
     * @return 返回列表
     */
    <T> List<T> getList(String[] field, String[] find, String term, String sortString, long uid, long pid, int page, int count);
    /**
     * @param field 字段
     * @param find  查询条件
     * @param term  条件
     * @param uid   用户id
     * @return 得到记录条数
     */
    int getCount(String[] field, String[] find, String term, long uid, long pid);

    /**
     *
     * @param pid 父id
     * @param <T> 类型
     * @return 缩图
     */
    <T> T getThumbnail(long pid);

    /**
     *
     * @return 机构
     */
    String getOrganizeId();

    /**
     *
     * @param organizeId 机构id
     */
    void setOrganizeId(String organizeId);
    /**
     *
     * @param uid 用户id
     * @return 分组列表
     */
    List<String> getGroups(long uid);

    int moveGroup(String groupName, String newGroupName, long uid) throws Exception;
}