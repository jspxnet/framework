package com.github.jspxnet.scriptmark.core.block;

import com.github.jspxnet.scriptmark.core.TagNode;

import java.util.List;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/14 21:54
 * description: jspbox
 **/
public interface BaseIfBlock {
    String getWhere();

    List<TagNode> getTrueNode();

    String getBody(boolean bt);

    List<TagNode> getElseBlock();
}
