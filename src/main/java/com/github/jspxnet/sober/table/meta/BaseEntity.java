package com.github.jspxnet.sober.table.meta;


import com.github.jspxnet.sober.TableModels;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

public class BaseEntity implements Serializable {
    //bean的结构模型
    @Setter
    @Getter
    private transient TableModels tableModels;

}
