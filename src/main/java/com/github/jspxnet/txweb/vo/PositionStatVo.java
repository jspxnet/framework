/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.vo;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;

/**
 * Created by yuan on 14-3-27.
 */
@Data
@Table(caption = "岗位统计", create = false)
public class PositionStatVo {
    public PositionStatVo() {

    }

    @Column(caption = "岗位名称", dataType = "isLengthBetween(2,50)", length = 50)
    private String position;

    @Column(caption = "在岗人数")
    private int userNum = 0;

    @Column(caption = "有效证件人数")
    private int effectiveNum = 0;
}