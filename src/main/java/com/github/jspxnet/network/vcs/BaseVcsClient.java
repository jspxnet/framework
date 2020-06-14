package com.github.jspxnet.network.vcs;

import com.github.jspxnet.utils.StringUtil;


/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/2/11 0:25
 * description: Vcs git svn 统一调用
 **/
public abstract class BaseVcsClient implements VcsClient {
    private String localPath = System.getProperty("java.io.tmpdir");
    private String name = StringUtil.empty;//用户名
    private String password = StringUtil.empty;//密码
    private String url = StringUtil.empty;

    public String getLocalPath() {
        return localPath;
    }

    public void setLocalPath(String localPath) {
        this.localPath = localPath;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
