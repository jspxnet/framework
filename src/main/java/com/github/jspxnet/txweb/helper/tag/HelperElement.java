package com.github.jspxnet.txweb.helper.tag;

import com.github.jspxnet.scriptmark.XmlEngine;
import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.scriptmark.parse.XmlEngineImpl;
import com.github.jspxnet.txweb.helper.PageHelper;
import com.github.jspxnet.utils.XMLUtil;

import java.util.List;

/**
 * Created by chenyuan on 15-3-30.
 */
public class HelperElement extends TagNode {

    public HelperElement() {

    }

    public String getName() {
        return XMLUtil.deleteQuote(getStringAttribute("name"));
    }

    public List<TagNode> getHelpElements() throws Exception {
        XmlEngine xmlEngine = new XmlEngineImpl();
        xmlEngine.putTag(PageHelper.Tag_HELP, HelpElement.class.getName());
        return xmlEngine.getTagNodes(getBody());
    }
}