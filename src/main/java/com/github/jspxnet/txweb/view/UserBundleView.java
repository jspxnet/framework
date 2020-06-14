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
import com.github.jspxnet.json.JSONArray;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.UserBundleDAO;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.MemberBundle;
import com.github.jspxnet.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-3-28
 * Time: 下午5:02
 */
@HttpMethod(caption = "用户变量")
public class UserBundleView extends ActionSupport {
    public UserBundleView() {

    }

    private String key = StringUtil.empty;

    public String getKey() {
        return key;
    }

    @Param(caption = "变量名")
    public void setKey(String key) {
        this.key = key;
    }

    protected UserBundleDAO userBundleDAO;

    @Ref(name = Environment.UserBundleDAO)
    public void setMemberDAO(UserBundleDAO userBundleDAO) {
        this.userBundleDAO = userBundleDAO;
    }

    public MemberBundle getUserBundle(String key) {
        IUserSession userSession = getUserSession();

        if (userSession == null || userSession.isGuest()) {
            return new MemberBundle();
        }
        return userBundleDAO.getUserBundle(key, userSession.getUid());
    }

    public List<MemberBundle> getList() {
        IUserSession userSession = getUserSession();
        if (userSession == null || userSession.isGuest()) {
            return new ArrayList<>();
        }
        return userBundleDAO.getList(userSession.getUid());
    }

    public Map<String, String> getBundleList()  {
        Map<String, String> map = new HashMap<String, String>();
        List<MemberBundle> list = getList();
        for (MemberBundle bundle : list) {
            map.put(bundle.getIdx(), bundle.getContext());
        }
        return map;
    }

    public JSONArray getArray()  {
        List<MemberBundle> list = getList();
        JSONArray array = new JSONArray();
        for (MemberBundle bundle : list) {
            JSONObject obj = new JSONObject();
            obj.put(bundle.getIdx(), bundle.getContext());
            array.put(obj);
        }
        return array;
    }


}