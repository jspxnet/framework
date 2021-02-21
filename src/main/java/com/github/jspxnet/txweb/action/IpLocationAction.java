/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.enums.ErrorEnumType;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.view.IpLocationView;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-7-16
 * Time: 下午8:32
 */
@HttpMethod(caption = "IP定位")
public class IpLocationAction extends IpLocationView {
    public IpLocationAction() {

    }

    /**
     * 删除内容,删除条件,1:包含mid列表,并且是在自己管理的栏目中
     *
     * @throws com.github.jspxnet.sober.exception.ValidException v
     * @throws Exception                                         异常
     */
    @Operate(caption = "保存")
    public RocResponse<Boolean> save() throws Exception {
        if (StringUtil.isNull(ipLocationDAO.getFileName())) {
            return RocResponse.error(ErrorEnumType.WARN.getValue(),language.getLang(LanguageRes.fileNameNotConfig));
        }
        return RocResponse.success(ipLocationDAO.deleteAll(),language.getLang(LanguageRes.importData) + "," + ipLocationDAO.fileToDataBase());
    }


}