/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.txweb.turnpage;

/**
 * Created by IntelliJ IDEA.
 * @author chenYuan (mail:39793751@qq.com)
 * date: 2007-6-13
 * Time: 16:32:05
 */
public interface TurnPageButton {
    void setCount(int count);

    int getCount();

    long getFirstRow();

    void setCurrentPage(int currentPage);

    int getCurrentPage();

    int getDefaultCount();

    void setDefaultCount(int defaultCount);

    void setTotalCount(int totalCount);

    void setTotalCount(long totalCount);

    long getTotalCount();

    void setQuerystring(String querystring);

    String getQuerystring();

    void setPageLink(String pageLink);

    String getPageLink();

    void setTotalPage(int totalPage);

    int getTotalPage();


    void setCurrentPath(String currentPath);

    String getCurrentPath();

    int getBound();

    void setBound(int bound);

    String getTurnPage() throws Exception;

    void setEncode(String encode);

    String getEncode();

    void setFileName(String fileName);

    String getFileName();

    void setRootDirectory(String rootDirectory);

    String getRootDirectory();

}