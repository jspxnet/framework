package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.PathVar;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.UiTemplate;
import java.util.ArrayList;
import java.util.List;


@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class UiTemplateView extends ActionSupport {

    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "UI模板", method = "index", post = false)
    public String index()  {
        return "帮助";
    }

    @Operate(caption = "列表", method = "list/page")
    public RocResponse<List<UiTemplate>> getListPage(@Param(caption = "翻页参数") PageParam pageParam) {
        long totalCount = genericDAO.getCount(UiTemplate.class,pageParam);
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
        }
        return RocResponse.success(genericDAO.getList(UiTemplate.class,pageParam), pageParam, totalCount);
    }

    @Operate(caption = "详细", method = "detail/${id}",post = false)
    public RocResponse<UiTemplate> detail(@PathVar(caption = "id") long id) {
        return RocResponse.success(genericDAO.load(UiTemplate.class,id));
    }
}
