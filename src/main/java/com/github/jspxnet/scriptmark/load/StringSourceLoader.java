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

import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;

import java.util.Map;
import java.util.HashMap;
import java.net.MalformedURLException;
import java.io.FileNotFoundException;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-13
 * Time: 13:59:02
 */
public class StringSourceLoader extends AbstractSourceLoader {

    private final Map<String, Source> resources = new HashMap<String, Source>();

    public boolean hasTemplate(String name) {
        return resources.containsKey(name);
    }

    public void addTemplate(String name, String source) {
        try {
            name = ScriptMarkUtil.cleanUrl(name);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        this.resources.put(name, new StringSource(source));
    }

    @Override
    protected Source loadResource(String path, String name, String encoding)
            throws FileNotFoundException {
        Source resource = resources.get(name);
        if (resource == null) {
            throw new FileNotFoundException("Not fount resouce: " + path);
        }
        return resource;
    }

}