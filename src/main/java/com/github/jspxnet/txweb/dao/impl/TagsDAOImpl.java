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

import com.github.jspxnet.txweb.table.TagWord;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.txweb.dao.TagsDAO;

import java.util.List;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2007-10-19
 * Time: 11:50:44
 * jspx.service.tag.impl.TagsDAOImpl
 */
public class TagsDAOImpl extends JdbcOperations implements TagsDAO {

    public TagsDAOImpl() {

    }


    /**
     * @return 更新关系
     */
    @Override
    public boolean updateTagWord(String words, String parentWord, int correlative) throws Exception {
        if (words == null) {
            return false;
        }
        if (parentWord == null) {
            return false;
        }
        if (words.equalsIgnoreCase(parentWord)) {
            return false;
        }
        TagWord tagsRelative = createCriteria(TagWord.class).add(Expression.eq("words", words)).add(Expression.eq("parentWord", parentWord)).objectUniqueResult(false);
        if (tagsRelative == null) {
            tagsRelative = new TagWord();
            tagsRelative.setWords(words);
            tagsRelative.setParentWord(parentWord);
            tagsRelative.setCorrelative(correlative);
            return save(tagsRelative, false) > 0;
        }
        tagsRelative.setCorrelative(tagsRelative.getCorrelative() + correlative);
        tagsRelative.setLastDate(new Date());
        return update(tagsRelative) > 0;
    }


    /**
     * @param currentPage 页数
     * @param totalCount  返回行数
     * @return 列表
     */
    @Override

    public List<TagWord> getList(int currentPage, int totalCount)  {
        return createCriteria(TagWord.class).addOrder(Order.desc("correlative"))
                .addOrder(Order.desc("lastDate")).setCurrentPage(currentPage).setTotalCount(totalCount).list(false);
    }

    /**
     * 得到相关tag列表
     *
     * @param word        相关
     * @param currentPage 页数
     * @param totalCount  返回行数
     * @return 列表
     */
    @Override

    public List<TagWord> getTagWordChild(String word, int currentPage, int totalCount) {
        return createCriteria(TagWord.class)
                .add(Expression.eq("parentWord", word))
                .addOrder(Order.desc("correlative"))
                .addOrder(Order.desc("lastDate")).setCurrentPage(currentPage).setTotalCount(totalCount).list(false);
    }
}