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


import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.json.JsonIgnore;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Data;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-27
 * Time: 15:22:59
 */
@Data
@Table(name = "jspx_user_session", caption = "在线信息")
public class UserSession implements IUserSession {
    @Id(auto = true, length = 24, type = IDType.uuid)
    @Column(caption = "ID", length = 255, notNull = true)
    private String id = StringUtil.empty;

    @Column(caption = "隐藏", option = "0:在线;1:隐身;2:忙碌;3:请勿打扰", notNull = true)
    private int invisible = 0;

    @Column(caption = "用户ID", notNull = true)
    private long uid = 0;

    //昵称，中文名称方式登录
    @Column(caption = "昵称", length = 50, dataType = "isLengthBetween(2,32)", notNull = true)
    private String name = StringUtil.empty;

    //达人称号
    @Column(caption = "绰号", length = 50, dataType = "isLengthBetween(2,26)")
    private String nickname = StringUtil.empty;

    //允许邮箱登录
    @Column(caption = "邮箱", length = 50, dataType = "isEmail")
    private String mail = StringUtil.empty;

    //手机方式登录
    @Column(caption = "手机", dataType = "isMobile", length = 50)
    private String phone = StringUtil.empty;

    @Column(caption = "卡号", length = 50)
    private String kid = StringUtil.empty;
    //支持这的登录方式 end

    @Column(caption = "勋章", length = 250)
    private String medals = StringUtil.empty;

    @Column(caption = "头像URL", length = 240)
    private String faceImage = StringUtil.empty;

    @Column(caption = "性别", option = "男;女;保密", length = 4, notNull = true)
    private String sex = "保密";

    //通过生日计数年龄
    @Column(caption = "生日")
    private Date birthday = DateUtil.empty;

    @Column(caption = "最后请求时间", notNull = true)
    private long lastRequestTime = System.currentTimeMillis();

    @Column(caption = "登陆时间", notNull = true)
    private Date createDate = new Date();

    @Column(caption = "IP",  length = 48)
    private String ip = StringUtil.empty;

    @JsonIgnore
    private List<Role> roleList = new ArrayList<>();


    public UserSession() {

    }

    /**
     * 得到当前角色
     * @param namespace 命名空间
     * @param organizeId 机构id
     * @return 得到当前角色
     */
    @Override
    public IRole getRole(String namespace,String organizeId)
    {
        if (namespace==null)
        {
            namespace = StringUtil.empty;
        }
        if (organizeId==null)
        {
            organizeId = StringUtil.empty;
        }
        for (Role role : roleList)
        {
            if (role == null || role.getNamespace()==null)
            {
                continue;
            }
            if (StringUtil.isEmpty(organizeId)&&namespace.equals(role.getNamespace()))
            {
                return role;
            }
            if (namespace.equals(role.getNamespace())&&organizeId.equals(role.getOrganizeId()))
            {
                return role;
            }
        }
        return null;
    }

    /**
     * 放入角色,重复放入的采用替换方式
     * @param role 角色
     */
    @Override
    public void setRole(Role role)
    {
        if (role==null)
        {
            return;
        }
        for (Role roleTmp:roleList)
        {
            if (roleTmp.getNamespace().equals(role.getNamespace())&&roleTmp.getOrganizeId()!=null&&roleTmp.getOrganizeId().equals(role.getOrganizeId()))
            {
                BeanUtil.copyFiledValue(role,roleTmp);
                return;
            }
        }
        if (!roleList.contains(role))
        {
            roleList.add(role);
        }
    }

    @Override
    public boolean isGuest() {
        return uid <= 0 || StringUtil.isEmpty(name) || Environment.guestName.equalsIgnoreCase(name);
    }

}