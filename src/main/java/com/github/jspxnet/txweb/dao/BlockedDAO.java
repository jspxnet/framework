package com.github.jspxnet.txweb.dao;

import com.github.jspxnet.sober.SoberSupport;
import com.github.jspxnet.txweb.table.BlockedWord;

import java.util.List;

/**
 * Created by yuan on 2015/6/11 0011.
 */
public interface BlockedDAO extends SoberSupport, DFAFilter {

    List<BlockedWord> getList(String[] fields, String[] find, String term, String sort, int page, int count) throws Exception;

}
