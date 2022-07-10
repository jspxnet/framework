package com.github.jspxnet.sober.dao.impl;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.dao.SqlMapConfDAO;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.table.SqlMapInterceptorConf;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class SqlMapConfDAOImpl extends JdbcOperations implements SqlMapConfDAO {

    /**
     *
     * @param namespace 命名空间
     * @param name 名称
     * @return sqlMap 配置
     */
    @Override
    public SqlMapConf getSqlMap(String namespace,String name)
    {
        Criteria criteria = createCriteria(SqlMapConf.class);
        if (!StringUtil.isNull(namespace))
        {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        criteria = criteria.add(Expression.eq("name", name));

        String databaseType = getSoberFactory().getDatabaseType();
        criteria = criteria.add(Expression.eq("databaseType", databaseType));
        SqlMapConf sqlMapConf = criteria.addOrder(Order.desc("version")).objectUniqueResult(false);
        if (sqlMapConf==null||sqlMapConf.getId()<=0)
        {
            Criteria criteriaDef = createCriteria(SqlMapConf.class).add(Expression.eq("namespace", namespace))
                    .add(Expression.eq("name", name));
            return criteriaDef.addOrder(Order.desc("version")).objectUniqueResult(false);
        }
        return sqlMapConf;
    }

    /**
     * 返回数据库中配置的拦截器
     * @param namespace 命名空间
     * @return 配置数据
     */
    @Override
    public List<SqlMapInterceptorConf> getInterceptorMap(String namespace)
    {
        Criteria criteria = createCriteria(SqlMapInterceptorConf.class).add(Expression.eq("enable", YesNoEnumType.YES.getValue()));
        if (!StringUtil.isNull(namespace))
        {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return criteria.setCurrentPage(1).setTotalCount(1000).list(false);
    }

    /**
     *
     * @param param 翻页参数
     * @return 得到配置列表
     */
    @Override
    public List<SqlMapConf> getSqlMapConfList(PageParam param) {
        if (StringUtil.isNull(param.getSort())) {
            param.setSort("namespace:A;name:A;version:D");
        }
        Criteria criteria = createCriteria(SqlMapConf.class);
        if (!StringUtil.isNull(param.getNamespace()))
        {
            criteria = criteria.add(Expression.eq("namespace", param.getNamespace()));
        }
        if (!ArrayUtil.isEmpty(param.getFind()) && !ArrayUtil.isEmpty(param.getField())) {
            criteria = criteria.add(Expression.find(param.getField(), param.getFind()));
        }
        if (param.getUid() != 0) {
            criteria = criteria.add(Expression.eq("putUid", param.getUid()));
        }
        criteria = SSqlExpression.getTermExpression(criteria, param.getTerm());
        criteria = SSqlExpression.getSortOrder(criteria, param.getSort());
        return criteria.setCurrentPage(param.getCurrentPage()).setTotalCount(param.getCount()).list(false);
    }

    /**
     *
     * @param param  翻页参数
     * @return 数量
     */
    @Override
    public long getSqlMapConfCount(PageParam param) {
        Criteria criteria = createCriteria(SqlMapConf.class);
        if (!StringUtil.isNull(param.getNamespace()))
        {
            criteria = criteria.add(Expression.eq("namespace", param.getNamespace()));
        }

        if (!ArrayUtil.isEmpty(param.getFind()) && !ArrayUtil.isEmpty(param.getField())) {
            criteria = criteria.add(Expression.find(param.getField(), param.getFind()));
        }
        if (param.getUid() != 0) {
            criteria = criteria.add(Expression.eq("putUid", param.getUid()));
        }
        criteria = SSqlExpression.getTermExpression(criteria, param.getTerm());
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }

    //---------------------------------------
    /**
     *
     * @param param 翻页参数
     * @return 得到拦截器配置列表
     */
    @Override
    public List<SqlMapInterceptorConf> getSqlMapInterceptorList(PageParam param) {
        if (StringUtil.isNull(param.getSort())) {
            param.setSort("sortType:D;D;createDate:D");
        }
        Criteria criteria = createCriteria(SqlMapInterceptorConf.class);
        if (!StringUtil.isNull(param.getNamespace()))
        {
            criteria = criteria.add(Expression.eq("namespace", param.getNamespace()));
        }
        if (!ArrayUtil.isEmpty(param.getFind()) && !ArrayUtil.isEmpty(param.getField())) {
            criteria = criteria.add(Expression.find(param.getField(), param.getFind()));
        }
        if (param.getUid() != 0) {
            criteria = criteria.add(Expression.eq("putUid", param.getUid()));
        }
        criteria = SSqlExpression.getTermExpression(criteria, param.getTerm());
        criteria = SSqlExpression.getSortOrder(criteria, param.getSort());
        return criteria.setCurrentPage(param.getCurrentPage()).setTotalCount(param.getCount()).list(false);
    }

    /**
     *
     * @param param  翻页参数
     * @return 拦截器数量
     */
    @Override
    public long getSqlMapInterceptorCount(PageParam param) {
        Criteria criteria = createCriteria(SqlMapInterceptorConf.class);
        if (!StringUtil.isNull(param.getNamespace()))
        {
            criteria = criteria.add(Expression.eq("namespace", param.getNamespace()));
        }
        if (!ArrayUtil.isEmpty(param.getFind()) && !ArrayUtil.isEmpty(param.getField())) {
            criteria = criteria.add(Expression.find(param.getField(), param.getFind()));
        }
        if (param.getUid() != 0) {
            criteria = criteria.add(Expression.eq("putUid", param.getUid()));
        }
        criteria = SSqlExpression.getTermExpression(criteria, param.getTerm());
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }
}
