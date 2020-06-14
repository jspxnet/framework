package com.github.jspxnet.scriptmark.core.block;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;

import java.util.List;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/14 22:02
 * description: jspbox
 **/
public interface BaseTryBlock {
    List<TagNode> getBodyList() throws Exception;

    String getBody();

    List<TagNode> getCatchBodyList() throws ScriptRunException;

    String getCatchBody();

    List<TagNode> getFinallyBodyList() throws ScriptRunException;

    String getFinallyBody();
}
