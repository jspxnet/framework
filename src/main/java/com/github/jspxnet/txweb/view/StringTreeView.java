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

import com.github.jspxnet.txweb.annotation.Operate;
import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.util.StringMap;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.StringUtil;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 11-3-12
 * Time: 下午1:52
 * 分类为两级，第三级为关键字备选
 * StringTreeView sView = new StringTreeView();
 * sView.setFileName("D:\\website\\www\\root\\WEB-INF\\分类.txt");
 * System.out.println(ArrayUtil.toString(sView.getRootList(),"|"));
 * System.out.println(ArrayUtil.toString(sView.getList("礼品、工艺品"),"|"));
 * System.out.println(ArrayUtil.toString(sView.getTagList("航空、航天"),"|"));
 * System.out.println("path=" + sView.getPath("客机"));
 * 文本的格式为
 * 通讯=交换机|电话机|移动电话|网络通信产品|通信配件|其他通讯设备
 * 交通运输=电梯、缆车|运输、仓储|集装箱|航空、航天|其他交通运输
 * [航空、航天]=客机|作业飞机|太空探测
 */
@HttpMethod(caption = "文本树")
public class StringTreeView extends ActionSupport {
    public StringTreeView() {

    }

    private final StringMap<String, String> map = new StringMap<>();

    public String getFileName() {
        return map.getFileName();
    }

    @Param(caption = "文件名")
    public void setFileName(String fileName) throws Exception {
        map.setKeySplit(StringUtil.EQUAL);
        map.setLineSplit(StringUtil.CRLF);
        map.loadFile(fileName);
    }

    @Param(caption = "字符串", max = 10000)
    public void setString(String text) {
        map.setKeySplit(StringUtil.EQUAL);
        map.setLineSplit(StringUtil.CRLF);
        map.setString(text);
    }

    /**
     * @return 得到根列表
     */
    @Operate(caption = "根列表")
    public String[] getRootList() {
        String[] result = null;
        for (String temp : map.keySet()) {
            if (temp != null && !temp.startsWith("[")) {
                result = ArrayUtil.add(result, temp);
            }
        }
        return result;
    }

    /**
     * @param name 路径名称
     * @return 下级得到列表
     */
    @Operate(caption = "下级列表", post = false)
    public String[] getList(@Param(caption = "路径名称") String name) {
        String temp = map.get(name);
        if (temp != null) {
            return StringUtil.split(StringUtil.replace(temp, "|", StringUtil.SEMICOLON));
        }
        return new String[0];
    }

    /**
     * @param name 路径名称
     * @return 得到Tag 列表
     */
    @Operate(caption = "路径列表", post = false)
    public String[] getTagList(@Param(caption = "路径名称") String name) {
        if (name != null && !name.startsWith("[")) {
            name = "[" + name + "]";
        }
        String temp = map.get(name);
        if (temp != null) {
            return StringUtil.split(StringUtil.replace(temp, "|", StringUtil.SEMICOLON));
        }
        return new String[0];
    }

    /**
     * @param name 路径名称
     * @return 目的是为了避免死循环
     */
    @Operate(caption = "路径名称", post = false)
    public String getPath(@Param(caption = "路径名称") String name) {
        for (String keys : map.keySet()) {
            String temp = map.get(keys);
            if (temp == null) {
                continue;
            }
            if (temp.contains(name)) {
                if (keys.startsWith("[") && keys.endsWith("]")) {
                    String tName = StringUtil.substringBetween(keys, "[", "]");
                    return getRoot(tName) + "|" + tName;
                }
                return keys;
            }
            //if (temp.contains(name))   return keys;
        }
        return StringUtil.empty;
    }

    /**
     * @param name 栏目名称
     * @return 目的是为了避免死循环
     */
    @Operate(caption = "栏目名称", post = false)
    private String getRoot(@Param(caption = "栏目名称") String name) {
        for (String keys : map.keySet()) {
            String temp = map.get(keys);
            if (temp == null) {
                continue;
            }
            if (temp.contains(name)) {
                return keys;
            }
        }
        return StringUtil.empty;
    }
}