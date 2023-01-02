package com.github.jspxnet.io;

import com.github.jspxnet.boot.environment.Environment;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;



import java.io.FileInputStream;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/22 20:32
 * description: jspbox
 *
 **/
@Slf4j
public class ReadWordTextFile extends AbstractRead {
    public final static String[] FILE_TYPE =  new String[]{"doc","docx"};
    public ReadWordTextFile() {
        encode = Environment.defaultEncode;
    }

    public ReadWordTextFile(String s) {
        resource = s;
    }

    @Override
    public boolean open() {
        return resource != null;
    }

    @Override
    protected void readContent() {
        if (resource==null)
        {
            return;
        }
        result.setLength(0);
        try {
            if (resource.toLowerCase().endsWith(".doc"))
            {
                try (FileInputStream is = new FileInputStream(resource))
                {
                    WordExtractor ex = new WordExtractor(is);
                    result.append(ex.getText());
                }
            } else if (resource.toLowerCase().endsWith("docx")) {
                OPCPackage opcPackage = POIXMLDocument.openPackage(resource);
                POIXMLTextExtractor extractor = new XWPFWordExtractor(opcPackage);
                result.append(extractor.getText());
            } else {
                log.info("此文件不是word文件！");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void close() {

    }
}