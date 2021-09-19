/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.upload.multipart;

import java.io.File;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-2-9
 * Time: 10:07:50
 * 使用覆盖方式
 */
public class CoveringsFileRenamePolicy extends FileRenamePolicy {


    @Override
    public File rename(File f) {
        if (f.isFile()) {
            if (!f.delete()) {
                f.deleteOnExit();
            }
        }
        return f;
    }


}