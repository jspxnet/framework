/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.boot.environment.impl;

/*
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2004-4-1
 * Time: 16:52:08
 * 配置文件接口
 */

import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;

@Slf4j
public class BaseConfigurationImpl implements JspxConfiguration {

    private String defaultPath = null;
    private String configFilePath = null;
    final private static Date START_RUN_DATE = new Date();
    private String defaultConfigFile = Environment.jspx_properties_file;

    public BaseConfigurationImpl() {

    }

    public String getDefaultConfigFile() {
        return defaultConfigFile;
    }

    @Override
    public void setDefaultConfigFile(String defaultConfigFile) {
        this.defaultConfigFile = defaultConfigFile;
    }

    @Override
    public void setDefaultPath(String defaultPath) {
        this.defaultPath = defaultPath;
    }

    /**
     * 得到配置文件路径
     */
    private void loadPath() {
        String path = null;
        URL url = Environment.class.getResource("/" + defaultConfigFile);
        if (url != null) {
            path = url.getPath();
            if (!FileUtil.isFileExist(path)) {
                path = null;
            }
        }
        if (url == null) {
            url = Environment.class.getResource(defaultConfigFile);
            if (url != null) {
                path = url.getPath();

            }
        }
        if (path == null) {
            url = Environment.class.getResource("/resources/"+defaultConfigFile);
            if (url != null) {
                path = url.getPath();
                if (!FileUtil.isFileExist(path)) {
                    path = null;
                }
            }
        }
        if (path == null) {
            path = FileUtil.mendPath(System.getProperty("user.dir")) + defaultConfigFile;
            if (!FileUtil.isFileExist(path)) {
                path = null;
            }
        }

        if (path == null) {
            url = ClassUtil.getResource("/" + defaultConfigFile);
            if (url != null) {
                path = url.getPath();
                int i = path.toLowerCase().indexOf("file-inf");
                if (i != -1) {
                    path = path.substring(0, i) + "web-inf/classes/" + defaultConfigFile;
                }
                if (!FileUtil.isFileExist(path)) {
                    path = null;
                }
            }
        }

        //查找 jar 路径
        if (path == null) {
            List<File> listDir = ClassUtil.getRunJarDir();
            for (File fileDir : listDir) {
                path = new File(fileDir.getPath(),defaultConfigFile).getPath();
                if (FileUtil.isFileExist(path)) {
                    break;
                }
            }
            if (!FileUtil.isFileExist(path)) {
                path = null;
            }
        }

        if (path == null) {

            List<File> list = FileUtil.getPatternFiles(null,defaultConfigFile);
            if (!list.isEmpty())
            {
                path = list.get(0).getPath();
            }

        }

        if (path == null) {
            File file = new File(FileUtil.mendPath(System.getProperty(" user.dir")) + defaultConfigFile);
            if (file.isFile() && file.exists()) {
                path = file.getParentFile().getPath();
            } else {
                path = null;
            }
        }

        if (path == null) {
            url = ClassUtil.getResource(defaultConfigFile);
            if (url != null) {
                path = url.getPath();
            }
        }


        if (path == null) {
            log.info("No find file:" + defaultConfigFile + ",不能找到" + defaultConfigFile + "文件");
        } else {
            configFilePath = path;
            if (path.endsWith(defaultConfigFile)) {
                path = FileUtil.getPathPart(path);
            }
        }
        if (StringUtil.isNull(path))
        {
            defaultPath = null;
            return ;
        }
        File file = new File(path);
        defaultPath = FileUtil.mendPath(file.getPath());
        log.info("user.dir=" + System.getProperty("user.dir"));
        log.info("defaultPath=" + defaultPath);
    }

    /**
     * 得到本jspx 包的默认工作目录.
     *
     * @return 目录
     */
    @Override
    public String getDefaultPath() {
        if (defaultPath==null)
        {
            loadPath();
        }
        return defaultPath;
    }

    @Override
    public String getConfigFilePath() {
        if (defaultPath==null)
        {
            loadPath();
        }
        return configFilePath;
    }
    /**
     * @return 得到配置文件
     */
    @Override
    public String getIocConfigFile() {
        return FileUtil.mendFile(defaultPath + Environment.config_file);
    }


    @Override
    public Date getStartRunDate() {
        return START_RUN_DATE;
    }

    @Override
    public long getRunDay() {
        return DateUtil.compareDay(START_RUN_DATE);
    }
}