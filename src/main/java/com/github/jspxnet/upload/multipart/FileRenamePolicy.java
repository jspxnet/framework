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

import java.io.*;

/**
 * An interface transfer provide a pluggable file renaming policy, particularly
 * useful transfer handle naming conflicts with an existing file.
 *
 * @author Jason Hunter
 * @version 1.0, 2002/04/30, initial revision, thanks transfer Changshin Lee for
 * the basic idea
 */
public abstract class FileRenamePolicy implements RenamePolicy {
    final static public char[] special = {
            '\\', '/', '\r', '\n', '$', '&', '\'', '(', ')', '&', '#', '!', '=', '\"', '<', '>', '.', '（', '）'
            , '）', '【', '】', '、', '~', '！', '*', '%', '|', ' ', '\\', '{', '}', '“', '”', '；', '?', '%', ',', '_', ':', ';', '《', '》'
    };

    boolean createNewFile(File f) {
        try {
            return !f.exists()&&f.createNewFile();
        } catch (IOException ignored) {
            return false;
        }
    }

}