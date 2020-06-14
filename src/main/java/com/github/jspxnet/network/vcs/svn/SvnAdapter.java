package com.github.jspxnet.network.vcs.svn;

import com.github.jspxnet.network.vcs.BaseVcsClient;
import com.github.jspxnet.utils.DateUtil;
import org.tmatesoft.svn.core.SVNCommitInfo;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNRevision;
import java.io.File;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/2/10 23:40
 * description: jspxpro
 **/
public class SvnAdapter extends BaseVcsClient  {

    /**
     * @return 版本信息
     * @throws Exception 异常
     */
    @Override
    public String upload() throws Exception {

        //实例化客户端管理类
        SVNClientManager clientManager =  SvnUtil.authSvn(getUrl(),getName(),getPassword());
        if (clientManager==null)
        {
            return null;
        }
        //要把版本库的内容check out到的目录
        SVNCommitInfo commitInfo = SvnUtil.upload(clientManager,  new File(getLocalPath()), "test"+ DateUtil.getDateST());
        return String.valueOf(commitInfo.getNewRevision());
    }

    /**
     *
     * @return 版本信息
     * @throws Exception 异常
     */
    @Override
    public String download() throws Exception
    {
        SVNClientManager clientManager =  SvnUtil.authSvn(getUrl(),getName(),getPassword());
        //要把版本库的内容check out到的目录
        if (clientManager==null)
        {
            return null;
        }
        return String.valueOf(SvnUtil.download(clientManager,getUrl(), new File(getLocalPath()), SVNRevision.HEAD));
    }
}
