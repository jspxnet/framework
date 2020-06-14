/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-10-19
 * Time: 10:55:22
 * 搜索文章,可以得到相关文章. 然后在查询得到 本库手工信息后得到连接或者说明文字
 * 计算各个关键词的关联度
 */
@Data
@Table(name = "jspx_tags", caption = "标签词库表")
public class TagWord implements Serializable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "词语", length = 30, notNull = true)
    private String words = StringUtil.empty;

    @Column(caption = "父词", length = 30, notNull = false)
    private String parentWord = StringUtil.empty;

    @Column(caption = "关联数", notNull = true)
    private int correlative = 0;

    @Column(caption = "关注度", notNull = true)
    private int attention = 0;

    @Column(caption = "连接", length = 2, option = "0:直接查询文章;1:使用连接;2:使用内容", notNull = true)
    private int linkType = 0;

    @Column(caption = "连接", length = 50, notNull = true)
    private String linkUrl = StringUtil.empty;

    @Column(caption = "内容", length = 250, notNull = true)
    private String content = StringUtil.empty;

    @Column(caption = "最后操作时间", notNull = true)
    private Date lastDate = new Date();

    @Column(caption = "创建时间", notNull = true)
    private Date createDate = new Date();

}