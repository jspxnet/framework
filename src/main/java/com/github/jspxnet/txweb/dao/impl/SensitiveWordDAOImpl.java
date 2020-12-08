package com.github.jspxnet.txweb.dao.impl;

import com.github.jspxnet.txweb.table.*;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.dao.SensitiveWordDAO;
import java.util.*;


@Slf4j
public class SensitiveWordDAOImpl extends DFAFilterImpl implements SensitiveWordDAO {


    /**
     * 构造函数，初始化敏感词库
     */
    public SensitiveWordDAOImpl() {

    }


    @Override
    public boolean deleteAll() {
        return createCriteria(SensitiveWord.class).delete(false) > 0;
    }


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
    public List<SensitiveWord> getList(String[] fields, String[] find, String term, String sort, int page, int count) throws Exception {
        Criteria criteria = createCriteria(SensitiveWord.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(fields)) {
            criteria = criteria.add(Expression.find(fields, find));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, sort);
        return criteria.setCurrentPage(page).setTotalCount(count).list(false);
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
        Criteria criteria = createCriteria(SensitiveWord.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(fields)) {
            criteria = criteria.add(Expression.find(fields, find));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    @Override
    public int getCount() {
        Criteria criteria = createCriteria(SensitiveWord.class);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    @Override
    public boolean hasWord(String word) {
        if (StringUtil.isNull(word)) {
            return false;
        }
        Criteria criteria = createCriteria(SensitiveWord.class).add(Expression.eq("word", word));
        return criteria.setProjection(Projections.rowCount()).intUniqueResult() > 0;
    }

    //------------------------------------------------------------------------------------------------------------------

    /**
     * @param txt 导入文本
     * @return 导入行数
     * @throws Exception 异常
     */
    @Override
    public int importWord(String txt) throws Exception {
        int saveRow = 0;
        List<SensitiveWord> saveSensitiveWordList = new ArrayList<SensitiveWord>();
        List<String> checkList = new ArrayList<String>();
        String[] lines = StringUtil.split(StringUtil.convertCR(txt), StringUtil.CR);
        for (String line : lines) {
            if (StringUtil.isNull(line) || checkList.contains(line)) {
                continue;
            }
            SensitiveWord sensitiveWord = new SensitiveWord();
            sensitiveWord.setWord(line);

            sensitiveWord.setIp(Environment.localeIP);
            sensitiveWord.setPutUid(Environment.SYSTEM_ID);
            sensitiveWord.setPutName(Environment.SYSTEM_NAME);
            saveSensitiveWordList.add(sensitiveWord);

            checkList.add(line);

            if (saveSensitiveWordList.size() > 500) {
                super.batchSave(saveSensitiveWordList);
                saveSensitiveWordList.clear();
            }

        }
        if (!saveSensitiveWordList.isEmpty()) {
            super.batchSave(saveSensitiveWordList);
            saveSensitiveWordList.clear();
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
            if (hasWord(line)) {
                continue;
            }
            SensitiveWord blockedWord = new SensitiveWord();
            blockedWord.setWord(line);
            blockedWord.setIp(Environment.localeIP);
            blockedWord.setPutUid(Environment.SYSTEM_ID);
            blockedWord.setPutName(Environment.SYSTEM_NAME);
            saveRow = saveRow + super.save(blockedWord);
        }
        return saveRow;
    }

    @Override
    public int updateTimes(Set<String> keys) throws Exception {
        TableModels soberTable = getSoberTable(SensitiveWord.class);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(StringUtil.quote(key, false)).append(",");
        }
        if (sb.toString().endsWith(",")) {
            sb.setLength(sb.length() - 1);
        }
        String sql = "UPDATE " + soberTable.getName() + " SET times=times+1 WHERE word IN (" + sb.toString() + ")";
        return super.update(sql);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void init() throws Exception {
        if (!wordMap.isEmpty()) {
            return;
        }
        log.info("开始载入敏感词库");
        int maxRows = super.getSoberFactory().getMaxRows();
        try {
            int maxWord = getCount();
            if (maxWord > 100000) {
                maxWord = 100000;
            }
            super.getSoberFactory().setMaxRows(maxWord);
            List<SensitiveWord> list = getList(null, null, null, null, 1, maxWord);
            Set<String> keyWordSet = new HashSet<String>();
            for (SensitiveWord sensitiveWord : list) {
                keyWordSet.add(sensitiveWord.getWord());
            }
            addWordToHashMap(keyWordSet);
            list.clear();
            keyWordSet.clear();
            log.info("开始载入敏感词结束,共载入" + wordMap.size() + "个");
        } finally {
            super.getSoberFactory().setMaxRows(maxRows);
        }
    }

}
