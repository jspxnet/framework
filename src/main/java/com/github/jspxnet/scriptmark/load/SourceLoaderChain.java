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

import java.util.List;
import java.util.ArrayList;
import java.util.Locale;
import java.io.IOException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 14:59:28
 */
public class SourceLoaderChain implements SourceLoader {
    private List<SourceLoader> resourceLoaders;

    public void setResourceLoaders(List<SourceLoader> resourceLoaders) {
        this.resourceLoaders = resourceLoaders;
    }

    public void addResourceLoader(SourceLoader resourceLoader) {
        if (this.resourceLoaders == null) {
            this.resourceLoaders = new ArrayList<SourceLoader>();
        }
        this.resourceLoaders.add(resourceLoader);
    }

    @Override
    public Source getSource(String name, String encoding)
            throws IOException {
        for (Object resourceLoader1 : resourceLoaders) {
            try {
                SourceLoader resourceLoader = (SourceLoader) resourceLoader1;
                if (resourceLoader != null) {
                    Source resource = resourceLoader.getSource(name, encoding);
                    if (resource != null) {
                        return resource;
                    }
                }
            } catch (IOException e) {
                // 忽略，继续取下一loader
            }
        }
        try {
            throw new Exception("文件没有找到");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Source getSource(String name) throws IOException {
        for (Object resourceLoader1 : resourceLoaders) {
            try {
                SourceLoader resourceLoader = (SourceLoader) resourceLoader1;
                Source resource = resourceLoader.getSource(name);
                if (resource != null) {
                    return resource;
                }
            } catch (IOException e) {
                // 忽略，继续取下一loader
            }
        }
        try {
            throw new Exception("文件没有找到");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Source getSource(String name, Locale locale) throws IOException {
        for (Object resourceLoader1 : resourceLoaders) {
            try {
                SourceLoader resourceLoader = (SourceLoader) resourceLoader1;
                Source resource = resourceLoader.getSource(name, locale);
                if (resource != null) {
                    return resource;
                }
            } catch (IOException e) {
                // 忽略，继续取下一loader
            }
        }
        try {
            throw new Exception("文件没有找到");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Source getSource(String name, Locale locale, String encoding)
            throws IOException {
        for (SourceLoader resourceLoader : resourceLoaders) {
            try {
                Source resource = resourceLoader.getSource(name, locale, encoding);
                if (resource != null) {
                    return resource;
                }
            } catch (IOException e) {
                // 忽略，继续取下一loader
            }
        }
        try {
            throw new Exception("文件没有找到");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String toString() {
        return resourceLoaders.toString();
    }

}