/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.view;

import com.github.jspxnet.txweb.annotation.HttpMethod;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.util.MutableLong;
import com.github.jspxnet.util.StringMap;

import com.github.jspxnet.utils.DateUtil;
import com.github.jspxnet.utils.StringUtil;
import com.github.jspxnet.utils.NumberUtil;
import com.github.jspxnet.txweb.support.ActionSupport;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2006-12-16
 * Time: 15:14:15
 * 简单的计数器
 */
@HttpMethod(caption = "文本计数器")
public class TextCountersView extends ActionSupport {
    private int id = 0;

    public int getId() {
        return id;
    }

    @Param(request = false)
    public void setId(int id) {
        this.id = id;
    }

    private String file;

    public TextCountersView() {

    }


    /**
     * @param file 文件路径
     */
    @Param(caption = "文件路径", request = false)
    public void setFileName(String file) {
        this.file = file;
    }

    public String getFileName() {
        return file;
    }

    public long add() throws Exception {
        // 利用 HashMap 的put方法弹出旧值的特性
        MutableLong initValue = new MutableLong(1);
        MutableLong oldValue = SingletonCounter.getInstance().getCounter().put(id, initValue);
        if (oldValue != null) {
            initValue.set(oldValue.get() - 1);
            initValue.setLastTimeMillis(oldValue.getLastTimeMillis());
            if (initValue.get() > 3 & System.currentTimeMillis() - oldValue.getLastTimeMillis() > DateUtil.SECOND * 5) {
                save();
                initValue.setLastTimeMillis(System.currentTimeMillis());
            }
        } else {
            //没有数据
            initValue.set(get() + 1);
        }
        return initValue.get();
    }

    public long desc() throws Exception {
        MutableLong initValue = new MutableLong(1);
        MutableLong oldValue = SingletonCounter.getInstance().getCounter().put(id, initValue);
        if (oldValue != null) {
            initValue.set(oldValue.get() - 1);
            initValue.setLastTimeMillis(oldValue.getLastTimeMillis());
            if (System.currentTimeMillis() - oldValue.getLastTimeMillis() > DateUtil.SECOND * 5) {
                save();
                initValue.setLastTimeMillis(System.currentTimeMillis());
            }
        }
        return initValue.get();
    }

    private long get() throws Exception {
        StringMap saveMap = new StringMap();
        saveMap.setLineSplit(StringUtil.CRLF);
        saveMap.setKeySplit(StringUtil.EQUAL);
        if (StringUtil.isNull(file)) {
            file = getEnv(ActionEnv.Key_RealPath) + "count.txt";
        }
        saveMap.loadFile(file);
        return saveMap.getInt(NumberUtil.toString(id), 0);
    }

    private void save() {
        Map<Integer, MutableLong> map = TextCounter.getInstance().getCounter();
        StringMap saveMap = new StringMap();
        saveMap.setLineSplit(StringUtil.CRLF);
        saveMap.setKeySplit(StringUtil.EQUAL);
        for (int key : map.keySet()) {
            saveMap.put(NumberUtil.toString(key), map.get(key).toString());

        }
        if (StringUtil.isNull(file)) {
            file = getEnv(ActionEnv.Key_RealPath) + "count.txt";
        }
        saveMap.save(file);
    }
}

class TextCounter {
    private Map<Integer, MutableLong> counter = new ConcurrentHashMap<Integer, MutableLong>();
    private static TextCounter instance = new TextCounter();

    private TextCounter() {
    }

    public static synchronized TextCounter getInstance() {
        return instance;
    }

    Map<Integer, MutableLong> getCounter() {
        return counter;
    }
}