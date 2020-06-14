package com.github.jspxnet.util;

import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.XML;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Table;
import com.github.jspxnet.utils.StringUtil;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by chenyuan on 2016-06-06.
 * 简单的交流对话记录，使用在单据,或者小范围间
 * 和留言不同，这里例如针对一个订单双方的交流，并且能够作为历史备份
 */
@Table(name = "jspx_scope_note", caption = "便签", create = false)
public class ScopeNote {
    @Id
    @Column(caption = "ID", notNull = true)
    private long id = 0;

    @Column(caption = "uid", notNull = true)
    private long uid = 0;

    @Column(caption = "用户名", length = 64)
    private String userName = StringUtil.empty;

    @Column(caption = "IP地址", length = 20, notNull = true, defaultValue = "127.0.0.1")
    private String ip = "127.0.0.1";

    @Column(caption = "正文", dataType = "isLengthBetween(0,512)", length = 512)
    private String content = StringUtil.empty;

    @Column(caption = "创建时间", notNull = true)
    private Date createDate = new Date();

    @Column(caption = "列表", notNull = true)
    private List list = new ArrayList();

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUid() {
        return uid;
    }

    public void setUid(long uid) {
        this.uid = uid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        json.put("uid", uid);
        json.put("userName", userName);
        json.put("ip", ip);
        json.put("createDate", createDate);
        json.put("cont", content);
        return json;
    }

    public String toXML() {
        JSONObject json = toJson();
        try {
            return XML.toString(json, "note");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }

    @Override
    public String toString() {
        JSONObject json = toJson();
        return json.toString(2);
    }

}
