/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober;

import com.github.jspxnet.sober.criteria.projection.Criterion;
import com.github.jspxnet.sober.criteria.projection.Projection;
import com.github.jspxnet.sober.criteria.Order;
import java.util.List;
import java.io.Serializable;
import java.util.Map;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-8
 * Time: 10:10:51
 */
public interface Criteria extends Serializable {
    /**
     *
     * @return 得到页数
     */
    Integer getCurrentPage();
    /**
     *
     * @param criterion 查询条件
     * @return 查询器
     */
    Criteria add(Criterion criterion);
    /**
     *
     * @param order 排序字段
     * @return 查询器
     */
    Criteria addOrder(Order order);
    /**
     *
     * @param group 分组字段
     * @return  查询器
     */
    Criteria addGroup(String group);

    Criteria setProjection(Projection projection);
    /**
     *
     * @return 得到行数
     */
    int getTotalCount();

    Criteria setTotalCount(Integer totalCount);

    Criteria setCurrentPage(Integer currentPage);

    <T> List<T> list(boolean loadChild);

    List<Object> groupList();

    Object uniqueResult();

    /**
     *
     * @param loadChild 是否载入映射
     * @param <T> 对象类型
     * @return 载入单个对象
     */
    <T> T objectUniqueResult(boolean loadChild);

    boolean booleanUniqueResult();

    int intUniqueResult();

    long longUniqueResult();

    float floatUniqueResult();

    double doubleUniqueResult();

    /**
     * 删除对象
     *
     * @param delChild 是否删除映射对象
     * @return boolean 是否成功
     */
    int delete(boolean delChild);
    /**
     * 更新
     * @param updateMap 参数
     * @return 是否成功
     */
    int update(Map<String, Object> updateMap);

    /**
     *
     * @param <T> 类型
     * @return 类对象
     */
    <T> Class<T> getCriteriaClass();

    /**
     *
     * @return 删除这个查询的缓存数据
     */
    String getDeleteListCacheKey();

    /**
     * 对一个类对象求合计并返回
     * @param <T> 类型
     * @return 类实体对象
     */
    <T> T autoSum();
    /**
     * 对一个类对象求合计并返回
     * @param fields 需要求和的字段
     * @param <T> 类型
     * @return 类实体对象
     */
    <T> T autoSum(String[] fields);

    /**
     * 对一个类对象里边的数字求平均数,在保存到对象返回
     * @param <T> 类型
     * @return 类实体对象
     */
    <T> T autoAvg();
    /**
     * 对一个类对象里边的数字求平均数,在保存到对象返回
     * @param fields 字段
     * @param <T> 类型
     * @return 类实体对象
     */
    <T> T autoAvg(String[] fields);
}