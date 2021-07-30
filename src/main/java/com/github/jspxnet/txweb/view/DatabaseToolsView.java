package com.github.jspxnet.txweb.view;

import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import java.util.List;

@HttpMethod(caption = "数据库工具",namespace = "global/tools",actionName = "*")
@Bean(singleton = true)
public class DatabaseToolsView  extends ActionSupport {

    @Ref
    private GenericDAO genericDAO;

    @Operate(caption = "生成Bean字段", method = "createcolumn",post = false)
    public RocResponse<List<SoberColumn>> createColumn(@Param(caption = "表名", required = true) String tableName)
    {
        return RocResponse.success(genericDAO.getTableColumns(tableName));
    }



}
