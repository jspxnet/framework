package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/4/27 9:29
 * description: 选项参数
 */
@Data
public class OptionBundleParam implements Serializable {
  
    @Param(caption = "ID")
    private long id;

    @Param(caption = "编码", max = 50)
    private String code = StringUtil.empty;

    //昵称，中文名称方式登录
    @Param(caption = "名称", max = 80)
    private String name = StringUtil.empty;

    @Param(caption = "描述", max = 200)
    private String description = StringUtil.empty;

    @Param(caption = "默认选择", max = 2, enumType = YesNoEnumType.class)
    private int selected = 0;

    @Param(caption = "排序", max = 1000)
    private int sortType = 0;

    @Param(caption = "命名空间", max = 50)
    private String namespace = StringUtil.empty;

    @Param(caption = "父编码")
    private String parentCode = StringUtil.empty;

    @Param(caption = "分组编码")
    private String groupCode;
}
