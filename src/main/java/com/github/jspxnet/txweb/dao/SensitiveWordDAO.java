package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.txweb.table.SensitiveWord;

import java.util.List;

/**
 * Created by yuan on 2015/6/19 0019.
 */
public interface SensitiveWordDAO extends SoberSupport, DFAFilter {

    List<SensitiveWord> getList(String[] fields, String[] find, String term, String sort, int page, int count) throws Exception;

}
