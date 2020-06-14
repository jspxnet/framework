/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.lucene.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.lucene.ChineseAnalyzer;
import com.github.jspxnet.lucene.tag.TagDictionary;
import com.github.jspxnet.lucene.wordcount.OneWord;
import com.github.jspxnet.lucene.wordcount.WordStatCount;
import com.github.jspxnet.utils.StringUtil;
import org.wltea.analyzer.core.IKSegmenter;
import org.wltea.analyzer.core.Lexeme;

import java.io.*;
import java.util.Set;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-11-14
 * Time: 15:00:35
 * <p>
 * com.github.jspxnet.lucene.impl.ChineseAnalyzerImpl;
 */

public class ChineseAnalyzerImpl implements ChineseAnalyzer {
    public ChineseAnalyzerImpl() {

    }

    /**
     * @param cline     已经分词的字符串
     * @param separator 分割符号
     * @param num       要求保留的过数
     * @return 自动生成文章关键字
     */
    @Override
    public String getTag(String cline, String separator, int num, boolean zh) {
        TagDictionary dictionary = TagDictionary.getInstance();
        String[] tagArray = StringUtil.split(getAnalyzerWord(cline, separator), separator);
        StringBuilder result = new StringBuilder();
        for (String tag : tagArray) {
            if (dictionary.isTagFormat(tag)) {
                if (StringUtil.isChinese(tag) != zh) {
                    continue;
                }
                result.append(tag).append(separator);
            }
        }
        WordStatCount wst = new WordStatCount();
        Set wordSet = wst.getWordCount(result.toString(), separator);
        result.setLength(0);
        for (Object o : wordSet) {
            OneWord ow = (OneWord) o;
            result.append(ow.getWord()).append(separator);
            num--;
            if (num <= 0) {
                break;
            }
        }
        if (result.length() < 1) {
            return StringUtil.empty;
        }
        if (result.toString().endsWith(separator)) {
            result.setLength(result.length() - separator.length());
        }
        return result.toString();
    }

    @Override
    public String getAnalyzerWord(String string, String separator) {
        return getAnalyzerWord(string, separator, 0, 0);
    }

    /**
     * @param string    脂肪层
     * @param separator 切分
     * @param length    长度，对应大文件，大字符串做个长度限制，更加安全
     * @param size      保留个数
     * @return 切分字符串
     */
    @Override
    public String getAnalyzerWord(String string, String separator, int length, int size) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new StringReader(string));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            return analyze(buffer.toString(), separator, length, size);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return StringUtil.empty;
    }


    @Override
    public String getAnalyzerWord(File file, String separator) {
        return getAnalyzerWord(file, separator, 0, 0);
    }

    /**
     * @param file      脂肪层
     * @param separator 切分
     * @param length    长度
     * @param size      保留个数
     * @return 切分一个文本文件
     */
    @Override
    public String getAnalyzerWord(File file, String separator, int length, int size) {
        InputStream in = null;
        InputStreamReader inReader = null;
        try {
            in = new FileInputStream(file);
            inReader = new InputStreamReader(in, Environment.defaultEncode);
            BufferedReader bufferedReader = new BufferedReader(inReader);
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
            return analyze(buffer.toString(), separator, length, size);

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (inReader != null) {
                    inReader.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return StringUtil.empty;
    }

    private String analyze(String text, String separator, int length, int size)
            throws IOException {
        int times = Integer.MAX_VALUE;
        if (size > 0) {
            times = size;
        }
        StringBuilder buffer = new StringBuilder();
        IKSegmenter segmentation = new IKSegmenter(new StringReader(text), true);
        Lexeme le;
        while ((le = segmentation.next()) != null) {
            String word = le.getLexemeText();
            if (length > 0 && word.length() < length) {
                continue;
            }

            buffer.append(word);
            buffer.append(separator);
            times--;
            if (times < 0) {
                break;
            }
        }
        return buffer.toString();
    }


}