package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.dao.SqlMapConfDAO;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.PathVar;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import java.util.ArrayList;
import java.util.List;

/**
 * @author chenYuan
 */
public class SqlMapView extends ActionSupport {
    @Ref
    protected SqlMapConfDAO sqlMapConfDAO;


    @Operate(caption = "文档索引", method = "index", post = false)
    public String index()  {
        return "SqlMap配置";
    }

    @Operate(caption = "SqlMap配置翻页列表", method = "list/page")
    public RocResponse<List<SqlMapConf>> getListPage(@Param(caption = "翻页参数") PageParam pageParam) {
        long totalCount = sqlMapConfDAO.getSqlMapConfCount(pageParam);
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
        }
        return RocResponse.success(sqlMapConfDAO.getSqlMapConfList(pageParam), pageParam, totalCount);
    }

    @Operate(caption = "SqlMap配置", method = "detail/${id}")
    public RocResponse<SqlMapConf> detail(@PathVar(caption = "id") Long id) {
        return RocResponse.success(sqlMapConfDAO.load(SqlMapConf.class,id));
    }

}
