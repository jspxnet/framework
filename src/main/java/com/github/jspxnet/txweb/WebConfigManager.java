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
    /**
     * @param namespace 命名空间
     * @return 得到默认拦截器配置, 默认拦截器不使用继承方式
     */
    List<String> getDefaultInterceptors(String namespace) ;
    /**
     * @param namespace 命名空间
     * @return 得到页面拦截配置
     */
    List<String> getPageDefaultInterceptors(String namespace);
    /**
     * @param namespace 命名空间
     * @return 得到默认返回配置
     */
    List<ResultConfigBean> getDefaultResults(String namespace);
    /**
     * 从新载入配置文件
     */
    void clear();
    /**
     * 总表,触发扫描动作
     * @return 得到配置列表
     */
    Map<String, Map<String, ActionConfigBean>> getConfigTable();
    /**
     * 得到配置信息
     *
     * @param namePart  action name
     * @param namespace 命名控件
     * @param reload  是否重新载入
     * @return 配置
     */
    ActionConfig getActionConfig(String namePart, String namespace, boolean reload);
    /**
     * @param namespace 命名空间列表
     * @return 得到本命名空间下的命名空间
     */
    List<String> getActionList(String namespace) ;
    /**
     * 得到某命名空间里边的动作列表,让你能够在开发的过程中得到动作列表来判断权限
     *
     * @param namespace 命名空间
     * @return 动作配置列表
     */
    Map<String, ActionConfigBean> getActionMap(String namespace);
    /**
     * @return 得到命名空间列表
     */
    List<String> getNamespaceList();
    /**
     * @return 得到命名空间继承关系列表
     */
    Map<String, String> getExtendList();
    /**
     * @param namespace 命名空间
     * @return 操作列表
     * @throws Exception 异常
     */
    List<OperateVo> getOperateForNamespace(String namespace) throws Exception;
    /**
     * @param namespace 支持多个是用 ; 分割
     * @return 操作列表, 包含继承的动作 /user/xxxx  条件为user
     * @throws Exception 异常
     */
    List<OperateVo> getOperateList(String namespace) throws Exception;
    /**
     * 命名空间第一层表示软件名称
     *
     * @return 得到部署了那些软件
     */
    List<String> getSoftList();
    /**
     * 动态注册
     *
     * @param cla 类对象
     */
    void registerAction(Class<?> cla);

    /**
     * 检查师傅已经载入
     */
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