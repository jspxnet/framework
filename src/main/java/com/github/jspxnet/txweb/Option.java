/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.table.OptionBundle;
import java.util.List;


/**
 * Created by yuan on 14-3-12.
 * 字典库，提供调用接口
 */
public interface Option {
    /**
     *
     * @param namespace 命名空间
     * @return 得到字典表列表
     */
    List<OptionBundle> getList(String namespace);
    /**
     * 字典表中得到key数据
     * @param key code
     * @param namespace 命名空间
     * @return 字典表数据
     */
    OptionBundle getBundle(@Param(caption = "关键字") String key, @Param(caption = "命名空间") String namespace);
    /**
     *
     * @param namespace 命名空间
     * @return 得到当前默认
     */
    OptionBundle getBundleSelected(@Param(caption = "命名空间") String namespace);
}