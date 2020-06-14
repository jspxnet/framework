/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.lucene.wordcount;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-9-25
 * Time: 16:49:23
 */
public class WordStatCount {
    //the default hyphen collection.
    private static String regex = " '";


    /**
     * @param text 字符串
     * @return the words as a Set by default comparator and hyphens
     */
    public Set getWordCount(String text) {
        return getWordCount(text, regex, FREQUENCY_ORDER);
    }


    /**
     * @param text  字符串
     * @param regex the default hyphen collection.
     * @return the words as a Set by the default comparator
     */
    public Set<com.github.jspxnet.lucene.wordcount.OneWord> getWordCount(String text, String regex) {
        return getWordCount(text, regex, FREQUENCY_ORDER);
    }

    /**
     * @param text  字符串
     * @param order 排序
     * @return the words as a Set by the default hyphens
     */
    public Set<com.github.jspxnet.lucene.wordcount.OneWord> getWordCount(String text, Comparator<com.github.jspxnet.lucene.wordcount.OneWord> order) {

        return getWordCount(text, regex, order);
    }

    /**
     * return the words as a Set by the text,the word are all changed transfer
     * lower case.
     *
     * @param text      the English text you want transfer split.
     * @param separator the hyphens that the word can use.
     * @param order     the order of the Set returned by.
     * @return the word Set that the text contains.
     */
    public Set<com.github.jspxnet.lucene.wordcount.OneWord> getWordCount(String text, String separator, Comparator<com.github.jspxnet.lucene.wordcount.OneWord> order) {
        Map<com.github.jspxnet.lucene.wordcount.OneWord, com.github.jspxnet.lucene.wordcount.OneWord> map = new HashMap<com.github.jspxnet.lucene.wordcount.OneWord, com.github.jspxnet.lucene.wordcount.OneWord>();
        String[] words = text.split(separator);
        for (String word : words) {
            com.github.jspxnet.lucene.wordcount.OneWord o = new com.github.jspxnet.lucene.wordcount.OneWord(word);
            if (map.containsKey(o)) {
                (map.get(o)).increase();
            } else {
                map.put(o, o);
            }
        }

        Set<com.github.jspxnet.lucene.wordcount.OneWord> sort = new TreeSet<com.github.jspxnet.lucene.wordcount.OneWord>(order);
        sort.addAll(map.keySet());
        return Collections.unmodifiableSet(sort);
    }

    /**
     * the sort constant of DICTIONARY,the default sort contant.
     */

    public static Comparator DICTIONARY_ORDER = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            com.github.jspxnet.lucene.wordcount.OneWord w1 = (com.github.jspxnet.lucene.wordcount.OneWord) o1;
            com.github.jspxnet.lucene.wordcount.OneWord w2 = (com.github.jspxnet.lucene.wordcount.OneWord) o2;
            return w1.getWord().compareTo(w2.getWord());
        }
    };
    /**
     * the sort contant of FREQUENCY,the words was sorted by the apperance
     * times in the Set.
     */
    final public static Comparator<com.github.jspxnet.lucene.wordcount.OneWord> FREQUENCY_ORDER = new Comparator<com.github.jspxnet.lucene.wordcount.OneWord>() {
        @Override
        public int compare(com.github.jspxnet.lucene.wordcount.OneWord w1, OneWord w2) {
            int i = w2.getCount() - w1.getCount();
            if (i == 0) {
                return w1.getWord().compareTo(w2.getWord());
            }
            return i;
        }
    };
}