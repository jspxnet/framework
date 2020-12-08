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

import com.github.jspxnet.sober.criteria.expression.InExpression;

import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.txweb.dao.VoteDAO;
import com.github.jspxnet.txweb.table.vote.VoteItem;
import com.github.jspxnet.txweb.table.vote.VoteMember;
import com.github.jspxnet.txweb.table.vote.VoteTopic;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.criteria.Order;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import lombok.extern.slf4j.Slf4j;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;


/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-2-28
 * Time: 17:23:13
 */
@Slf4j
public class VoteDAOImpl extends JdbcOperations implements VoteDAO {

    private String namespace = StringUtil.empty;
    private String organizeId = StringUtil.empty;

    @Override
    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    @Override
    public String getNamespace() {
        return namespace;
    }

    @Override
    public String getOrganizeId() {
        return organizeId;
    }

    @Override
    public void setOrganizeId(String organizeId) {
        this.organizeId = organizeId;
    }

    /**
     * 得到一个投票的投票总数
     *
     * @param topicId 投票id
     * @return int
     */
    @Override
    public int getSumVotePoint(String topicId) {
        if (StringUtil.isEmpty(topicId)) {
            return 1;
        }
        int x = (Integer) createCriteria(VoteItem.class)
                .add(Expression.eq("topicId", topicId))
                .setProjection(Projections.sum("votePoint")).uniqueResult();
        if (x <= 0) {
            return 1;
        }
        return x;
    }

    /**
     * @param topicId 主题id
     * @param uid     用户id
     * @return boolean 判断是否已经投票
     */
    @Override
    public boolean isVoted(String topicId, long uid, String unionid) {
        if (StringUtil.isEmpty(topicId) || uid <= 0) {
            return true;
        }
        Criteria criteria = createCriteria(VoteMember.class).add(Expression.eq("topicId", topicId))
        .add(Expression.or(Expression.eq("putUid", uid), Expression.eq("unionid", unionid)));
        return criteria.setProjection(Projections.rowCount()).intUniqueResult() > 0;
    }

    /**
     * @param topicId 主题id
     * @param ip      ip
     * @param system  系统
     * @param browser 浏览器
     * @param openId  微信用户id
     * @param day     几天以内算一次
     * @return 判断是否已经投票
     */
    @Override
    public int getGuestVoteCount(String topicId, String ip, String system, String browser, String openId, String unionid, int day) {
        if (StringUtil.isEmpty(topicId) || ip == null || browser == null || system == null) {
            return 0;
        }
        Date startDate = DateUtil.getStartDateTime(DateUtil.addDate(-day)); //一天内
        if (!StringUtil.isNull(openId)) {
            //普通方式判断
            Criteria criteria = createCriteria(VoteMember.class).add(Expression.gt("createDate", startDate))
                    .add(Expression.eq("topicId", topicId));
            if (!StringUtil.isNull(unionid)) {
                criteria = criteria.add(Expression.eq("unionid", unionid));
            } else {
                criteria.add(Expression.eq("ip", ip)).add(Expression.eq("openId", openId));
            }
            return criteria.setProjection(Projections.rowCount()).intUniqueResult();
        } else if (!StringUtil.isNull(unionid)) {
            //普通方式判断
            Criteria criteria = createCriteria(VoteMember.class).add(Expression.gt("createDate", startDate)).add(Expression.eq("unionid", unionid));
            return criteria.setProjection(Projections.rowCount()).intUniqueResult();
        } else {
            return createCriteria(VoteMember.class)
                    .add(Expression.eq("topicId", topicId))
                    .add(Expression.eq("ip", ip))
                    .add(Expression.eq("browser", browser))
                    .add(Expression.eq("system", system))
                    .add(Expression.gt("createDate", startDate))
                    .setProjection(Projections.rowCount()).intUniqueResult();
        }
    }

    /**
     * @param topicId 得到ID定影的投票
     * @return VoteTopic
     */
    @Override
    public VoteTopic getVoteTopic(String topicId) {
        return super.load(VoteTopic.class, topicId, true);
    }

    /**
     * @param groupId 得到ID定影的投票
     * @return VoteTopic
     */
    @Override
    public List<VoteTopic> getVoteTopicForGroupId(String groupId) {
        Criteria criteria = createCriteria(VoteTopic.class).add(Expression.eq("groupId", groupId)).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        return criteria.list(true);
    }


    /**
     * 得到第一个投票
     *
     * @return VoteTopic
     */
    @Override
    public VoteTopic getFirstVoteTopic(String groupId) {
        Criteria criteria = createCriteria(VoteTopic.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isNull(groupId) && !StringUtil.ASTERISK.equals(groupId)) {
            criteria = criteria.add(Expression.eq("groupId", groupId));
        }
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }

        VoteTopic v = criteria.addOrder(Order.desc("sortType"))
                .addOrder(Order.desc("sortDate"))
                .addOrder(Order.desc("createDate"))
                .objectUniqueResult(true);
        if (v == null) {
            v = new VoteTopic();
            v.setTopicText("NO Vote");
        }
        return v;
    }


    /**
     * 得到投票列表
     *
     * @param topicId 主题id
     * @return List
     */
    @Override
    public List<VoteItem> getVoteList(String topicId) {
        if (StringUtil.isEmpty(topicId)) {
            return new ArrayList<>(0);
        }
        return createCriteria(VoteItem.class).add(Expression.eq("namespace", namespace)).add(Expression.eq("topicId", topicId))
                .addOrder(Order.asc("sortType")).list(false);
    }

    /**
     * @param voteIds 投票
     * @return boolean
     */
    @Override
    public boolean postVote(String[] voteIds) throws Exception {
        if (ArrayUtil.isEmpty(voteIds)) {
            return false;
        }
        InExpression inExpression = new InExpression(getSoberTable(VoteItem.class).getPrimary(), voteIds);
        String sql = "UPDATE " + getSoberTable(VoteItem.class).getName() + " SET votePoint=votePoint+1 WHERE " + inExpression.toSqlString(getSoberTable(VoteItem.class), getSoberFactory().getDatabaseName());
        return update(sql, inExpression.getParameter(getSoberTable(VoteItem.class)))>=0;
    }

    /**
     * 删除
     *
     * @param topicId 主题id
     * @return boolean
     */
    @Override
    public boolean deleteVote(String topicId) {
        if (StringUtil.isEmpty(topicId)) {
            return false;
        }
        try {
            /////////////删除投票begin
            super.delete(VoteTopic.class, topicId, true);
            /////////////删除投票end

            /////////////删除投票选项begin
            super.delete(VoteItem.class, "topicId", topicId);
            /////////////删除投票选项end
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteVote(String[] topicIds) {
        if (ArrayUtil.isEmpty(topicIds)) {
            return false;
        }
        try {
            for (String topicId : topicIds) {
                if (StringUtil.isEmpty(topicId)) {
                    continue;
                }
                /////////////删除投票begin
                super.delete(VoteTopic.class, topicId, true);
                /////////////删除投票end

                /////////////删除投票的人begin
                super.delete(VoteMember.class, "topicId", topicId);
                /////////////删除投票的人end

                /////////////删除投票选项begin
                super.delete(VoteItem.class, "topicId", topicId);
                /////////////删除投票选项end
            }
        } catch (Exception e) {
            log.error(ArrayUtil.toString(topicIds, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }

    /**
     * 排序
     *
     * @param topicIds 主题id
     * @param sortType 排序
     * @return 是否成功
     */
    @Override
    public boolean updateSortType(String[] topicIds, int sortType) {
        if (ArrayUtil.isEmpty(topicIds)) {
            return true;
        }
        try {
            for (String topicId : topicIds) {
                if (StringUtil.isEmpty(topicId)) {
                    continue;
                }
                VoteTopic votetopic = get(VoteTopic.class, topicId);
                if (votetopic == null || !votetopic.getOrganizeId().equals(organizeId)) {
                    continue;
                }
                votetopic.setSortType(sortType);
                super.update(votetopic, new String[]{"sortType"});
            }

        } catch (Exception e) {
            log.error(ArrayUtil.toString(topicIds, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }

    /**
     * 提前
     *
     * @param topicIds 主题id
     * @return boolean
     */
    @Override
    public boolean updateSortDate(String[] topicIds) {
        if (null == topicIds) {
            return true;
        }
        try {
            for (String topicId : topicIds) {
                if (StringUtil.isEmpty(topicId)) {
                    continue;
                }
                VoteTopic votetopic = get(VoteTopic.class, topicId);
                if (votetopic == null) {
                    continue;
                }
                votetopic.setSortDate(new Date());
                update(votetopic, new String[]{"sortDate"});
            }

        } catch (Exception e) {
            log.error(ArrayUtil.toString(topicIds, StringUtil.COMMAS), e);
            return false;
        }
        return true;
    }


    /**
     * @param find       询字符串 "" 自动分开查询
     * @param term       条件
     * @param sortString 排序字符串
     * @param uid        用户ID
     * @param groupId    分组
     * @param ipage      页数
     * @param count      返回数量
     * @return 主题列表
     * @throws Exception 异常
     */
    @Override
    public List<VoteTopic> getList(
            String find,
            String term,
            String sortString,
            long uid,
            String groupId,
            int ipage, int count) throws Exception {
        Criteria criteria = createCriteria(VoteTopic.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isNull(find)) {

            criteria = criteria.add(Expression.like("topicText", "%" + StringUtil.checkSql(find) + "%"));
        }
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }
        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        if (!StringUtil.isNull(groupId) && !StringUtil.ASTERISK.equals(groupId)) {
            criteria = criteria.add(Expression.eq("groupId", groupId));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, sortString);
        return criteria.setCurrentPage(ipage).setTotalCount(count).list(false);
    }


    /**
     * @param find    查询
     * @param term    条件
     * @param uid     用户id
     * @param groupId 分组
     * @return 得到记录条数
     * @throws Exception 异常
     */
    @Override
    public int getCount(
            String find,
            String term,
            long uid,
            String groupId
    ) throws Exception {
        Criteria criteria = createCriteria(VoteTopic.class).add(Expression.eq("namespace", namespace));
        if (!StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.like("topicText", "%" + StringUtil.checkSql(find) + "%"));
        }
        if (!StringUtil.isEmpty(organizeId)) {
            criteria = criteria.add(Expression.eq("organizeId", organizeId));
        }

        if (uid > 0) {
            criteria = criteria.add(Expression.eq("putUid", uid));
        }
        if (!StringUtil.isNull(groupId) && !StringUtil.ASTERISK.equals(groupId)) {
            criteria = criteria.add(Expression.eq("groupId", groupId));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * 投票项目搜索
     *
     * @param tid        主题id
     * @param id         id
     * @param find       查询
     * @param term       条件
     * @param sortString 排序
     * @param page       页数
     * @param count      行数
     * @return 投票选项列表
     */
    @Override
    public List<VoteItem> getItemList(
            String tid,
            String id,
            String find,
            String term,
            String sortString,
            int page, int count) {
        Criteria criteria = createCriteria(VoteItem.class).add(Expression.eq("topicId", tid));
        if (!StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.like("title", "%" + StringUtil.checkSql(find) + "%"));
        }
        if (!StringUtil.isEmpty(id)) {
            criteria = criteria.add(Expression.eq("id", id));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, sortString);
        return criteria.setCurrentPage(page).setTotalCount(count).list(false);
    }

    /**
     * @param tid  主题id
     * @param id   id
     * @param find 查询
     * @param term 条件
     * @return 行数, 投票项目有几个
     * @throws Exception 异常
     */
    @Override
    public int getItemCount(
            String tid,
            String id,
            String find,
            String term
    ) throws Exception {
        Criteria criteria = createCriteria(VoteItem.class).add(Expression.eq("topicId", tid));
        if (!StringUtil.isNull(find)) {
            criteria = criteria.add(Expression.like("title", "%" + StringUtil.checkSql(find) + "%"));
        }
        if (!StringUtil.isEmpty(id)) {
            criteria = criteria.add(Expression.eq("id", id));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }
}