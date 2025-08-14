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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.enums.UserEnumType;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IRole;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.annotation.TurnPage;
import com.github.jspxnet.txweb.dao.MemberDAO;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.UserSession;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-7-30
 * Time: 17:49:10
 * com.github.jspxnet.txweb.view.UserSessionListView
 */
@HttpMethod(caption = "在线用户")
public class UserSessionListView extends ActionSupport {
    public UserSessionListView() {

    }

    @Setter
    @Getter
    private boolean load = false;

    @Ref
    private MemberDAO memberDAO;



    @Operate(caption = "在线人数翻页列表", method = "/list/page")
    public RocResponse<List<UserSession>> getList(@Param("翻页参数") PageParam param)
    {
        IRole role = getRole();
        if (role.getUserType() < UserEnumType.MANAGER.getValue()) {
            return RocResponse.error(ErrorEnumType.POWER);
        }
        RocResponse<List<UserSession>> rocResponse = BeanUtil.copy(param,RocResponse.class);
        return rocResponse.setData( memberDAO.getOnlineList(param.getTerm(),param.getSort(),param.getCurrentPage(),param.getCount(),load)).setTotalCount(memberDAO.getOnlineCount(param.getTerm()));
    }

}