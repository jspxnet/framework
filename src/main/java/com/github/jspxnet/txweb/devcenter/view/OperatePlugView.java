package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.meta.OperatePlug;
import com.github.jspxnet.utils.ClassUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OperatePlugView extends ActionSupport {
    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "详细",post = false, method = "detail")
    public RocResponse<Map<String,Object>> detail(@Param(caption = "className", message = "className 类路径不能为空",required = true) String className)
    {
        Class<?> cls = null;
        try {
            cls = ClassUtil.loadClass(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.WARN.getValue(),"不存在的类路径");
        }
        TableModels soberTable = genericDAO.getSoberTable(cls);
        List<OperatePlug> operatePlugList = genericDAO.getOperatePlugList(cls);
        Map<String,Object> result = new HashMap<>();
        result.put("soberTable",soberTable);
        result.put("operatePlugList",operatePlugList);
        return RocResponse.success(result);
    }
}
