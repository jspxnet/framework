/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.component.zhex.bg2big5;

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.io.*;
import java.util.Objects;

/**
 * 用来处理GB2312/BIG5码字符互相转换的类.
 * 需要两个码表文件:/zeal/util/gb-big5.table,/zeal/util/big5-gb.table.
 * 这两个码表可以根据具体情况补充映射不正确的码.
 * Title: GB transfer Big5
 * Description: Deal with the convertion between gb2312 and big5 charset Strings.
 * Copyright: Copyright (c) 2004
 * Company: NewmenBase
 *
 * @author Zeal Li
 * @version 1.0
 */

@Slf4j
public class GB2Big5 {
    private final static String zhtwusedFile = "gbbig5word.txt";
    private final static String gb2Big5File = "gb-big5.txt";
    private final static String big52gb2File = "big5-gb.txt";

    private static byte[] b_big5Table;
    private static byte[] b_gbTable = null;
    private static StringMap<String, String> twZhUsedMap = new StringMap<>();

    private static GB2Big5 instance = null;

    public synchronized static GB2Big5 getInstance() {

        if (instance==null)
        {
            instance = new GB2Big5();
        }

        return instance;
    }



    /**
     * @param input 输入
     * @return 字符流转化为字节数组
     * @throws IOException 异常
     */
    public byte[] getByteArray(InputStream input) throws IOException {
        if (input == null) {
            log.error("不能读取配置文件");
            return null;
        }
        int status;
        int blockSize = 5000;
        int totalBytesRead = 0;
        int blockCount = 1;
        byte[] dynamicBuffer = new byte[blockSize * blockCount];
        final byte[] buffer = new byte[blockSize];

        boolean endOfStream = false;
        while (!endOfStream) {
            int bytesRead = 0;
            if (input.available() != 0) {
                // data is waiting so read as much as is available
                status = input.read(buffer);
                endOfStream = (status == -1);
                if (!endOfStream) {
                    bytesRead = status;
                }
            } else {
                // no data waiting so use the
                //one zhex read transfer block until
                // data is available or the end of the input stream is reached
                status = input.read();
                endOfStream = (status == -1);
                buffer[0] = (byte) status;
                if (!endOfStream) {
                    bytesRead = 1;
                }
            }

            if (!endOfStream) {
                if (totalBytesRead + bytesRead > blockSize * blockCount) {
                    // expand the size of the buffer
                    blockCount++;
                    byte[] newBuffer = new byte[blockSize * blockCount];
                    System.arraycopy(dynamicBuffer, 0,
                            newBuffer, 0, totalBytesRead);
                    dynamicBuffer = newBuffer;
                }
                System.arraycopy(buffer, 0,
                        dynamicBuffer, totalBytesRead, bytesRead);
                totalBytesRead += bytesRead;
            }
        }

        // make a copy of the array of the exact length
        byte[] result = new byte[totalBytesRead];
        if (totalBytesRead != 0) {
            System.arraycopy(dynamicBuffer, 0, result, 0, totalBytesRead);

        }
        return result;
    }




    /**
     * 指定两个码表文件来进行初始化
     *
     * @throws NullPointerException 异常
     */
    private GB2Big5() throws NullPointerException {
        if (twZhUsedMap==null)
        {
            twZhUsedMap = new StringMap<>();
        }

        twZhUsedMap.setKeySplit(StringUtil.EQUAL);
        twZhUsedMap.setLineSplit(StringUtil.CRLF);

        InputStream inputStream = GB2Big5.class.getResourceAsStream(zhtwusedFile);
        if (inputStream==null)
        {
            inputStream = GB2Big5.class.getResourceAsStream("/resources/reslib/table/" +zhtwusedFile);
        }
        if (inputStream == null) {
            File file = EnvFactory.getFile(zhtwusedFile);
            if (file != null) {
                try {
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (inputStream != null) {
            try {
                twZhUsedMap.setString(new String(Objects.requireNonNull(getBytesFromFile(inputStream)), Environment.defaultEncode));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }

        inputStream = GB2Big5.class.getResourceAsStream(gb2Big5File);
        if (inputStream==null)
        {
            inputStream = GB2Big5.class.getResourceAsStream("/resources/reslib/table/" +gb2Big5File);
        }
        if (inputStream==null)
        {
            try {
                inputStream = new FileInputStream(new File(System.getProperty("user.dir"),"/reslib/table/" +gb2Big5File));
            } catch (FileNotFoundException e) {
                inputStream =  null;
                e.printStackTrace();

            }
        }

        if (inputStream == null) {
            File file = EnvFactory.getFile(gb2Big5File);
            if (file != null) {
                try {
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        if (inputStream != null) {
            b_gbTable = getBytesFromFile(inputStream);
        }

        inputStream = GB2Big5.class.getResourceAsStream(big52gb2File);
        if (inputStream==null)
        {
            inputStream = GB2Big5.class.getResourceAsStream("/resources/reslib/table/" +big52gb2File);
        }
        if (inputStream==null)
        {

            try {
                inputStream = new FileInputStream( new File(System.getProperty("user.dir"),"/reslib/table/" +big52gb2File));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                inputStream =  null;
            }
        }

        if (inputStream == null) {
            File file = EnvFactory.getFile(big52gb2File);
            if (file != null) {
                try {
                    inputStream = new FileInputStream(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
        b_big5Table = getBytesFromFile(inputStream);
        if (null == b_gbTable) {


            throw new NullPointerException("No gb table can be load:" + System.getProperty("user.dir"));
        }
        if (null == b_big5Table) {
            throw new NullPointerException("No big5 table can be load:" + System.getProperty("user.dir"));
        }
    }

    /**
     * 把gb2312编码的字符串转化成big5码的字节流
     *
     * @param inStr gb2中文
     * @return big5中文
     * @throws Exception 异常
     */
    public String gb2big5(String inStr) throws Exception {
        if (null == inStr || inStr.length() <= 0) {
            return StringUtil.empty;
        }
        for (String big5Word : twZhUsedMap.keySet()) {
            inStr = StringUtil.replace(inStr, twZhUsedMap.get(big5Word), big5Word);
        }
        byte[] text = new String(inStr.getBytes("GBK"), "GBK").getBytes("GBK");
        int max = text.length - 1;
        int h;
        int l;
        int p;
        int b = 256;
        byte[] big = new byte[2];
        for (int i = 0; i < max; i++) {
            h = text[i];
            if (h < 0) {
                h = b + h;
                l = text[i + 1];
                if (l < 0) {
                    l = b + (int) (text[i + 1]);
                }
                if (h == 161 && l == 64) {
                    big[0] = big[1] = (byte) (161 - b);
                } else {
                    p = (h - 160) * 510 + (l - 1) * 2;
                    try {
                        big[0] = (byte) (b_gbTable[p] - b);
                    } catch (Exception e) {
                        big[0] = 45;
                    }
                    try {
                        big[1] = (byte) (b_gbTable[p + 1] - b);
                    } catch (Exception e) {
                        big[1] = 45;
                    }
                }
                text[i] = big[0];
                text[i + 1] = big[1];
                i++;
            }

        }
        return new String(text, "BIG5");
    }

    /**
     * 把big5码的字符串转化成gb2312码的字符串
     *
     * @param inStr big5中文
     * @return gb2中文
     * @throws Exception 异常
     */
    public String big52gb(String inStr) throws Exception {
        if (null == inStr || inStr.length() <= 0) {
            return StringUtil.empty;
        }

        byte[] Text = new String(inStr.getBytes("BIG5"), "BIG5").getBytes("BIG5");
        int max = Text.length - 1;
        int h;
        int l;
        int p;
        int b = 256;
        byte[] big = new byte[2];
        for (int i = 0; i < max; i++) {
            h = Text[i];
            if (h < 0) {
                h = b + h;
                l = Text[i + 1];
                if (l < 0) {
                    l = b + (int) (Text[i + 1]);
                }
                if (h == 161 && l == 161) {
                    big[0] = (byte) (161 - b);
                    big[1] = (byte) (64 - b);
                } else {
                    p = (h - 160) * 510 + (l - 1) * 2;
                    try {
                        big[0] = (byte) (b_big5Table[p] - b);
                    } catch (Exception e) {
                        big[0] = 45;
                    }
                    try {
                        big[1] = (byte) (b_big5Table[p + 1] - b);
                    } catch (Exception e) {
                        big[1] = 45;
                    }
                }
                Text[i] = big[0];
                Text[i + 1] = big[1];
                i++;
            }

        }
        String result = new String(Text, "GBK");
        for (String big5Word : twZhUsedMap.keySet()) {
            result = StringUtil.replace(result, big5Word, twZhUsedMap.get(big5Word));
        }
        return result;
    }


    /**
     * 把文件读入字节数组，读取失败则返回null
     *
     * @param in 输入流
     * @return 返回byte
     */
    private byte[] getBytesFromFile(InputStream in) {
        try {
            return getByteArray(in);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String getGbkToBig5(String gbk) throws Exception {

        return getInstance().gb2big5(gbk);
    }

    public static String getBig5ToGbk(String big5) throws Exception {
        return getInstance().big52gb(big5);
    }


}