package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by ChenYuan on 2017/6/15.
 */
@Data
@Table(name = "jspx_evasive_ip", caption = "登录历史记录表")
public class EvasiveIp implements Serializable {
    //id 保存 ip long
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "IP", length = 36, dataType = "isLengthBetween(2,36)", notNull = true)
    private String ip = null;

    @Column(caption = "次数", notNull = true)
    private int times = 0;

    @Column(caption = "监禁时间", defaultValue = "1000")
    private int imprisonSecond = 60;

    @Column(caption = "创建时间", notNull = true)
    private long createTimeMillis = System.currentTimeMillis();

    @Column(caption = "返回", length = 50)
    private String result = "";


    public int updateTimes() {
        return this.times++;
    }

}
