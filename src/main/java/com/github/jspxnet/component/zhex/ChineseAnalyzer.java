/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex;

import java.io.File;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-11-14
 * Time: 15:04:08
 */
public interface ChineseAnalyzer {


    String getTag(String cline, String separator, int num, boolean zh) throws IOException;

    String getAnalyzerWord(File file, String separator, int length, int size);

    String getAnalyzerWord(String string, String separator, int length, int size);

    String getAnalyzerWord(String String, String separator);

    String getAnalyzerWord(File file, String separator);

}