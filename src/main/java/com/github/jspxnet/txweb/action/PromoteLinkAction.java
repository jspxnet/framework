package com.github.jspxnet.txweb.action;

import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.PromoteLinkParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.PromoteLink;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.txweb.view.PromoteLinkView;
import com.github.jspxnet.utils.BeanUtil;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/6 22:05
 * description:
 **/

//@HttpMethod(caption = "分享连接", actionName = "*", namespace = EnvNamespace.JCOMMON + "/promote/link")
@Bean(singleton = true)
public class PromoteLinkAction extends PromoteLinkView {

    @Operate(caption = "创建分享",method = "create")
    //@Describe("创建分享数据,分享带这个id")
    public RocResponse<PromoteLink> create(@Param(caption = "参数",required = true) PromoteLinkParam params) throws Exception {
        UserSession userSession = getUserSession();
        PromoteLink promoteLink = BeanUtil.copy(params, PromoteLink.class);
        promoteLink.setPutName(userSession.getName());
        promoteLink.setPutUid(userSession.getUid());
        promoteLink.setIp(getRemoteAddr());
        promoteLinkDAO.save(promoteLink);
        return RocResponse.success(promoteLink);
    }

    @Operate(caption = "创建分享连接",method = "crate/link")
    //@Describe("分享的时候创建分享数据返回一个连接")
    public RocResponse<String> createLink(@Param(caption = "参数",required = true) PromoteLinkParam params) throws Exception {
        UserSession userSession = getUserSession();
        PromoteLink promoteLink = BeanUtil.copy(params, PromoteLink.class);
        promoteLink.setPutName(userSession.getName());
        promoteLink.setPutUid(userSession.getUid());
        promoteLink.setIp(getRemoteAddr());
        promoteLinkDAO.save(promoteLink);
        String result = promoteLink.getUrl() + "?linkId=" + promoteLink.getId();
        return RocResponse.success(result);
    }

}
