package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.HelpTipParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.HelpTip;
import com.github.jspxnet.txweb.view.HelpTipView;
import com.github.jspxnet.utils.BeanUtil;

/**
 *
 */
@HttpMethod(caption = "帮助", actionName = "*", namespace = Environment.DEV_CENTER+"/help")
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class HelpTipAction extends HelpTipView {

    @Operate(caption = "编辑保存")
    public RocResponse<Long> save(@Param(caption="参数",required = true)  HelpTipParam param) {
        HelpTip helpTip = BeanUtil.copy(param, HelpTip.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            helpTip.setPutName(userSession.getName());
            helpTip.setPutUid(userSession.getUid());
        } else
        {
            helpTip.setPutName(Environment.SYSTEM_NAME);
            helpTip.setPutUid(Environment.SYSTEM_ID);
        }
        try {
            if (helpTip.getId()>0)
            {
                HelpTip helpTipOld = genericDAO.get(HelpTip.class,helpTip.getId());
                if (helpTipOld!=null)
                {
                    helpTip.setVersion(helpTipOld.getVersion()+1);
                    if (genericDAO.update(helpTip) > 0) {
                        return RocResponse.success(helpTip.getId(),language.getLang(LanguageRes.updateSuccess));
                    }
                }
            }
            if (genericDAO.save(helpTip) > 0) {
                return RocResponse.success(helpTip.getId(),language.getLang(LanguageRes.saveSuccess));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),e.getMessage());
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.saveFailure));
    }


    @Operate(caption = "删除")
    public RocResponse<Long[]> delete(@Param(caption = "ID",required = true) Long[] ids)  {
        if (genericDAO.delete(HelpTip.class,ids,false)> 0) {
            return RocResponse.success(ids,language.getLang(LanguageRes.updateSuccess));
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.updateFailure));
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            genericDAO.evict(HelpTip.class);
        }
        return super.execute();
    }
}
