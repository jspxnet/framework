package com.github.jspxnet.txweb.model.dto;

import com.github.jspxnet.sober.annotation.Column;
import lombok.Data;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 配置方式的页面权限控制，这个类载入url页面权限表，配置为yaml格式：
 * {@code
 *  guest:
 *   where: ${userType}<=0
 *   url:
 *     - untitled
 *     - /voluntary/untitled
 * }
 */

@Data
public class AccessUrlRuleDto implements Serializable {
    @Column(caption = "名称")
    private String name;

    //支持表达式
    @Column(caption = "条件")
    private String where;

    @Column(caption = "地址列表")
    private List<String> url = new ArrayList<>();
}
