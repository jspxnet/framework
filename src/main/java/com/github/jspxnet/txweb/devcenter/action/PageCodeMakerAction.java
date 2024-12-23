package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.PageCodeMakerParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.PageCodeMaker;
import com.github.jspxnet.txweb.table.PageCodeMakerVersion;
import com.github.jspxnet.txweb.devcenter.view.PageCodeMakerView;
import com.github.jspxnet.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@HttpMethod(caption = "页面数据", actionName = "*", namespace = Environment.DEV_CENTER+"/page/code")
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class PageCodeMakerAction  extends PageCodeMakerView {

    @Operate(caption = "编辑保存")
    public RocResponse<Long> save(@Param(caption="参数",required = true) PageCodeMakerParam param) {
        PageCodeMaker pageCodeMaker = BeanUtil.copy(param, PageCodeMaker.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            pageCodeMaker.setPutName(userSession.getName());
            pageCodeMaker.setPutUid(userSession.getUid());
        } else
        {
            pageCodeMaker.setPutName(Environment.SYSTEM_NAME);
            pageCodeMaker.setPutUid(Environment.SYSTEM_ID);
        }
        try {
            if (pageCodeMaker.getId()>0)
            {
                PageCodeMaker pageCodeMakerOld = genericDAO.get(PageCodeMaker.class,pageCodeMaker.getId());
                if (pageCodeMakerOld!=null)
                {
                    pageCodeMaker.setVersion(pageCodeMakerOld.getVersion()+1);
                    if (genericDAO.update(pageCodeMaker) > 0) {
                        PageCodeMakerVersion pageCodeMakerVersion = BeanUtil.copy(pageCodeMakerOld, PageCodeMakerVersion.class);
                        pageCodeMakerVersion.setId(0);
                        genericDAO.save(pageCodeMakerVersion);
                        return RocResponse.success(pageCodeMaker.getId(),language.getLang(LanguageRes.updateSuccess));
                    }
                }
            }
            if (genericDAO.save(pageCodeMaker) > 0) {
                return RocResponse.success(pageCodeMaker.getId(),language.getLang(LanguageRes.saveSuccess));
            }
        } catch (Exception e) {
            log.error("save",e);
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),e.getMessage());
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.saveFailure));
    }


    @Operate(caption = "删除")
    public RocResponse<Long[]> delete(@Param(caption = "id列表",required = true) Long[] ids)  {
        if (genericDAO.delete(PageCodeMaker.class,ids,false)>=0) {
            return RocResponse.success(ids,language.getLang(LanguageRes.deleteSuccess));
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.deleteFailure));
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            genericDAO.evict(PageCodeMaker.class);
        }
        return super.execute();
    }
}
