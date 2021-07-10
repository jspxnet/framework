package com.github.jspxnet.io;

import com.github.jspxnet.boot.environment.Environment;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/22 20:32
 * description: jspbox
 **/

public class ReadWordTextFile extends AbstractRead {
    private static final Logger log = LoggerFactory.getLogger(ReadWordTextFile.class);
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