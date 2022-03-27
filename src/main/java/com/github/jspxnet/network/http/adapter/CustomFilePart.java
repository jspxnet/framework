package com.github.jspxnet.network.http.adapter;

import com.github.jspxnet.boot.environment.Environment;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.util.EncodingUtil;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/2/25 22:32
 * description: jspxpro
 **/
public class CustomFilePart  extends FilePart {

    public CustomFilePart(String filename, File file,String charset)
            throws FileNotFoundException {
        super(filename, file,DEFAULT_CONTENT_TYPE, charset);
    }

    @Override
    protected void sendDispositionHeader(OutputStream out) throws IOException {
        out.write(CONTENT_DISPOSITION_BYTES);
        out.write(QUOTE_BYTES);
        out.write(EncodingUtil.getBytes(getName(), getCharSet()));
        out.write(QUOTE_BYTES);
        String filename = getSource().getFileName();
        if (filename != null) {
            out.write(EncodingUtil.getAsciiBytes(FILE_NAME));
            out.write(QUOTE_BYTES);
            out.write(EncodingUtil.getBytes(filename, getCharSet()));
            out.write(QUOTE_BYTES);
        }

    }
}
