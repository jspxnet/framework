/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.component.zhex.spell.ChineseUtil;
import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.tag.MapElement;
import com.github.jspxnet.sioc.tag.ValueElement;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.ssql.SSqlExpression;

import com.github.jspxnet.txweb.dao.OptionDAO;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.txweb.view.OptionProvider;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.*;

/**
 * Created by yuan on 14-2-16.
 * <p>
 * com.github.jspxnet.txweb.dao.impl.OptionDAOImpl
 */
@Bean
@Slf4j
public class OptionDAOImpl extends JdbcOperations implements OptionDAO {

    public OptionDAOImpl() {

    }

    private String folder;

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    @Override
    public int storeDatabase() throws Exception {
        //载入目录索引
        Map<String, String> spaceMap = new HashMap<>();
        File indexFile = new File(this.folder, "index.xml");
        if (!indexFile.isFile()) {
            return 0;
        }
        AbstractRead read = new AutoReadTextFile();
        read.setEncode(Environment.defaultEncode);
        read.setFile(indexFile);

        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag("map", MapElement.class.getName());
        MapElement mapElement = (MapElement) xmlEngine.createTagNode(read.getContent());
        List<TagNode> valueList = mapElement.getValueList();
        for (TagNode aList : valueList) {
            ValueElement valueElement = (ValueElement) aList;
            spaceMap.put(valueElement.getKey(), valueElement.getValue());
            createCriteria(OptionBundle.class).add(Expression.eq("namespace",valueElement.getKey())).delete(false);
        }
        valueList.clear();

        int i = 0;
        for (String key : spaceMap.keySet()) {
            File file = new File(this.folder, key + ".xml");
            if (!file.isFile()) {
                continue;
            }
            read = new AutoReadTextFile();
            read.setEncode(Environment.defaultEncode);
            read.setFile(file);
            String xml = read.getContent();
            if (StringUtil.isNull(xml)) {
                continue;
            }
            xmlEngine = new XmlEngineImpl();
            xmlEngine.putTag("map", MapElement.class.getName());
            mapElement = (MapElement) xmlEngine.createTagNode(xml);
            valueList = mapElement.getValueList();
            for (TagNode aList : valueList) {
                ValueElement valueElement = (ValueElement) aList;
                OptionBundle optionBundle = new OptionBundle();
                optionBundle.setCode(valueElement.getKey());
                optionBundle.setName(valueElement.getValue());
                optionBundle.setSelected(valueElement.getSelected() ? 1 : 0);
                optionBundle.setDescription(valueElement.getKey() + " " + valueElement.getValue());
                optionBundle.setNamespace(key);
                optionBundle.setSpelling(ChineseUtil.getFullSpell(optionBundle.getName(), ""));
                optionBundle.setSortType(StringUtil.toInt(StringUtil.getNumber(valueElement.getKey())));
                optionBundle.setPutName(Environment.SYSTEM_NAME);
                optionBundle.setPutUid(Environment.SYSTEM_ID);
                optionBundle.setIp("127.0.0.1");
                i = i + super.save(optionBundle);

            }
            valueList.clear();
        }

        List<OptionBundle> list = getList(null,null, null, OptionProvider.ALL_NAMESPACE,null,1,50000);
        List<String> checkList = new ArrayList<>();
        for (OptionBundle optionBundle:list)
        {
            checkList.add(optionBundle.getCode());
        }
        for (String code:spaceMap.keySet())
        {
            if (checkList.contains(code))
            {
                continue;
            }
            String caption = spaceMap.get(code);
            OptionBundle optionBundle = new OptionBundle();
            optionBundle.setCode(code);
            optionBundle.setName(caption);
            optionBundle.setSelected(0);
            optionBundle.setDescription(code + " " + caption);
            optionBundle.setNamespace(OptionProvider.ALL_NAMESPACE);
            optionBundle.setSpelling(ChineseUtil.getFullSpell(caption, ""));
            optionBundle.setSortType(0);
            optionBundle.setPutName(Environment.SYSTEM_NAME);
            optionBundle.setPutUid(Environment.SYSTEM_ID);
            optionBundle.setIp("127.0.0.1");
            i = i + super.save(optionBundle);
        }
        return i;
    }

    /**
     * @param ids id
     * @return 删除
     */
    @Override
    public boolean delete(Long[] ids) {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        for (Long id : ids) {
            OptionBundle optionBundle = super.get(OptionBundle.class, id);
            if (optionBundle != null) {
                super.delete(optionBundle);
            }
        }
        return true;
    }


    /**
     * @param id id
     * @return 设置默认选项
     */
    @Override
    public boolean updateSelected(Long id) throws Exception {
        if (id <= 0) {
            return false;
        }
        OptionBundle optionBundle = super.get(OptionBundle.class, id);
        if (optionBundle == null) {
            return false;
        }
        Map<String, Object> valueMap = new LinkedHashMap<String, Object>();
        valueMap.put("selected", 0);
        if (super.createCriteria(OptionBundle.class).add(Expression.eq("namespace", optionBundle.getNamespace())).update(valueMap) > 0) {
            optionBundle.setSelected(1);
            super.update(optionBundle, new String[]{"selected"});
        }
        return true;
    }

    /**
     * @param ids      id
     * @param sortType 排序
     * @return 更新排序
     */
    @Override
    public boolean updateSortType(Long[] ids, int sortType) {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        try {
            for (Long id : ids) {
                OptionBundle optionBundle = get(OptionBundle.class, id);
                if (optionBundle != null) {
                    optionBundle.setSortType(sortType);
                    super.update(optionBundle, new String[]{"sortType"});
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(ids, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }

    /**
     * @param ids id
     * @return 更新排序日期
     */
    @Override
    public boolean updateSortDate(Long[] ids) {
        if (ArrayUtil.isEmpty(ids)) {
            return false;
        }
        try {
            for (Long id : ids) {
                OptionBundle optionBundle = get(OptionBundle.class, id);
                if (optionBundle != null) {
                    optionBundle.setSortDate(new Date());
                    super.update(optionBundle, new String[]{"sortDate"});
                }
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(ids, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }

    /**
     * @param field      字段
     * @param find       查询条件
     * @param term       条件
     * @param namespace  命名空间
     * @param sortString 排序
     * @param page       页数
     * @param count      返回数量
     * @return 返回列表
     */

    @Override
    public List<OptionBundle> getList(String[] field, String[] find, String term,String namespace, String sortString, int page, int count){
        if (StringUtil.isNull(sortString)) {
            sortString = "sortType:A;sortDate:D";
        }
        Criteria criteria = createCriteria(OptionBundle.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return SSqlExpression.getSortOrder(criteria, sortString).setCurrentPage(page).setTotalCount(count).list(false);
    }


    /**
     * @param field 字段
     * @param find  查询条件
     * @param term  条件
     * @param namespace  命名空间
     * @return 得到记录条数
     */
    @Override
    public int getCount(String[] field, String[] find, String term, String namespace) {
        Criteria criteria = createCriteria(OptionBundle.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }


    /**
     *
     * @param namespace 命名空间
     * @return 得到默认选项
     */
    @Override
    public OptionBundle getSelected(String namespace) {
        Criteria criteria = createCriteria(OptionBundle.class).add(Expression.eq("selected", 1));
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        } else {
            return new OptionBundle();
        }
        return (OptionBundle) criteria.objectUniqueResult(false);
    }

    /**
     *
     * @param code 得到选项
     * @param namespace 命名空间
     * @return 得到字典数据
     */
    @Override
    public OptionBundle getOptionValue(String code, String namespace) {
        Criteria criteria = createCriteria(OptionBundle.class).add(Expression.eq("code", code));
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        } else {
            return new OptionBundle();
        }
        return (OptionBundle) criteria.objectUniqueResult(false);
    }


    /**
     * 得到子列表
     * @param field 字段
     * @param find 查询
     * @param parentCode 父编码
     * @param namespace 命名空间
     * @param sortString 排序
     * @param page 页数
     * @param count 行数
     * @return 得到子列表
     */
    @Override
    public List<OptionBundle> getChildList(String[] field, String[] find, String parentCode, String namespace, String sortString, int page, int count){
        if (StringUtil.isNull(sortString)) {
            sortString = "sortType:A;sortDate:D";
        }
        Criteria criteria = createCriteria(OptionBundle.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (!StringUtil.isNull(parentCode)) {
            criteria = criteria.add(Expression.eq("parentCode", parentCode));
        }
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return SSqlExpression.getSortOrder(criteria, sortString).setCurrentPage(page).setTotalCount(count).list(false);
    }

    /**
     *
     * @param field 字段
     * @param find 查询
     * @param parentCode 父编码
     * @param namespace 命名空间
     * @return 得到子列表数量
     */
    @Override
    public int getChildCount(String[] field, String[] find, String parentCode, String namespace) {
        Criteria criteria = createCriteria(OptionBundle.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(field)) {
            criteria = criteria.add(Expression.find(field, find));
        }
        if (!StringUtil.isNull(parentCode)) {
            criteria = criteria.add(Expression.eq("parentCode", parentCode));
        }
        if (!StringUtil.isNull(namespace)) {
            criteria = criteria.add(Expression.eq("namespace", namespace));
        }
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

}