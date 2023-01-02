package com.github.jspxnet.txweb.model.param.component;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

@Data
public class ControlPropertyParam implements Serializable {

        @Param(caption = "ID")
        private long id;

        @Param(caption = "分组", max = 30)
        private String groupName = StringUtil.empty;
        //控件名称
        @Param(caption = "名称", max = 50)
        private String name = StringUtil.empty;

        @Param(caption = "属性名称", max = 100)
        private String propertyName = StringUtil.empty;

        @Param(caption = "值域", max = 200)
        private String propertyRange = StringUtil.empty;

        @Param(caption = "默认值", max = 100)
        private String propertyDef = StringUtil.empty;

        @Param(caption = "说明", max = 250)
        private String desc = StringUtil.empty;

}
