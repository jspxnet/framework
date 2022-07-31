package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.enums.QueryModelEnumType;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.util.SoberUtil;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.PathVar;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.model.param.GenericPageParam;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.HelpTip;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ObjectUtil;

import java.util.ArrayList;
import java.util.List;


public class DataCallView extends ActionSupport {
    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "数据列表", method = "list/page")
    public RocResponse<List<?>> getListPage(@Param(caption = "翻页参数") GenericPageParam pageParam) {
        TableModels tableModels = genericDAO.getAllTableModels(true).get(pageParam.getModelId());
        AssertException.isNull(tableModels, "不存在的模型对象");
        long totalCount = genericDAO.getCount(tableModels.getEntity(), pageParam);
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
        }
        return RocResponse.success(genericDAO.getList(tableModels.getEntity(), pageParam), pageParam, totalCount);
    }

    @Operate(caption = "得到详细", method = "detail/${modelId}/${id}")
    public RocResponse<?> detail(@PathVar(caption = "modelId") String modelId, @PathVar(caption = "id") Long id) {
        TableModels tableModels = genericDAO.getAllTableModels(true).get(modelId);
        AssertException.isNull(tableModels, "不存在的模型对象");
        return RocResponse.success(genericDAO.load(tableModels.getEntity(), id));
    }

    @Operate(caption = "得到详细", method = "sqlmap")
    public RocResponse<?> sqlmap(@Param(caption = "查询参数") JSONObject json) {
        String namespace = json.getString("namespace");
        AssertException.isNull(namespace, "参数:namespace 必须填写");
        String exeName = json.getString("exeName");
        AssertException.isNull(exeName, "参数:exeName 配置的SqlMap名称必须填写");
        SqlMapConf sqlMapConf;
        try {
            sqlMapConf = SoberUtil.getSqlMapConf(genericDAO.getSoberFactory(), namespace, exeName);
            return RocResponse.success(SoberUtil.invokeSqlMapInvocation(genericDAO, sqlMapConf, json));
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.WARN.getValue(), e.getMessage());
        }
    }

    @Operate(caption = "得到详细", method = "sqlmap/page")
    public RocResponse<?> sqlMapPage(@Param(caption = "查询参数") JSONObject json) {
        String namespace = json.getString("namespace");
        AssertException.isNull(namespace, "参数:namespace 必须填写");
        String exeName = json.getString("exeName");
        AssertException.isNull(exeName, "参数:exeName 配置的SqlMap名称必须填写");
        try
        {
            long totalCount = 0;
            if (SoberUtil.containsSqlMapConf(namespace,exeName+"_count"))
            {
                SqlMapConf sqlMapConfCount = SoberUtil.getSqlMapConf(genericDAO.getSoberFactory(), namespace, exeName+"_count");
                totalCount = ObjectUtil.toLong(SoberUtil.invokeSqlMapInvocation(genericDAO, sqlMapConfCount, json));
                if (totalCount <= 0) {
                    return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
                }
            } else
            {
                totalCount = genericDAO.buildSqlMap().queryCount(namespace,exeName,json);
                if (totalCount <= 0) {
                    return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
                }
            }
            SqlMapConf sqlMapConfList = SoberUtil.getSqlMapConf(genericDAO.getSoberFactory(), namespace, exeName);
            Object list = SoberUtil.invokeSqlMapInvocation(genericDAO, sqlMapConfList, json);
            PageParam pageParam = json.parseObject(PageParam.class);
            return RocResponse.success(list, pageParam, totalCount);
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.WARN.getValue(), e.getMessage());
        }
    }
}
