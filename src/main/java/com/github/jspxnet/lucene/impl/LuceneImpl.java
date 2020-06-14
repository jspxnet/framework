
package com.github.jspxnet.lucene.impl;

import com.github.jspxnet.lucene.Lucene;
import com.github.jspxnet.lucene.LuceneVO;
import com.github.jspxnet.lucene.SearchResult;
import com.github.jspxnet.sioc.annotation.Bean;

import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import com.github.jspxnet.utils.HtmlUtil;
import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.document.*;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.search.highlight.Highlighter;
import org.apache.lucene.search.highlight.QueryScorer;
import org.apache.lucene.search.highlight.SimpleFragmenter;
import org.apache.lucene.search.highlight.SimpleHTMLFormatter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.wltea.analyzer.lucene.IKAnalyzer;

import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2014-05-23
 * Time: 9:39:21
 * com.github.jspxnet.lucene.impl.LuceneImpl
 * 此版本支持 lucene 7.X
 */
@Slf4j
@Bean
public class LuceneImpl implements Lucene {
    public final static String id = "id";
    public final static String title = "title";
    public final static String content = "content";
    public final static String docType = "docType";
    public final static String nodeId = "nodeId";
    public final static String other = "other";
    public final static String domain = "domain";
    public final static String createDate = "createDate";
    private final static String defaultHighlighterColor = "#c60a00";
    private Analyzer analyzer = new IKAnalyzer();

    private Path file = null;

    public LuceneImpl() {
    }

    private String filePath = StringUtil.empty;

    @Override
    public void setFilePath(String filePath) {

        file = Paths.get(filePath);
        FileUtil.makeDirectory(file.toFile());
        this.filePath = file.toFile().getAbsolutePath();
    }

    @Override
    public String getFilePath() {
        return filePath;
    }


    public Directory getDirectory() {
        try {
            return FSDirectory.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 取得IndexWriter
     *
     * @return IndexWriter
     */
    private IndexWriter getWriter() {
        IndexWriterConfig iwConfig = new IndexWriterConfig(analyzer);
        iwConfig.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
        IndexWriter indexwriter = null;
        try {
            indexwriter = new IndexWriter(getDirectory(), iwConfig);
        } catch (IOException e) {
            log.error("搜索索引" + getDirectory(), e);
        }
        return indexwriter;
    }

    @Override
    public boolean save(LuceneVO luceneVO) {
        List<LuceneVO> list = new ArrayList<>(1);
        list.add(luceneVO);
        return save(list, true);
    }

    /**
     * 保存索引,一次添加多个
     *
     * @param list   LuceneTO
     * @param commit 提交
     * @return boolean
     */
    @Override
    public boolean save(Collection<LuceneVO> list, boolean commit) {
        IndexWriter indexwriter = getWriter();
        if (indexwriter == null) {
            return false;
        }
        Directory directory = getDirectory();
        try {
            for (LuceneVO luceneVO : list) {
                if (StringUtil.isNull(luceneVO.getTitle()) || StringUtil.isNull(luceneVO.getContent())) {
                    continue;
                }
                Document doc = toDocument(luceneVO);
                indexwriter.addDocument(doc);
            }
        } catch (IOException ex) {
            log.error(" Lucene addDocument(document)错误.." + filePath, ex);
            return false;
        } finally {
            try {
                //需要重新建立Index，则需要调用
                if (indexwriter.hasUncommittedChanges()) {

                    indexwriter.commit();
                    indexwriter.close();
                }
                if (directory != null) {
                    directory.close();
                }

            } catch (Exception ex) {
                log.error(" Lucene 关闭 indexwriter文档错误.." + filePath, ex);
            }
        }
        return true;
    }

    /**
     * 删除索引
     *
     * @param term 删除条件
     * @return int
     */
    @Override
    public int delete(Term term) throws Exception {
        int deleted = 0;
        IndexWriter indexwriter = getWriter();
        try {
            indexwriter.deleteDocuments(term);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (indexwriter != null) {
                    indexwriter.commit();
                    indexwriter.close(); //关闭目录
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return deleted;
    }


    @Override
    public SearchResult search(String queryText, int fontLength, int page, int count) throws Exception {
        return search(queryText, fontLength, defaultHighlighterColor, page, count);
    }

    /**
     * @param queryText  查询字符串
     * @param fontLength 文字长度
     * @param color      高亮颜色
     * @param page       页数
     * @param count      显示数量
     * @return 搜索结果
     * @throws Exception 异常
     */
    @Override
    public SearchResult search(String queryText, int fontLength, String color, int page, int count) throws Exception {
        if (queryText == null) {
            return null;
        }
        if (fontLength < 2) {
            fontLength = 300;
        }
        if (StringUtil.isNull(color)) {
            color = defaultHighlighterColor;
        }
        QueryParser parser = new QueryParser(content, analyzer);
        parser.setDefaultOperator(QueryParser.AND_OPERATOR);

        Query query = parser.parse(queryText);

        if (query == null) {
            return null;
        }
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color=\"" + color + "\">", "</font></b>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
        highlighter.setTextFragmenter(new SimpleFragmenter(fontLength));//这个100是指定关键字字符串的context的长度，你可以自己设定，因为不可能返回整篇正文内容

        int begin = page * count - count;
        if (begin < 0) {
            begin = 0;
        }
        int end = begin + count;
        if (end < begin) {
            end = begin;
        }


        DirectoryReader reader = DirectoryReader.open(getDirectory());
        IndexSearcher searcher = new IndexSearcher(reader);
        //在索引器中使用IKSimilarity相似度评估器
        SearchResult searchResult = new SearchResult();
        List<LuceneVO> result = new ArrayList<>();
        try {
            TopDocs topDocs = searcher.search(query, end);
            ScoreDoc[] scoreDoc = topDocs.scoreDocs;
            searchResult.setTotalCount(scoreDoc.length);
            if (end > scoreDoc.length) {
                end = scoreDoc.length;
            }
            for (int i = begin; i < end; i++) {
                ScoreDoc sDoc = scoreDoc[i];
                Document doc = searcher.doc(sDoc.doc);
                LuceneVO lto = new LuceneVO();
                lto.setId(doc.get(id));
                lto.setTitle(doc.get(title));
                lto.setContent(doc.get(content));
                TokenStream tokenStream = analyzer.tokenStream(content, new StringReader(lto.getContent()));
                lto.setContent(highlighter.getBestFragment(tokenStream, lto.getContent()));
                lto.setDocType(doc.get(docType));
                lto.setNodeId(doc.get(nodeId));
                lto.setOther(doc.get(other));
                lto.setDomain(doc.get(domain));
                lto.setCreateDate(DateTools.stringToDate(doc.get(createDate)));
                result.add(lto);
            }
        } finally {
            reader.close();
        }
        searchResult.setList(result);
        return searchResult;
    }

    @Override
    public SearchResult search(String[] keyName, String[] queryText, int fontLength, int page, int count) throws Exception {
        return search(keyName, queryText, fontLength, defaultHighlighterColor, page, count);
    }

    /**
     * @param keyName    搜索字段
     * @param queryText  搜索数据
     * @param fontLength 切的文章长度
     * @param color      颜色
     * @param page       页数
     * @param count      每页显示数量
     * @return 搜索结果封装
     * @throws Exception 异常
     */
    @Override
    public SearchResult search(String[] keyName, String[] queryText, int fontLength, String color, int page, int count) throws Exception {
        if (StringUtil.isNull(color)) {
            color = defaultHighlighterColor;
        }
        if (keyName == null) {
            keyName = new String[]{"title", "content"};
        }
        if (queryText == null) {
            return null;
        }
        if (fontLength < 1) {
            fontLength = 300;
        }

        Query query = MultiFieldQueryParser.parse(queryText, keyName, analyzer);
        SimpleHTMLFormatter simpleHTMLFormatter = new SimpleHTMLFormatter("<b><font color=\"" + color + "\">", "</font></b>");
        Highlighter highlighter = new Highlighter(simpleHTMLFormatter, new QueryScorer(query));
        highlighter.setTextFragmenter(new SimpleFragmenter(fontLength));//这个100是指定关键字字符串的context的长度，你可以自己设定，因为不可能返回整篇正文内容

        int begin = page * count - count;
        if (begin < 0) {
            begin = 0;
        }
        int end = begin + count;
        if (end < begin) {
            end = begin;
        }

        DirectoryReader reader = DirectoryReader.open(getDirectory());
        IndexSearcher searcher = new IndexSearcher(reader);
        SearchResult searchResult = new SearchResult();
        List<LuceneVO> result = new ArrayList<>();
        try {
            TopDocs topDocs = searcher.search(query, end);
            ScoreDoc[] scoreDoc = topDocs.scoreDocs;
            searchResult.setTotalCount(scoreDoc.length);
            if (end > scoreDoc.length) {
                end = scoreDoc.length;
            }
            for (int i = begin; i < end; i++) {
                ScoreDoc sDoc = scoreDoc[i];
                Document doc = searcher.doc(sDoc.doc);
                LuceneVO lto = new LuceneVO();
                lto.setId(doc.get(id));
                lto.setTitle(doc.get(title));
                lto.setContent(doc.get(content));
                TokenStream tokenStream = analyzer.tokenStream(content, new StringReader(lto.getContent()));
                lto.setContent(highlighter.getBestFragment(tokenStream, lto.getContent()));
                lto.setDocType(doc.get(docType));
                lto.setNodeId(doc.get(nodeId));
                lto.setOther(doc.get(other));
                lto.setDomain(doc.get(domain));
                lto.setCreateDate(DateTools.stringToDate(doc.get(createDate)));
                result.add(lto);
            }
        } finally {
            reader.close();
        }
        searchResult.setList(result);
        return searchResult;
    }


    /**
     * @param luceneVO 增加 到 index
     * @return 转换到文档
     */
    @Override
    public Document toDocument(LuceneVO luceneVO) {
        Document document = new Document();
        try {
            document.add(new StringField(id, luceneVO.getId() + "", Field.Store.YES));
            document.add(new StringField(title, HtmlUtil.deleteHtml(luceneVO.getTitle()), Field.Store.YES));
            document.add(new TextField(content, HtmlUtil.deleteHtml(luceneVO.getContent()), Field.Store.YES));
            document.add(new StringField(other, HtmlUtil.deleteHtml(luceneVO.getOther()), Field.Store.YES));
            document.add(new StringField(nodeId, HtmlUtil.deleteHtml(luceneVO.getNodeId()), Field.Store.YES));
            document.add(new StringField(docType, HtmlUtil.deleteHtml(luceneVO.getDocType()), Field.Store.YES));
            document.add(new StringField(domain, HtmlUtil.deleteHtml(luceneVO.getDocType()), Field.Store.YES));
            document.add(new StringField(createDate, DateTools.dateToString(luceneVO.getCreateDate(), DateTools.Resolution.MILLISECOND), Field.Store.YES));
        } catch (Exception ex) {
            log.error(id, ex);
        }
        return document;
    }

    @Override
    public int delete(String id) throws Exception {
        Term term = new Term("id", "" + id);
        return delete(term);
    }

    /**
     * 删除文件
     *
     * @return 是否删除成功
     */
    @Override
    public boolean deleteFile() {
        return FileUtil.deleteDirectory(filePath);
    }

    @Override
    public int delete(String kayName, String value) throws Exception {
        Term term = new Term(kayName, value);
        return delete(term);
    }

    /**
     * 删除所有数据s
     */
    @Override
    public void deleteAll() {
        Directory directory = getDirectory();
        try {
            if (ArrayUtil.isEmpty(directory.listAll())) {
                return;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        IndexWriter indexwriter = getWriter();
        if (indexwriter != null) {
            try {
                indexwriter.deleteAll();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    indexwriter.close();  //记得关闭,否则删除不会被同步到索引文件中
                    directory.close(); //关闭目录
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}