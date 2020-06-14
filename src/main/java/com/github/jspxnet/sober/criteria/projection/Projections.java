/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.sober.criteria.projection;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-1-9
 * Time: 23:35:04
 */
public final class Projections {

    private Projections() {

    }


    /**
     * @param proj 单个条件
     * @return distinct
     */
    public static Projection distinct(Projection proj) {
        return new Distinct(proj);
    }


    public static ProjectionList projectionList() {
        return new ProjectionList();
    }


    public static Projection rowCount() {
        return new RowCountProjection();
    }


    public static CountProjection count(String propertyName) {
        return new CountProjection(propertyName);
    }


    public static CountProjection countDistinct(String propertyName) {
        return new CountProjection(propertyName).setDistinct();
    }


    /**
     * 集合方式max
     *
     * @param propertyName 字段
     * @return max
     */
    public static AggregateProjection max(String propertyName) {
        return new AggregateProjection("max", propertyName);
    }


    /**
     * 集合方式min
     *
     * @param propertyName 字段
     * @return min
     */
    public static AggregateProjection min(String propertyName) {
        return new AggregateProjection("min", propertyName);
    }


    /**
     * 集合方式avg
     *
     * @param propertyName 字段
     * @return avg
     */
    public static AggregateProjection avg(String propertyName) {
        return new AvgProjection(propertyName);
    }

    /**
     * 集合方式sum
     *
     * @param propertyName 字段
     * @return sum
     */
    public static AggregateProjection sum(String propertyName) {
        return new AggregateProjection("sum", propertyName);
    }

    public static SQLProjection sql(String propertyName) {
        return new SQLProjection(propertyName);
    }


    public static Projection alias(Projection projection, String alias) {
        return new AliasedProjection(projection, alias);
    }

    public static Projection add(Projection projection1, Projection projection2) {
        return new AddProjection(projection1, projection2);
    }

    public static Projection dec(Projection projection1, Projection projection2) {
        return new DecProjection(projection1, projection2);
    }

    public static Projection mul(Projection projection1, Projection projection2) {
        return new MulProjection(projection1, projection2);
    }

    public static Projection div(Projection projection1, Projection projection2) {
        return new DivProjection(projection1, projection2);
    }
}