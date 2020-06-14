/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-8-4
 * Time: 17:27:49
 * com.github.jspxnet.txweb.dao.impl.GenericDAOImpl
 */
public class GenericDAOImpl extends JdbcOperations implements GenericDAO {
    public GenericDAOImpl() {

    }

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
     * @return 返回列表
     */
    @Override
    public <T> List<T> getList(
            T cls,
            String field,
            String find,
            String term,
            long uid,
            String sortString,
            int page, int count, boolean load)  {
        String sort;
        if (StringUtil.isNull(sortString)) {
            sort = "sortType:D;createDate:D";
        } else {
            sort = sortString;
        }
        Criteria criteria = createCriteria((Class<T>)cls);
        if (!StringUtil.isNull(field) && !StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.like(field, "%" + find + "%"));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, sort);
        return criteria.setCurrentPage(page).setTotalCount(count).list(load);
    }

    /**
     * @param cls     类
     * @param field 字段
     * @param find  查询
     * @param term  条件
     * @param uid   用户id
     * @return 得到长度
     * @throws Exception 异常
     */
    @Override
    public int getCount(
            Class cls,
            String field,
            String find,
            String term,
            long uid
    ) throws Exception {
        Criteria criteria = createCriteria(cls);
        if (!StringUtil.isNull(field) && !StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.like(field, "%" + find + "%"));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }

        return SSqlExpression.getTermExpression(criteria, term).setProjection(Projections.rowCount()).intUniqueResult();
    }

}