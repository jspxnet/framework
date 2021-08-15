package com.github.jspxnet.util;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.utils.StringUtil;
import org.apache.poi.hssf.converter.ExcelToHtmlConverter;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/6/15 23:23
 * description: office 文档处理
 **/
public final class OfficeUtil {
    private OfficeUtil()
    {}

    public static String excelToHtml(InputStream input,String cssPath) throws Exception {
        String content = null;
        try (ByteArrayOutputStream outStream = new ByteArrayOutputStream()){
            HSSFWorkbook excelBook=new HSSFWorkbook(input);
            ExcelToHtmlConverter excelToHtmlConverter = new ExcelToHtmlConverter (DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument() );
            excelToHtmlConverter.processWorkbook(excelBook);
            Document htmlDocument = excelToHtmlConverter.getDocument();

            DOMSource domSource = new DOMSource (htmlDocument);
            StreamResult streamResult = new StreamResult (outStream);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer serializer = tf.newTransformer();
            serializer.setOutputProperty (OutputKeys.ENCODING, Environment.defaultEncode);
            serializer.setOutputProperty (OutputKeys.INDENT, "yes");
            serializer.setOutputProperty (OutputKeys.STANDALONE, "yes");
            serializer.setOutputProperty (OutputKeys.METHOD, "html");
            serializer.transform (domSource, streamResult);
            content = new String (outStream.toByteArray(), Environment.defaultEncode);
        } finally {
            input.close();
        }

        if (!StringUtil.isEmpty(cssPath))
        {
            int pos = content.indexOf("</head>");
            if (pos!=-1)
            {
                //添加一个外部可干扰的样式表
                content = content.substring(0,pos) + "\r\n<link rel=\"stylesheet\" type=\"text/css\" href=\""+cssPath+"\">\r\n" + content.substring(pos);
            }
        }
        return content;
    }

}
