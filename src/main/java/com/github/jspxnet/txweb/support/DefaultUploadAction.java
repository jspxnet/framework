/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.support;

import com.github.jspxnet.txweb.annotation.HttpMethod;
import com.github.jspxnet.upload.MultipartRequest;
import com.github.jspxnet.txweb.annotation.MulRequest;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-8-18
 * Time: 20:46:21
 * 上传接收 action 封装
 */

@HttpMethod(caption = "上传")
public class DefaultUploadAction extends MultipartSupport {
    public DefaultUploadAction() {

    }

    //是否检测文件内部编码
    private boolean filterCodeMarker = true;

    public boolean isFilterCodeMarker() {
        return filterCodeMarker;
    }

    public void setFilterCodeMarker(boolean filterCodeMarker) {
        this.filterCodeMarker = filterCodeMarker;
    }

    @Override
    @MulRequest(saveDirectory = "@saveDirectory", fileTypes = "@fileTypes", maxPostSize = "@maxPostSize")
    public void setMultipartRequest(MultipartRequest multipartRequest) {
        this.multipartRequest = multipartRequest;
        if (filterCodeMarker) {
            int iCheck = checkFileMatching("jpg;png;bmp;gif;zip;swftools");
            if (iCheck > 0) {
                addFieldInfo("提示", "不允许上传包含代码标识的文件" + iCheck);
            }
        }
    }

}