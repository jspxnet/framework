package com.github.jspxnet.io;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.network.http.HttpClient;
import com.github.jspxnet.network.http.HttpClientFactory;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.StringUtil;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2020/12/27 0:09
 * description: 封装一下都起单元
 **/
public class IoUtil {
    public final static String[] WORD_FILE_TYPE =  new String[]{"doc","docx"};
    public final static String[] PDF_FILE_TYPE =  new String[]{"pdf"};
    private IoUtil()
    {

    }

    /**
     *
     * @param file 文件
     * @return 读取文件信息
     * @throws Exception 异常
     */
    public static String autoReadText(String file) throws Exception {
        return autoReadText( file, Environment.defaultEncode);
    }

    /**
     *
     * @param file 文件
     * @return 读取文件信息
     * @throws Exception 异常
     */
    public static String autoReadText(File file) throws Exception {
        return autoReadText( file.getPath(), Environment.defaultEncode);
    }

    /**
     *
     * @param file 文件
     * @param encode 编码
     * @return 读取文件信息
     * @throws Exception 异常
     */
    public static String autoReadText(String file,String encode) throws Exception
    {

        if (file!=null&&file.startsWith("http"))
        {
            HttpClient httpClient = HttpClientFactory.createHttpClient(file);
            httpClient.setEncode(StringUtil.isNull(encode)? StandardCharsets.UTF_8.name():encode);
            return httpClient.getString();
        }

        String fileType = FileUtil.getTypePart(file);
        AbstractRead abstractRead;
        if (ArrayUtil.inArray(WORD_FILE_TYPE,fileType,true))
        {
            abstractRead = (AbstractRead)ClassUtil.newInstance(ReadWordTextFile.class.getName());
        } else
        if (ArrayUtil.inArray(PDF_FILE_TYPE,fileType,true))
        {
            abstractRead = (AbstractRead)ClassUtil.newInstance(ReadPdfTextFile.class.getName());
        } else
        {
            abstractRead = new AutoReadTextFile();
        }
        abstractRead.setEncode(encode);
        abstractRead.setFile(file);
        return abstractRead.getContent();
    }



}
