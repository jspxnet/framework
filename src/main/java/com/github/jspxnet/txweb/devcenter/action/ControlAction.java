package com.github.jspxnet.txweb.devcenter.action;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.model.dto.ControlDto;
import com.github.jspxnet.txweb.table.meta.ControlEvent;
import com.github.jspxnet.txweb.table.meta.ControlBase;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.meta.ControlProperty;
import com.github.jspxnet.txweb.devcenter.view.ControlView;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.txweb.model.param.component.ControlParam;
import com.github.jspxnet.utils.ObjectUtil;
import java.util.List;

//@HttpMethod(caption = "基础控件(目前不是用)", actionName = "*", namespace = Environment.DEV_CENTER+"/control")
//@Bean(singleton = true)

/**
 * 作为地带吗扩展功能,目前是用底代码vform 这里基本不需要
 */
public class ControlAction extends ControlView {

    @Operate(caption = "删除控件", method = "delete")
    public RocResponse<Integer> delete(@Param(caption = "日志参数", required = true) long[] ids)  {
        if (isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        int i = 0;
        for (long id:ids)
        {
            i = genericDAO.delete(ControlBase.class,id,true) + i;
        }
        return RocResponse.success(i);
    }

    //id为0是保存
    @Operate(caption = "保存", method = "save")
    public RocResponse<?> save(@Param(caption = "参数",  required = true) ControlParam param) throws Exception {
        if (isGuest()) {
            return RocResponse.error(ErrorEnumType.NEED_LOGIN);
        }
        ControlBase control = BeanUtil.copy(param, ControlBase.class);
        long id = param.getId();
        List<ControlProperty> controlPropertyList = control.getPropertyList();
        for (ControlProperty controlProperty : controlPropertyList)
        {
            controlProperty.setName(control.getName());
        }
        List<ControlEvent> controlEventList = control.getEventList();
        for (ControlEvent controlEvent : controlEventList)
        {
            controlEvent.setName(control.getName());
        }
        if (id==0)
        {
            int x = genericDAO.save(control,true);
            if (x>=0)
            {
                return RocResponse.success(control.getId(),language.getLang(LanguageRes.saveSuccess));
            }
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(), language.getLang(LanguageRes.saveFailure));
        } else
        {
            int x = genericDAO.update(control);
            if (x>=0)
            {
                return RocResponse.success(control.getId(),language.getLang(LanguageRes.updateSuccess));
            }
            return RocResponse.error(ErrorEnumType.DATABASE.getValue(), language.getLang(LanguageRes.updateFailure));
        }
    }

    @Override
    public String execute() throws Exception {
        // clear缓存
        if (isMethodInvoked()) {
            genericDAO.evict(ControlBase.class);
        }
        return super.execute();
    }

    public static void main(String[] args) {

        ControlDto dto = new ControlDto();
        RocResponse rocResponse = RocResponse.success(dto);
        System.out.println(ObjectUtil.getJson(rocResponse));

                /*
        String jsonStr = "{\"id\":1,\"name\":\"浏览帮助提示\",\"caption\":\"111\",\"desc\":\"222\",\"demo\":\"222\",\"propertyList\":[{\"name\":\"\",\"groupName\":\"a1\",\"propertyName\":\"11\",\"propertyValue\":\"22\",\"propertyDef\":\"33\",\"desc\":\"44\"}],\"eventList\":[{\"name\":\"\",\"eventName\":\"b1\",\"eventCaption\":\"22\",\"template\":\"\",\"desc\":\"444\"}],\"sortType\":0,\"sortDate\":\"2022-12-05 21:26:58\",\"createDate\":\"2022-12-05 21:40:26\"}";
        JSONObject json = new JSONObject(jsonStr);
        ControlParam param = json.parseObject(ControlParam.class);

        ControlBase control = BeanUtil.copy(param, ControlBase.class);

        List<ControlProperty> controlPropertyList = control.getPropertyList();
        for (ControlProperty controlProperty : controlPropertyList)
        {
            controlProperty.setName(control.getName());
        }
        List<ControlEvent> controlEventList = control.getEventList();
        for (ControlEvent controlEvent : controlEventList)
        {
            controlEvent.setName(control.getName());
        }

        System.out.println(ObjectUtil.toString(control));*/


    }
}
