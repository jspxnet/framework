/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.env;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-8-27
 * Time: 11:26:55
 */
public abstract class TXWebIoc {
    private TXWebIoc() {

    }


    //树结构
    static public final String treeItemDAO = "treeItemDAO";
    //用户权限树
    static public final String memberTreeDAO = "memberTreeDAO";

    //部门树DAO
    static public final String departmentDAO = "departmentDAO";
    //用户所属部门
    //static public final String memberDepartmentDAO = "memberDepartmentDAO";
    //Tag
    static public final String tagsDAO = "tagsDAO";


}