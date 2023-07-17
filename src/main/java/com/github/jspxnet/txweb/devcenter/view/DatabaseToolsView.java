package com.github.jspxnet.txweb.devcenter.view;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.model.dto.SoberColumnDto;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.BeanUtil;

import java.util.List;

@HttpMethod(caption = "数据库工具",namespace = Environment.DEV_CENTER+"/db",actionName = "*")
@Bean(singleton = true)
public class DatabaseToolsView  extends ActionSupport {

    @Ref
    private GenericDAO genericDAO;

    @Operate(caption = "生成Bean字段", method = "createcolumn",post = false)
    public RocResponse<List<SoberColumn>> createColumn(@Param(caption = "表名",min = 2,max = 100, required = true) String tableName)
    {
        return RocResponse.success(genericDAO.getTableColumns(tableName));
    }

    @Operate(caption = "生成Bean字段", method = "beanfield",post = false)
    public RocResponse<String> beanField(@Param(caption = "表名",min = 2,max = 100,required = true) String tableName)
    {
        List<SoberColumn>  list = genericDAO.getTableColumns(tableName);
        StringBuilder sb = new StringBuilder();
        for (SoberColumn column:list)
        {
            SoberColumnDto dto = BeanUtil.copy(column, SoberColumnDto.class);
            sb.append(dto.getBeanField(false)).append("\r\n");
        }
        return RocResponse.success(sb.toString());
    }

    @Operate(caption = "sql生成Bean字段", method = "sqlbeanfield",post = true)
    public RocResponse<String> sqlBeanField(@Param(caption = "sql",min = 2,max = 100,required = true) String sql,
                                            @Param(caption = "是否转驼峰命名",required = true,value = "true") boolean camel)
    {
        List<SoberColumn>  list = genericDAO.getSqlColumns(sql);
        StringBuilder sb = new StringBuilder();
        for (SoberColumn column:list)
        {
            SoberColumnDto dto = BeanUtil.copy(column, SoberColumnDto.class);
            sb.append(dto.getBeanField(camel)).append("\r\n");
        }
        return RocResponse.success(sb.toString());
    }

    @Operate(caption = "得到sql字段", method = "getsqlfields",post = true)
    public RocResponse<String> getsqlfields(@Param(caption = "sql",min = 2,max = 100,required = true) String sql)
    {
        List<SoberColumn>  list = genericDAO.getSqlColumns(sql);
        StringBuilder sb = new StringBuilder();
        for (SoberColumn column:list)
        {
            sb.append(column.getName()).append(",");
        }
        return RocResponse.success(sb.toString());
    }


    @Operate(caption = "测试查询", method = "sql",post = false)
    public RocResponse<List<?>> sql(@Param(caption = "sql",min = 2,max = 500, required = true) String sql)
    {
        return RocResponse.success(genericDAO.prepareQuery(sql,null));
    }
}
