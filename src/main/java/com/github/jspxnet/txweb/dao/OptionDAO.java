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
    Map<String, String> getSpaceMap();

    void setNamespace(String namespace);

    String getNamespace();

    int storeDatabase() throws Exception;


    boolean delete(Long[] ids);

    boolean updateSortType(Long[] ids, int sortType);

    boolean updateSortDate(Long[] ids);

    List<OptionBundle> getList(String[] field, String[] find, String term, String sortString, int page, int count);

    int getCount(String[] field, String[] find, String term) ;

    String getCaption();

    boolean updateSelected(Long id) throws Exception;

    OptionBundle getSelected();

    OptionBundle getOptionValue(String key) throws Exception;
}