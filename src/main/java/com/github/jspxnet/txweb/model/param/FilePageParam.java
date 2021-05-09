package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/5/3 14:47
 * description: jspbox
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class FilePageParam extends PageParam {
    @Param(caption = "文件类型列表")
    private String[] fileTypes;
}
