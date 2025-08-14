package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.sober.table.SqlMapInterceptorConf;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.param.SqlMapInterceptorConfParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.devcenter.view.SqlMapInterceptorView;
import com.github.jspxnet.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@HttpMethod(caption = "SqlMap拦截器", actionName = "*", namespace = Environment.DEV_CENTER+"/sqlmap/interceptor")
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class SqlMapInterceptorAction extends SqlMapInterceptorView {

    @Operate(caption = "保存")
    public RocResponse<Long> save(@Param(caption="参数",required = true) SqlMapInterceptorConfParam param) {
        SqlMapInterceptorConf sqlMapInterceptorConf = BeanUtil.copy(param, SqlMapInterceptorConf.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            sqlMapInterceptorConf.setPutName(userSession.getName());
            sqlMapInterceptorConf.setPutUid(userSession.getUid());
        } else
        {
            sqlMapInterceptorConf.setPutName(Environment.SYSTEM_NAME);
            sqlMapInterceptorConf.setPutUid(Environment.SYSTEM_ID);
        }

        SqlMapConf sqlMapConfOld = sqlMapConfDAO.getSqlMap(param.getNamespace(),param.getName());
        if (sqlMapConfOld!=null)
        {
            sqlMapInterceptorConf.setVersion(sqlMapConfOld.getVersion()+1);
        }
        try {
            if (sqlMapInterceptorConf.getId()>0)
            {
                if (sqlMapConfDAO.update(sqlMapInterceptorConf) > 0) {
                    return RocResponse.success(sqlMapInterceptorConf.getId(),language.getLang(LanguageRes.updateSuccess));
                }
            }
            if (sqlMapConfDAO.save(sqlMapInterceptorConf) > 0) {
                return RocResponse.success(sqlMapInterceptorConf.getId(),language.getLang(LanguageRes.saveSuccess));
            }
        } catch (Exception e) {
            log.error("save",e);
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),e.getMessage());
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.saveFailure));
    }

    @Operate(caption = "删除")
    public RocResponse<Long[]> delete(@Param(caption = "id列表",required = true) Long[] ids)  {
        if (sqlMapConfDAO.delete(SqlMapInterceptorConf.class,ids,false)> 0) {
            return RocResponse.success(ids,language.getLang(LanguageRes.deleteSuccess));
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.deleteFailure));
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            sqlMapConfDAO.evict(SqlMapInterceptorConf.class);
        }
        return super.execute();
    }
}