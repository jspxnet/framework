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
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.StringUtil;
import java.util.ArrayList;
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

    /**
     *
     * @param mode 兼容老版本
     * @param namespace 命名空间
     * @return 字符串方式
     */
    @Override
    public String getOptions(int mode,@Param(caption = "命名空间") String namespace)  {
        List<OptionBundle> optionBundleList = getList(namespace);
        StringBuilder sb = new StringBuilder();
        for (OptionBundle optionBundle : optionBundleList) {
            if (mode == 0) {
                sb.append(optionBundle.getName()).append(StringUtil.SEMICOLON);
            } else {
                sb.append(optionBundle.getCode()).append(":").append(optionBundle.getName()).append(StringUtil.SEMICOLON);
            }
        }
        if (sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * @param mode      显示模式 0:得到名称 1:得到代码
     * @param namespace 命名空间
     * @return 得到选项
     */
    @Override
    public String getSelected(@Param(caption = "模式") int mode, @Param(caption = "命名空间") String namespace) {
        OptionBundle optionBundle = getBundleSelected(namespace);
        if (optionBundle == null) {
            return StringUtil.empty;
        }
        return mode == 0 ? optionBundle.getName() : optionBundle.getCode();
    }

    /**
     *
     * @return 得到所有key列表
     */
    @Override
    public List<String> getSpaceSet() {
        List<String> result = new ArrayList<>();
        List<OptionBundle> list = getList(ALL_NAMESPACE);
        for (OptionBundle optionBundle:list)
        {
            result.add(optionBundle.getName());
        }
        if (!ObjectUtil.isEmpty(result))
        {
            return result;
        }
        return new ArrayList<>();
    }


    @Override
    public String getCaption(String key) {
        List<OptionBundle> list = getList(ALL_NAMESPACE);
        for (OptionBundle optionBundle:list)
        {
            if (key!=null&&key.equalsIgnoreCase(optionBundle.getCode()))
            {
                return optionBundle.getName();
            }
        }
        return StringUtil.empty;
    }
}