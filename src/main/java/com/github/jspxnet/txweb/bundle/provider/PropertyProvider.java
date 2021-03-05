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

import com.github.jspxnet.txweb.bundle.table.BundleTable;
import com.github.jspxnet.txweb.bundle.BundleProvider;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.ClassUtil;
import com.github.jspxnet.io.AbstractWrite;
import com.github.jspxnet.io.WriteFile;
import java.util.List;
import java.util.ArrayList;
import java.io.*;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-6-30
 * Time: 16:23:41
 */
public class PropertyProvider extends BundleProvider {
    private static final Logger log = LoggerFactory.getLogger(PropertyProvider.class);

    // private String exname = ".properties";
    private File file = null;

    //表示路径
    public PropertyProvider() {

    }

    private File getFile() {
        if (file != null) {
            return file;
        }
        String name = namespace + "_" + dataType + ".properties";
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
        if (file==null)
        {
            file = new File(name);
        }
        return file;
    }

    @Override
    public boolean save(String key, String value) throws Exception {
        return save(key, value, 0);
    }

    /**
     * @param bundletable 绑定对象
     * @return boolean  保存成功
     */
    @Override
    public boolean save(BundleTable bundletable) {
        if (bundletable == null) {
            return false;
        }
        cache.put(bundletable.getIdx(), bundletable.getContext());
        try {
            save(file.getAbsolutePath());
        } catch (Exception e) {
            log.error(bundletable.getContext(), e);
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (String keys : cache.keySet()) {
            if (keys != null) {
                sb.append(keys).append("=").append(getString(keys)).append(StringUtil.CRLF);
            }
        }
        return sb.toString();
    }

    public void save(String file) {
        AbstractWrite aw = new WriteFile();
        aw.setFile(file);
        aw.setContent(toString(), false);
    }

    /**
     * @return 得到绑定方式的列表
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

    //彻底删除数据
    @Override
    public boolean deleteAll() {
        cache.clear();
        File file = getFile();
        return file.isFile() && file.exists() && file.delete();
    }


    //载入数据
    @Override
    public void loadMap() {
        File file = getFile();
        if (file.isFile() && file.exists()) {
            try {
                loadFile(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void loadFile(File fileName) {

        cache.clear();

        BufferedReader in = null;
        try {
            in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), getEncode()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                if (StringUtil.isNull(inputLine)) {
                    continue;
                }
                if (inputLine.startsWith("#")) {
                    continue;
                }
                String fen;
                if (inputLine.contains(":")) {
                    fen = ":";
                } else {
                    fen = "=";
                }
                if (!inputLine.contains(fen)) {
                    cache.put(inputLine, inputLine);
                } else {
                    String keys = StringUtil.substringBefore(inputLine, fen);
                    cache.put(keys, inputLine.substring(keys.length() + 1));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public boolean remove(String key) {
        cache.remove(key);
        save(file.getAbsolutePath());
        return true;
    }


    @Override
    public void flush() {
        cache.clear();
    }
}