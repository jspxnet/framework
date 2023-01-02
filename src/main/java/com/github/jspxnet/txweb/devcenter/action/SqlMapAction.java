package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sober.table.SqlMapConf;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.view.SqlMapView;
import com.github.jspxnet.txweb.model.param.SqlMapConfParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.utils.BeanUtil;

<<<<<<< HEAD
=======
import java.util.HashMap;
import java.util.Map;

>>>>>>> dev
/**
 * @author chenYuan
 *
 */
@HttpMethod(caption = "SqlMap配置", actionName = "*", namespace = Environment.DEV_CENTER+"/sqlmap/conf")
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class SqlMapAction extends SqlMapView {

    @Operate(caption = "保存")
    public RocResponse<Long> save(@Param(caption="参数",required = true) SqlMapConfParam param) {
        SqlMapConf sqlMapConf = BeanUtil.copy(param, SqlMapConf.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            sqlMapConf.setPutName(userSession.getName());
            sqlMapConf.setPutUid(userSession.getUid());
        } else
        {
            sqlMapConf.setPutName(Environment.SYSTEM_NAME);
            sqlMapConf.setPutUid(Environment.SYSTEM_ID);
        }

        SqlMapConf sqlMapConfOld = sqlMapConfDAO.getSqlMap(param.getNamespace(),param.getName());
        if (sqlMapConfOld!=null)
        {
            sqlMapConf.setVersion(sqlMapConfOld.getVersion()+1);
        }
        try {
            if (sqlMapConf.getId()>0)
            {
                if (sqlMapConfDAO.update(sqlMapConf) > 0) {
                    return RocResponse.success(sqlMapConf.getId(),language.getLang(LanguageRes.updateSuccess));
                }
            }
            if (sqlMapConfDAO.save(sqlMapConf) > 0) {
                return RocResponse.success(sqlMapConf.getId(),language.getLang(LanguageRes.saveSuccess));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),e.getMessage());
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.saveFailure));
    }

    @Operate(caption = "删除")
    public RocResponse<Long[]> delete(@Param(caption = "id列表",required = true) Long[] ids)  {
        if (sqlMapConfDAO.delete(SqlMapConf.class,ids,false)> 0) {
            return RocResponse.success(ids,language.getLang(LanguageRes.deleteSuccess));
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.deleteFailure));
    }

<<<<<<< HEAD
=======
    @Operate(caption = "测试get方法",post = false,method = "testget")
    public RocResponse<Map<String,Object>> testget(@Param(caption = "id列表",required = true) Long[] ids,@Param(caption = "id列表") String id)  {
        Map<String,Object> result = new HashMap<>();
        result.put("ids",ids);
        result.put("id",id);
        return RocResponse.success(result);
    }

>>>>>>> dev
    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            sqlMapConfDAO.evict(SqlMapConf.class);
        }
        return super.execute();
    }
}
