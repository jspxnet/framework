/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.dispose;

import com.github.jspxnet.scriptmark.Phrase;
import com.github.jspxnet.scriptmark.ScriptRunner;
import com.github.jspxnet.scriptmark.ListIterator;
import com.github.jspxnet.scriptmark.core.block.BreakBlock;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.core.EnvRunner;
import com.github.jspxnet.scriptmark.core.block.ContinueBlock;
import com.github.jspxnet.scriptmark.core.script.ScriptTypeConverter;
import com.github.jspxnet.scriptmark.core.iterator.RangeIterator;
import com.github.jspxnet.scriptmark.core.block.ListBlock;
import com.github.jspxnet.scriptmark.exception.ScriptException;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.StringUtil;

import java.io.IOException;
import java.io.Writer;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2009-1-2
 * Time: 16:22:10
 * 循环列表
 */
public class ListPhrase implements Phrase {

    public ListPhrase() {

    }
    /**
     * 1:break 2:continue
     * @param env 运行环境
     * @param tagNode 节点
     * @param out 输出
     * @return 0:正常返回; 1:break 2:continue
     * @throws ScriptRunException 异常
     */
    @Override
    public int getRun(EnvRunner env, TagNode tagNode, Writer out) throws ScriptRunException {
        ScriptRunner scriptEngine = env.getScriptRunner();
        ListBlock listBlock = (ListBlock) tagNode;
        ///listBlock.getListName(); 可能就是一个表达式
        String listName = listBlock.getListName();
        if (!StringUtil.hasLength(listName)) {
            throw new ScriptRunException(tagNode, tagNode.getLineNumber() + ": error list tag ,列表表达式中的列表名称错误" + listBlock.getSource());

        }
        ListIterator listIterator;
        if (listName.contains("..")) {
            String sBegin = StringUtil.substringBefore(listName, "..");
            if (sBegin.startsWith("[")) {
                sBegin = sBegin.substring(1);
            }
            String send = StringUtil.substringAfter(listName, "..");
            if (send.endsWith("]")) {
                send = send.substring(0, send.length() - 1);
            }
            Object low;
            Object hig;

            if (ScriptTypeConverter.isStandardNumber(sBegin)) {
                low = StringUtil.toInt(sBegin);
            } else if (scriptEngine.containsVar(sBegin)) {
                try {
                    low = scriptEngine.eval(sBegin, listBlock.getLineNumber());
                } catch (ScriptException e) {
                    throw new ScriptRunException(tagNode, tagNode.getSource());
                }
            } else {
                low = sBegin;
            }
            if (ScriptTypeConverter.isStandardNumber(send)) {
                hig = StringUtil.toInt(send);
            } else if (scriptEngine.containsVar(send)) {
                try {
                    hig = scriptEngine.eval(send, listBlock.getLineNumber());
                } catch (ScriptException e) {
                    throw new ScriptRunException(tagNode, tagNode.getSource());
                }
            } else {
                hig = send;
            }
            if (!listBlock.getEquals() && low == hig) {
                return 0;
            }
            listIterator = new RangeIterator(low, hig);

        } else {
            try {
                listIterator = ScriptTypeConverter.getCollection(scriptEngine.eval(listName, listBlock.getLineNumber()));
            } catch (Exception e) {
                throw new ScriptRunException(tagNode, tagNode.getSource());
            }
        }
        
        if (listIterator.getLength()==0)
        {
            String emptyOut = listBlock.getEmpty();
            if (emptyOut!=null&&!StringUtil.empty.equals(emptyOut))
            {
                try {
                    out.write(emptyOut);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return 0;
        }

        int bc = 0;
        String varName = listBlock.getVarName();
        String separator = listBlock.getSeparator();
        String open = listBlock.getOpen();
        if (open!=null&&!StringUtil.empty.equals(open))
        {
            try {
                out.write(open);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (listIterator.hasNext()) {
            scriptEngine.put(varName + "_index", listIterator.getIndex());
            scriptEngine.put(varName + "_even", listIterator.getIndex() % 2 == 0);
            Object value = listIterator.next();
            boolean hasNext = listIterator.hasNext();
            scriptEngine.put(varName,value);
            scriptEngine.put(varName + "_has_next", hasNext);
            List<TagNode> childNode = listBlock.getChildList();
            //如果在循环中碰到错误，保持已经处理的数据，并返回,如果抛出异常就不能保持处理结果了
            for (TagNode node : childNode) {
                bc = env.runBlock(node, out);
                if (bc == ContinueBlock.VALUE || bc == BreakBlock.value) {
                    break;       //相当于conintue
                }
            }
            childNode.clear();
            //放入分割符号 begin
            if (hasNext&&separator!=null&&!StringUtil.empty.equals(separator))
            {
                try {
                    out.write(separator);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            //放入分割符号 end
            if (bc == BreakBlock.value) {
                break;
            }
        }
        String close = listBlock.getClose();
        if (close!=null&&!StringUtil.empty.equals(close))
        {
            try {
                out.write(close);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }
}