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

import com.github.jspxnet.txweb.table.TreeItem;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-7-12
 * Time: 0:31:37
 * Xtree 树的结构生成DAO  dtree 和 loadTree都可以使用
 */
public class XTreeItemDAOImpl extends TreeItemDAOImpl {

    /**
     * 生成js 树的节点代码,的规调用
     *
     * @return String  返回tree
     */
    private String getItemJs(TreeItem node) {
        if (node == null) {
            return StringUtil.empty;
        }
        if (!show && node.getHide() == 1) {
            return StringUtil.empty;
        }

        StringBuilder result = new StringBuilder();
        if (!StringUtil.isNull(input)) {
            StringBuilder title = new StringBuilder();
            if (useLimb == isLimb(node.getNodeId())) {
                if (!StringUtil.isNull(input)) {
                    if (TYPE_CHECKBOX.equalsIgnoreCase(input) || TYPE_RADIO.equalsIgnoreCase(input)) {
                        title.append("<input type=").append(input).append(" name=").append(inputName).append(" value=").append(node.getNodeId());
                    } else {
                        title.append("<input type=").append(input).append(" name=").append(node.getNodeId()).append(" value=").append(node.getItemValue());
                    }
                    if (TYPE_CHECKBOX.equalsIgnoreCase(input) || TYPE_RADIO.equalsIgnoreCase(input)) {
                        if (ArrayUtil.inArray(selected, node.getNodeId(), false)) {
                            title.append(" checked");
                        }
                    } else {
                        title.append(" value=").append(ArrayUtil.inArray(selected, node.getNodeId(), false));
                    }
                    title.append(">&nbsp;");
                }
            }

            title.append(node.getCaption());
            /////有输入框的情况
            if (StringUtil.isNull(node.getIcon()) || StringUtil.isNull(node.getOpenIcon())) {
                result.append("  var ").append(node.getNodeId()).append("= new WebFXTreeItem('").append(title.toString()).append("','javascript:").append(namespace).append("TreeClick(\\'").append(node.getNodeId()).append("\\')',").append(node.getParentNodeId()).append(");\r\n");
            } else {
                result.append("  var ").append(node.getNodeId()).append("= new WebFXTreeItem('").append(title.toString()).append("','javascript:").append(namespace).append("TreeClick(\\'").append(node.getNodeId()).append("\\')',").append(node.getParentNodeId()).append(",'").append(node.getIcon()).append("','").append(node.getOpenIcon()).append("');\r\n");
            }
        } else {
            ///////////无输入框的情况
            if (StringUtil.isNull(node.getIcon()) || StringUtil.isNull(node.getOpenIcon())) {
                result.append("     var ").append(node.getNodeId()).append("= new WebFXTreeItem('").append(node.getCaption()).append("','javascript:").append(namespace).append("TreeClick(\\'").append(node.getNodeId()).append("\\')',").append(node.getParentNodeId()).append(");\r\n");
            } else {
                result.append("     var ").append(node.getNodeId()).append("= new WebFXTreeItem('").append(node.getCaption()).append("','javascript:").append(namespace).append("TreeClick(\\'").append(node.getNodeId()).append("\\')',").append(node.getParentNodeId()).append(",'").append(node.getIcon()).append("','").append(node.getOpenIcon()).append("');\r\n");
            }
        }
        for (TreeItem nNode : getChildTreeItem(node.getNodeId())) {
            if (checked == null || checked.length < 1) {
                result.append(getItemJs(nNode));
            } else if (ArrayUtil.inArray(checked, nNode.getNodeId(), true)) {
                result.append(getItemJs(nNode));
            }

        }
        return result.toString();
    }

    /**
     * @param node     节点
     * @param show     显示
     * @param selected 选择节点
     * @return 节点js
     */
    private String getItemJs(TreeItem node, boolean show, String[] selected) {
        if (node == null) {
            return StringUtil.empty;
        }
        if (!show && node.getHide() == 1) {
            return StringUtil.empty;
        }

        StringBuilder result = new StringBuilder();
        StringBuilder title = new StringBuilder();
        if (useLimb == isLimb(node.getNodeId())) {
            if (!StringUtil.isNull(input)) {
                if (TYPE_CHECKBOX.equalsIgnoreCase(input) || TYPE_RADIO.equalsIgnoreCase(input)) {
                    title.append("<input type=").append(input).append(" name=").append(node.getNodeId()).append(" value=").append(node.getNodeId());
                } else {
                    title.append("<input type=").append(input).append(" name=").append(node.getNodeId()).append(" value=").append(node.getItemValue());
                }
                if (TYPE_CHECKBOX.equalsIgnoreCase(input) || TYPE_RADIO.equalsIgnoreCase(input)) {
                    title.append(" checked=").append(ArrayUtil.inArray(selected, node.getNodeId(), false));
                } else {
                    title.append(" value=").append(ArrayUtil.inArray(selected, node.getNodeId(), false));
                }
                title.append(">");
            }
        }

        title.append(node.getCaption());
        result.append("<tree id=\"").append(node.getNodeId()).append("\" html=\"").append(XMLUtil.escape(title.toString())).append("\" view=\"javascript:").append(namespace).append("TreeClick('").append(node.getNodeId()).append("')\" icon=\"").append(node.getIcon()).append("\" openIcon=\"").append(node.getOpenIcon()).append("\"");
        if (isLimb(node.getNodeId())) {
            result.append(" src=\"").append("?nodeId=").append(node.getNodeId()).append("\"");
        }
        result.append(">\r\n");
        result.append("</tree>\r\n");
        return result.toString();
    }


    /**
     * @return 得到树结构
     */
    public String getTreeSrc() {
        TreeItem rootNode = getRootTreeItem();
        if (rootNode == null) {
            rootNode = createRootTreeItem(namespace);
        }
        StringBuilder result = new StringBuilder();
        result.append("if (document.getElementById) {\r\n");
        if (StringUtil.isNull(input)) {
            result.append(" var ").append(rootNode.getNodeId()).append("= new WebFXTree('").append(rootNode.getCaption()).append("','javascript:").append(namespace).append("TreeClick(\\'").append(rootNode.getNodeId()).append("\\')','").append(rootNode.getIcon()).append("','").append(rootNode.getOpenIcon()).append("');\r\n");
        } else {
            result.append(" var ").append(rootNode.getNodeId()).append("= new WebFXTree('").append(rootNode.getCaption()).append("','javascript:").append(namespace).append("TreeClick(\\'").append(rootNode.getNodeId()).append("\\')','").append(rootNode.getIcon()).append("','").append(rootNode.getOpenIcon()).append("');\r\n");
        }
        result.append("    ").append(rootNode.getNodeId()).append(".setBehavior('").append(behavior).append("');\r\n");

        for (TreeItem nNode : getChildTreeItem(rootNode.getNodeId())) {
            if (checked == null || checked.length < 1) {
                result.append(getItemJs(nNode));
            } else if (ArrayUtil.inArray(checked, nNode.getNodeId(), true)) {
                result.append(getItemJs(nNode));
            }
        }

        result.append(" if (").append(rootNode.getNodeId()).append(".write)").append(rootNode.getNodeId()).append(".write(); else document.write(root);\r\n}\r\n");
        return result.toString();
    }

    /**
     * @param nodeId 节点
     * @return 得到一个子节点的树结构
     */
    public String getChildSrc(String nodeId) {
        TreeItem rootNode = getTreeItem(nodeId);
        if (rootNode == null) {
            return toXMLString();
        }
        StringBuilder result = new StringBuilder("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n");
        result.append("<tree>\r\n");
        for (TreeItem nNode : getChildTreeItem(rootNode.getNodeId())) {
            if (checked == null || checked.length < 1) {
                result.append(getItemJs(nNode, show, selected));
            } else if (ArrayUtil.inArray(checked, nNode.getNodeId(), true)) {
                result.append(getItemJs(nNode, show, selected));
            }
        }
        result.append("</tree>\r\n");
        return result.toString();
    }


    @Override
    public String getTreeSrc(String type) {
        return null;
    }

    @Override
    public String getXmlChildSrc(String nodeId) {
        return null;
    }

    @Override
    public String getJsonChildSrc(String nodeId) {
        return null;
    }
}