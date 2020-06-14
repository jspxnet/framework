/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.table.vote;

import com.github.jspxnet.sober.annotation.*;
import com.github.jspxnet.sober.table.OperateTable;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.sober.enums.MappingType;
import com.github.jspxnet.utils.RandomUtil;
import com.github.jspxnet.utils.StringUtil;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-2-28
 * Time: 16:56:59
 */

@Table(name = "jspx_vote_item", caption = "投票选项")
public class VoteItem extends OperateTable {
    @Id(auto = true, length = 24, type = IDType.uuid)
    @Column(caption = "ID", length = 32, notNull = true)
    private String id = StringUtil.empty;

    @Column(caption = "排序", notNull = true)
    private int sortType = 0;

    @Column(caption = "投票主题的ID", length = 32, notNull = true)
    private String topicId = StringUtil.empty;

    @Nexus(mapping = MappingType.ManyToOne, field = "topicId", targetField = "id", targetEntity = VoteTopic.class)
    private VoteTopic voteTopic;

    @Column(caption = "投票选项说明", length = 100, notNull = true)
    private String title = StringUtil.empty;

    @Column(caption = "图片投票", length = 250)
    private String images = StringUtil.empty;

    //外链
    @Column(caption = "外链", length = 250)
    private String linkUrl = StringUtil.empty;

    @Column(caption = "描述", length = 1024)
    private String description = StringUtil.empty;

    @Column(caption = "票数", notNull = true)
    private int votePoint = 0;

    @Column(caption = "排序时间", notNull = true)
    private Date sortDate = new Date();


    public VoteItem() {

    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTopicId() {
        return topicId;
    }

    public void setTopicId(String topicId) {
        this.topicId = topicId;
    }

    public int getSortType() {
        return sortType;
    }

    public void setSortType(int sortType) {
        this.sortType = sortType;
    }

    public VoteTopic getVoteTopic() {
        return voteTopic;
    }

    public void setVoteTopic(VoteTopic voteTopic) {
        this.voteTopic = voteTopic;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImages() {
        return images;
    }

    public void setImages(String images) {
        this.images = images;
    }

    public String getColor() {
        switch (sortType) {
            case 0:
                return "E1E11E";
            case 1:
                return "00EEAA";
            case 2:
                return "0099CC";
            case 3:
                return "990000";
            case 4:
                return "0033F0";
            case 5:
                return "FFFF00";
            case 6:
                return "FF99CC";
            case 8:
                return "0F9900";
            case 9:
                return "003300";
            case 10:
                return "00FFCC";
            case 11:
                return "00EEEE";
            case 12:
                return "00CC10";
            case 13:
                return "aa33B0";
            case 14:
                return "Ea22B0";
            case 15:
                return "af13B0";
            case 16:
                return "aa3310";
            case 17:
                return "Fa3320";
            case 18:
                return "EA13E0";
            case 19:
                return "BB0000";
            case 20:
                return "00EE00";
            default:
                return RandomUtil.getColor(sortType);
        }

    }


    public int getVotePoint() {
        return votePoint;
    }

    public void setVotePoint(int votePoint) {
        this.votePoint = votePoint;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    /**
     * @param sum 百分百
     * @return 得到投票比例
     */
    public Float getScale(int sum) {
        if (sum <= 0) {
            return (float) 0;
        }
        BigDecimal votePointBig = new BigDecimal(votePoint);
        return votePointBig.divide(BigDecimal.valueOf(sum),8,BigDecimal.ROUND_HALF_UP).multiply(BigDecimal.valueOf(100)).setScale(2,BigDecimal.ROUND_HALF_UP).floatValue();
    }

    /**
     * 得到投票比例
     *
     * @param sum 百分百
     * @return 两位比例
     */
    public String getScaleTwo(int sum) {
        return NumberUtil.format(getScale(sum) * 2, "####.##");
    }

    /**
     * @param sum 百分百
     * @return 字符串方式
     */
    public String getScaleString(int sum) {
        return NumberUtil.format(getScale(sum), "####.##");
    }

    public Date getSortDate() {
        return sortDate;
    }

    public void setSortDate(Date sortDate) {
        this.sortDate = sortDate;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

}