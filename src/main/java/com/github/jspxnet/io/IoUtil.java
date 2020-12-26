package com.github.jspxnet.io;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;

import java.io.IOException;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/12/27 0:09
 * description: 封装一下都起单元
 **/
public class IoUtil {
    private IoUtil()
    {

    }

    /**
     *
     * @param file 文件
     * @return 读取文件信息
     * @throws IOException 异常
     */
    public static String autoReadText(String file) throws IOException {
        return autoReadText( file, Environment.defaultEncode);
    }

    /**
     *
     * @param file 文件
     * @param encode 编码
     * @return 读取文件信息
     * @throws IOException 异常
     */
    public static String autoReadText(String file,String encode) throws IOException
    {
        String fileType = FileUtil.getTypePart(file);
        AbstractRead abstractRead;
        if (ArrayUtil.inArray(ReadWordTextFile.fileType,fileType,true))
        {
            abstractRead = new ReadWordTextFile();
        } else
        if ("pdf".equalsIgnoreCase(fileType))
        {
            abstractRead = new ReadPdfTextFile();

        } else
        {
            abstractRead = new AutoReadTextFile();
        }
        abstractRead.setEncode(encode);
        abstractRead.setFile(file);
        return abstractRead.getContent();

    }
}
