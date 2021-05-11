package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2021/5/11 23:32
 * @description: jspbox
 **/
@Data
public abstract class BaseParam implements Serializable {

    @Param(caption = "编辑的字段列表,空标识所有")
    private String[] updateFields = null;
}
