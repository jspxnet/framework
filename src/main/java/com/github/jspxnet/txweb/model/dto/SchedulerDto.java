package com.github.jspxnet.txweb.model.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class SchedulerDto implements Serializable {

    private String guid;

    //起个名字,如果为空 就是 methodName
    private String name;

    //时间表达式
    private String pattern;

    //0 普通  1:系统级别
    private int taskType = 0;

    //是否只执行异常
    private int once = 0;

    //延时多少秒执行
    private int delayed = 0;

    //调用方法名称
    private String methodName;

    private String className;

    //运行次数
    private int runTimes = 0;

    //运行状态
    private int started = 0;



}
