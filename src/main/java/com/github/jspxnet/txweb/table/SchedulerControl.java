package com.github.jspxnet.txweb.table;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.IDType;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.io.Serializable;

@EqualsAndHashCode(callSuper = false)
@Data
@Table(name = "jspx_scheduler_control", caption = "任务控制")
public class SchedulerControl implements Serializable {

    @Id(type = IDType.none)
    @Column(caption = "guid", notNull = true,length = 40)
    private String guid;

    //起个名字,如果为空 就是 methodName
    @Column(caption = "名词", notNull = true,length = 40)
    private String name;

    //时间表达式
    @Column(caption = "时间表达式", notNull = true,length = 20)
    private String pattern;

    //0 普通  1:系统级别
    @Column(caption = "时间表达式",option = " 0:普通;1:系统级别",notNull = true)
    private int taskType = 0;

    //是否只执行一次
    @Column(caption = "执行一次",enumType = YesNoEnumType.class,notNull = true)
    private int once = YesNoEnumType.NO.getValue();

    //延时多少秒执行
    @Column(caption = "延时多少秒执行", notNull = true)
    private int delayed = 0;

    //调用方法名称
    @Column(caption = "调用方法名称",length = 50, notNull = true)
    private String methodName;

    @Column(caption = "类名", length = 200, notNull = true)
    private String className;

    //运行次数
    @Column(caption = "运行次数", notNull = true)
    private int runTimes = 0;

    //运行状态
    @Column(caption = "运行次数", notNull = true)
    private int started = 0;
}
