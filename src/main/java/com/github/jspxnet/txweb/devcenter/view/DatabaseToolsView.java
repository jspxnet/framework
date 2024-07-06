package com.github.jspxnet.txweb.devcenter.view;


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.model.dto.SoberColumnDto;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.util.KingdeeXkUtil;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

            //中文字段名称修复为英文方式begin
            if (StringUtil.isChinese(dto.getName()))
            {
               String fieldName =  StringUtil.substringAfterLast(StringUtil.substringBefore(sql," "+dto.getCaption()),",");
               if (!StringUtil.isNull(fieldName))
               {
                   int pos = StringUtil.indexIgnoreCaseOf(fieldName,"AS");
                   if (pos!=-1)
                   {
                       fieldName = StringUtil.trim(fieldName.substring(0,pos));
                       if (fieldName.contains("."))
                       {
                           fieldName = StringUtil.substringAfter(fieldName,StringUtil.DOT);
                       }
                       dto.setName(StringUtil.underlineToCamel(fieldName));
                       dto.setNotNull(false);
                   }
               }

            }
            //中文字段名称修复为英文方式end
            sb.append(dto.getBeanField(camel)).append("\r\n");
        }
        return RocResponse.success(sb.toString());
    }

    @Operate(caption = "sql生成Bean字段带过滤", method = "sqlbeanfield",post = true)
    public RocResponse<String> sqlBeanField(@Param(caption = "sql",min = 2,max = 100,required = true) String sql,
                                            @Param(caption = "Column列表") List<SoberColumnDto> listNew)
    {
        List<SoberColumn>  list = genericDAO.getSqlColumns(sql);
        Map<String,SoberColumnDto> columnMap = new HashMap<>();
        for (SoberColumnDto dto:listNew)
        {
            if (!columnMap.containsKey(dto.getCaption()))
            {
                columnMap.put(dto.getCaption(),dto);
            } else {
                columnMap.put(dto.getCaption()+"D",dto);
            }
        }

        StringBuilder sb = new StringBuilder();
        for (SoberColumn column:list)
        {
            SoberColumnDto dto = columnMap.get(column.getCaption());
            if (dto==null)
            {
                dto = BeanUtil.copy(column, SoberColumnDto.class);
            }
            if (dto.getName()!=null&&dto.getName().startsWith("F_"))
            {
                dto.setName(StringUtil.substringAfter(dto.getName(),"_"));
            }
            //中文字段名称修复为英文方式begin
            if (StringUtil.isChinese(dto.getName()))
            {
                String fieldName =  StringUtil.substringAfterLast(StringUtil.substringBefore(sql," "+dto.getCaption()),",");
                if (!StringUtil.isNull(fieldName))
                {
                    int pos = StringUtil.indexIgnoreCaseOf(fieldName,"AS");
                    if (pos!=-1)
                    {
                        fieldName = StringUtil.trim(fieldName.substring(0,pos));
                        if (fieldName.contains("."))
                        {
                            fieldName = StringUtil.substringAfter(fieldName,StringUtil.DOT);
                        }
                        dto.setName(StringUtil.underlineToCamel(fieldName));
                        dto.setNotNull(false);
                    }
                }
            }
            //中文字段名称修复为英文方式end
            String fileName = KingdeeXkUtil.getFileName(dto.getCaption());
            if (StringUtil.isNull(fileName))
            {
                fileName = dto.getName();
            }
            dto.setName(KingdeeXkUtil.fixWordName(fileName));
            sb.append(dto.getBeanField(true)).append("\r\n");
        }
        return RocResponse.success(sb.toString());
    }


    @Operate(caption = "得到sql字段", method = "getsqlfields",post = true)
    public RocResponse<String> getSqlFields(@Param(caption = "sql",min = 2,max = 100,required = true) String sql)
    {
        List<SoberColumn>  list = genericDAO.getSqlColumns(sql);
        StringBuilder sb = new StringBuilder();
        for (SoberColumn column:list)
        {
            sb.append(column.getName()).append(",");
        }
        return RocResponse.success(sb.toString());
    }



    @Operate(caption = "得到字段的AS表达式", method = "getfieldassql",post = true)
    public RocResponse<String> getFieldAsSql(@Param(caption = "sql",min = 2,max = 100,required = true) String className)
    {
        Class<?> cla = null;
        try {
            cla = ClassUtil.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        TableModels tableModel = genericDAO.getSoberTable(cla);
        List<SoberColumn>  list = tableModel.getColumns();
        StringBuilder sb = new StringBuilder();
        for (SoberColumn column:list)
        {
            sb.append(column.getName()).append(" AS ").append(column.getCaption()).append(StringUtil.COMMAS);
        }
        if (sb.toString().endsWith(StringUtil.COMMAS))
        {
            sb.setLength(sb.length()-1);
        }
        return RocResponse.success(sb.toString());
    }

    @Operate(caption = "测试查询", method = "sql",post = false)
    public RocResponse<List<?>> sql(@Param(caption = "sql",min = 2,max = 500, required = true) String sql)
    {
        return RocResponse.success(genericDAO.prepareQuery(sql,null));
    }

}
