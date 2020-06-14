/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot.sign;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-10-23
 * Time: 16:12:11
 */
public abstract class TreeListType {
    private TreeListType() {
    }

    //树列表显示方式，treeListType  0：不处理用户栏目 1:安栏目方式  2：栏目+用户自己发布的内容
    static public final int SimpleType = 0;
    static public final int NodeType = 1;
    static public final int PostType = 2;

}