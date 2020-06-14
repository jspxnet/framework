package com.github.jspxnet.scriptmark.core.dispose;

import com.github.jspxnet.scriptmark.Phrase;
import com.github.jspxnet.scriptmark.ScriptmarkEnv;
import com.github.jspxnet.scriptmark.core.EnvRunner;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.exception.ScriptRunException;
import com.github.jspxnet.utils.StringUtil;

import java.io.IOException;
import java.io.Writer;

/**
 * Created by yuan on 2015/2/1 0001.
 * 压缩输出
 */
public class CompressPhrase implements Phrase {
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
        tagNode.setTagName(tagNode.getTemplate().getConfigurable().getString(ScriptmarkEnv.CompressBlockName));
        String s = tagNode.getBody();
        StringBuilder sb = new StringBuilder();
        if (s != null) {
            for (int i = 0; i < s.length(); i++) {
                char c = s.charAt(i);
                if (c == ' ') {
                    continue;
                }
                sb.append(c);
            }
        }
        try {
            out.write(StringUtil.trim(sb.toString()));
        } catch (IOException e) {
            throw new ScriptRunException(tagNode, tagNode.getSource());
        }
        return 0;
    }

}