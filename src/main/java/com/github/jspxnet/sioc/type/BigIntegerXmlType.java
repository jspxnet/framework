package com.github.jspxnet.sioc.type;

import com.github.jspxnet.utils.StringUtil;


import java.math.BigInteger;

public class BigIntegerXmlType extends TypeSerializer {
    @Override
    public String getTypeString() {
        return "BigInteger";
    }

    @Override
    public Object getTypeObject() {

        if (value == null || !StringUtil.isStandardNumber(value.toString())) {
            return BigInteger.ZERO;
        }
        return new BigInteger(value.toString());
    }

    @Override
    public String getXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<BigInteger name=\"").append(name).append("\">").append(value).append("</BigInteger>\r\n");
        return sb.toString();
    }
}