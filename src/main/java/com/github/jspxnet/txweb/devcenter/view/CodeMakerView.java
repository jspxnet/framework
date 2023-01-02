package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.codemaker.CodeMaker;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.StringUtil;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@HttpMethod(caption = "帮助", actionName = "*", namespace = Environment.DEV_CENTER+"/codemaker")
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class CodeMakerView extends ActionSupport {

    @Ref
    private CodeMaker codeMaker;

    @Operate(caption = "生成窗体", method = "builder")
    public RocResponse<String> builder(@Param(caption = "模版页面", required = true) String templateName, @Param(caption = "模型ID",min = 2,max = 100, required = true) String modelId, @Param(caption = "跳过字段") List<String> jumpFields)
    {
        return codeMaker.builderPage(templateName,modelId,jumpFields);
    }


    @Operate(caption = "命名空间", method = "list/namespace",post = false)
    public RocResponse<List<Map<String,String>>> getNamespaceList(@Param(caption = "查询",max = 200)String find)
    {
        List<Map<String,String>> result = new ArrayList<>();
        List<String>  list = codeMaker.getNamespaceList();
        for (String str:list)
        {
            if (StringUtil.isNull(str))
            {
                continue;
            }
            if (!StringUtil.isEmpty(find) && !str.contains(find))
            {
                continue;
            }
            Map<String,String> map = new HashMap<>();
            map.put("namespace",str);
            result.add(map);
        }
        return RocResponse.success(result);
    }

    @Operate(caption = "实体列表", method = "list/table",post = false)
    public RocResponse< List<TableModels>> getTableList(@Param(caption = "查询",max = 200)String find,@Param(caption = "是否包含DTO",value = "false") boolean dto,@Param(caption = "0:任意1:扩展;2:不可扩展",value = "0") int extend,@Param(caption = "页数",value = "1") int currentPage,@Param(caption = "页数",value = "12") int count)
    {
        Map<String, TableModels>  map = codeMaker.getSoberTableList(dto,extend);
        int firstRow = currentPage * count - count;
        if (firstRow < 0) {
            firstRow = 1;
        }

        int i = 0;
        List<TableModels> result = new ArrayList<>();
        for (TableModels tableModels:map.values())
        {
            i++;
            if (StringUtil.isEmpty(find))
            {
                if (i>=firstRow)
                {
                    result.add(tableModels);
                }
            } else
            if (tableModels.getName()!=null&&tableModels.getName().contains(find)||
                    tableModels.getCaption()!=null&&tableModels.getCaption().contains(find)
            || tableModels.getEntity()!=null&&tableModels.getEntity().getName().contains(find))
            {
                if (i>=firstRow)
                {
                    result.add(tableModels);
                }
            }
            if (result.size()>=count)
            {
                break;
            }
        }
        return RocResponse.success(result).setCurrentPage(currentPage).setCount(count).setTotalCount(map.size());
    }

}
