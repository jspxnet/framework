package com.github.jspxnet.sober.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.table.SqlMapInterceptorConf;
import com.github.jspxnet.txweb.model.param.PageParam;

import java.util.List;

public interface SqlMapConfDAO extends SoberSupport {
    /**
     *
     * @param namespace 命名空间
     * @param name 名称
     * @return sqlMap 配置
     */
    SqlMapConf getSqlMap(String namespace, String name);
    /**
     *
     * @param namespace 命名空间
     * @param name  sqlmap名称
     * @return 判断sqlMap是否存在
     */
    boolean contains(String namespace, String name);

    /**
     * 返回数据库中配置的拦截器
     * @param namespace 命名空间
     * @return 配置数据
     */
    List<SqlMapInterceptorConf> getInterceptorMap(String namespace);
    /**
     *
     * @param param 翻页参数
     * @return 得到配置列表
     */
    List<SqlMapConf> getSqlMapConfList(PageParam param);
    /**
     *
     * @param param  翻页参数
     * @return 数量
     */
    long getSqlMapConfCount(PageParam param);
    /**
     *
     * @param param 翻页参数
     * @return 得到拦截器配置列表
     */
    List<SqlMapInterceptorConf> getSqlMapInterceptorList(PageParam param);
    /**
     *
     * @param param  翻页参数
     * @return 拦截器数量
     */
    long getSqlMapInterceptorCount(PageParam param);
}
