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

import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.cache.JSCacheManager;
import com.github.jspxnet.enums.YesNoEnumType;
import com.github.jspxnet.security.symmetry.Encrypt;
import com.github.jspxnet.txweb.dao.impl.GenericDAOImpl;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.txweb.bundle.BundleProvider;
import com.github.jspxnet.txweb.bundle.table.BundleTable;
import com.github.jspxnet.sober.criteria.expression.Expression;
import com.github.jspxnet.sober.SoberFactory;
import com.github.jspxnet.sober.SoberSupport;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2005-6-24
 * Time: 14:27:16
 * com.github.jspxnet.txweb.bundle.provider.DBBundleProvider
 */
@Slf4j
public class DBBundleProvider extends BundleProvider {
    private final SoberSupport soberTemplate = new GenericDAOImpl();
    final private static String BUNDLE_MODEL = "bundle";
    final private static String LANGUAGE_MODEL = "language";




    public void setSoberFactory(SoberFactory soberFactory) {
        soberTemplate.setSoberFactory(soberFactory);
    }


    public DBBundleProvider() {

    }


    private String model = BUNDLE_MODEL;

    public void setModel(String model) {
        this.model = model;
    }

    /**
     * 得到绑定值
     *
     * @param keys 关键字
     * @return BundleTable  绑定对象
     */
    @Override
    public BundleTable getBundleTable(String keys) {
        if (StringUtil.isNull(keys)) {
            return null;
        }
        return soberTemplate.createCriteria(BundleTable.class)
                .add(Expression.eq("namespace", namespace))
                .add(Expression.eq("dataType", dataType))
                .add(Expression.eq("idx", keys))
                .objectUniqueResult(false);
    }

    @Override
    public boolean save(String key, String value) throws Exception {
        return super.save(key, value, 0);
    }


    /**
     * 保存
     *
     * @param bundletable 绑定对象
     * @return boolean  是否成功
     */
    @Override
    public boolean save(BundleTable bundletable) throws Exception {
        if (bundletable == null) {
            return false;
        }
        Encrypt symmetryEncrypt = EnvFactory.getSymmetryEncrypt();
        BundleTable editBundleTable = getBundleTable(bundletable.getIdx());
        if (editBundleTable != null) {
            editBundleTable.setIdx(bundletable.getIdx());
            editBundleTable.setContext(bundletable.getContext());
            if (editBundleTable.getEncrypt() == 1 && !StringUtil.isNull(editBundleTable.getContext())) {
                editBundleTable.setContext(symmetryEncrypt.getEncode(editBundleTable.getContext()));
                editBundleTable.setEncrypt(YesNoEnumType.YES.getValue());
            }
            try {
                return soberTemplate.update(editBundleTable, new String[]{"context", "encrypt"}) >= 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return soberTemplate.save(bundletable) > 0;
    }

    /**
     * @return 返回列表
     */
    @Override
    public List<BundleTable> getList()
    {
        List<BundleTable> bundleTableList = soberTemplate.createCriteria(BundleTable.class)
                .add(Expression.eq("namespace", namespace))
                .add(Expression.eq("dataType", dataType))
                .setCurrentPage(1)
                .setTotalCount(10000)
                .list(false);

        Encrypt symmetryEncrypt = EnvFactory.getSymmetryEncrypt();
        for (BundleTable bundleTable : bundleTableList) {
            try {
                if (bundleTable.getEncrypt() == YesNoEnumType.YES.getValue() && !StringUtil.isNull(bundleTable.getContext())) {
                    bundleTable.setContext(symmetryEncrypt.getDecode(bundleTable.getContext()));
                    bundleTable.setEncrypt(YesNoEnumType.NO.getValue());
                }
            } catch (Exception e) {
                log.error("bundleTable list", e);
            }
        }

        return bundleTableList;
    }

    /**
     * @return boolean 删除所有
     */
    @Override
    public boolean deleteAll() {
        if (soberTemplate.createCriteria(BundleTable.class)
                .add(Expression.eq("namespace", namespace))
                .add(Expression.eq("dataType", dataType))
                .delete(false) > 0) {
            soberTemplate.evict(BundleTable.class);
            return true;
        }
        return false;
    }


    @Override
    public String getValue(String key) {
        for (Object o : getList()) {
            BundleTable bundleTable = (BundleTable) o;
            if (bundleTable.getIdx().equals(key)) {
                return bundleTable.getContext();
            }
        }
        if (LANGUAGE_MODEL.equalsIgnoreCase(key)) {
            return key;
        }
        return StringUtil.empty;
    }

    /**
     * 载入
     */
    @Override
    public void loadMap() {

        for (Object o : getList()) {
            BundleTable bundleTable = (BundleTable) o;
            cache.put(bundleTable.getIdx(), bundleTable.getContext());
        }
    }

    /**
     * @param key key
     * @return 删除
     */
    @Override
    public boolean remove(String key) {
        if (soberTemplate.createCriteria(BundleTable.class)
                .add(Expression.eq("namespace", namespace))
                .add(Expression.eq("dataType", dataType))
                .add(Expression.eq("idx", key))
                .delete(false) > 0) {
            return true;
        }
        soberTemplate.evict(BundleTable.class);
        return false;
    }


    @Override
    public void flush() {
        String key = soberTemplate.createCriteria(BundleTable.class)
                .add(Expression.eq("namespace", namespace))
                .add(Expression.eq("dataType", dataType)).getDeleteListCacheKey();
        JSCacheManager.queryRemove(BundleTable.class, key);
        cache.clear();
    }
}