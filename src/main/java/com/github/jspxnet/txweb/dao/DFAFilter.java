package com.github.jspxnet.txweb.dao;

import java.util.Set;

/**
 * Created by chenyuan on 15-6-19.
 * DAF 算法接口
 * http://cmsblogs.com/?p=1031
 */
public interface DFAFilter {
    void init() throws Exception;

    boolean deleteAll();

    int getCount(String[] fields, String[] find, String term) throws Exception;

    int getCount();

    void refresh();

    int updateTimes(Set<String> keys) throws Exception;

    int importWord(String txt) throws Exception;

    int checkImportWord(String txt) throws Exception;

    boolean contains(String txt, int matchType);

    Set<String> search(String txt, int matchType);

    String replace(String txt, int matchType, String replaceChar);

    int indexOf(String txt, int beginIndex, int matchType);

    boolean hasWord(String word);

    int size();

    Set<String> getOriginal(Set<String> words);

}
