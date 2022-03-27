package com.github.jspxnet.sioc.type;

import com.github.jspxnet.utils.StringUtil;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

public class BigDecimalXmlType extends TypeSerializer {

    @Override
    public Type getJavaType()
    {
        return BigDecimal.class;
    }

    @Override
    public String getTypeString() {
        return "BigDecimal";
    }

    @Override
    public Object getTypeObject() {
        if (value == null || !StringUtil.isStandardNumber(value.toString())) {
            return BigInteger.ZERO;
        }
        return new BigDecimal(value.toString());
    }

    @Override
    public String getXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<BigDecimal name=\"").append(name).append("\">").append(value).append("</BigDecimal>\r\n");
        return sb.toString();
    }
}