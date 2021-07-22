package com.github.jspxnet.txweb.dao.impl;

import lombok.extern.slf4j.Slf4j;


import com.github.jspxnet.io.AbstractRead;
import com.github.jspxnet.io.AutoReadTextFile;
import com.github.jspxnet.txweb.dao.StringDFADAO;
import com.github.jspxnet.utils.StringUtil;
import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * 文本文档，DFA 过滤接口
 */
@Slf4j
public class StringDFADAOImpl extends DFAFilterImpl implements StringDFADAO {

    /**
     * 构造函数，初始化敏感词库
     */
    public StringDFADAOImpl() {

    }

    private String[] fileName;

    public void setFileName(String[] fileName) {
        this.fileName = fileName;
    }

    @Override
    public boolean deleteAll() {
        return false;
    }


    /**
     * @param find 查询
     * @param term 条件
     * @return 返回数据条数
     */
    @Override
    public int getCount(String[] fields, String[] find, String term) {
        return wordMap.size();
    }

    @Override
    public int getCount() {
        return wordMap.size();
    }

    @Override
    public boolean hasWord(String word) {
        return false;
    }

    @Override
    public Set<String> getOriginal(Set<String> words) {
        return words;
    }
    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param txt 导入文本
     * @return 导入行数
     */
    @Override
    public int importWord(String txt) {
        return 0;
    }

    @Override
    public int checkImportWord(String txt) {
        return 0;
    }

    @Override
    public int updateTimes(Set<String> keys) {
        return 0;
    }

    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void init() throws Exception {
        if (!wordMap.isEmpty()) {
            return;
        }
        log.info("开始载入词库");
        Set<String> keyWordSet = new HashSet<String>();
        for (String name : fileName) {
            File file = new File(name);
            if (!file.exists()) {
                continue;
            }
            AbstractRead abstractRead = new AutoReadTextFile();
            abstractRead.setEncode("UTF-8");
            abstractRead.setFile(file);
            String text = abstractRead.getContent();
            String[] lines = StringUtil.split(StringUtil.replace(text, "\r\n", "\n"), "\n");
            for (String line : lines) {
                if (StringUtil.isNull(line)) {
                    continue;
                }
                keyWordSet.add(StringUtil.trim(line));
            }
        }
        addWordToHashMap(keyWordSet);
        log.info("载入词结束,共载入" + wordMap.size() + "个");
    }


}
