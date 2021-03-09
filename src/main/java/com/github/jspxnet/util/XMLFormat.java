package com.github.jspxnet.util;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.io.StringInputStream;
import com.github.jspxnet.io.StringOutputStream;
import com.github.jspxnet.utils.BeanUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.StringUtil;
import com.sun.org.apache.xml.internal.serialize.OutputFormat;
import com.sun.org.apache.xml.internal.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by yuan on 2014/6/14 0014.
 * xml 格式化有不同的方式，本类主要用于兼容安卓系统
 * 可以使用dom4j包，或者 xercesImpl.jar  xml-apis.jar 两个包
 */
public class XMLFormat {
    private static final String CLASS_w3c = "w3c";
    //private static final String CLASS_dom4j = "dom4j";
    private static final String CLASS_jdom = "jdom";

    final private static Map<String, String> classMap = new LinkedHashMap<String, String>();
    private static String className = null;
    private static String defaultEncode = Environment.defaultEncode;


    static {
        classMap.put(CLASS_jdom, "org.jdom.output.Format");
        classMap.put(CLASS_w3c, "org.w3c.dom.Document");
      //  classMap.put(CLASS_dom4j, "org.dom4j.io.SAXReader");

        defaultEncode = EnvFactory.getEnvironmentTemplate().getString(Environment.encode, Environment.defaultEncode);
        className = EnvFactory.getEnvironmentTemplate().getString(Environment.xmlFormatClass, Environment.auto);

        //jdk 内带
        if (StringUtil.isNull(className) || Environment.auto.equalsIgnoreCase(className)) {
            className = null;
            for (String name : classMap.keySet()) {
                if (className == null) {
                    try {
                        Class.forName(classMap.get(name));
                        className = name;
                        if (!StringUtil.isNull(className)) {
                            break;
                        }
                    } catch (Exception e) {
                        className = CLASS_jdom;
                    }
                }
            }
        }
    }

    /**
     * @param xml 字符串
     * @return 删除头
     */
    private static String deleteHtmlHead(String xml) {
        if (StringUtil.isNull(xml)) {
            return StringUtil.empty;
        }
        if (xml.contains("<?xml") && xml.contains("?>")) {
            return xml.substring(xml.indexOf("?>") + 2);
        }
        return xml;
    }

    /**
     * 不能有 <?xml version=\"1.0\" encoding=\"utf-8\"?> 这个头
     * org/w3c/dom/ElementTraversal
     *
     * @param xml xml 字符串
     * @return 格式化xml
     */
   /* static private String formatForDom4j(String xml) {
        try {
            org.dom4j.io.SAXReader saxReader = (org.dom4j.io.SAXReader) ClassUtil.newInstance("org.dom4j.io.SAXReader");
            org.dom4j.Document document = saxReader.read(new StringReader(deleteHtmlHead(xml)));
            *//** 格式化输出,类型IE浏览一样 *//*
            org.dom4j.io.OutputFormat format = org.dom4j.io.OutputFormat.createPrettyPrint();
            format.setEncoding(defaultEncode);
            format.setOmitEncoding(true);
            format.setXHTML(false);
            format.setExpandEmptyElements(false);

            StringWriter out = new StringWriter();
            org.dom4j.io.XMLWriter writer = new org.dom4j.io.XMLWriter(out, format);
            writer.write(document);
            writer.close();
            return deleteHtmlHead(out.toString());
        } catch (Exception ex) {

            ex.printStackTrace();
            return StringUtil.empty;
        }
    }
*/
    /**
     * @param xml xml 字符串
     * @return 格式化xml jdom 方式
     */
    static private String formatForW3C(String xml) {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource(new StringReader(deleteHtmlHead(xml)));
            Document document = db.parse(is);
            OutputFormat format = new OutputFormat(document);
            format.setLineWidth(65);
            format.setIndenting(true);
            format.setIndent(2);
            Writer out = new StringWriter();
            XMLSerializer serializer = new XMLSerializer(out, format);
            serializer.serialize(document);
            return deleteHtmlHead(out.toString());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param xml xml 字符串
     * @return 格式化xml jdom 方式
     */
    static private String formatForJDom(String xml) {
        StringOutputStream out = new StringOutputStream();
        try {
            Object format = ClassUtil.callStaticMethod(Class.forName("org.jdom.output.Format"), "getCompactFormat");
            //org.jdom.output.Format format = org.jdom.output.Format.getCompactFormat();
            BeanUtil.setSimpleProperty(format, "setEncoding", defaultEncode);
            //format.setEncoding(defaultEncode);
            BeanUtil.setSimpleProperty(format, "setIndent", "  ");
            //format.setIndent("  ");

            Object builder = ClassUtil.newInstance("org.jdom.input.SAXBuilder");
            //org.jdom.input.SAXBuilder builder = new org.jdom.input.SAXBuilder();//建立构造器关注裴文龙的博客

            InputStream inputStream = new StringInputStream(deleteHtmlHead(xml));
            Method method = builder.getClass().getMethod("build", InputStream.class);

            Object doc = method.invoke(builder, inputStream);
            //org.jdom.Document doc = builder.build(inputStream);
            Class c = ClassUtil.loadClass("org.jdom.output.XMLOutputter");
            Constructor c1 = c.getDeclaredConstructor(ClassUtil.loadClass("org.jdom.output.Format"));
            c1.setAccessible(true);
            Object XmlOut = c1.newInstance(format);
            //org.jdom.output.XMLOutputter XmlOut = new org.jdom.output.XMLOutputter(format);
            Method output = XmlOut.getClass().getMethod("output", ClassUtil.loadClass("org.jdom.Document"), OutputStream.class);
            //XmlOut.output(doc, out);
            output.invoke(XmlOut, doc, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deleteHtmlHead(out.toString());
    }


    /**
     * 为了避免错误
     *
     * @param xml 字符串
     * @return 格式化
     */
    static private String formatForJspx(String xml) {
        xml = deleteHtmlHead(xml);
        boolean end = false;
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < xml.length(); i++) {
            char c = xml.charAt(i);
            sb.append(c);
            if (c == '<' && i < xml.length() - 1 && xml.charAt(i + 1) == '/') {
                end = true;
            }
            if (end && c == '>') {
                end = false;
                sb.append(StringUtil.CRLF);
            }
        }
        return StringUtil.replace(sb.toString(), "\r\n\r\n", "\r\n");
    }


    /**
     * @param xml 字符串
     * @return 格式化xml  支持JDK,安卓需要添加 xercesImpl.jar  xml-apis.jar
     */
    static public String format(String xml) {
        try {
            if (CLASS_jdom.equals(className)) {
                return formatForJDom(xml);
            }
            if (CLASS_w3c.equals(className)) {
                return formatForW3C(xml);
            }
           /* if (CLASS_dom4j.equals(className)) {
                return formatForDom4j(xml);
            }
*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        return formatForJspx(xml);
    }

    public String getFormatClass() {
        return className;
    }
}
