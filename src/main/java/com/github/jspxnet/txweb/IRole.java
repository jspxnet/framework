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

import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-9-13
 * Time: 21:56:18
 * 作为角色基本接口，用来扩展中间件
 */
public interface IRole extends Serializable {
    /**
     * @return 角色ID
     */
    String getId();


    /**
     * @return 角色名称
     */
    String getName();

    /**
     * @return 描述
     */
    String getDescription();


    /**
     * @return 角色的用户类型
     */
    int getUserType();


    /**
     * @return 表示图片
     */
    String getImages();

    /**
     * @return 是否允许上传
     */
    int getUseUpload();


    /**
     * @return 上传的文件大小
     */
    int getUploadSize();


    /**
     * @return 上传的图片大小
     */
    int getUploadImageSize();

    /**
     * @return 上传的视频大小
     */
    int getUploadVideoSize();

    /**
     * @return 上传的文件类型
     */
    String getUploadFileTypes();


    String getJsonUploadTypes(String type);

    String getOptionUploadTypes();

    String getOptionUploadTypes(boolean cut);

    String getUploadFolder();

    /**
     * @return 存储空间
     */
    long getDiskSize();


    /**
     * rwde  读 写 删 执行
     *
     * @return 权限
     */
    String getPermission();


    /**
     * @return 本角色组的管理者
     */
    String getManager();

    int getAuditingType();


    /**
     * @return 冻结否
     */
    int getCongealType();

    /**
     * @return 命名空间
     */
    String getNamespace();


    String getOperates();

    boolean checkOperate(String namespace, String className, String classMethod);

    boolean isOperateConfig(String namespace, String className, String classMethod);

    boolean isOperateConfig(String actionMethodId);

    int getOfficeType();

    String getOrganizeId();

    void setOrganizeId(String organizeId);

}