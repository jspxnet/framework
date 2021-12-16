package com.github.jspxnet.component.juhe.dto;

import com.github.jspxnet.sober.annotation.Column;
import lombok.Data;

import java.io.Serializable;

/**
 * 聚合天气传过来的个数
 */
@Data
public class WeatherInfoDto implements Serializable {

    @Column(caption = "温度")
    private String temperature;
    @Column(caption = "天气描述")
    private String weather;
    @Column(caption = "风")
    private String wind;
    @Column(caption = "日期")
    private String date;
    @Column(caption = "星期")
    private String week;
}
