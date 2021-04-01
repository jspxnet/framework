package com.github.jspxnet.txweb.model.vo;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by ChenYuan on 2017/7/9.
 */
@Data
@Table(caption = "钱和积分", create = false)
public class AmountPointsVo implements Serializable {

    @Column(caption = "金额")
    private double amount = 0;

    @Column(caption = "积分")
    private int points = 0;

    @Override
    public String toString() {
        return amount + ":" + points;
    }
}
