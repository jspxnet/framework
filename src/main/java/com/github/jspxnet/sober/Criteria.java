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
    Criteria add(Criterion criterion);

    Criteria addOrder(Order order);

    Criteria addGroup(String group);

    Criteria setProjection(Projection projection);

    Criteria setTotalCount(Integer totalCount);

    Criteria setCurrentPage(Integer currentPage);

    <T> List<T> list(boolean loadChild);

    List<Object> groupList();

    Object uniqueResult();

    boolean booleanUniqueResult();

    int intUniqueResult();

    long longUniqueResult();

    float floatUniqueResult();

    double doubleUniqueResult();

    <T> T objectUniqueResult(boolean loadChild);

    int delete(boolean delChild);

    int update(Map<String, Object> updateMap);

    <T> Class<T> getCriteriaClass();

    String getDeleteListCacheKey();
}