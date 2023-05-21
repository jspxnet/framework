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

/**
 *
 * @author chenYuan
 */
public interface OptionDAO extends SoberSupport {

    /**
     *
     * @return 导入
     * @throws Exception 异常
     */
    int storeDatabase() throws Exception;
    /**
     * @param ids id
     * @return 删除
     */
    boolean delete(Long[] ids);
    /**
     * @param ids      id
     * @param sortType 排序
     * @return 更新排序
     */
    boolean updateSortType(Long[] ids, int sortType);
    /**
     * @param ids id
     * @return 更新排序日期
     */
    boolean updateSortDate(Long[] ids);
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
    List<OptionBundle> getList(String[] field, String[] find, String term, String namespace, String sortString, int page, int count);
    /**
     * @param id id
     * @return 设置默认选项
     * @throws Exception 异常
     */
    boolean updateSelected(Long id) throws Exception;
    /**
     * @param field 字段
     * @param find  查询条件
     * @param term  条件
     * @param namespace  命名空间
     * @return 得到记录条数
     */
    int getCount(String[] field, String[] find, String term, String namespace);
    /**
     *
     * @param namespace 命名空间
     * @return 得到默认选项
     */
    OptionBundle getSelected(String namespace);
    /**
     *
     * @param code 得到选项
     * @param namespace 命名空间
     * @return 得到字典数据
     */
    OptionBundle getOptionValue(String code, String namespace);


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
    List<OptionBundle> getChildList(String[] field, String[] find, String parentCode, String namespace, String sortString, int page, int count);
    /**
     *
     * @param field 字段
     * @param find 查询
     * @param parentCode 父编码
     * @param namespace 命名空间
     * @return 得到子列表数量
     */
    int getChildCount(String[] field, String[] find, String parentCode, String namespace);
    /**
     *
     * @param id id
     * @return 上移
     * @throws Exception 异常
     */
    boolean top(long id) throws Exception;
    /**
     *
     * @param id id
     * @return 下移
     * @throws Exception 异常
     */
    boolean down(long id) throws Exception;
}