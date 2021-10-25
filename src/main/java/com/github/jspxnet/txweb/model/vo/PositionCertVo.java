/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.model.vo;

import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenyuan on 14-3-14.
 * 统计证书的合格比例
 */
@Data
@Table(caption = "证书比例视图", create = false,cache = false)
public class PositionCertVo implements Serializable {
    public PositionCertVo() {

    }

    @JsonIgnore
    @Column(caption = "机构ID", length = 32)
    private String organizeId = StringUtil.empty;

    @Column(caption = "组织机构", length = 20)
    private String organize = StringUtil.empty;

    @Column(caption = "岗位", dataType = "isLengthBetween(0,200)", length = 200)
    private List<PositionStatVo> list = new ArrayList<>();

    public boolean containsPosition(String position) {
        for (PositionStatVo positionNum : this.list) {
            if (positionNum.getPosition().equals(position)) {
                return true;
            }
        }
        return false;
    }
}