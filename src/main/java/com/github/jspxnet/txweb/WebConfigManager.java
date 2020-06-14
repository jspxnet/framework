/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb;

import com.github.jspxnet.txweb.config.ActionConfig;
import com.github.jspxnet.txweb.config.ActionConfigBean;
import com.github.jspxnet.txweb.config.ResultConfigBean;
import com.github.jspxnet.txweb.vo.OperateVo;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-3-31
 * Time: 17:24:19
 */
public interface WebConfigManager {
    List<String> getDefaultInterceptors(String namespace) throws Exception;

    List<String> getPageDefaultInterceptors(String namespace);

    List<ResultConfigBean> getDefaultResults(String namespace);

    void clear();


    /**
     * 总表,触发扫描动作
     * @return 得到配置列表
     */
    Map<String, Map<String, ActionConfigBean>> getConfigTable();

    ActionConfig getActionConfig(String namePart, String namespace, boolean wildcard) throws Exception;

    List<String> getActionList(String namespace) ;

    Map<String, ActionConfigBean> getActionMap(String namespace) throws Exception;

    List<String> getNamespaceList() throws Exception;

    Map<String, String> getExtendList() throws Exception;

    List<OperateVo> getOperateForNamespace(String namespace) throws Exception;

    List<OperateVo> getOperateList(String namespace) throws Exception;

    List<String> getSoftList();

    void registerAction(Class<?> cla);


    void checkLoad();

    /**
     * 扫描要加载的类对象
     * @param className 类路径
     * @throws IOException 异常
     */
    void sanAction(String className) throws IOException;

    /**
     * 通过id得到 操作方法
     * @param namespace  命名空间
     * @param id id
     * @return 操作方法
     * @throws Exception 异常
     */
    OperateVo getOperate(String namespace, String id) throws Exception;
}