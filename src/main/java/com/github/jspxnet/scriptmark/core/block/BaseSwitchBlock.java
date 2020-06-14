package com.github.jspxnet.scriptmark.core.block;

import com.github.jspxnet.scriptmark.core.TagNode;

import java.util.List;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/14 22:00
 * description: jspbox
 **/
public interface BaseSwitchBlock {
    String getVarName();

    List<TagNode> getCaseAndDefaultBlock();
}
