package com.github.jspxnet.txweb.view;

import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.PromoteLinkDAO;
import com.github.jspxnet.txweb.model.param.PromoteLinkParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.PromoteLink;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.utils.BeanUtil;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/12/6 22:24
 * description:
 **/
public class PromoteLinkView extends ActionSupport {

    @Ref
    protected PromoteLinkDAO promoteLinkDAO;

    @Operate(caption = "功能描述", method = "index", post = false)
    public RocResponse<String> index() {
        return RocResponse.success("描述", "拉新分销");
    }

    @Operate(caption = "详细", method = "detail")
    public PromoteLink detail(@Param(caption = "id", min = 1, max = 32, required = true, message = "id不能为空") long id) {
        return promoteLinkDAO.get(PromoteLink.class, id);
    }

    @Operate(caption = "得到连接", method = "getlink")
    public RocResponse<String> getLink(@Param(caption = "id", min = 1, max = 32, required = true, message = "id不能为空") long id) {
        PromoteLink promoteLink = promoteLinkDAO.load(PromoteLink.class, id);
        if (promoteLink == null || promoteLink.getId() <= 0) {
            return RocResponse.error(ErrorEnumType.PARAMETERS);
        }
        String result = promoteLink.getUrl() + "?linkId=" + promoteLink.getId();
        return RocResponse.success(result);
    }
}



