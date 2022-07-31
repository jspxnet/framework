package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.dao.SqlMapConfDAO;
import com.github.jspxnet.sober.table.SqlMapInterceptorConf;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.PathVar;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import java.util.ArrayList;
import java.util.List;

public class SqlMapInterceptorView extends ActionSupport {
    @Ref
    protected SqlMapConfDAO sqlMapConfDAO;

    @Operate(caption = "SqlMap拦截", method = "index", post = false)
    public String index()  {
        return "SqlMap拦截器";
    }

    @Operate(caption = "SqlMap拦截器列表", method = "list/page")
    public RocResponse<List<SqlMapInterceptorConf>> getListPage(@Param(caption = "翻页参数") PageParam pageParam) {
        long totalCount = sqlMapConfDAO.getSqlMapInterceptorCount(pageParam);
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
        }
        return RocResponse.success(sqlMapConfDAO.getSqlMapInterceptorList(pageParam), pageParam, totalCount);
    }

    @Operate(caption = "sqlMap拦截器", method = "detail/${id}")
    public RocResponse<SqlMapInterceptorConf> detail(@PathVar(caption = "id") Long id) {
        return RocResponse.success(sqlMapConfDAO.load(SqlMapInterceptorConf.class,id));
    }

}
