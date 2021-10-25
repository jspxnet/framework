/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.action;

import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.boot.res.ToolTipsRes;
import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.result.RocResponse;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.txweb.view.ActionLogView;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 13-10-16
 * Time: 下午10:43
 */
@HttpMethod(caption = "日志管理")
public class ActionLogAction extends ActionLogView {

    @Operate(caption = "删除")
    public RocResponse<Boolean> delete(@Param(caption = "id列表",required = true,max = 64,message = ToolTipsRes.notSelectObject) String[] ids)  {
        return RocResponse.success(actionLogDAO.delete(ids),language.getLang(LanguageRes.deleteSuccess));
    }

    @Operate(caption = "清除一年前")
    public RocResponse<Integer> deleteYear() throws Exception {
        return RocResponse.success(actionLogDAO.deleteYearBefore(1),language.getLang(LanguageRes.deleteSuccess));
    }

    @Operate(caption = "清空")
    public RocResponse<Integer> clear() {
        return RocResponse.success(actionLogDAO.clear(),language.getLang(LanguageRes.deleteSuccess));
    }

    @Override
    public String execute() throws Exception {
        if (isMethodInvoked()) {
            actionLogDAO.evict(ActionLog.class);
        }
        return super.execute();
    }
}



