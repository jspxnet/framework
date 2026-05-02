package com.github.jspxnet.txweb.devcenter.view;


import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.config.SoberTable;
import com.github.jspxnet.sober.enums.EntityLevelEnumType;
import com.github.jspxnet.sober.table.meta.FormMetaTable;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.BeanUtil;

import java.util.List;


public class TableMetaView extends ActionSupport {
    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "详细",post = false, method = "detail")
    public RocResponse<FormMetaTable> detail(@Param(caption = "数据库表名", message = "表明不能为空",required = true) String tableName)
    {
        FormMetaTable tableMeta = genericDAO.load(FormMetaTable.class,"tableName",tableName,true);
        return RocResponse.success(tableMeta);
    }

    @Operate(caption = "字段列表",post = false, method = "fieldlist")
    public RocResponse<List<SoberColumn>>  fieldList(@Param(caption = "数据库表名", message = "表明不能为空",required = true) String tableName)
    {
        return RocResponse.success(genericDAO.getTableColumns(tableName));
    }


    /**
     *
     * @param tableName 表
     * @return 默认构建一个空的
     */
    public  SoberTable create(String tableName)
    {
        //默认构建一个空的
        TableModels tableModels = genericDAO.getTableModels(tableName);
        if (tableModels==null)
        {
            SoberTable tableMeta = new SoberTable();
            tableMeta.setId(0);
            tableMeta.setName(tableName);
            tableMeta.setCaption("未知");
            return tableMeta;
        }
        SoberTable tableMeta = new SoberTable();
        tableMeta.setId(0);
        tableMeta.setName(tableModels.getName());
        tableMeta.setCaption(tableModels.getCaption());
        tableMeta.setEntityLevel(EntityLevelEnumType.MAIN.getValue());
        tableMeta.setPrimaryKey(tableModels.getPrimaryKey());
        List<SoberColumn> webColumns = BeanUtil.copyList(tableModels.getColumns(),SoberColumn.class);
        tableMeta.setColumns(webColumns);
        return tableMeta;
    }
}
