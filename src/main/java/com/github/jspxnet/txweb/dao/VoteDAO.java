/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;

import java.sql.SQLException;
import java.util.List;

import com.github.jspxnet.txweb.table.vote.VoteItem;
import com.github.jspxnet.txweb.table.vote.VoteTopic;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-2-28
 * Time: 17:22:43
 */
public interface VoteDAO extends SoberSupport {
    int getSumVotePoint(String topicId);

    boolean updateSortDate(String[] topicIds);

    boolean updateSortType(String[] topicIds, int sortType);

    List<VoteItem> getVoteList(String topicId);

    boolean isVoted(String topicId, long uid, String unionid);

    int getGuestVoteCount(String topicId, String ip, String system, String browser, String openId, String unionid, int day);

    VoteTopic getFirstVoteTopic(String groupId);

    VoteTopic getVoteTopic(String topicId) ;

    boolean postVote(String[] voteIds) throws Exception;

    //不删除投票日志
    boolean deleteVote(String topicId);

    //删除投票日志记录
    boolean deleteVote(String[] topicIds);

    List<VoteTopic> getVoteTopicForGroupId(String groupId);

    List<VoteTopic> getList(
            String find,
            String term,
            String sortString,
            long uid,
            String groupId,
            int page, int count) throws Exception;

    int getCount(
            String find,
            String term,
            long uid,
            String groupId
    ) throws Exception;

    List<VoteItem> getItemList(
            String tid,
            String id,
            String find,
            String term,
            String sortString,
            int page, int count) ;

    int getItemCount(
            String tid,
            String id,
            String find,
            String term
    ) throws Exception;

    String getNamespace();

    void setNamespace(String namespace);

    String getOrganizeId();

    void setOrganizeId(String organizeId);

}