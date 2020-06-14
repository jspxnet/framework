/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.json.JSONException;
import com.github.jspxnet.txweb.table.TreeItem;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.json.JSONArray;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-9-18
 * Time: 14:56:53
 * jspx.net file ui 树的XML结构生成
 */

public class JTreeItemDAOImpl extends TreeItemDAOImpl {

    public JTreeItemDAOImpl() {

    }

    private String getXmlTree() {
        TreeItem rootNode = getRootTreeItem();
        if (rootNode == null) {
            rootNode = createRootTreeItem(namespace);
        }
        String[] selectArray = selected;
        StringBuilder result = new StringBuilder();
        result.append("<tree>\r\n");
        result.append(rootNode.getXmlString(ArrayUtil.inArray(selectArray, rootNode.getNodeId(), true)));
        for (TreeItem nNode : getList()) {
            if (checked == null || checked.length < 1) {
                result.append(nNode.getXmlString(ArrayUtil.inArray(selectArray, nNode.getNodeId(), true)));
            } else if (ArrayUtil.inArray(checked, nNode.getNodeId(), true)) {
                result.append(nNode.getXmlString(ArrayUtil.inArray(selectArray, nNode.getNodeId(), true)));
            }
        }
        result.append("</tree>\r\n");
        return result.toString();
    }


    private String getJson(String treeType) {
        return getJson(getList(), treeType, selected, checked, getNamespace());
    }


    private String getJsonTree(String treeType) {
        try {
            return getJsonTree(getList(), treeType, selected, checked, getNamespace()).toString(4);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return StringUtil.empty;
    }

    @Override
    public String getTreeSrc(String type) {
        if (Type_json.equalsIgnoreCase(type)) {
            return getJson("none");
        }
        if (Type_roleJson.equalsIgnoreCase(type)) {
            return getJson("role");
        }
        if (Type_jsonTree.equalsIgnoreCase(type)) {
            return getJsonTree("none");
        }
        if (Type_roleJsonTree.equalsIgnoreCase(type)) {
            return getJsonTree("role");
        }
        return getXmlTree();
    }

    @Override
    public String getXmlChildSrc(String nodeId) {
        TreeItem rootNode = getTreeItem(nodeId);
        if (rootNode == null) {
            return toXMLString();
        }

        StringBuilder result = new StringBuilder();
        result.append("<tree>\r\n");
        for (TreeItem nNode : getChildTreeItem(rootNode.getNodeId())) {
            if (ArrayUtil.isEmpty(checked)) {
                result.append(nNode.getXmlString(ArrayUtil.inArray(selected, nNode.getNodeId(), true)));
            } else if (ArrayUtil.inArray(checked, nNode.getNodeId(), true)) {
                result.append(nNode.getXmlString(ArrayUtil.inArray(selected, nNode.getNodeId(), true)));
            }
        }
        result.append("</tree>\r\n");
        return result.toString();
    }

    @Override
    public String getJsonChildSrc(String nodeId) {
        TreeItem rootNode = getTreeItem(nodeId);
        if (rootNode == null) {
            return "{}";
        }

        String treeName = getNamespace();
        JSONArray jsona = new JSONArray();
        for (TreeItem nNode : getChildTreeItem(rootNode.getNodeId())) {
            JSONObject jsono = new JSONObject();
            jsono.put("id", nNode.getNodeId());
            jsono.put("icon", nNode.getIcon());
            jsono.put("openIcon", nNode.getOpenIcon());
            jsono.put("inputType", nNode.getInputType());
            jsono.put("text", nNode.getCaption());
            jsono.put("pid", nNode.getParentNodeId());
            jsono.put("value", nNode.getItemValue());
            jsono.put("action", treeName + "Click('" + nNode.getNodeId() + "');");
            jsono.put("checked", ArrayUtil.inArray(selected, nNode.getNodeId(), true));
            if (checked == null || checked.length < 1) {
                jsona.put(jsono);
            } else if (ArrayUtil.inArray(checked, nNode.getNodeId(), true)) {
                jsona.put(jsono);
            }
        }
        return jsona.toString();
    }
}