package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.view.OperatePlugView;
import com.github.jspxnet.txweb.model.param.component.TableModelOperatePlugParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.txweb.table.meta.OperatePlug;
import com.github.jspxnet.utils.BeanUtil;
import java.util.List;

@HttpMethod(caption = "插件管理", actionName = "*", namespace = Environment.DEV_CENTER+"/operate/plug")
@Bean(singleton = true)
public class OperatePlugAction extends OperatePlugView {

    //id为0是保存
    @Operate(caption = "保存", method = "save")
    public RocResponse<?> save(@Param(caption = "参数",  required = true) TableModelOperatePlugParam param) throws Exception {
        if (isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        //先删除后在保存
        genericDAO.delete(OperatePlug.class,"tableName",param.getTableName());
        UserSession userSession = getUserSession();
        List<OperatePlug> operatePlugList =  BeanUtil.copyList(param.getOperatePlugList(),OperatePlug.class);
        for (OperatePlug operatePlug:operatePlugList)
        {
            operatePlug.setId(0);
            operatePlug.setTableName(param.getTableName());
            operatePlug.setIp(getRemoteAddr());
            operatePlug.setPutName(userSession.getName());
            operatePlug.setPutUid(userSession.getUid());
        }

        int x = genericDAO.save(operatePlugList);
        if (x>=0)
        {
            List<String> list =  BeanUtil.copyFieldList(operatePlugList,"id");
            return RocResponse.success(list,language.getLang(LanguageRes.saveSuccess));
        }
        return RocResponse.error(ErrorEnumType.DATABASE.getValue(), language.getLang(LanguageRes.saveFailure));
    }

    @Override
    public String execute() throws Exception {
        // clear缓存
        if (isMethodInvoked()) {
            genericDAO.evict(OperatePlug.class);
        }
        return super.execute();
    }
}
