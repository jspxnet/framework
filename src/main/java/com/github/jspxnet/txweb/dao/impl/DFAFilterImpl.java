package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.sioc.annotation.Init;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.sober.jdbc.JdbcOperations;
import com.github.jspxnet.txweb.dao.DFAFilter;
import com.github.jspxnet.utils.StringUtil;
import java.util.*;

/**
 * @author 原作者，chenming 2014
 * Created by yuan on 2015/6/19 0019.
 * <p>
 * 读取敏感词库，将敏感词放入HashSet中，构建一个DFA算法模型：<br>
 * 中 = {
 * isEnd = 0
 * 国 = {<br>
 * isEnd = 1
 * 人 = {isEnd = 0
 * 民 = {isEnd = 1}
 * }
 * 男  = {
 * isEnd = 0
 * 人 = {
 * isEnd = 1
 * }
 * }
 * }
 * }
 * 五 = {
 * isEnd = 0
 * 星 = {
 * isEnd = 0
 * 红 = {
 * isEnd = 0
 * 旗 = {
 * isEnd = 1
 * }
 * }
 * }
 * }
 */
@Slf4j
public abstract class DFAFilterImpl extends JdbcOperations implements DFAFilter {

    //关键词
    protected final Map<String, String> wordMap = new HashMap<String, String>();
    //要排除的关键词，排除方法，是判断前边的字符串，或者后边的字符串相等，就排除
    protected final Map<String, List<String>> outMap = new HashMap<String, List<String>>();

    public static int minMatchTYpe = 1;      //最小匹配规则
    public static int maxMatchType = 2;      //最大匹配规则
    protected final static String isEnd = "isEnd";

    //为了调试方便，加一个开关，调试的时候将不载入关键词，这样优化调试速度
    protected boolean loadWord = true;

    public void setLoadWord(boolean loadWord) {
        this.loadWord = loadWord;
    }


    @Override
    @Init
    public void refresh() {

        wordMap.clear();
        try {
            init();
        } catch (Exception e) {
            log.error(e.getLocalizedMessage());
        }
    }

    /**
     * 将关键词添加到 map里边，留到后边搜索
     *
     * @param keyWordSet 关键词
     */
    protected void addWordToHashMap(Set<String> keyWordSet) {
        wordMap.clear();

        Map nowMap;
        Map<String, String> newWorMap;
        //迭代keyWordSet
        for (String key : keyWordSet) {
            //如果开头为#号的关键字将被过滤丢掉
            if (key.startsWith("#")) {
                continue;
            }
            if (key.contains("![") && key.contains("]")) {
                //判断是否有排除词组 性服务![技术性服务]  口交![进出口交易]
                List<String> noList = new ArrayList<String>();
                String cf = key;
                key = StringUtil.substringBefore(key, "!");
                String[] lines = StringUtil.split(StringUtil.replace(StringUtil.substringBetween(cf, "[", "]"), ";", ","), ",");
                for (String line : lines) {
                    line = StringUtil.trim(line);
                    noList.add(line);
                }
                outMap.put(key, noList);
            }
            //要过滤
            nowMap = wordMap;
            for (int i = 0; i < key.length(); i++) {
                char keyChar = key.charAt(i);              //转换成char型
                Object wordMap = nowMap.get(keyChar);       //获取
                if (wordMap != null) {        //如果存在该key，直接赋值
                    nowMap = (Map) wordMap;
                } else {     //不存在则，则构建一个map，同时将isEnd设置为0，因为他不是最后一个
                    newWorMap = new HashMap<String, String>();
                    newWorMap.put(isEnd, "0");     //不是最后一个
                    nowMap.put(keyChar, newWorMap);
                    nowMap = newWorMap;
                }
                if (i == key.length() - 1) {
                    nowMap.put(isEnd, "1");    //最后一个
                }
            }
        }

    }


    //DFA  算法--------------------------------------------------------------------------------------------------------

    /**
     * @param txt       文字
     * @param matchType 匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @return 若包含返回true，否则返回false  判断文字是否包含敏感字符
     */
    @Override
    public boolean contains(String txt, int matchType) {
        boolean flag = false;
        for (int i = 0; i < txt.length(); i++) {
            int matchFlag = indexOf(txt, i, matchType); //判断是否包含敏感字符
            if (matchFlag > 0) {    //大于0存在，返回true
                flag = true;
            }
        }
        return flag;
    }


    /**
     * @param txt       文字
     * @param matchType 匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @return 获取文字中的敏感词
     */
    @Override
    public Set<String> search(String txt, int matchType) {
        Set<String> wordList = new HashSet<String>();
        if (StringUtil.isNull(txt)) {
            return wordList;
        }
        for (int i = 0; i < txt.length(); i++) {
            int length = indexOf(txt, i, matchType);    //判断是否包含敏感字符
            if (length > 0) {    //存在,加入list中
                String key = txt.substring(i, i + length);
                //找到的关键词，key，再找是否排除
                List<String> noList = outMap.get(key);
                if (noList != null && !noList.isEmpty()) {
                    boolean have = false;
                    //判断是否存在排除字符串
                    for (String okKey : noList) {
                        int pos = okKey.indexOf(key);
                        // pos 如果为 -1 表示配置错误,和当前关键词无关
                        if (pos != -1) {
                            String textWord = txt.substring(i - pos, (i - pos + okKey.length()) > txt.length() ? txt.length() : (i - pos + okKey.length()));
                            if (okKey.equalsIgnoreCase(textWord)) {
                                //存在排除
                                have = true;
                                break;
                            }
                        }
                    }
                    if (!have) {
                        wordList.add(key);
                        i = i + length - 1;    //减1的原因，是因为for会自增
                    }
                } else {
                    //没有配置，排除字符串
                    wordList.add(key);
                    i = i + length - 1;    //减1的原因，是因为for会自增
                }
            }
        }
        return wordList;
    }

    /**
     * 替换敏感字字符
     *
     * @param txt         文字
     * @param matchType   匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @param replaceChar 替换字符，默认*
     */
    @Override
    public String replace(String txt, int matchType, String replaceChar) {
        if (StringUtil.isNull(txt)) {
            return StringUtil.empty;
        }
        String resultTxt = txt;
        Set<String> set = search(txt, matchType);     //获取所有的敏感词
        Iterator<String> iterator = set.iterator();
        String word;
        String replaceString;
        while (iterator.hasNext()) {
            word = iterator.next();
            replaceString = StringUtil.replaceChars(replaceChar, word.length());
            resultTxt = resultTxt.replaceAll(word, replaceString);
        }

        return resultTxt;
    }


    /**
     * 检查文字中是否包含敏感字符，检查规则如下：<br>
     *
     * @param txt        字符串
     * @param beginIndex 开始位置
     * @param matchType  匹配规则&nbsp;1：最小匹配规则，2：最大匹配规则
     * @return 如果存在，则返回敏感词字符的长度，不存在返回0
     */
    @Override
    public int indexOf(String txt, int beginIndex, int matchType) {
        boolean flag = false;    //敏感词结束标识位：用于敏感词只有1位的情况
        int matchFlag = 0;     //匹配标识数默认为0
        char word;
        Map nowMap = wordMap;
        for (int i = beginIndex; i < txt.length(); i++) {
            word = txt.charAt(i);
            nowMap = (Map) nowMap.get(word);     //获取指定key
            if (nowMap != null) {     //存在，则判断是否为最后一个
                matchFlag++;     //找到相应key，匹配标识+1
                if ("1".equals(nowMap.get(isEnd))) {       //如果为最后一个匹配规则,结束循环，返回匹配标识数
                    flag = true;       //结束标志位为true
                    if (minMatchTYpe == matchType) {    //最小规则，直接返回,最大规则还需继续查找
                        break;
                    }
                }
            } else {
                //不存在，直接返回
                break;
            }
        }
        if (matchFlag < 2 || !flag) {        //长度必须大于等于1，为词
            matchFlag = 0;
        }
        return matchFlag;
    }

    @Override
    public int size() {
        return wordMap.size();
    }


}
