/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.table.vote;

import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.StringUtil;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-11-26
 * Time: 18:21:23
 */

@Table(name = "jspx_vote_member", caption = "投票记录", cache = false)
public class VoteMember extends OperateTable {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id;

    @Column(caption = "主题ID", length = 50, notNull = true)
    private String topicId;

    @Column(caption = "主题ID", notNull = true)
    private long voteId = 0;

    @Column(caption = "主题ID", dataType = "isLengthBetween(1,100)", length = 100)
    private String voteIds = "";

    @Column(caption = "浏览器", dataType = "isLengthBetween(1,100)", length = 100, notNull = true)
    private String browser = StringUtil.empty;

    @Column(caption = "操作系统", dataType = "isLengthBetween(1,100)", length = 100, notNull = true)
    private String system = StringUtil.empty;

    @Column(caption = "网络类型", dataType = "isLengthBetween(1,20)", length = 20)
    private String netType = StringUtil.empty;

    @Column(caption = "微信openid", dataType = "isLengthBetween(1,50)", length = 50)
    private String openid = StringUtil.empty;

    //如果没有，保存的是sessionId
    @Column(caption = "微信unionid", dataType = "isLengthBetween(1,50)", length = 50)
    private String unionid = StringUtil.empty;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public String getVoteIds() {
        return voteIds;
    }

    public void setVoteIds(String voteIds) {
        this.voteIds = voteIds;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getSystem() {
        return system;
    }

    public void setSystem(String system) {
        this.system = system;
    }

    public long getVoteId() {
        return voteId;
    }

    public void setVoteId(long voteId) {
        this.voteId = voteId;
    }

    public String getNetType() {
        return netType;
    }

    public void setNetType(String netType) {
        this.netType = netType;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }
}