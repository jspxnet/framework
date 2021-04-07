/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.model.vo;

import com.github.jspxnet.json.JsonField;
import com.github.jspxnet.security.utils.EncryptUtil;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-9
 * Time: 15:05:09
 * 系统自动生成的VO对象,不需要保存到数据库,但提供保存到数据库的注释,
 * 构架会更具配置文件自动生成
 */
@Data
@Table(name = "jspx_operate_vo", caption = "TXWeb动作", create = false)
public class OperateVo implements Serializable {

    @Column(caption = "名称", length = 100, notNull = true)
    private String caption = StringUtil.empty;

    @Column(caption = "方法名称", length = 100, notNull = true)
    private String methodCaption = StringUtil.empty;

    @Column(caption = "类名", length = 200, notNull = true)
    private String className = StringUtil.empty;

    @Column(caption = "方法", length = 100, notNull = true)
    private String classMethod = StringUtil.empty;

    @Column(caption = "转发结点", length = 200, notNull = true)
    private String actionName = StringUtil.empty;

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @JsonField(name = "actionMethodId")
    public String getActionMethodId() {
        return TXWebUtil.getOperateMethodId(namespace, className, classMethod);
    }

    @JsonField(name = "id")
    public String getId() {
        return EncryptUtil.getMd5(TXWebUtil.getOperateMethodId(namespace, className, classMethod));
    }
}