/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.annotation;

/*
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-5
 * Time: 21:24:46
 *
    表示让数据库自动生成ID
    @Id

    表示让sober来生成数字ID
    @Id(auto=true,type = IDType.seq)

    标识sober自动生成字符串ID
    @Id(auto=true,type = IDType.uuid)

     主要是用在订单
    seq:系统默认生成 yyyyMMddhhss + 序列,类型可以是long 和 String
    seq:的时候使用下边的配置创建系列

    是用在资源定位，或者不希望别人能够推算出其他地址
    uuid:为long类型使用 UUID getMostSignificantBits 生成

    //数据库默认，是用在对内的，数据处理上
    serial:数据库 自动增加

    果 auto=false 就会 type＝serial 更具数据库得到
    auto=true 如果里边已经有值，将不创建id,否则更具type生成
*
*/

import com.github.jspxnet.utils.DateUtil;

import java.lang.annotation.Target;
import java.lang.annotation.Retention;
import java.lang.annotation.ElementType;
import java.lang.annotation.RetentionPolicy;

@Target({ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Id {
    //是否sober生成ID
    boolean auto() default false;

    String type() default IDType.serial;

    //数字不要超16，js里边容易错误
    int length() default 17;  //max 19

    long max() default Integer.MAX_VALUE;

    int min() default 0;

    int next() default 1;

    boolean dateStart() default false;

    String dateFormat() default DateUtil.DAY_NUMBER_FORMAT;

    //添加硬件区别
    boolean mac() default false;
}