/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
 * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.turnpage.impl;

import com.github.jspxnet.txweb.annotation.Param;
import com.github.jspxnet.txweb.turnpage.TurnPageButton;
import com.github.jspxnet.boot.environment.Placeholder;
import com.github.jspxnet.boot.environment.EnvironmentTemplate;
import com.github.jspxnet.boot.environment.Environment;
import com.github.jspxnet.boot.EnvFactory;
import com.github.jspxnet.utils.StringUtil;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

/**
 * Created by IntelliJ IDEA.
 *
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-6-13
 * Time: 16:33:00
 * com.github.jspxnet.txweb.turnpage.impl.TurnPageTemplate
 */
public class TurnPageButtonImpl implements TurnPageButton {
    //行数
    protected int defaultCount = 0;
    //页数
    protected int currentPage = 1;
    //总行数
    protected int totalCount = 1;
    //总页数
    protected int totalPage = 1;
    //页面参数
    protected String querystring = StringUtil.empty;
    //连接地址
    protected String pageLink = StringUtil.empty;
    //一页显示行数
    protected int count = 0;
    //列表范围
    protected int bound = 4;
    //更目录,模板只会在这里边
    private String rootDirectory;
    //模板路径
    private String currentPath;
    //模板文件名称
    protected String fileName;
    //默认编码
    private String encode = Environment.defaultEncode;


    @Override
    public int getBound() {
        return bound;
    }

    @Override
    public void setBound(int bound) {
        this.bound = bound;
    }

    @Override
    public void setDefaultCount(int defaultCount) {
        this.defaultCount = defaultCount;
        if (count <= 0) {
            count = this.defaultCount;
        }
    }

    @Override
    public int getDefaultCount() {
        return defaultCount;
    }


    @Override
    public void setCount(int count) {
        if (count > 0) {
            this.count = count;
        } else {
            this.count = defaultCount;
        }
    }

    @Override
    public int getCount() {
        if (count < 1) {
            count = defaultCount;
        }
        if (count <= 0) {
            count = 0;
        }
        return count;
    }

    @Override
    public long getFristRow() {
        long temp = currentPage * count - count;
        if (temp < 0) {
            temp = 1;
        }
        return temp;
    }

    @Override
    @Param(caption = "页数", min = 1)
    public void setCurrentPage(int currentPage) {
        if (currentPage > 0) {
            this.currentPage = currentPage;
        }
    }

    @Override
    public int getCurrentPage() {
        return currentPage;
    }

    @Override
    public void setTotalCount(int totalCount) {
        if (totalCount >= 0) {
            this.totalCount = totalCount;
        }
    }

    @Override
    public long getTotalCount() {
        return totalCount;
    }

    @Override
    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
    }

    @Override
    public int getTotalPage() {
        int ic = getCount();
        if (ic <= 0) {
            return 1;
        }
        totalPage = totalCount / ic;
        if (totalCount % ic > 0) {
            totalPage = totalPage + 1;
        }
        return totalPage;
    }

    @Override
    public void setQuerystring(String querystring) {
        this.querystring = querystring;
    }

    @Override
    public String getQuerystring() {
        if (querystring == null) {
            return StringUtil.empty;
        }
        return querystring;
    }

    @Override
    public void setPageLink(String pageLink) {
        this.pageLink = pageLink;
    }

    @Override
    public String getPageLink() {
        return pageLink;
    }


    @Override
    public String getCurrentPath() {
        return currentPath;
    }

    @Override
    public void setCurrentPath(String currentPath) {
        this.currentPath = currentPath;
    }

    @Override
    public String getEncode() {
        return encode;
    }

    @Override
    public void setEncode(String encode) {
        this.encode = encode;
    }

    @Override
    public String getFileName() {
        return fileName;
    }

    @Override
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String getRootDirectory() {
        return rootDirectory;
    }

    @Override
    public void setRootDirectory(String rootDirectory) {
        this.rootDirectory = rootDirectory;
    }

    @Override
    public String getTurnPage() {
        long currentPage = getCurrentPage();
        long beginPage = currentPage - bound;
        if (beginPage <= 1) {
            beginPage = 2;
        }
        long maxPage = currentPage + bound;
        if (maxPage - beginPage - 1 < bound * 2) {
            maxPage = maxPage + bound * 2 - Math.abs(maxPage - beginPage);
        }
        if (maxPage > getTotalPage()) {
            maxPage = getTotalPage();
        }
        if (maxPage < 1) {
            maxPage = 1;
        }
        if (beginPage > maxPage) {
            beginPage = maxPage;
        }


        Map<String, Object> turnPageMap = new HashMap<>();
        turnPageMap.put("defaultCount", getDefaultCount());
        turnPageMap.put("count", getCount());
        turnPageMap.put("fristRow", getFristRow());
        turnPageMap.put("currentPage", currentPage);
        turnPageMap.put("totalCount", getTotalCount());
        turnPageMap.put("totalPage", getTotalPage());
        turnPageMap.put("querystring", getQuerystring());
        turnPageMap.put("pageLink", getPageLink());
        turnPageMap.put("beginPage", beginPage);
        turnPageMap.put("endPage", maxPage);

        File file = new File(currentPath, fileName);
        if (!file.exists() && !file.isFile()) {
            EnvironmentTemplate envTemplate = EnvFactory.getEnvironmentTemplate();
            String templatePath = envTemplate.getString(Environment.templatePath);
            file = new File(templatePath, fileName);
        }
        if (!file.isFile()) {
            file = EnvFactory.getFile(fileName);
        }
        Placeholder placeholder = EnvFactory.getPlaceholder();
        placeholder.setRootDirectory(rootDirectory);
        placeholder.setCurrentPath(currentPath);
        return placeholder.processTemplate(turnPageMap, file, encode);
    }
}