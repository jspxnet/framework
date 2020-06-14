/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.lucene;

import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;

import java.util.Collection;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-2-24
 * Time: 9:38:55
 */
public interface Lucene {

    void setFilePath(String filePath);

    String getFilePath();

    boolean save(LuceneVO luceneVO);

    boolean save(Collection<LuceneVO> list, boolean commit);

    int delete(String kayName, String value) throws Exception;

    Document toDocument(LuceneVO luceneVO);

    int delete(String id) throws Exception;

    int delete(Term term) throws Exception;

    SearchResult search(String queryText, int fontLength, int page, int count) throws Exception;

    SearchResult search(String[] keyName, String[] queryText, int fontLength, int page, int count) throws Exception;

    boolean deleteFile();

    SearchResult search(String queryText, int fontLength, String color, int page, int count) throws Exception;

    SearchResult search(String[] keyName, String[] queryText, int fontLength, String color, int page, int count) throws Exception;

    void deleteAll();
}