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

import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.table.OperateTable;

import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-3-13
 * Time: 下午2:46
 * 城市树结构
 */
@Table(name = "jspx_city_item", caption = "城市节点")
public class CityItem extends OperateTable {
    public CityItem() {

    }

    @Id
    @Column(caption = "ID", length = 100, notNull = true)
    private String id = StringUtil.empty; //最好试用英文

    @Column(caption = "父ID", length = 100, notNull = true)
    private String parentId = StringUtil.empty;

    @Column(caption = "城市名称", length = 200, notNull = true)
    private String caption = StringUtil.empty;

    @Column(caption = "长途区号", length = 50, notNull = true)
    private String phoneCode;

    @Column(caption = "邮编", length = 50, notNull = true)
    private String postcode;

    @Column(caption = "隐藏", option = "0:不隐藏;1:隐藏", notNull = true)
    private int hide = 0;

    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "描述", length = 250, notNull = true)
    private String description = StringUtil.empty;

    @Column(caption = "密码", length = 200, notNull = true)
    private String password = StringUtil.empty;

    @Column(caption = "查看角色(多个用;分开)", length = 200, notNull = true)
    private String roleIds = StringUtil.empty;

    @Column(caption = "节点类型", option = "0:表示国家;1:省份;2:地区(州);3:城市;4:县", notNull = true)
    private int cityType = 0;

    @Column(caption = "图片URL", length = 200, notNull = true)
    private String showImage = StringUtil.empty;

    @Column(caption = "栏目连接地址", length = 200, notNull = true)
    private String linkPage = StringUtil.empty;

    //多个使用;分开
    @Column(caption = "管理员", length = 250)
    private String manager = StringUtil.empty;

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();

    //论坛做最后发布时间
    @Column(caption = "操作时间", notNull = true)
    private Date lastDate = new Date();

    @Column(caption = "命名空间", length = 50, dataType = "isLengthBetween(1,50)")
    private String namespace = StringUtil.empty;

    @Nexus(mapping = MappingType.OneToMany, field = "id", targetField = "parentId", targetEntity = CityItem.class)
    private List<CityItem> childList = new ArrayList<CityItem>();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPhoneCode() {
        return phoneCode;
    }

    public void setPhoneCode(String phoneCode) {
        this.phoneCode = phoneCode;
    }

    public String getPostcode() {
        return postcode;
    }

    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }

    public int getCityType() {
        return cityType;
    }

    public void setCityType(int cityType) {
        this.cityType = cityType;
    }

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public int getHide() {
        return hide;
    }

    public void setHide(int hide) {
        this.hide = hide;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }

    /**
     * @param uid 用户ID  [1:xxx][2:fddd]
     * @return 判断是为管理员
     */
    public boolean isManager(long uid) {
        return manager != null && manager.contains("[" + uid + ":");
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean inRoleIds(String roleId) {
        return StringUtil.isNull(roleIds) || ArrayUtil.inArray(StringUtil.split(roleIds, StringUtil.SEMICOLON), roleId + StringUtil.COLON, true);
    }

    public String getRoleIds() {
        return roleIds;
    }

    public void setRoleIds(String roleIds) {
        this.roleIds = roleIds;
    }

    public String getLinkPage() {
        return linkPage;
    }

    public void setLinkPage(String linkPage) {
        this.linkPage = linkPage;
    }

    public Date getSortDate() {
        return sortDate;
    }

    public void setSortDate(Date sortDate) {
        this.sortDate = sortDate;
    }

    public Date getLastDate() {
        return lastDate;
    }

    public void setLastDate(Date lastDate) {
        this.lastDate = lastDate;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public List<CityItem> getChildList() {
        return childList;
    }

    public void setChildList(List<CityItem> childList) {
        this.childList = childList;
    }

    public String getShowImage() {
        return showImage;
    }

    public void setShowImage(String showImage) {
        this.showImage = showImage;
    }

    public boolean lookThrough(String roleId) {
        return StringUtil.isNull(roleId) || ArrayUtil.inArray(StringUtil.split(roleId, StringUtil.SEMICOLON), roleId, false);
    }

}