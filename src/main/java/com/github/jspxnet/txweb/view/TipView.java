package com.github.jspxnet.txweb.view;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.model.dto.TipDto;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.util.TipUtil;
import com.github.jspxnet.utils.ObjectUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.List;


@Slf4j
@HttpMethod(caption = "提示信息", actionName = "*", namespace = Environment.Global + "/tip")
@Bean(singleton = true)
public class TipView extends ActionSupport {
    @Operate(caption = "得到单个的提示", post = false, method = "single")
    public RocResponse<TipDto> getSingleTip(@Param(caption = "提示ID", required = true,min = 2,max = 50) String tipId)
    {
        return RocResponse.success(TipUtil.getSingleTip(tipId));
    }
    @Operate(caption = "得到单个的提示", post = false, method = "list")
    public RocResponse<List<TipDto>> getTipList(@Param(caption = "提示ID", required = true,min = 2,max = 50) String tipId)
    {
        return RocResponse.success(TipUtil.getTipList(tipId));
    }

}
