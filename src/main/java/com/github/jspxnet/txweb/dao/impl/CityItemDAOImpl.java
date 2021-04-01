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

import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.txweb.dao.CityItemDAO;
import com.github.jspxnet.txweb.table.CityItem;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-3-13
 * Time: 下午3:32
 */
@Slf4j
public class CityItemDAOImpl extends JdbcOperations implements CityItemDAO {

    protected String namespace; //命名空间

    public CityItemDAOImpl() {

    }


    public CityItem createRootCityItem() {
        CityItem root = new CityItem();
        root.setId("root");
        root.setCaption("root");
        root.setDescription("root");
        root.setIp("127.0.0.1");
        root.setPutName("admin");
        root.setPutUid(0);
        root.setNamespace(namespace);
        root.setHide(0);
        return root;
    }

    static public CityItem fixAttribute(CityItem boxattr) {
        if (boxattr == null) {
            boxattr = new CityItem();
            boxattr.setPassword("");
            boxattr.setRoleIds("");
            boxattr.setShowImage("");
            boxattr.setDescription("");
            boxattr.setLinkPage("");

            boxattr.setPutName("admin");
            boxattr.setPutUid(0);
            boxattr.setIp("127.0.0.1");
            return boxattr;
        }
        return boxattr;
    }


    /**
     * 得到首页的排序
     *
     * @param nodeType 节点类型
     * @return 得到首页的排序
     */
    @SuppressWarnings("unchecked")
    public List<CityItem> getListForType(int nodeType) {
        List<CityItem> result = new ArrayList<CityItem>();
        for (CityItem treeItem : getList()) {
            if (nodeType == treeItem.getCityType()) {
                result.add(treeItem);
            }
        }
        Collections.sort(result, new SortTypeComparator());
        return result;
    }


    /**
     * 得到页面列表
     *
     * @param find   查询
     * @param ipage  页数
     * @param icount 一页行数
     * @return 得到列表
     */
    public List<CityItem> getCityItemForFind(final String find, final int ipage, final int icount) {
        Criteria criteria = createCriteria(CityItem.class);
        if (!StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.or(Expression.like("description", "%" + find + "%"), Expression.like("caption", "%" + find + "%")));
        }
        return criteria.addOrder(Order.desc("sortType")).addOrder(Order.desc("sortDate")).addOrder(Order.desc("createDate"))
                .setCurrentPage(ipage).setTotalCount(icount).list(false);

    }


    /**
     * @param find 查询
     * @return 数量
     */
    public long getCountForFind(String find) {
        Criteria criteria = createCriteria(CityItem.class);
        if (StringUtil.hasLength(find)) {
            criteria = criteria.add(Expression.or(Expression.like("description", "%" + find + "%"), Expression.like("caption", "%" + find + "%")));
        }
        return criteria.setProjection(Projections.rowCount()).longUniqueResult();
    }

    /**
     * 排序
     *
     * @param checkbox 参数
     * @param sortType 排序
     * @return 是否成功
     */
    public boolean editShowSortType(String[] checkbox, int sortType) {
        if (null == checkbox) {
            return true;
        }
        try {
            for (String aCheckbox : checkbox) {
                if (StringUtil.isNull(aCheckbox)) {
                    continue;
                }
                CityItem treeItem = get(CityItem.class, aCheckbox);
                if (treeItem != null) {
                    treeItem.setSortType(sortType);
                    update(treeItem);
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(checkbox, StringUtil.COMMAS), e);
            return false;
        }
        clear();
        return true;
    }

    /**
     * 提前
     *
     * @param checkbox 参数
     * @return 提前是否成功
     */
    public boolean editSortDate(String[] checkbox) {
        if (null == checkbox) {
            return true;
        }
        try {
            for (String aCheckbox : checkbox) {
                if (!StringUtil.hasLength(aCheckbox)) {
                    continue;
                }
                CityItem treeItem = get(CityItem.class, aCheckbox);
                if (treeItem != null) {
                    treeItem.setSortDate(new Date());
                    update(treeItem);
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(checkbox, StringUtil.COMMAS), e);
            return false;
        }
        clear();
        return true;
    }

    /**
     * 删除树枝节点，保留叶子部分
     *
     * @param nodeId 节点id
     * @return 删除的id集合
     */
    public String[] deleteLimb(String[] nodeId) {
        if (nodeId == null || nodeId.length < 1) {
            return nodeId;
        }
        String[] result = null;
        for (String id : nodeId) {
            if (!isLimb(id)) {
                result = ArrayUtil.add(result, id);
            }
        }
        return result;
    }


    /**
     * 为了方便ManTree 保持权限的时候加入中间节点
     *
     * @param nodeId 节点id
     * @return 是否成
     */
    public String[] addLimb(String[] nodeId) {
        if (nodeId == null || nodeId.length < 1) {
            return nodeId;
        }
        String[] result = null;
        for (String id : nodeId) {
            List<CityItem> treeItemList = getCityItemPath(id);
            if (!treeItemList.isEmpty()) {
                for (CityItem treeItem : treeItemList) {
                    if (!ArrayUtil.inArray(result, treeItem.getId(), true)) {
                        result = ArrayUtil.add(result, treeItem.getId());
                    }
                }
            }
        }
        return result;
    }


    /**
     * @param nodeId 节点id
     * @return 判断是否为树枝节点
     */
    public boolean isLimb(String nodeId) {
        if (nodeId == null) {
            return false;
        }
        List<CityItem> list = getList();
        for (CityItem treeItem : list) {
            if (nodeId.equals(treeItem.getParentId())) {
                return true;
            }
        }
        return false;
    }


    /**
     * @param nodeId 节点id
     * @return CityItem 得到  CityItem
     */
    public CityItem getCityItem(String nodeId) {
        if (nodeId == null) {
            return null;
        }
        List<CityItem> list = getList();
        for (CityItem treeItem : list) {
            if (treeItem.getId().equals(nodeId)) {
                return treeItem;
            }
        }
        return null;
    }


    /**
     * @param nodeId 得到节点
     * @return 得到子节点列表
     */
    @SuppressWarnings("unchecked")
    public List<CityItem> getChildCityItem(String nodeId) {
        List<CityItem> result = new ArrayList<CityItem>();
        for (CityItem treeItem : getList()) {
            if (StringUtil.hasLength(treeItem.getParentId()) && treeItem.getParentId().equals(nodeId)) {
                result.add(treeItem);
            }
        }
        Collections.sort(result, new SortTypeComparator());
        return result;
    }

    /**
     * @param nodeId 节点
     * @return 得到子节点数组
     */
    public String[] getChildCityItemIdArray(String nodeId) {
        if (nodeId == null) {
            return new String[0];
        }
        String[] resultArray = new String[0];
        List<CityItem> treeItemList = getChildCityItem(nodeId);
        if (treeItemList != null) {
            for (CityItem treeIt : treeItemList) {
                resultArray = ArrayUtil.add(resultArray, treeIt.getId());
            }
        }
        return resultArray;
    }

    /**
     * @return 得到根节点
     */
    public CityItem getRootCityItem() {
        List<CityItem> list = getList();
        for (CityItem treeItem : list) {
            if ("root".equals(treeItem.getId()) || treeItem.getParentId() == null) {
                return treeItem;
            }
        }
        CityItem treeItem = new CityItem();
        treeItem.setId("root");
        treeItem.setCaption("root");
        return treeItem;
    }

    /**
     * 得到树的所有节点
     *
     * @return 得到树的所有节点
     */
    public List<CityItem> getList() {
        return createCriteria(CityItem.class)
                .add(Expression.eq("namespace", namespace))
                .addOrder(Order.asc("sortType"))
                .list(false);
    }

    /**
     * @return 得到数的所有节点ID
     */
    public String[] getCityItemIdArray() {
        String[] result = null;
        List<CityItem> list = getList();
        for (CityItem treeItem : list) {
            result = ArrayUtil.add(result, treeItem.getId());
        }
        return result;
    }

    /**
     * @return 得到节点的数目
     */
    public int getSize() {
        return getList().size();
    }


    /**
     * @param node 节点
     * @param fen  风格号
     * @return 数据够转换为选择框形式
     */
    private Map<String, String> getCityItemMap(CityItem node, String fen) {
        Map<String, String> result = new LinkedHashMap<String, String>();
        if (StringUtil.isNull(node.getParentId())) {
            //result.put("", "ALL");
            for (CityItem nNode : getChildCityItem(node.getId())) {

                Map<String, String> childResult = getCityItemMap(nNode, fen);
                for (String key : childResult.keySet()) {
                    result.put(key, childResult.get(key));
                }
            }
            return result;
        }
        for (CityItem nNode : getChildCityItem(node.getId())) {
            Map<String, String> childResult = getCityItemMap(nNode, fen);
            for (String key : childResult.keySet()) {
                if ("root".equalsIgnoreCase(nNode.getParentId())) {
                    result.put(key, childResult.get(key));
                } else {
                    result.put(key, fen + childResult.get(key));
                }
            }
        }
        return result;
    }

    /**
     * @param fen 分隔号
     * @return 选择框
     */
    public Map<String, String> getSelectCityItemMap(String fen) {
        CityItem rootNode = getRootCityItem();
        if (rootNode == null) {
            rootNode = createRootCityItem();
        }
        return getCityItemMap(rootNode, fen);
    }

    /**
     * @return String 创建节点ID,不能是数字开头和有特殊字符
     */
    private String createCityItemId() {
        int size = getSize() + 1;
        String id = namespace.toLowerCase() + size;
        CityItem checkCityItem = getCityItem(id);
        while (checkCityItem != null) {
            size++;
            id = namespace.toLowerCase() + size;
            checkCityItem = getCityItem(id);
        }
        return id;
    }


    /**
     * @param nodeId 添加的节点id
     * @param node   要添加的节点
     * @return 是否添加成功
     * @throws Exception 异常
     */
    public boolean addCityItem(String nodeId, CityItem node) throws Exception {
        if (node == null) {
            return false;
        }
        CityItem treeItem = getCityItem(nodeId);
        if (treeItem == null || "root".equalsIgnoreCase(nodeId)) {
            treeItem = getRootCityItem();
            if (treeItem == null) {
                treeItem = createRootCityItem();
            }
            save(treeItem, false);
            return addChildCityItem(treeItem.getId(), node);
        }
        if (StringUtil.isNull(treeItem.getParentId())) {
            return false;
        }
        node.setId(createCityItemId());
        if (node.getId().equalsIgnoreCase(node.getParentId())) {
            return false;
        }
        node.setNamespace(namespace);
        node.setCaption(node.getCaption());
        node.setParentId(treeItem.getParentId());
        save(node, false);
        clear();
        return true;
    }


    /**
     * @param nodeId 相对节点
     * @param node   节点
     * @return 增加到子节点 是否成功
     * @throws Exception 异常
     */
    public boolean addChildCityItem(String nodeId, CityItem node) throws Exception {
        if (node == null) {
            return false;
        }
        CityItem rootCityItem = getRootCityItem();
        if (rootCityItem == null) {
            rootCityItem = createRootCityItem();
            save(rootCityItem, false);
        }
        node.setCaption(node.getCaption());
        if (StringUtil.isNull(nodeId)) {
            nodeId = rootCityItem.getId();
        }
        CityItem treeItem = getCityItem(nodeId);
        if (treeItem == null) {
            return false;
        }
        node.setId(createCityItemId());
        node.setNamespace(namespace);
        node.setParentId(treeItem.getId());
        if (node.getId().equalsIgnoreCase(node.getParentId())) {
            return false;
        }
        save(node, false);
        clear();
        return true;
    }

    /**
     * @param nodeId 删除的节点id
     * @return 删除节点 是否成功
     */
    public boolean deleteCityItem(String nodeId) {
        boolean result = createCriteria(CityItem.class).add(Expression.eq("namespace", namespace)).add(Expression.or(Expression.eq("parentNodeId", nodeId), Expression.eq("nodeId", nodeId))).delete(false) > 0;
        clear();
        return result;
    }

    /**
     * @return boolean 删除树
     */
    public boolean deleteTree() {
        clear();
        return createCriteria(CityItem.class).add(Expression.eq("namespace", namespace)).delete(false) > 0;
    }

    /**
     * @param node 删除的节点
     * @return 编辑节点 是否成功
     * @throws Exception 异常
     */
    public boolean editCityItem(CityItem node) throws Exception {
        if (node == null || !StringUtil.hasLength(node.getId())) {
            return false;
        }

        CityItem nowNode = getCityItem(node.getId());
        if (nowNode == null) {
            return false;
        }
        nowNode.setCaption(node.getCaption());
        nowNode.setHide(node.getHide());
        nowNode.setSortType(node.getSortType());
        nowNode.setSortType(node.getSortType());
        nowNode.setRoleIds(node.getRoleIds());
        nowNode.setPassword(node.getPassword());
        nowNode.setShowImage(node.getShowImage());
        nowNode.setDescription(node.getDescription());
        nowNode.setLinkPage(node.getLinkPage());
        nowNode.setManager(node.getManager());
        nowNode.setPutName(node.getPutName());
        nowNode.setPutUid(node.getPutUid());
        nowNode.setIp(node.getIp());
        if (update(nowNode) > 0) {
            clear();
            return true;
        }
        return false;
    }

    /**
     * NodeId 得到路径ID
     *
     * @param nodeId 树节点
     * @return 树列表
     */
    public List<CityItem> getCityItemPath(String nodeId) {
        CityItem treeItem = getCityItem(nodeId);
        return getCityItemPath(treeItem);
    }

    /**
     * 路径
     *
     * @param treeItem tree
     * @return 路径
     */
    public List<CityItem> getCityItemPath(CityItem treeItem) {
        List<CityItem> list = new LinkedList<CityItem>();
        CityItem parentNode = treeItem;
        while (parentNode != null && !"root".equalsIgnoreCase(parentNode.getId())) {
            list.add(0, parentNode);
            parentNode = getCityItem(parentNode.getParentId());
        }
        return list;
    }

    /**
     * @return 返回结点Map方便 得到对应的分类
     */
    public Map<String, String> getCityItemMap() {
        Map<String, String> result = new LinkedHashMap<String, String>();
        List<CityItem> list = getList();
        for (CityItem treeItem : list) {
            result.put(treeItem.getId(), treeItem.getCaption());
        }
        return result;
    }


    public void clear() {
        evict(CityItem.class);
    }


    /**
     * 排序数
     */
    public static class SortTypeComparator implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            if (o1 == null) {
                return 0;
            }
            if (o2 == null) {
                return 0;
            }
            return Integer.compare(((CityItem) o1).getSortType(), ((CityItem) o2).getSortType());
        }
    }

    /**
     * @return 得到xml格式数据
     */
    public String toXmlString() {
        List<CityItem> list = getList();
        StringBuilder sb = new StringBuilder();
        sb.append("<city>\r\n");
        for (CityItem treeItem : list) {
            sb.append(treeItem.toString());
        }
        sb.append("</city>\r\n");
        return sb.toString();
    }
}