package com.github.jspxnet.txweb.annotation;

import com.github.jspxnet.sober.annotation.NullClass;
import com.github.jspxnet.sober.enums.ParamModeType;
import com.github.jspxnet.txweb.enums.SafetyEnumType;
import com.github.jspxnet.utils.StringUtil;

import java.lang.annotation.*;

/**
 *
 * @author chenYuan
 *
 * */
@Documented
@Target({ElementType.PARAMETER, ElementType.TYPE,ElementType.METHOD,ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Param {
    String caption() default StringUtil.empty;

    //只对integer long 类型有效
    long min() default -1;

    //只对integer long 类型有效
    long max() default Long.MAX_VALUE;

    //错误的时候显示的提示信息
    String message() default StringUtil.empty;

    //分三个安全等级,1:表示基本的特殊字符<>,2:表示特殊的sql关键字和html特殊脚本;3:html中的脚本和事件
    //上一级包含了下一级;例如3里边包含了2和1
    //没有判断到的特殊支付可以中evasive里边配置,统一过滤
    //0就不检查了
    SafetyEnumType level() default SafetyEnumType.LOW;

    //参数枚举字符串，只允许是这里边的getValue
    Class<?> enumType() default NullClass.class;

    //是否必须
    boolean required() default false;

    //是否接受请求方式的参数，false 将不会接收外部请求进入的参数
    boolean request() default true;

    //参数对象,主要用于 签名参数解析,和文档生成
    Class<?> type() default  NullClass.class;

    //参数进入模式
    ParamModeType modeType() default ParamModeType.RocMode;
    /**
     *  当参数中不存在或者为空的时候载入
     *
     * 数字,字符串直接采用字符串表示
     * List格式: ["one","two","three"]
     * array格式:["str1","str2","str3",...]
     * map格式:{ 1=聊天室,  2=公告,  3=应用提示, -1=未知,  4=草稿,  5=收件箱,  6=已发邮件,  7=垃圾箱,  8=管理员消息,  9=IM私人消息}
     * 如果是对象:用json格式
     * @return  默认值
     */
    String value() default StringUtil.empty;
}
