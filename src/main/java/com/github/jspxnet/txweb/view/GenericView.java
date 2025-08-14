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

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.GenericDAO;
import com.github.jspxnet.txweb.model.param.PageParam;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 12-2-4
 * Time: 下午10:10
 */

public class GenericView extends ActionSupport {

    protected String className = StringUtil.empty;
    @Ref
    public void setClassName(String className) {
        this.className = className;
    }

    @Ref
    protected GenericDAO genericDAO;

    public List<?> getList(@Param(caption = "通用参数")PageParam  params) throws Exception {
        return genericDAO.getList(ClassUtil.loadClass(className),params.getField() , params.getFind(), params.getTerm(),params.getUid(),params.getSort(),params.getCurrentPage(),params.getCount(),false);
    }

    public int getTotalCount(PageParam  params) throws Exception {
        return genericDAO.getCount(ClassUtil.loadClass(className),params.getField() , params.getFind(), params.getTerm(),params.getUid());
    }


    public RocResponse<List<?>> list(@Param(caption = "通用参数")PageParam  params) throws Exception {
        int totalCount = getTotalCount(params);
        if (totalCount <= 0) {
            return RocResponse.success(new ArrayList<>(), language.getLang(LanguageRes.notDataFind));
        }
        List<?> list = getList(params);
        RocResponse<List<?>> rocResponse = RocResponse.success(BeanUtil.copyList(list, ClassUtil.loadClass(className)));
        return rocResponse.setCurrentPage(params.getCurrentPage()).setCount(params.getCount()).setTotalCount(totalCount);
    }


}