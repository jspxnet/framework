package com.github.jspxnet.network.vcs.git;

import com.github.jspxnet.network.vcs.BaseVcsClient;
import com.github.jspxnet.utils.DateUtil;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/2/10 23:36
 * description: jspxpro
 **/
public class GitAdapter extends BaseVcsClient   {

    /**
     * 上传数据
     * @return  版本
     * @throws Exception 异常
     */
    @Override
    public String upload() throws  Exception {
        CredentialsProvider credentialsProvider = GitUtil.getCredentialsProvider(getName(),getPassword());
        Git git = GitUtil.getGit(credentialsProvider,getUrl(),getLocalPath());
        GitUtil.upload(git,credentialsProvider,"测试提交"+ DateUtil.getDateST());
        Iterable<RevCommit> gitlog= git.log().call();
        for (RevCommit revCommit : gitlog) {
            return revCommit.getName();//版本号
        }
        return null;
    }


    /**
     * 更新本地
     * @return 版本
     * @throws Exception 异常
     */
    @Override
    public String download() throws Exception {
        CredentialsProvider credentialsProvider = GitUtil.getCredentialsProvider(getName(),getPassword());
        Git git = GitUtil.getGit(credentialsProvider,getUrl(),getLocalPath());
        GitUtil.download(git,credentialsProvider);
        Iterable<RevCommit> gitlog= git.log().call();
        for (RevCommit revCommit : gitlog) {
            return revCommit.getName();//版本号
        }
        return null;

    }

}
