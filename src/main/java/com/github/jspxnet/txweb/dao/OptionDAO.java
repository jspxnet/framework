/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.txweb.table.OptionBundle;

import java.util.List;
import java.util.Map;

/**
 * Created by yuan on 14-2-16.
 */
public interface OptionDAO extends SoberSupport {


    int storeDatabase() throws Exception;

    boolean delete(Long[] ids);

    boolean updateSortType(Long[] ids, int sortType);

    boolean updateSortDate(Long[] ids);

    List<OptionBundle> getList(String[] field, String[] find, String term, String namespace, String sortString, int page, int count);

    boolean updateSelected(Long id) throws Exception;

    int getCount(String[] field, String[] find, String term, String namespace);

    OptionBundle getSelected(String namespace);

    OptionBundle getOptionValue(String key, String namespace);

    List<OptionBundle> getChildList(String[] field, String[] find, String parentCode, String namespace, String sortString, int page, int count);

    int getChildCount(String[] field, String[] find, String parentCode, String namespace);

    boolean top(long id) throws Exception;

    boolean dwon(long id) throws Exception;
}