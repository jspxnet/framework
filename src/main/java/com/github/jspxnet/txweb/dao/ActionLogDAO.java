/*
 * Copyright (c) 2013. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.txweb.table.ActionLog;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: yuan
 * date: 13-10-16
 * Time: 下午10:13
 */
public interface ActionLogDAO extends SoberSupport {
    String getOrganizeId();

    void setOrganizeId(String organizeId);

    void setNamespace(String namespace);

    String getNamespace();

    ActionLog load(String id);

    boolean delete(String[] ids);

    List<ActionLog> getList(String[] field, String[] find, String term, String sortString, long uid, int page, int count) ;

    long getCount(String[] field, String[] find, String term, long uid);

    int deleteYearBefore(int year) throws Exception;

    int clear();
}