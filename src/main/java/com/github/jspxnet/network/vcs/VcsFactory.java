package com.github.jspxnet.network.vcs;

import com.github.jspxnet.network.vcs.git.GitAdapter;
import com.github.jspxnet.network.vcs.svn.SvnAdapter;
import com.github.jspxnet.utils.StringUtil;


/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/2/11 0:12
 * description: jspxpro
 **/
public class VcsFactory {

    public static VcsClient createClient(String url,String localPath,String name,String password)
    {
        if (StringUtil.isNull(url))
        {
            return null;
        }
        BaseVcsClient vcsClient = null;
        if (url.contains("git"))
        {
            vcsClient = new GitAdapter();
        } else
        {
            vcsClient = new SvnAdapter();
        }
        vcsClient.setUrl(url);
        vcsClient.setLocalPath(localPath);
        vcsClient.setName(name);
        vcsClient.setPassword(password);
        return vcsClient;
    }

}
