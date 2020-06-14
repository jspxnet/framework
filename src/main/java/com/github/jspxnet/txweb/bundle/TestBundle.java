/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.bundle;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.sioc.BeanFactory;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-6-30
 * Time: 15:45:26
 */
public class TestBundle {

    public static void main(String[] args) throws Exception {
        JspxNetApplication.autoRun();
        BeanFactory beanFactory = EnvFactory.getBeanFactory();
        Bundle config = (Bundle) beanFactory.getBean("config", "jbbs");
        config.remove("jspx/test");
        String tt = config.get("jspx/test");
        //System.out.println("-------------1--tt=" + tt);

        tt = config.get("jspx/test");
        // System.out.println("-------------2--tt=" + tt);

        config.save("jspx/test", "ss");

        tt = config.get("jspx/test");
        // System.out.println("-------------3--tt=" + tt);

        tt = config.get("jspx/test");
        // System.out.println("-------------4--tt=" + tt);

    }

}