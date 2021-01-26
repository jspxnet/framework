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

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.res.LanguageRes;
import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.FileInfo;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: chenyuan
 * date: 12-8-3
 * Time: 下午10:39
 */
@HttpMethod(caption = "目录图片列表")
public class FolderPhotoView extends ActionSupport {
    public FolderPhotoView() {

    }

    private String configName = StringUtil.empty;

    public String getConfigName() {
        return configName;
    }

    public void setConfigName(String configName) {
        this.configName = configName;
    }

    /**
     * @return 上传路径的计算方式配置方式
     */
    private String getPhotoDirectory() {
        String saveDirectory = FileUtil.mendPath(config.getString(configName));
        if (!FileUtil.isDirectory(saveDirectory)) {
            saveDirectory = FileUtil.mendPath(config.getString(Environment.setupPath)) + FileUtil.mendPath(config.getString(configName));
        }
        return saveDirectory;
    }

    private String getSetupPath() throws Exception {
        String setupPath = FileUtil.mendPath(config.getString(Environment.setupPath));
        if (!FileUtil.isDirectory(setupPath)) {
            setupPath = FileUtil.mendPath(FileUtil.getParentPath(getTemplatePath()));
            config.save(Environment.setupPath, setupPath);
            addFieldInfo(Environment.warningInfo, language.getLang(LanguageRes.setupConfigPathError) + ":" + setupPath);
        }
        return setupPath;
    }

    private String find;

    public String getFind() {
        return find;
    }

    @Param(caption = "查询数据", max = 40)
    public void setFind(String find) {
        this.find = find;
    }

    private String type;

    public String getType() {
        return type;
    }

    @Param(caption = "类型", max = 40)
    public void setType(String type) {
        this.type = type;
    }

    private String order = "name";

    public String getOrder() {
        return order;
    }

    @Param(caption = "排序（name,size）", max = 40)
    public void setOrder(String order) {
        this.order = order;
    }

    public List<FileInfo> getFileList() throws Exception {
        File file = new File(getPhotoDirectory());
        if (file.exists() && file.isDirectory()) {
            return FileUtil.getFileListSort(file.getAbsolutePath(), find, type, order, getSetupPath());
        }
        return new ArrayList<>();
    }

}