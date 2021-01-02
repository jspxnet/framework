package com.github.jspxnet.txweb.model.param;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.io.Serializable;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/3/3 0:24
 * description: 分页参数
 *
 * @author chenyuan
 **/
@Data
public class PageParam implements Serializable  {
    @Param(caption = "查询字段",max = 30)
    private String[] field;

    @Param(caption = "查询关键字",max = 30)
    private String[] find;

    @Param(caption = "排序方式",min = 3,max = 30)
    private String sort;

    @Param(caption = "命名空间",min = 1,max = 60)
    private String namespace;

    @Param(caption = "行数",min = 1,max = 5000,value = "12")
    private Integer count;

    @Param(caption = "当前页数",max = 5000,value = "1")
    private Integer currentPage;

    @Param(caption = "用户ID")
    private long uid;

    @Param(caption = "父ID")
    private long pid = 0;

    @Param(caption = "条件",max = 100)
    private String term = StringUtil.empty;

}
