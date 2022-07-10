package com.github.jspxnet.sober.config;

import com.github.jspxnet.scriptmark.core.TagNode;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.XMLUtil;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/15 23:34
 * description: jspbox
 **/
public abstract class BaseXmlTagNode extends TagNode {


    public String getId()
    {
        String name = XMLUtil.deleteQuote(getStringAttribute("id"));
        if (StringUtil.isNull(name))
        {
            name = XMLUtil.deleteQuote(getStringAttribute("name"));
        }
        return name;
    }

    public String getCaption()
    {
        return XMLUtil.deleteQuote(getStringAttribute("caption"));
    }

    public String getResultType()
    {
        String resultType = XMLUtil.deleteQuote(getStringAttribute("class"));
        if (!StringUtil.isNull(resultType))
        {
            return resultType;
        }
        return XMLUtil.deleteQuote(getStringAttribute("resultType"));
    }

    public String getIndex()
    {
        String result = XMLUtil.deleteQuote(getStringAttribute("index"));
        if (!StringUtil.isNull(result))
        {
            return result;
        }
        return null;
    }

    public String getDatabase()
    {
        String resultType = XMLUtil.deleteQuote(getStringAttribute("db"));
        if (!StringUtil.isNull(resultType))
        {
            return resultType;
        }
        return XMLUtil.deleteQuote(getStringAttribute("database"));
    }

    public String getQuote()
    {
        return XMLUtil.deleteQuote(getStringAttribute("quote"));
    }


    public String getTerm()
    {
        return XMLUtil.deleteQuote(getStringAttribute("term"));
    }

}
