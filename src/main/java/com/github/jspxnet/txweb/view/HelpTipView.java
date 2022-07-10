package com.github.jspxnet.txweb.view;

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
import com.github.jspxnet.txweb.table.HelpTip;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyuan on 15-4-1.
 * 帮助接口提供resetful方式
 * 格式标准：
 *devcenter/help/
 */
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class HelpTipView extends ActionSupport {

    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "文档索引", method = "index", post = false)
    public String index()  {
        return "帮助";
    }

    @Operate(caption = "帮助列表", method = "list/page")
    public RocResponse<List<HelpTip>> getListPage(@Param(caption = "翻页参数") PageParam pageParam) {
        long totalCount = genericDAO.getCount(HelpTip.class,pageParam);
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(0), language.getLang(LanguageRes.notDataFind));
        }
        return RocResponse.success(genericDAO.getList(HelpTip.class,pageParam), pageParam, totalCount);
    }

    @Operate(caption = "得到帮助正文", method = "detail/${id}",post = false)
    public RocResponse<HelpTip> detail(@PathVar(caption = "id") long id) {
        return RocResponse.success(genericDAO.load(HelpTip.class,id));
    }

    @Operate(caption = "得到帮助正文", method = "helptip")
    public RocResponse<HelpTip> getHelpTip(@Param(caption = "id") long id) {
        return RocResponse.success(genericDAO.load(HelpTip.class,id));
    }
}
