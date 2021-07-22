/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.bundle.provider;

import com.github.jspxnet.utils.*;
import com.github.jspxnet.txweb.bundle.table.BundleTable;
import com.github.jspxnet.txweb.bundle.BundleProvider;
import com.github.jspxnet.io.WriteFile;
import com.github.jspxnet.io.AbstractWrite;
import java.util.*;
import java.io.*;
import java.net.URL;


import lombok.extern.slf4j.Slf4j;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-6-24
 * Time: 15:37:33
 */
@Slf4j
public class XMLBundleProvider extends BundleProvider {
    //  private String exname = ".properties.xml";
    private File file = null;

    //表示路径
    public XMLBundleProvider() {

    }

    private File getFile() {
        if (file != null) {
            return file;
        }
        String name = namespace + "_" + dataType + ".properties.xml";
        URL url = ClassUtil.getResource(name);
        if (url == null) {
            url = ClassUtil.getResource("/" + name);
        }
        if (url == null) {
            url = ClassUtil.getResource("/resources/" + name);
        }
        if (url != null) {
            file = new File(url.getPath());
        }
        file = new File(name);
        return file;
    }

    @Override
    public boolean save(String key, String value) throws Exception {
        return save(key, value, 0);
    }


    /**
     * 保存
     *
     * @param bundletable 保存
     * @return boolean 保存成功
     */
    @Override
    public boolean save(BundleTable bundletable) {
        if (bundletable == null) {
            return false;
        }
        cache.put(bundletable.getIdx(), bundletable.getContext());
        try {
            AbstractWrite aw = new WriteFile();
            aw.setFile(file.getPath());
            aw.setEncode(encode);
            aw.setContent(toXml(), false);
        } catch (Exception e) {
            log.error(bundletable.getContext(), e);
        }
        return true;
    }


    /**
     * 得到绑定方式的列表
     *
     * @return 绑定数据
     */
    @Override
    public List<BundleTable> getList() {
        List<BundleTable> list = new ArrayList<BundleTable>();
        for (String key : cache.keySet()) {
            if (StringUtil.isNull(key)) {
                continue;
            }
            list.add(getBundleTable(key));
        }
        return list;
    }


    @Override
    public String getValue(String key) {
        for (Object o : getList()) {
            BundleTable bundleTable = (BundleTable) o;
            if (bundleTable.getIdx().equals(key)) {
                return bundleTable.getContext();
            }
        }
        return StringUtil.empty;
    }


    @Override
    public BundleTable getBundleTable(final String keys) {
        if (StringUtil.isNull(keys)) {
            return null;
        }
        BundleTable bundleTable = new BundleTable();
        bundleTable.setIdx(keys);
        bundleTable.setDataType(dataType);
        bundleTable.setNamespace(namespace);
        return bundleTable;
    }

    //boolean createForXML(String xmlString);
    //彻底删除数据
    @Override
    public boolean deleteAll() {
        File file = getFile();
        if (file.isFile() && file.exists() && file.delete()) {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    //载入数据
    @Override
    public void loadMap() {
        File file = getFile();
        if (file.isFile() && file.exists()) {
            try {
                ReadXMLProperties rxp = (ReadXMLProperties) XMLUtil.parseXmlFile(new ReadXMLProperties(), file);
                cache.putAll(rxp.getMap());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    class ReadXMLProperties extends DefaultHandler {
        private CharArrayWriter contents = new CharArrayWriter();
        private Map<String, String> map = new HashMap<String, String>();
        private String key = StringUtil.empty;

        public ReadXMLProperties() {
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes)
                throws SAXException {
            contents.reset();
            if ("value".equalsIgnoreCase(localName)) {
                key = attributes.getValue("key");
            }
        }

        @Override
        public void endElement(String namespaceURI,
                               String localName,
                               String qName) throws SAXException {
            if ("value".equalsIgnoreCase(localName)) {
                map.put(key, contents.toString());
            }
        }

        @Override
        public void characters(char[] ch, int start, int length)
                throws SAXException {

            contents.write(ch, start, length);
        }

        public Map<String, String> getMap() {
            return map;
        }
    }

    @Override
    public boolean remove(String key) {
        cache.remove(key);
        return true;
    }


    @Override
    public void flush() {
        cache.clear();
    }

}