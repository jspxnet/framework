/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb;

import java.util.Set;

/**
 * Created by yuan on 14-3-12.
 * 字典库，提供调用接口
 */
public interface Option {
    String getOptions(int mode);

    String getSelected(int mode, String namespace) throws Exception;

    String getOptions(int mode, String namespace);

    String getCaption(String key);

    Set<String> getSpaceSet();

    String getOptions(String namespace) ;

    String getSelected(String namespace) throws Exception;

    String getOptionValue(String key, String namespace) throws Exception;


}