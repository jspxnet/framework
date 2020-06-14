package com.github.jspxnet.txweb.dao.impl;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import com.github.jspxnet.boot.environment.Environment;
import org.slf4j.LoggerFactory;
import com.github.jspxnet.sober.Criteria;
import com.github.jspxnet.sober.TableModels;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.criteria.projection.Projections;
import com.github.jspxnet.sober.ssql.SSqlExpression;
import com.github.jspxnet.txweb.dao.WrongWordDAO;
import com.github.jspxnet.txweb.table.WrongWord;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yuan on 2015/6/21 0021.
 * 错别字 修正
 */
@Slf4j
public class WrongWordDAOImpl extends DFAFilterImpl implements WrongWordDAO {

    /**
     * 构造函数，初始化敏感词库
     */
    public WrongWordDAOImpl() {

    }

    @Override
    public boolean deleteAll() {
        return createCriteria(WrongWord.class).delete(false) > 0;
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
    public List<WrongWord> getList(String[] fields, String[] find, String term, String sort, int page, int count) throws Exception {
        Criteria criteria = createCriteria(WrongWord.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(fields)) {
            criteria = criteria.add(Expression.find(fields, find));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        criteria = SSqlExpression.getSortOrder(criteria, sort);
        return criteria.setCurrentPage(page).setTotalCount(count).list(false);
    }


    @Override
    public Set<String> getOriginal(Set<String> words) {
        if (words.isEmpty()) {
            return words;
        }
        Criteria criteria = createCriteria(WrongWord.class).add(Expression.in("wrong", words));
        List<WrongWord> list = criteria.setCurrentPage(1).setTotalCount(100).list(false);
        Set<String> result = new HashSet<String>();
        for (WrongWord wrongWord : list) {
            result.add(wrongWord.getWrong() + "\t" + wrongWord.getWord());
        }
        return result;
    }

    /**
     * @param find 查询
     * @param term 条件
     * @return 返回数据条数
     * @throws Exception 异常
     */
    @Override
    public int getCount(String[] fields, String[] find, String term) throws Exception {
        Criteria criteria = createCriteria(WrongWord.class);
        if (!ArrayUtil.isEmpty(find) && !ArrayUtil.isEmpty(fields)) {
            criteria = criteria.add(Expression.find(fields, find));
        }
        criteria = SSqlExpression.getTermExpression(criteria, term);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    @Override
    public int getCount() {
        Criteria criteria = createCriteria(WrongWord.class);
        return criteria.setProjection(Projections.rowCount()).intUniqueResult();
    }

    @Override
    public boolean hasWord(String word) {
        if (StringUtil.isNull(word)) {
            return false;
        }
        Criteria criteria = createCriteria(WrongWord.class).add(Expression.eq("wrong", word));
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
        List<WrongWord> saveWrongWordList = new ArrayList<WrongWord>();
        List<String> checkList = new ArrayList<String>();
        String[] lines = StringUtil.split(StringUtil.convertCR(txt), StringUtil.CR);
        for (String line : lines) {
            if (StringUtil.isNull(line) || checkList.contains(line)) {
                continue;
            }
            if (!line.contains(StringUtil.TAB)) {
                continue;
            }
            String wrong = StringUtil.substringBefore(line, StringUtil.TAB);
            if (wrong.startsWith("#")) {
                continue;
            }
            String right = StringUtil.substringAfter(line, StringUtil.TAB);
            if (StringUtil.isNull(right)) {
                continue;
            }
            WrongWord wrongWord = new WrongWord();
            wrongWord.setWrong(wrong);
            wrongWord.setWord(right);
            wrongWord.setIp(Environment.localeIP);
            wrongWord.setPutUid(Environment.SYSTEM_ID);
            wrongWord.setPutName(Environment.SYSTEM_NAME);
            saveWrongWordList.add(wrongWord);

            checkList.add(line);

            if (saveWrongWordList.size() > 500) {
                super.batchSave(saveWrongWordList);
                saveWrongWordList.clear();
            }
        }
        if (!saveWrongWordList.isEmpty()) {
            super.batchSave(saveWrongWordList);
            saveWrongWordList.clear();
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
            if (!line.contains(StringUtil.TAB)) {
                continue;
            }
            String wrong = StringUtil.substringBefore(line, StringUtil.TAB);
            if (wrong.startsWith("#")) {
                continue;
            }
            if (hasWord(wrong)) {
                continue;
            }
            String right = StringUtil.substringAfter(line, StringUtil.TAB);
            if (StringUtil.isNull(right)) {
                continue;
            }
            WrongWord wrongWord = new WrongWord();
            wrongWord.setWrong(wrong);
            wrongWord.setWord(right);
            wrongWord.setIp(Environment.localeIP);
            wrongWord.setPutUid(Environment.SYSTEM_ID);
            wrongWord.setPutName(Environment.SYSTEM_NAME);
            saveRow = saveRow + super.save(wrongWord);
        }
        return saveRow;
    }

    @Override
    public int updateTimes(Set<String> keys) {
        TableModels soberTable = getSoberTable(WrongWord.class);
        StringBuilder sb = new StringBuilder();
        for (String key : keys) {
            sb.append(StringUtil.quote(key, false)).append(",");
        }
        if (sb.toString().endsWith(",")) {
            sb.setLength(sb.length() - 1);
        }
        String sql = "UPDATE " + soberTable.getName() + " SET times=times+1 WHERE wrong IN(" + sb.toString() + ")";
        return super.update(sql);
    }

    //------------------------------------------------------------------------------------------------------------------
    @Override
    public void init() throws Exception {
        if (!wordMap.isEmpty()) {
            return;
        }
        log.info("开始错别字词库");
        int maxRows = super.getSoberFactory().getMaxRows();
        try {
            int maxWord = getCount();
            if (maxWord > 100000) {
                maxWord = 100000;
            }
            super.getSoberFactory().setMaxRows(maxWord);
            List<WrongWord> list = getList(null, null, null, null, 1, maxWord);
            Set<String> keyWordSet = new HashSet<String>();
            for (WrongWord WrongWord : list) {
                keyWordSet.add(WrongWord.getWrong());
            }
            addWordToHashMap(keyWordSet);
            list.clear();
            keyWordSet.clear();
            log.info("开始错别字结束,共载入" + wordMap.size() + "个");
        } finally {
            super.getSoberFactory().setMaxRows(maxRows);
        }
    }

}
