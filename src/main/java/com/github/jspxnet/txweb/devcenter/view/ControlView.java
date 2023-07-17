package com.github.jspxnet.txweb.devcenter.view;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.PathVar;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.table.meta.ControlEvent;
import com.github.jspxnet.txweb.table.meta.ControlBase;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.dto.ControlDto;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.meta.ControlProperty;
import com.github.jspxnet.utils.BeanUtil;
import java.util.ArrayList;
import java.util.List;

public class ControlView extends ActionSupport {
    @Ref
    protected GenericDAO genericDAO;

    @Operate(caption = "详细",post = false, method = "detail/${id}")
    public RocResponse<ControlDto> detail(@PathVar(caption = "id", message = "id不能为空") long id) {
        ControlBase controlBase = genericDAO.load(ControlBase.class,id,true);
        RocResponse<ControlDto> rocResponse =  null;
        if (controlBase==null || controlBase.getId()<=0)
        {
            ControlDto dto = new ControlDto();
            dto.getPropertyList().add(new ControlProperty());
            dto.getEventList().add(new ControlEvent());
            rocResponse = RocResponse.success(dto);
        }
        else
        {
            rocResponse = RocResponse.success(BeanUtil.copy(controlBase, ControlDto.class));
        }
        return rocResponse;
    }

    @Operate(caption = "配置分页列表", method = "list/page")
    public RocResponse<List<ControlDto>> listPage(@Param(caption = "请求参数", required = true) PageParam pageParam) {
        int totalCount = genericDAO.getCount(ControlBase.class,pageParam);
        if (totalCount<=0)
        {
            return RocResponse.success(new ArrayList<>(),language.getLang(LanguageRes.notDataFind));
        }
        List<ControlBase> list = genericDAO.getList(ControlBase.class,pageParam);
        return RocResponse.success(BeanUtil.copyList(list, ControlDto.class),pageParam,totalCount);
    }

}
