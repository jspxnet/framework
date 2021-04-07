/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.model.vo.OperateVo;

import java.util.Comparator;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2010-11-10
 * Time: 15:36:12
 */
public class OperateComparator implements Comparator {
    @Override
    public int compare(Object o1, Object o2) {

        OperateVo q1 = (OperateVo) o1;
        OperateVo q2 = (OperateVo) o2;
        if (q1 == null || q2 == null) {
            return 0;
        }
        if (q1.getNamespace() == null || q2.getNamespace() == null) {
            return 0;
        }
        if (q1.getNamespace().equals(q2.getNamespace()) && q1.getCaption() != null && q2.getCaption() != null) {
            return q1.getCaption().compareTo(q2.getCaption());
        } else {
            return q1.getNamespace().compareTo(q2.getNamespace());
        }
    }
}