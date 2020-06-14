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
    public CustomFilePart(String filename, File file)
            throws FileNotFoundException {
        super(filename, file);
    }

    @Override
    protected void sendDispositionHeader(OutputStream out) throws IOException {
        super.sendDispositionHeader(out);
        String filename = getSource().getFileName();
        if (filename != null) {
            out.write(EncodingUtil.getAsciiBytes(FILE_NAME));
            out.write(QUOTE_BYTES);
            out.write(EncodingUtil.getBytes(filename, Environment.defaultEncode));
            out.write(QUOTE_BYTES);
        }
    }
}
