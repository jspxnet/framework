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

import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;

import com.github.jspxnet.utils.*;


import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.dao.TemplateDAO;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenYuan
 * date: 12-11-30
 * Time: 下午3:43
 * 通用DAO模板
 * com.github.jspxnet.txweb.dao.impl.TemplateDAOImpl
 */
@Slf4j
public class TemplateDAOImpl<T> extends JdbcOperations implements TemplateDAO<T> {

    private Class<T> tableClass;

    public TemplateDAOImpl() {

    }

    public String getClassName() {
        if (tableClass == null) {
            return StringUtil.empty;
        }
        return tableClass.getName();
    }

    @Param(request = false, caption = "DO对象名称")
    public void setClassName(String className) throws Exception {
        tableClass = (Class<T>) ClassUtil.loadClass(className);
    }

    @Override
    public Class<T> getClassType() {
        return tableClass;
    }


    private String namespace = StringUtil.empty;

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    /**
     * @param id id
     * @return 载入
     */
    @Operate(caption = "载入")
    @Override
    public T load(@Param(caption = "id列表", required = true, message = "id不允许为空") Serializable id) {
        return super.load(tableClass, id);
    }

    /**
     * @param id id
     * @return 对象
     */
    @Operate(caption = "得到")
    @Override
    public T get(@Param(caption = "id列表", required = true, message = "id不允许为空") Serializable id) {
        return super.get(tableClass, id);
    }

    /**
     * @param ids id
     * @return 删除
     */
    @Operate(caption = "删除")
    @Override
    public boolean delete(@Param(caption = "id列表", required = true, message = "id不允许为空") Serializable[] ids) throws Exception {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        TableModels tableModels = getSoberTable(tableClass);
        Field field = ClassUtil.getDeclaredField(tableClass, tableModels.getPrimary());
        try {
            for (Serializable sid : ids) {
                Serializable id =  BeanUtil.getTypeValue(sid, field.getType());
                Object obj = get(tableClass, id);
                if (obj != null) {
                    delete(obj);
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(ids, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }


    /**
     * @param ids      id
     * @param sortType 排序
     * @return 更新排序
     */
    @Operate(caption = "更新排序")
    @Override
    public boolean updateSortType(@Param(caption = "id列表", required = true, message = "id不允许为空") Serializable[] ids, int sortType) {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        TableModels tableModels = getSoberTable(tableClass);
        Field field = ClassUtil.getDeclaredField(tableClass, tableModels.getPrimary());
        try {
            for (Serializable sid : ids) {
                Serializable id = BeanUtil.getTypeValue(sid, field.getType());
                Object obj = get(tableClass, id);
                if (obj != null) {
                    BeanUtil.setSimpleProperty(obj, "sortType", sortType);
                    super.update(obj, new String[]{"sortType"});
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(ids, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }

    /**
     * @param ids id
     * @return 更新排序日期
     */
    @Operate(caption = "提前")
    @Override
    public boolean updateSortDate(@Param(caption = "id列表") Serializable[] ids) {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        TableModels tableModels = getSoberTable(tableClass);
        Field field = ClassUtil.getDeclaredField(tableClass, tableModels.getPrimary());
        try {
            for (Serializable sid : ids) {
                Serializable id = BeanUtil.getTypeValue(sid, field.getType());
                Object obj = get(tableClass, id);
                if (obj != null) {
                    BeanUtil.setSimpleProperty(obj, "sortDate", new Date());
                    super.update(obj, new String[]{"sortDate"});
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(ids, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }


    /**
     * @param field      字段
     * @param find       查询条件
     * @param term       条件
     * @param sortString 排序
     * @param uid        用户id
     * @param page       页数
     * @param count      返回数量
     * @return 返回列表
     * @throws Exception 异常
     */
    @Operate(caption = "得到列表")
    @Override
    public List<T> getList(@Param(caption = "字段") String[] field, @Param(caption = "查询条件") String[] find,
                           @Param(caption = "条件") String term,
                           @Param(caption = "用户id") long uid, @Param(caption = "排序") String sortString,
                           @Param(caption = "页数") int page, @Param(caption = "数量") int count) throws Exception {
        if (StringUtil.isNull(sortString)) {
            sortString = "createDate:D";
        }
        Criteria criteria = createCriteria(tableClass);
        if (ClassUtil.haveMethodsName(tableClass, "setNamespace")) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }

        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return SSqlExpression.getSortOrder(criteria, sortString).setCurrentPage(page).setTotalCount(count).list(false);
    }


    /**
     * @param field 字段
     * @param find  查询条件
     * @param term  条件
     * @param uid   用户id
     * @return 得到记录条数
     * @throws Exception 异常
     */
    @Operate(caption = "得到列表")
    @Override
    public int getCount(@Param(caption = "字段") String[] field, @Param(caption = "查询条件") String[] find,
                        @Param(caption = "条件") String term, @Param(caption = "用户id") long uid) throws Exception {
        Criteria criteria = createCriteria(tableClass);
        if (ClassUtil.haveMethodsName(tableClass, "setNamespace")) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

}