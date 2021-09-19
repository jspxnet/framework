package com.github.jspxnet.network.oss;

import com.github.jspxnet.network.oss.adapter.OssSts;

import java.io.File;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/9/19 23:58
 * description: 云盘适配器适配器
 * 目前主要考虑的是阿里云和华为云
 * 阿里云叫盘oss, 华为云盘叫obs
 **/
public interface CloudFileClient {

    /**
     * 上传到云盘空间
     * @param cloudPath 保存路径
     * @param file 文件
     * @return 访问路径
     * @throws Exception 异常
     */
    String upload(String cloudPath, File file) throws Exception;

    boolean delete(String cloudPath);

    OssSts getOssSts(String roleSessionName);
}
