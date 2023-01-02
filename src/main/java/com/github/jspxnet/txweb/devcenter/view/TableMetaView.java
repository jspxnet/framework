package com.github.jspxnet.txweb.devcenter.view;


import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.meta.TableMeta;
import com.github.jspxnet.utils.BeanUtil;
import java.util.List;

public class TableMetaView extends ActionSupport {
    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "详细",post = false, method = "detail")
    public RocResponse<TableMeta> detail(@Param(caption = "数据库表名", message = "表明不能为空",required = true) String tableName)
    {
        TableMeta tableMeta = genericDAO.load(TableMeta.class,"tableName",tableName,true);
        return RocResponse.success(tableMeta);
    }

    @Operate(caption = "字段列表",post = false, method = "fieldlist")
    public RocResponse<List<SoberColumn>>  fieldList(@Param(caption = "数据库表名", message = "表明不能为空",required = true) String tableName)
    {
        return RocResponse.success(genericDAO.getTableColumns(tableName));
    }


    public TableMeta create(String tableName)
    {
        //默认构建一个空的
        TableModels tableModels = genericDAO.getTableModels(tableName);
        if (tableModels==null)
        {
            TableMeta tableMeta = new TableMeta();
            tableMeta.setId(0);
            tableMeta.setTableName(tableName);
            tableMeta.setCaption("未知");
            return tableMeta;
        }
        TableMeta tableMeta = new TableMeta();
        tableMeta.setId(0);
        tableMeta.setTableName(tableModels.getName());
        tableMeta.setCaption(tableModels.getCaption());
        tableMeta.setTableType(1);
        tableMeta.setEntityClass(tableModels.getEntity().getName());
        tableMeta.setPrimary(tableModels.getPrimary());
        List<SoberColumn> webColumns = BeanUtil.copyList(tableModels.getColumns(),SoberColumn.class);
        tableMeta.setColumns(webColumns);
        return tableMeta;
    }
}
