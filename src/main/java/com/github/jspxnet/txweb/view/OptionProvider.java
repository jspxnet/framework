/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.view;

import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.Option;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.OptionDAO;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.utils.StringUtil;
import java.util.List;


/**
 * Created by yuan on 14-3-13.
 * 系统提供页面的默认字典库 调用接口
 */
@Bean
public class OptionProvider implements Option {
    @Ref
    protected OptionDAO optionDAO;
    public static final String ALL_NAMESPACE = "all";

    /**
     *
     * @param namespace 命名空间
     * @return 得到字典表列表
     */
    @Override
    public List<OptionBundle> getList(@Param(caption = "命名空间") String namespace)  {
        return optionDAO.getList(null, null, null, namespace,null, 1, 500);
    }


    /**
     * 字典表中得到key数据
     * @param key code
     * @param namespace 命名空间
     * @return 字典表数据
     */
    @Override
    public OptionBundle getBundle(@Param(caption = "关键字") String key, @Param(caption = "命名空间") String namespace) {
        if (StringUtil.isEmpty(key))
        {
            return null;
        }
        List<OptionBundle> list = getList(namespace);
        for (OptionBundle optionBundle:list)
        {
            if (key.equalsIgnoreCase(optionBundle.getCode()))
            {
                return optionBundle;
            }
        }
        return null;
    }

    /**
     *
     * @param namespace 命名空间
     * @return 得到当前默认
     */
    @Override
    public OptionBundle getBundleSelected(@Param(caption = "命名空间") String namespace) {
        List<OptionBundle> list = getList(namespace);
        for (OptionBundle optionBundle:list)
        {
            if (YesNoEnumType.YES.getValue()==optionBundle.getSelected())
            {
                return optionBundle;
            }
        }
        return null;
    }


}