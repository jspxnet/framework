package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.scriptmark.load.InputStreamSource;
import com.github.jspxnet.scriptmark.load.Source;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.devcenter.codemaker.impl.CodeMakerImpl;
import com.github.jspxnet.txweb.devcenter.view.UiTemplateView;
import com.github.jspxnet.txweb.model.param.UiTemplateParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.UiTemplate;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.BeanUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@HttpMethod(caption = "UI模板", actionName = "*", namespace = Environment.DEV_CENTER+"/ui/template")
@Bean(namespace = Environment.DEV_CENTER, singleton = true)
public class UiTemplateAction extends UiTemplateView {


    @Operate(caption = "编辑保存")
    public RocResponse<Long> save(@Param(caption="参数",required = true) UiTemplateParam param) {
        UiTemplate uiTemplate = BeanUtil.copy(param, UiTemplate.class);
        IUserSession userSession = getUserSession();
        if (userSession != null) {
            uiTemplate.setPutName(userSession.getName());
            uiTemplate.setPutUid(userSession.getUid());
        } else
        {
            uiTemplate.setPutName(Environment.SYSTEM_NAME);
            uiTemplate.setPutUid(Environment.SYSTEM_ID);
        }
        try {
            if (uiTemplate.getId()>0)
            {
                UiTemplate old = genericDAO.get(UiTemplate.class,uiTemplate.getId());
                if (old!=null)
                {
                    uiTemplate.setVersion(old.getVersion()+1);
                    if (genericDAO.update(uiTemplate) > 0) {
                        return RocResponse.success(uiTemplate.getId(),language.getLang(LanguageRes.updateSuccess));
                    }
                }
            }
            if (genericDAO.save(uiTemplate) > 0) {
                return RocResponse.success(uiTemplate.getId(),language.getLang(LanguageRes.saveSuccess));
            }
        } catch (Exception e) {
            log.error("save",e);
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(),e.getMessage());
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.saveFailure));
    }

    @Operate(caption = "载入默认ui",method = "loadui")
    public RocResponse<Long[]> loadUi()
    {
        Map<String,String> templateList = new HashMap<>();
        templateList.put("aeui.ftl","添加编辑");
        templateList.put("acui.ftl","浏览页面");
        templateList.put("list.ftl","列表页面");


        Long[] result = null;
        try {
            for (String name:templateList.keySet())
            {
                CodeMakerImpl.class.getResourceAsStream(name);
                Source reader = new InputStreamSource(CodeMakerImpl.class.getResourceAsStream(name), name, Environment.defaultEncode);
                UiTemplate template = new UiTemplate();
                template.setIp("127.0.0.1");
                template.setPutName(Environment.SYSTEM_NAME);
                template.setPutUid(Environment.SYSTEM_ID);
                template.setName(templateList.getOrDefault(name,"none"));
                template.setContent(reader.getSource());
                genericDAO.save(template);
                result = ArrayUtil.add(result,template.getId());
            }
        } catch (Exception e) {
            log.error("loadUi",e);
            return RocResponse.error(ErrorEnumType.WARN.getValue(),e.getMessage());
        }
        return RocResponse.success(result,language.getLang(LanguageRes.saveSuccess));
    }


    @Operate(caption = "删除")
    public RocResponse<Long[]> delete(@Param(caption = "ID",required = true) Long[] ids)  {
        if (genericDAO.delete(UiTemplate.class,ids,false)> 0) {
            return RocResponse.success(ids,language.getLang(LanguageRes.updateSuccess));
        }
        return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.updateFailure));
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            genericDAO.evict(UiTemplate.class);
        }
        return super.execute();
    }
}
