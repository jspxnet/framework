/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.load;


import com.github.jspxnet.scriptmark.SourceLoader;

import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.SystemUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;
import java.io.FileNotFoundException;
import java.util.Locale;
import java.net.MalformedURLException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:48:12
 */
@Slf4j
public abstract class AbstractSourceLoader implements SourceLoader {

    static private String defaultEncoding = SystemUtil.encode;
    //当前路径
    private String currentPath = StringUtil.empty;
    //根路径,只能在这里路径里边
    private String rootDirectory = null;
    private String fileName = null;

    @Override
    public Source getSource(String name) throws FileNotFoundException, MalformedURLException {
        return getSource(name, defaultEncoding);
    }

    @Override
    public Source getSource(String name, String encoding) throws FileNotFoundException, MalformedURLException {

        this.fileName = name;
        int i = name.indexOf("/*/");
        if (name.startsWith("/")) {
            if (i > -1) {
                String path = rootDirectory + name.substring(0, i + 1) + name.substring(i + 3);
                try {
                    return loadResource(path, encoding);
                } catch (IOException e) {
                    if (i > 0) {
                        int j = path.lastIndexOf('/', i - 1);
                        while (j > -1) {
                            path = path.substring(0, j) + path.substring(i);
                            try {
                                return loadResource(path, encoding);
                            } catch (IOException e1) {
                                if (j == 0) {
                                    break;
                                }
                                i = j;
                                j = path.lastIndexOf('/', i - 1);
                            }
                        }
                    }
                }
                throw new FileNotFoundException("通配目录查找失败，*号之上的所有目录均没有找到该文件: " + name);
            }
            return loadResource(rootDirectory + name, encoding);

        } else {

            if (i > -1) {
                String path = currentPath + name.substring(0, i + 1) + name.substring(i + 3);
                try {
                    return loadResource(path, encoding);
                } catch (IOException e) {
                    if (i > 0) {
                        int j = path.lastIndexOf('/', i - 1);
                        while (j > -1) {
                            path = path.substring(0, j) + path.substring(i);
                            try {
                                return loadResource(path, encoding);
                            } catch (IOException e1) {
                                if (j == 0) {
                                    break;
                                }
                                i = j;
                                j = path.lastIndexOf('/', i - 1);
                            }
                        }
                    }
                }
                throw new FileNotFoundException("通配目录查找失败，*号之上的所有目录均没有找到该文件: " + name);
            }
            return loadResource(currentPath + name, encoding);
        }
    }

    /**
     * 这里菜是
     *
     * @param abspath  结对路径
     * @param encoding 编码
     * @return 读取数据
     * @throws java.io.FileNotFoundException  文件不存在
     * @throws java.net.MalformedURLException 格式错误
     */
    private Source loadResource(String abspath, String encoding) throws FileNotFoundException, MalformedURLException {
        if (rootDirectory != null && !abspath.startsWith(abspath)) {
            FileNotFoundException e = new FileNotFoundException("sdk.security  error: " + abspath + " not in " + rootDirectory);
            log.info("sdk.security  error: " + abspath + " not in " + rootDirectory + " 目录安全错误,文件不能超出根目录", e);
            throw e;
        }
        return loadResource(abspath, fileName, encoding);
    }

    @Override
    public Source getSource(String name, Locale locale) throws IOException {
        return getSource(name, locale, defaultEncoding);
    }

    @Override
    public Source getSource(String name, Locale locale, String encoding)
            throws IOException {
        if (locale != null) {
            try {
                return getSource(getLanguageName(name, locale.getLanguage()));
            } catch (IOException e) {
                try {
                    return getSource(getLanguageName(name, locale.getLanguage()));
                } catch (IOException e2) {
                    return getSource(name, encoding);
                }
            }
        }
        return getSource(name, encoding);
    }

    private String getLanguageName(String name, String language) {
        int i = name.lastIndexOf('.');
        if (i > -1) {
            return name.substring(0, i) + "_" + language + name.substring(i);
        }
        return name + "_" + language;
    }

    protected abstract Source loadResource(String path, String name, String encoding) throws FileNotFoundException, MalformedURLException;

    public final String getRootDirectory() {
        return rootDirectory;
    }

    public final void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }
}