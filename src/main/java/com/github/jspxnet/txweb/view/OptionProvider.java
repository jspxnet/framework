/*
 * Copyright (c) 2014. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.github.jspxnet.txweb.view;

import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.Option;
import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.dao.OptionDAO;
import com.github.jspxnet.txweb.table.OptionBundle;
import com.github.jspxnet.utils.StringUtil;

import java.util.List;
import java.util.Set;

/**
 * Created by yuan on 14-3-13.
 * 系统提供页面的默认字典库 调用接口
 */

public class OptionProvider implements Option {
    @Ref
    protected OptionDAO optionDAO;

    @Param(caption = "命名空间")
    public void setNamespace(String namespace) {
        optionDAO.setNamespace(namespace);
    }

    /**
     * @param mode 模式:0.模式使用名称作为值,1:模式使用code字段作为值
     * @return 生成选项字符串
     */
    @Override
    @Param(caption = "模式")
    public String getOptions(int mode)  {
        List<OptionBundle> optionBundleList = optionDAO.getList(null, null, null, "sortType:A", 1, 500);
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

    @Override
    @Operate(caption = "选项值")
    public String getOptionValue(@Param(caption = "关键字") String key, @Param(caption = "命名空间") String namespace) throws Exception {
        optionDAO.setNamespace(namespace);
        OptionBundle optionBundle = optionDAO.getOptionValue(key);
        if (optionBundle == null) {
            return StringUtil.empty;
        }
        return optionBundle.getName();
    }

    @Override
    @Operate(caption = "选项")
    public String getOptions(@Param(caption = "命名空间") String namespace)  {
        return getOptions(0, namespace);

    }


    @Override
    public String getOptions(int mode, String namespace) {
        optionDAO.setNamespace(namespace);
        return getOptions(mode);
    }

    @Override
    @Operate(caption = "选择")
    public String getSelected(@Param(caption = "命名空间") String namespace)  {
        return getSelected(0, namespace);
    }

    /**
     * @param mode      显示模式 0:得到名称 1:得到代码
     * @param namespace 命名空间
     * @return 得到选项
     */
    @Override
    @Operate(caption = "选项")
    public String getSelected(@Param(caption = "模式") int mode, @Param(caption = "命名空间") String namespace) {
        optionDAO.setNamespace(namespace);
        OptionBundle optionBundle = optionDAO.getSelected();
        if (optionBundle == null) {
            return StringUtil.empty;
        }
        return mode == 0 ? optionBundle.getName() : optionBundle.getCode();
    }

    @Override
    public Set<String> getSpaceSet() {
        return optionDAO.getSpaceMap().keySet();
    }

    @Override
    public String getCaption(String key) {
        return optionDAO.getSpaceMap().get(key);
    }


}