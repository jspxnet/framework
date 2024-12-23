package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.BeanFactory;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.sober.SoberFactory;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.PathVar;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.PageCodeMaker;
import java.util.ArrayList;
import java.util.List;


public class PageCodeMakerView extends ActionSupport {

    protected GenericDAO genericDAO;

    @Ref
    public void setGenericDAO(GenericDAO genericDAO)
    {
        this.genericDAO = genericDAO;
        //begin 载入默认数据源
        if (genericDAO.getSoberFactory() == null) {
            String soberFactoryName = EnvFactory.getEnvironmentTemplate().getString("soberFactory", "jspxSoberFactory");
            BeanFactory beanFactory = EnvFactory.getBeanFactory();
            SoberFactory soberFactory = (SoberFactory) beanFactory.getBean(soberFactoryName);
            genericDAO.setSoberFactory(soberFactory);
        }
        //end 载入默认数据源
    }

    @Operate(caption = "构建页面", method = "index", post = false)
    public String index()  {
        return "构建页面";
    }

    @Operate(caption = "帮助列表", method = "list/page")
    public RocResponse<List<PageCodeMaker>> getListPage(@Param(caption = "翻页参数") PageParam pageParam) {
        long totalCount = genericDAO.getCount(PageCodeMaker.class,pageParam);
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
        }
        return RocResponse.success(genericDAO.getList(PageCodeMaker.class,pageParam), pageParam, totalCount);
    }

    @Operate(caption = "构建", method = "detail/${id}",post = false)
    public RocResponse<PageCodeMaker> detail(@PathVar(caption = "id") long id) {
        return RocResponse.success(genericDAO.load(PageCodeMaker.class,id));
    }

    public PageCodeMaker getLastVersion(String urlId,String namespace) {
        return genericDAO.createCriteria(PageCodeMaker.class)
                .add(Expression.eq("namespace", namespace))
                .add(Expression.eq("urlId", urlId))
                .addOrder(Order.desc("version")).objectUniqueResult(false);
    }

}