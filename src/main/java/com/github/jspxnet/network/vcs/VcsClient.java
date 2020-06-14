package com.github.jspxnet.network.vcs;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/2/11 0:12
 * description: jspxpro
 **/
public interface VcsClient {

    String upload() throws Exception;
    String download() throws Exception;

}
