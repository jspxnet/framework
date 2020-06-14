/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.postalcode;

import com.github.jspxnet.sober.annotation.Id;
import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Nexus;
import com.github.jspxnet.sober.enums.MappingType;

import java.util.List;
import java.util.ArrayList;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-3-14
 * Time: 17:04:09
 * jspx.juser.table.AreaCity
 */
public class AreaCity implements Serializable {
    public AreaCity() {

    }

    @Id(auto = false)
    @Column(caption = "ID号", length = 50, notNull = true)
    private String id;

    @Column(caption = "地区", length = 100, notNull = true)
    private String areaName;

    @Column(caption = "区号", length = 100, notNull = true)
    private String areaCode;

    @Column(caption = "邮编", length = 100, notNull = true)
    private String postalcode;

    @Column(caption = "类型", option = "0:这个地区是一般地区;1:这个地区是直辖市;2:这个地区是省会", notNull = true, defaultValue = "80")
    private int isDirectly = 0;

    //子地区集
    @Nexus(mapping = MappingType.OneToMany, field = "id", targetField = "parentAreaId", targetEntity = AreaCity.class)
    private List<AreaCity> childAreas = new ArrayList<AreaCity>();

    @Column(caption = "父ID", length = 50, notNull = true)
    private String parentAreaId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getPostalcode() {
        return postalcode;
    }

    public void setPostalcode(String postalcode) {
        this.postalcode = postalcode;
    }

    public int getDirectly() {
        return isDirectly;
    }

    public void setDirectly(int directly) {
        isDirectly = directly;
    }

    public List<AreaCity> getChildAreas() {
        return childAreas;
    }

    public void setChildAreas(List<AreaCity> childAreas) {
        this.childAreas = childAreas;
    }

    public String getParentAreaId() {
        return parentAreaId;
    }

    public void setParentAreaId(String parentAreaId) {
        this.parentAreaId = parentAreaId;
    }
}