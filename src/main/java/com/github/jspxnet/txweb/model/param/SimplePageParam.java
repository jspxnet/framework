package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.txweb.annotation.Param;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/4/26 23:39
 * description: 简单翻页参数
 **/
@Data
public class SimplePageParam implements Serializable {
    @Param(caption = "查询关键字",max = 30)
    private String find;

    @Param(caption = "排序方式",max = 30)
    private String sort;

    @Param(caption = "命名空间",max = 30)
    private String namespace;

    @Param(caption = "行数",value = "10")
    private int count;

    @Param(caption = "当前页数",max = 1000,value = "1")
    private int currentPage;
}
