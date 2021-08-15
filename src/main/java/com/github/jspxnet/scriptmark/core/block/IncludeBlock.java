/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.block;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.scriptmark.util.ScriptMarkUtil;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.TemplateElement;
import com.github.jspxnet.scriptmark.load.*;

import java.io.IOException;
import java.util.List;
import java.net.URL;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-16
 * Time: 10:57:33
 *
 * <pre>{@code
 * <#include file="xxxx.ftl" encoding=StandardCharsets.UTF_8.name()/> //文件代码方式读取
 *
 * <#include file="http://xxxx.ftl" encoding=StandardCharsets.UTF_8.name() varName="表达式"  caption="模块说明"/>  //URL方式读取
 *
 * }</pre>
 */
public class IncludeBlock extends TagNode {
    public static final String file = "file";
    public static final String encoding = "encoding";
    public static final String caption = "caption";
    public static final String name = "name";


    //当前路径
    private String currentPath = null;
    private String rootDirectory = null;

    public IncludeBlock() {
        repair = true;
    }

    public String getCurrentPath() {
        return currentPath;
    }

    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    public String getRootDirectory() {
        return rootDirectory;
    }

    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    public String getFile() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute(file));
    }

    public String getEncoding() {
        String encode = ScriptMarkUtil.deleteQuote(getStringAttribute(encoding));
        if (encode == null || encode.length() < 2) {
            encode = Environment.defaultEncode;
        }
        return encode;
    }

    public String getCaption() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute(caption));
    }

    public String getName() {
        return ScriptMarkUtil.deleteQuote(getStringAttribute(name));
    }

    public List<TagNode> getIncludeNodeList() throws ScriptRunException {
        String encode = getEncoding();
        String fileName = getFile();

        //${sitePath}
        Source source = null;
        if (fileName != null) {
            if (fileName.startsWith("http://") || fileName.startsWith("https://")) {
                try {
                    source = new URLSource(new URL(fileName), fileName, encode);
                } catch (Exception e) {
                    throw new ScriptRunException(this, fileName);
                }
            } else {
                FileSourceLoader fileSourceLoader = new FileSourceLoader();
                fileSourceLoader.setCurrentPath(currentPath);
                fileSourceLoader.setRootDirectory(rootDirectory);
                try {
                    source = fileSourceLoader.getSource(fileName, encode);
                } catch (Exception e) {
                    throw new ScriptRunException(this, fileName);
                }
            }
        }
        if (source == null) {
            fileName = ScriptMarkUtil.deleteQuote(getAttributes());
            FileSourceLoader fileSourceLoader = new FileSourceLoader();
            fileSourceLoader.setCurrentPath(currentPath);
            fileSourceLoader.setRootDirectory(rootDirectory);
            try {
                source = fileSourceLoader.getSource(fileName, encode);
            } catch (Exception e) {
                throw new ScriptRunException(this, fileName);
            }
        }

        TemplateElement tempTemplateEl = null;
        try {
            tempTemplateEl = new TemplateElement(source.getSource(), source.getLastModified(), getTemplate().getConfigurable());
            return tempTemplateEl.getBlockTree(source.getSource(), getTemplate().getConfigurable().getTagMap());
        } catch (IOException e) {
            throw new ScriptRunException(this, fileName);
        }

    }
}