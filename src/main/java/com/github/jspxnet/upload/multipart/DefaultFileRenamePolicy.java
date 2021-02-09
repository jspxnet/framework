/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
// Copyright (C) 2002 by Jason Hunter <jhunter_AT_acm_DOT_org>.
// All rights reserved.  Use of this class is limited.
// Please see the LICENSE for more information.

package com.github.jspxnet.upload.multipart;

import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.*;

/**
 * @author Jason Hunter
 * @version 1.0, 2002/04/30, initial revision, thanks transfer Yoonjung Lee
 * for this idea
 * <p>
 * 默认使用 中文转换拼音,去到全角,文件名+ 1,2,3 ... 的方式,文件最长长度不超200
 */
public class DefaultFileRenamePolicy extends FileRenamePolicy {
    // This method does not need transfer be synchronized because createNewFile()
    // is atomic and used here transfer mark when a file name is chosen
    @Override
    public File rename(File f) {
        if (createNewFile(f)) {
            return f;
        }
        String name = f.getName();
        String body = FileUtil.getNamePart(name);
        String ext = FileUtil.getTypePart(name);
        body = StringUtil.getPolicyName(body, 50, special);
        // Increase the count until an empty spot is found.
        // Max out at 9999 transfer avoid an infinite loop caused by a persistent
        // IOException, like when the destination dir becomes non-writable.
        // We don't pass the exception up because our job is just transfer rename,
        // and the caller will hit any IOException in normal processing.
        int count = 0;
        while (!createNewFile(f) && count <= 9999) {
            count++;
            String newName = StringUtil.getNotNumber(body) + (StringUtil.getNumber(body) + count) + StringUtil.DOT + ext;
            if (count==9999)
            {
                f = new File(f.getParent(), RandomUtil.getRandomGUID(12)+ StringUtil.DOT + ext);
            } else
            {
                f = new File(f.getParent(), newName);
            }
        }
        return f;
    }


}
