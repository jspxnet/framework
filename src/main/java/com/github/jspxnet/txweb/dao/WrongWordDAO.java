package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.txweb.table.WrongWord;

import java.util.List;

/**
 * Created by yuan on 2015/6/21 0021.
 */
public interface WrongWordDAO extends SoberSupport, DFAFilter {

    List<WrongWord> getList(String[] fields, String[] find, String term, String sort, int page, int count) throws Exception;

}
