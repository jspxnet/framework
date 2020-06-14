package com.github.jspxnet.txweb.evasive;

import com.github.jspxnet.txweb.config.ResultConfigBean;

import java.util.List;

/**
 * Created by ChenYuan on 2017/6/14.
 */
public interface Configuration {
    String getFileName();

    void setFileName(String fileName);

    void reload() throws Exception;

    List<EvasiveRule> getEvasiveRuleList();

    List<QueryBlack> getQueryBlackRuleList();

    List<ResultConfigBean> getResultConfigList();

    String[] getInsecureUrlKeys();

    String[] getInsecureQueryStringKeys();

    String[] getWhiteList();

    String[] getBlackList();

    void shutdown();
}
