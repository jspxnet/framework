/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.lucene;

import com.github.jspxnet.utils.ObjectUtil;
import lombok.Data;

import java.util.Date;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-9
 * Time: 18:13:57
 */
@Data
public class LuceneVO implements Serializable {
    private String id;
    private String title;
    private String content;
    private Date createDate = new Date();
    private String nodeId;
    private String docType;
    private String other;
    private String domain;

    public void setId(Object id) {
        this.id = ObjectUtil.toString(id);
    }
}