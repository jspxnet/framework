package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.txweb.evasive.EvasiveIp;
import com.github.jspxnet.txweb.evasive.EvasiveManager;
import com.github.jspxnet.txweb.evasive.EvasiveRule;
import com.github.jspxnet.txweb.evasive.QueryBlack;
import com.github.jspxnet.txweb.support.ActionSupport;

import java.util.Collection;

@HttpMethod(caption = "访问管理")
public class EvasiveView extends ActionSupport {
    private EvasiveManager evasiveManager = EvasiveManager.getInstance();


    public String[] getWhiteList() {
        return evasiveManager.getWhiteList();
    }

    public String[] getBlackList() {
        return evasiveManager.getBlackList();
    }

    public Collection<EvasiveIp> getBlackIpList() {
        return evasiveManager.getBlackIpList();
    }

    public Collection<QueryBlack> getQueryBlackRuleList() {
        return evasiveManager.getQueryBlackRuleList();
    }

    public Collection<EvasiveRule> getEvasiveRuleList() {
        return evasiveManager.getEvasiveRuleList();
    }


    public Collection<ResultConfigBean> getResultConfigList() {
        return evasiveManager.getResultConfigList();
    }


}
