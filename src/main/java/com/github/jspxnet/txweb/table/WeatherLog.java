package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.DateUtil;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

@Data
@Table(name = "jspx_weather_log", caption = "天气记录")
public class WeatherLog implements Serializable {

    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "城市id",length = 32, notNull = true)
    private String cityId;

    @Column(caption = "城市",length = 50, notNull = true)
    private String city;

    @Column(caption = "天气",length = 80, notNull = true)
    private String weather;

    @Column(caption = "最低温度",notNull = true)
    private int lowTemp;

    @Column(caption = "最高温度",notNull = true)
    private int heightTemp;

    //方便查询
    @Column(caption = "日期字符串",length = 15)
    private String dateStr= DateUtil.getDateST();

    @Column(caption = "创建时间",notNull = true)
    private Date createDate = new Date();
}
