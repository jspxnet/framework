package com.github.jspxnet.txweb.model.param;

import lombok.Data;
import java.io.Serializable;
import java.util.List;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2020/6/14 20:55
 * description: jspbox
 **/
@Data
public class TreeParam implements Serializable {
    private List<TreeItemParam> list;

}
