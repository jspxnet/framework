package com.github.jspxnet.txweb.view;


import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ValidatorAction;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by yuan on 2015/4/1 0001.
 * 这里是提供给前台页面的验证入口,
 * action名称就是验证的formId
 */
@Bean(bind = ValidatorView.class)
@HttpMethod(caption = "URL数据验证")
public class ValidatorView extends ValidatorAction {
    @Override
    public String execute() throws Exception {
        String url = request.getRequestURI();
        if (StringUtil.countMatches(url, "/") < 2) {
            print(RocResponse.error(ErrorEnumType.PARAMETERS.getValue(), "验证配置不能找到"));
            return NONE;
        }
        String formId = getEnv(ActionEnv.Key_ActionName);
        super.setId(formId);
        return super.execute();
    }
}
