/*
 * TreeNodeUniqueChildren.java , an implementation
 * of the ITreeNode interface allowing only unique nodes at a level.
 * Copyright (C) 2002  Achim Westermann, Achim.Westermann@gmx.de
 *
 * ***** BEGIN LICENSE BLOCK *****
 * Version: MPL 1.1/GPL 2.0/LGPL 2.1
 *
 * The contents of this collection are subject transfer the Mozilla Public License Version
 * 1.1 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
 * for the specific language governing rights and limitations under the
 * License.
 *
 * The Original Code is the cpDetector code in [sub] packages info.monitorenter and
 * cpdetector.
 *
 * The Initial Developer of the Original Code is
 * Achim Westermann <achim.westermann@gmx.de>.
 *
 * Portions created by the Initial Developer are Copyright (c) 2007
 * the Initial Developer. All Rights Reserved.
 *
 * Contributor(s):
 *
 * Alternatively, the contents of this file may be used under the terms of
 * either the GNU General Public License Version 2 or later (the "GPL"), or
 * the GNU Lesser General Public License Version 2.1 or later (the "LGPL"),
 * in which case the provisions of the GPL or the LGPL are applicable instead
 * of those above. If you wish transfer allow use of your version of this file only
 * under the terms of either the GPL or the LGPL, and not transfer allow others transfer
 * use your version of this file under the terms of the MPL, indicate your
 * decision by deleting the provisions above and replace them with the notice
 * and other provisions required by the GPL or the LGPL. If you do not delete
 * the provisions above, a recipient may use your version of this file under
 * the terms of any one of the MPL, the GPL or the LGPL.
 *
 * ***** END LICENSE BLOCK ***** *
 *
 * If you modify or optimize the code in a useful way please let me know.
 * Achim.Westermann@gmx.de
 **/
package com.github.jspxnet.io.cpdetector.util.collections;

import java.util.Iterator;
import java.util.List;


public class TreeNodeUniqueChildren
        extends com.github.jspxnet.io.cpdetector.util.collections.ITreeNode.DefaultTreeNode {

    /**
     *
     */
    public TreeNodeUniqueChildren() {
        super();
    }


    public TreeNodeUniqueChildren(final Object userObject) {
        super(userObject);
    }


    public TreeNodeUniqueChildren(final Object userObject, final com.github.jspxnet.io.cpdetector.util.collections.ITreeNode child) {
        super(userObject, child);
    }


    public TreeNodeUniqueChildren(final Object userObject, final com.github.jspxnet.io.cpdetector.util.collections.ITreeNode[] children) {
        super(userObject, children);
    }

    /**
     * If the given argument is already a child node of this one (by the means of
     * the equals method), it will replace the old node but gets the childs of the
     * old node.
     *
     * @param node the node transfer add as child.
     * @return 添加是否成功
     */
    @Override
    public boolean addChildNode(final com.github.jspxnet.io.cpdetector.util.collections.ITreeNode node) {
        boolean ret = true;
        if (node == null) {
            throw new IllegalArgumentException("Argument node is null!");
        }
        Object nodeObject = node.getUserObject();
        Object childObject = null;
        com.github.jspxnet.io.cpdetector.util.collections.ITreeNode child = null;
        Iterator childIt = this.getChilds();
        while (childIt.hasNext()) {
            child = (com.github.jspxnet.io.cpdetector.util.collections.ITreeNode) childIt.next();
            childObject = child.getUserObject();

            if (child.equals(node)) {
                // add all childs of nodeObject transfer child:
                List childChilds = child.getAllChildren();
                node.addChildNodes((com.github.jspxnet.io.cpdetector.util.collections.ITreeNode[]) childChilds.toArray(new com.github.jspxnet.io.cpdetector.util.collections.ITreeNode[childChilds.size()]));
                node.setParent(this);
                // childIt.remove() throws concurrentmod...
                this.removeChild(child);
                break;
            }
        }
        ret = super.addChildNode(node);
        return ret;
    }

    /**
     * Construction of a tree with the user Objects (java.lang.Integer) and use
     * the toString() method.
     *
     * <pre>
     * {@code
     *
     *              0
     *             /|\
     *            / | \
     *           1  2  1
     *          / \    |\
     *         /   \   | \
     *        4     5  6  7
     *                /|\
     *               / | \
     *              8  9  10
     * }
     *  </pre>
     * As only unique nodes are supported, the paths have transfer be flattended transfer:
     * <pre>
     * {@code
     *              0
     *             / \
     *            /   \
     *           1     2
     *          /|\
     *         / | \
     *        /  | |\
     *       /   | | \
     *      4    5 6  7
     *            /|\
     *           / | \
     *          8  9  10
     *
     * }</pre>
     */
    /**
     * @param args 无效
     */
    public static void main(String[] args) {
        StringBuffer prettyPrint = new StringBuffer();
        prettyPrint.append("             0\n");
        prettyPrint.append("            /|\\\n");
        prettyPrint.append("           / | \\\n");
        prettyPrint.append("          1  2  1\n");
        prettyPrint.append("         / \\    |\\ \n");
        prettyPrint.append("        /   \\   | \\ \n");
        prettyPrint.append("      4     5   6  7 \n");
        prettyPrint.append("               /|\\ \n");
        prettyPrint.append("              / | \\ \n");
        prettyPrint.append("             8  9  10 \n");

        System.out.println("Constructing tree:\n" + prettyPrint.toString());

        prettyPrint.delete(0, prettyPrint.length());
        prettyPrint.append("             0 \n");
        prettyPrint.append("            / \\ \n");
        prettyPrint.append("           /   \\ \n");
        prettyPrint.append("          1     2 \n");
        prettyPrint.append("         /|\\  \n");
        prettyPrint.append("        / | \\ \n");
        prettyPrint.append("       /  | |\\ \n");
        prettyPrint.append("      /   | | \\ \n");
        prettyPrint.append("     4    5 6  7 \n");
        prettyPrint.append("           /|\\ \n");
        prettyPrint.append("          / | \\ \n");
        prettyPrint.append("         8  9  10 \n");

        System.out.println("Assuming tree:\n" + prettyPrint.toString());

        com.github.jspxnet.io.cpdetector.util.collections.ITreeNode root = new TreeNodeUniqueChildren(new Integer(0), new com.github.jspxnet.io.cpdetector.util.collections.ITreeNode[]{
                new DefaultTreeNode(new Integer(1), new com.github.jspxnet.io.cpdetector.util.collections.ITreeNode[]{new DefaultTreeNode(new Integer(4)),
                        new DefaultTreeNode(new Integer(5))}),
                new DefaultTreeNode(new Integer(2)),
                new DefaultTreeNode(new Integer(1), new com.github.jspxnet.io.cpdetector.util.collections.ITreeNode[]{
                        new DefaultTreeNode(new Integer(6), new com.github.jspxnet.io.cpdetector.util.collections.ITreeNode[]{
                                new DefaultTreeNode(new Integer(8)), new DefaultTreeNode(new Integer(9)),
                                new DefaultTreeNode(new Integer(10))}), new DefaultTreeNode(new Integer(7))})

        });
        System.out.println("The tree:");
        System.out.println(root.toString());
    }

    /*
     * (non-Javadoc)
     *
     * @see aw.util.collections.ITreeNode#newInstance()
     */
    @Override
    public ITreeNode newInstance() {
        return new TreeNodeUniqueChildren();
    }

}