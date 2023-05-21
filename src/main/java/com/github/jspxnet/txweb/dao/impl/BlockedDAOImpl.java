package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.dao.BlockedDAO;
import com.github.jspxnet.txweb.table.BlockedWord;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * Created by yuan on 2015/6/11 0011.
 * 违法关键词,如果有就不允许发布
 */
@Slf4j
public class BlockedDAOImpl extends DFAFilterImpl implements BlockedDAO {


    @Override
    public boolean deleteAll() {
        return createCriteria(BlockedWord.class).delete(false) > 0;
    }

    /**
     * @param txt 导入文本
     * @return 导入行数
     * @throws Exception 异常
     */
    @Override
    public int importWord(String txt) throws Exception {
        int saveRow = 0;
        List<BlockedWord> saveBlockedWordList = new ArrayList<BlockedWord>();
        List<String> checkList = new ArrayList<String>();
        String[] lines = StringUtil.split(StringUtil.convertCR(txt), StringUtil.CR);
        for (String line : lines) {
            if (StringUtil.isNull(line) || checkList.contains(line)) {
                continue;
            }
            if (line.startsWith("http") && line.contains(StringUtil.TAB)) {
                String url = StringUtil.substringBefore(line, StringUtil.TAB);
                if (url.length() > 4) {
                    checkList.add(url);
                    BlockedWord blockedWord = new BlockedWord();
                    blockedWord.setWord(url);
                    blockedWord.setIp(Environment.localeIP);
                    blockedWord.setPutUid(Environment.SYSTEM_ID);
                    blockedWord.setPutName(Environment.SYSTEM_NAME);
                    saveBlockedWordList.add(blockedWord);
                }
                String host = StringUtil.substringAfter(line, StringUtil.TAB);
                if (!StringUtil.isNull(host) || host.length() > 3) {
                    checkList.add(host);
                    BlockedWord blockedWord = new BlockedWord();
                    blockedWord.setWord(host);
                    blockedWord.setIp(Environment.localeIP);
                    blockedWord.setPutUid(Environment.SYSTEM_ID);
                    blockedWord.setPutName(Environment.SYSTEM_NAME);
                    saveBlockedWordList.add(blockedWord);
                }
            } else {
                if (line.length() < 3 && !StringUtil.isChinese(line)) {
                    continue;
                }
                checkList.add(line);
                BlockedWord blockedWord = new BlockedWord();
                blockedWord.setWord(line);
                blockedWord.setIp(Environment.localeIP);
                blockedWord.setPutUid(Environment.SYSTEM_ID);
                blockedWord.setPutName(Environment.SYSTEM_NAME);
                saveBlockedWordList.add(blockedWord);
            }

            if (saveBlockedWordList.size() > 500) {
                super.batchSave(saveBlockedWordList);
                saveBlockedWordList.clear();
            }
        }

        if (!saveBlockedWordList.isEmpty()) {
            super.batchSave(saveBlockedWordList);
            saveBlockedWordList.clear();
        }

        checkList.clear();
        return saveRow;
    }

    @Override
    public int checkImportWord(String txt) throws Exception {
        int saveRow = 0;
        String[] lines = StringUtil.split(StringUtil.convertCR(txt), StringUtil.CR);
        for (String line : lines) {
            if (StringUtil.isNull(line)) {
                continue;
            }
            if (line.startsWith("http") && line.contains(StringUtil.TAB)) {

                String url = StringUtil.substringBefore(line, StringUtil.TAB);
                if (url.length() > 2) {
                    BlockedWord blockedWord = new BlockedWord();
                    blockedWord.setWord(url);
                    blockedWord.setIp(Environment.localeIP);
                    blockedWord.setPutUid(Environment.SYSTEM_ID);
                    blockedWord.setPutName(Environment.SYSTEM_NAME);
                    saveRow = saveRow + super.save(blockedWord);
                }
                String host = StringUtil.substringAfter(line, StringUtil.TAB);
                if (!StringUtil.isNull(host) || host.length() > 3) {
                    BlockedWord blockedWord = new BlockedWord();
                    blockedWord.setWord(host);
                    blockedWord.setIp(Environment.localeIP);
                    blockedWord.setPutUid(Environment.SYSTEM_ID);
                    blockedWord.setPutName(Environment.SYSTEM_NAME);
                    saveRow = saveRow + super.save(blockedWord);
                }
            } else {
                if (line.length() < 3 && !StringUtil.isChinese(line)) {
                    continue;
                }
                if (hasWord(line)) {
                    continue;
                }
                BlockedWord blockedWord = new BlockedWord();
                blockedWord.setWord(line);
                blockedWord.setIp(Environment.localeIP);
                blockedWord.setPutUid(Environment.SYSTEM_ID);
                blockedWord.setPutName(Environment.SYSTEM_NAME);
                saveRow = saveRow + super.save(blockedWord);
            }
        }
        return saveRow;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param find  查询
     * @param term  条件
     * @param sort  排序方式
     * @param page  页数
     * @param count 数量
     * @return 得到列表
     * @throws Exception 异常
     */
    @Override
    public List<BlockedWord> getList(String[] fields, String[] find, String term, String sort, int page, int count) throws Exception {
        Criteria criteria = createCriteria(BlockedWord.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(fields)) {
            criteria = criteria.add(Expression.find(fields, find));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, sort);
        return criteria.setCurrentPage(page).setTotalCount(count).list(false);
    }

    @Override
    public int getCount() {
        Criteria criteria = createCriteria(BlockedWord.class);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    @Override
    public Set<String> getOriginal(Set<String> words) {
        return words;
    }

    /**
     * @param find 查询
     * @param term 条件
     * @return 返回数据条数
     * @throws Exception 异常
     */
    @Override
    public int getCount(String[] fields, String[] find, String term) throws Exception {
        Criteria criteria = createCriteria(BlockedWord.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(fields)) {
            criteria = criteria.add(Expression.find(fields, find));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    @Override
    public boolean hasWord(String word) {
        if (StringUtil.isNull(word)) {
            return false;
        }
        Criteria criteria = createCriteria(BlockedWord.class).add(Expression.eq("word", word));
        return criteria.setProjection(Projections.rowCount()).intUniqueResult() > 0;
    }

    @Override
    public int updateTimes(Set<String> keys) throws Exception {

        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(StringUtil.quote(key, false)).append(",");
        }
        if (sb.toString().endsWith(",")) {
            sb.setLength(sb.length() - 1);
        }

        if (sb.toString().length() > 2) {
            TableModels blockedWordTable = getSoberTable(BlockedWord.class);
            String sql2 = "UPDATE " + blockedWordTable.getName() + " SET times=times+1 WHERE word IN (" + sb.toString() + ")";
            return super.update(sql2);
        }
        return -1;
    }

    //------------------------------------------------------------------------------------------------------------------

    @Override
    public void init() throws Exception {
        if (!loadWord)
        {
            return;
        }
        if (!wordMap.isEmpty()) {
            return;
        }
        log.info("开始载入屏蔽域名");
        int maxRows = super.getSoberFactory().getMaxRows();
        try {
            int maxWord = getCount();
            super.getSoberFactory().setMaxRows(maxWord);
            Set<String> keyWordSet = new HashSet<String>();

            log.info("开始载入屏蔽关键字");
            List<BlockedWord> blockedWordList = getList(null, null, null, null, 1, maxWord);
            for (BlockedWord blockedWord : blockedWordList) {
                keyWordSet.add(blockedWord.getWord());
            }
            addWordToHashMap(keyWordSet);
            log.info("载入屏蔽关键字,共载入" + keyWordSet.size() + "个");
            blockedWordList.clear();
            keyWordSet.clear();
        } finally {
            super.getSoberFactory().setMaxRows(maxRows);
        }
    }


}
