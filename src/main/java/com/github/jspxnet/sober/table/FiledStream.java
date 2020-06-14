package com.github.jspxnet.sober.table;

import java.io.IOException;
import java.io.InputStream;


public class FiledStream extends InputStream {
    private byte[] body;
    private long length;
    private long skip;

    public FiledStream(InputStream inputStream) throws IOException {

        byte[] data = new byte[inputStream.available()];
        inputStream.read(body, 0, data.length);
        length = data.length;
    }

    public byte[] getBytes() {
        return body;
    }

    @Override
    public long skip(long n) throws IOException {

        skip = n;
        if (skip < 0) {
            skip = 0;
        }
        if (skip > length) {
            skip = length;
        }
        return skip;
    }

    @Override
    public int read() throws IOException {
        return body[(int) skip++];
    }
}
