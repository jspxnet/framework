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

import com.github.jspxnet.boot.conf.JarDefaultConfig;
import com.github.jspxnet.boot.environment.JspxConfiguration;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.AbstractWrite;
import com.github.jspxnet.io.WriteFile;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;


import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;

@Slf4j
public class BaseConfigurationImpl implements JspxConfiguration {

    private String defaultPath = StringUtil.empty;
    final private static Date START_RUN_DATE = new Date();
    private long configFileTime = 0;
    private boolean handSet = false;
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
        handSet = true;
    }

    @Override
    public void checkLoader() {
        if (!handSet || StringUtil.isNull(defaultPath) || (defaultPath != null && configFileTime != FileUtil.lastModified(defaultPath + defaultConfigFile))) {
            loadPath();
        }
        configFileTime = FileUtil.lastModified(defaultPath + defaultConfigFile);
    }

    /**
     * 得到配置文件路径
     */
    private void loadPath() {
        String path = null;
        URL url = ClassUtil.getResource("/" + defaultConfigFile);
        if (url == null) {
            url = ClassUtil.getResource(defaultConfigFile);
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

        /**
         * 查找 lib 目录
         *
         */
        if (path == null) {
            path = FileUtil.getPathPart(ClassUtil.getClassFilePath(com.github.jspxnet.boot.conf.JarDefaultConfig.class.getName()));
            if (path.contains(".jar!") || path.contains(".war!") || path.contains(".zip!")) {
                path = FileUtil.mendPath(FileUtil.getPathPart(StringUtil.substringBefore(path, "!/")));
            }
            int i = 3;
            while (!StringUtil.isNull(path) && i > 0) {
                File f = new File(path + defaultConfigFile);
                if (f.isFile()) {
                    path = FileUtil.getPathPart(f.getAbsolutePath());
                    break;
                }
                i--;
            }
            if (!FileUtil.isFileExist(path)) {
                path = null;
            }
        }

        /**
         * 查找 classpath 路径
         */
        if (path == null) {
            String classpath = System.getProperty("java.class.path");
            String[] listDir = StringUtil.split(classpath, StringUtil.SEMICOLON);
            for (String fileDir : listDir) {
                path = FileUtil.getPathPart(fileDir) + defaultConfigFile;
                if (FileUtil.isFileExist(path)) {
                    break;
                }
            }
            if (!FileUtil.isFileExist(path)) {
                path = null;
            }
        }

        if (path == null) {
            path = FileUtil.getPathPart(ClassUtil.getClassFilePath(JarDefaultConfig.class.getName())) + defaultConfigFile;
            if (!FileUtil.isFileExist(path)) {
                path = null;
            }
        }

        if (path == null) {
            File file = new File(FileUtil.mendPath(System.getProperty(" user.dir")) + defaultConfigFile);
            if (file.isFile() && file.exists()) {
                path = file.getParentFile().getAbsolutePath();
            } else {
                path = null;
            }
        }
        if (path == null) {
            InputStream inputStream = BaseConfigurationImpl.class.getResourceAsStream("/resources/" + defaultConfigFile);
            if (inputStream != null) {
                String temp = FileUtil.readInputStream(inputStream);
                File file = new File(FileUtil.mendPath(System.getProperty(" user.dir")) + defaultConfigFile);
                if (file.isFile()) {
                    file.delete();
                }
                AbstractWrite write = new WriteFile();
                write.setFile(Environment.defaultEncode);
                write.setFile(file.getAbsolutePath());
                if (write.setContent(temp)) {
                    path = file.getAbsolutePath();
                }
            }
        }

        if (path == null) {
            url = ClassUtil.getResource(defaultConfigFile);
            if (url != null) {
                path = url.getPath();
            }
        }

        if (path == null) {
            File directory = new File("");
            File file = new File(directory.getParent(), defaultConfigFile);
            if (file.isFile() && file.exists()) {
                path = file.getParentFile().getAbsolutePath();
            } else {
                path = null;
            }
        }

        if (path == null) {
            log.info("No find file:" + defaultConfigFile + ",不能找到" + defaultConfigFile + "文件");
        } else {
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
        defaultPath = FileUtil.mendPath(file.getAbsolutePath());
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
        if (StringUtil.isNull(defaultPath)) {
            checkLoader();
        }
        return defaultPath;
    }

    /**
     * @return 得到配置文件
     */
    @Override
    public String getIocConfigFile() {

        return defaultPath + Environment.config_file;
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