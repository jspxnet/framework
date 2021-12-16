package com.github.jspxnet.component.k3cloud;

import lombok.Data;
import java.io.Serializable;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/11/29 21:49
 * description: thermo-model
 **/
@Data
public class KingdeeField implements Serializable {
    private String name;
    private String kingdeeField;
    private String field;
}
