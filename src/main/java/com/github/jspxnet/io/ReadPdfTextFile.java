package com.github.jspxnet.io;

import com.github.jspxnet.boot.environment.Environment;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.parser.PdfReaderContentParser;
import com.itextpdf.text.pdf.parser.SimpleTextExtractionStrategy;
import com.itextpdf.text.pdf.parser.TextExtractionStrategy;
import java.io.IOException;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/10/22 20:47
 * description: jspbox
 **/
public class ReadPdfTextFile extends AbstractRead {
    public ReadPdfTextFile() {
        encode = Environment.defaultEncode;
    }

    private  PdfReader reader = null;
    public ReadPdfTextFile(String s) {
        resource = s;
    }

    @Override
    public boolean open() {
        return resource != null;
    }

    @Override
    protected void readContent() {
        result.setLength(0);
        try {
            reader = new PdfReader(resource);
            PdfReaderContentParser parser = new PdfReaderContentParser(reader);
            TextExtractionStrategy strategy;
            for (int i = 1; i <= reader.getNumberOfPages(); i++) {
                strategy = parser.processContent(i,new SimpleTextExtractionStrategy());
                result.append(strategy.getResultantText());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void close() {
        if (reader!=null)
        {
            reader.close();
        }
    }
}