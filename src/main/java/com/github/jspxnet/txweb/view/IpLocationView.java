/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.table.IpLocation;
import com.github.jspxnet.txweb.dao.IpLocationDAO;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-14
 * Time: 17:24:14
 */
@HttpMethod(caption = "IP位置查询")
public class IpLocationView extends ActionSupport {
    public IpLocationView() {

    }

    @Ref
    protected IpLocationDAO ipLocationDAO;

    private String ip = StringUtil.empty;

    public String getIp() {
        return ip;
    }

    @Param(caption = "id")
    public void setIp(String ip) {
        this.ip = ip;
    }

    public IpLocation getIpLocation() {
        IpLocation ipLocation;
        if (!StringUtil.isIPAddress(ip)) {
            ipLocation = new IpLocation();
            ipLocation.setCity("错误的IP");
            return ipLocation;
        }
        ipLocation = ipLocationDAO.getIpLocation(ip);
        if (ipLocation == null) {
            return new IpLocation();
        }
        return ipLocation;
    }

    @Operate(caption = "手机验证码登录",method = "ip/location")
    public IpLocation getIpLocation(String ipAddress) {
        return ipLocationDAO.getIpLocation(ipAddress);
    }

    @Operate(caption = "手机验证码登录",method = "ip/remote")
    public IpLocation getRemoteIpLocation() {
        return ipLocationDAO.getIpLocation(getRemoteAddr());
    }

}