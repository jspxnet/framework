/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark;

import com.github.jspxnet.scriptmark.core.TagNode;

import java.util.List;
import java.util.Map;
import java.io.Serializable;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-11-14
 * Time: 11:44:27
 */
public interface Configurable extends Serializable, Cloneable {
    long getLong(String name);

    void addStaticModels(String str);

    Map<String, Phrase> getPhrases();

    void setAutoIncludes(String[] autoIncludes);

    void setSearchPath(String[] searchPath);

    void setTagMap(Map<String, String> tagMap);

    String[] getSearchPath();

    String removeTag(String name);

    void addAutoIncludes(String file);

    String[] getAutoIncludes();

    Map<String, String> getTagMap();

    void setTag(String name, String className);

    String[] getStaticModels();

    void setStaticModels(String[] staticModels);

    void put(String name, Object o);

    String getString(String name);

    int getInt(String name);

    boolean getBoolean(String name);

    void setGlobalMap(Map<String, Object> globalMap);

    Map<String, Object> getGlobalMap();

    void setHashMap(Map<String, Object> hashMap);

    void addAutoImports(String str);

    String[] getAutoImports();

    void setAutoImports(String[] autoImports);

    List<TagNode> getAutoImportTagNodeList();

    void setAutoImportTagNodeList(List<TagNode> autoImportTagNodeList);

    Configurable copy();

}