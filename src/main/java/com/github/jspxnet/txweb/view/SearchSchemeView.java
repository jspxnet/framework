package com.github.jspxnet.txweb.view;


import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.config.SoberColumn;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.model.dto.SearchFieldDto;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.SearchScheme;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.utils.BeanUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通用搜索，返回包括
 * 1.保存的搜索条件列表 SearchScheme,
 * 2.单据的字段列表
 * 3.字段的枚举类
 * @author chenYuan
 *
 */
public class SearchSchemeView extends ActionSupport {
    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "得到选项列表")
    public RocResponse<Map<String,Object>> range(@Param(caption = "单据ID",required = true) String table) {
        if (isGuest())
        {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        UserSession userSession = getUserSession();
        List<SearchScheme> list = genericDAO.createCriteria(SearchScheme.class).add(Expression.or(Expression.eq("putUid", userSession.getUid()),
                Expression.and(Expression.eq("share", YesNoEnumType.YES.getValue()),
                        Expression.or(Expression.like("shareUser", "%" + userSession.getUid() + "%"),
                        Expression.isNull("shareUser")))
                )).setCurrentPage(1).setTotalCount(20).list(false);


        TableModels tableModels = genericDAO.getTableModels(table);
        Map<String,Object> result =  new HashMap<>();
        result.put("searchStore",list);


        Map<String,Object> enumResult =  new HashMap<>();

        List<SoberColumn> columns = tableModels.getColumns();
        for (SoberColumn column:columns)
        {
            SearchFieldDto dto = BeanUtil.copy(column, SearchFieldDto.class);
            JSONArray jsonArray = genericDAO.getFieldEnumType(table,column.getName());
            dto.setOptionEnum(jsonArray);

            enumResult.put(column.getName() +"EnumType",dto);
        }
        result.put("columns",columns);

        result.put("enumTypes",enumResult);
        return RocResponse.success(result);
    }
}
