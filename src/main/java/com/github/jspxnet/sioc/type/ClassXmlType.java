package com.github.jspxnet.sioc.type;

import com.github.jspxnet.sioc.util.TypeUtil;
import com.github.jspxnet.utils.ClassUtil;
import java.lang.reflect.Type;

public class ClassXmlType extends TypeSerializer {

    @Override
    public Type getJavaType()
    {
        return Class.class;
    }

    @Override
    public String getTypeString() {
        return TypeUtil.TYPE_CLASS;
    }

    @Override
    public Object getTypeObject() {
        if (value instanceof java.lang.Class) {
            return value;
        }
        if (value instanceof String) {
            try {
                String className = (String) value;
                return ClassUtil.loadClass(className);
            } catch (Exception e) {
                return null;
            }
        }
        return value;
    }

    @Override
    public String getXmlString() {
        StringBuilder sb = new StringBuilder();
        sb.append("<date name=\"").append(name).append("\">").append(value).append("</date>\r\n");
        return sb.toString();
    }
}
