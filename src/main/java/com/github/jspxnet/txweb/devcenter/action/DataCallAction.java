package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.txweb.AssertException;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.devcenter.view.DataCallView;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ObjectUtil;
//dynout/call
@HttpMethod(caption = "数据接口", actionName = "*", namespace = Environment.DYN_OUT+"/call")
@Bean(namespace = Environment.DYN_OUT, singleton = true)
public class DataCallAction extends DataCallView {
    final public static String TABLE_MODELS = "TableModels";

    @Operate(caption = "编辑保存",method = "save")
    public RocResponse<Long> save()
    {

        String modelId = getString("modelId",true);
        TableModels tableModels = genericDAO.getAllTableModels(true).get(modelId);
        AssertException.isNull(tableModels,"不存在的模型对象");
        Object object = getBean(tableModels.getEntity());
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            if (tableModels.containsField("putName"))
            {
                BeanUtil.setSimpleProperty(object,"putName",userSession.getName());
            }
            if (tableModels.containsField("uid"))
            {
                BeanUtil.setSimpleProperty(object,"uid",userSession.getUid());
            }
        } else
        {
            if (tableModels.containsField("putName"))
            {
                BeanUtil.setSimpleProperty(object,"putName",Environment.GUEST_NAME);
            }
            if (tableModels.containsField("uid"))
            {
                BeanUtil.setSimpleProperty(object,"uid",Environment.GUEST_ID);
            }
        }
        if (tableModels.containsField("ip"))
        {
            BeanUtil.setSimpleProperty(object,"id",getRemoteAddr());
        }

        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.put(TABLE_MODELS,tableModels);
        long id = getLong("id",0);
        try {
            if (id>0)
            {
                Object old = genericDAO.get(tableModels.getEntity(),id);
                if (old!=null)
                {
                    if (tableModels.containsField("version"))
                    {

                        int version = ObjectUtil.toInt(BeanUtil.getProperty(old,"version"));
                        BeanUtil.setSimpleProperty(object,"version",(version+1));
                    }
                    if (genericDAO.update(object) > 0) {
                        return RocResponse.success(id,language.getLang(LanguageRes.updateSuccess));
                    }
                }
            }
            if (genericDAO.save(object) > 0) {
                id = ObjectUtil.toLong(BeanUtil.getProperty(object,"id"));
                return RocResponse.success(id,language.getLang(LanguageRes.saveSuccess));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),e.getMessage());
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.saveFailure));
    }


    @Operate(caption = "删除")
    public RocResponse<Long[]> delete(@Param(caption = "modelId",required = true,min = 10,max = 64) String modelId,
                                      @Param(caption = "ID",required = true) Long[] ids)  {
        TableModels tableModels = genericDAO.getAllTableModels(true).get(modelId);
        AssertException.isNull(tableModels,"不存在的模型对象");
        ActionContext actionContext = ThreadContextHolder.getContext();
        actionContext.put(TABLE_MODELS,tableModels);

        if (genericDAO.delete(tableModels.getEntity(),ids,false)>=0) {
            return RocResponse.success(ids,language.getLang(LanguageRes.updateSuccess));
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.updateFailure));
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            ActionContext actionContext = ThreadContextHolder.getContext();
            TableModels tableModels = (TableModels)actionContext.get(TABLE_MODELS);
            if (tableModels!=null)
            {
                genericDAO.evict(tableModels.getEntity());
            }
        }
        return super.execute();
    }

}
