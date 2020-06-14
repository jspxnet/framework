package com.github.jspxnet.network.vcs.git;

import com.github.jspxnet.utils.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.jgit.api.CloneCommand;
import org.eclipse.jgit.api.Git;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/2/9 23:38
 * description: jspxframework
 **/
import java.io.File;
import java.io.IOException;
import org.eclipse.jgit.api.PullResult;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.dircache.DirCache;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.*;

@Slf4j
public class GitUtil {


    private GitUtil() {
    }

    public static void createLocalPath(String localPath) throws IOException {
        //本地新建仓库地址
        Repository newRepo = FileRepositoryBuilder.create(new File(localPath + "/.git"));
        newRepo.create();
    }

    public static Git getGit(CredentialsProvider credentialsProvider,String uri,  String localDir) throws Exception {
        Git git = null;
        if (new File(localDir).exists() ) {
            git = Git.open(new File(localDir));
        } else {
            git = Git.cloneRepository().setCredentialsProvider(credentialsProvider).setURI(uri)
                    .setDirectory(new File(localDir)).call();
        }
        //设置一下post内存，否则可能会报错Error writing request body to server
        git.getRepository().getConfig().setInt(HttpConfig.HTTP, null, HttpConfig.POST_BUFFER_KEY, 512*1024*1024);
        return git;
    }

    public static CredentialsProvider getCredentialsProvider(String username, String password) {
        return new UsernamePasswordCredentialsProvider(username, password);
    }

    public static Repository getRepository(Git git) {
        return git.getRepository();
    }

    /**
     * 克隆到本地
     * @param credentialsProvider 认证信息
     * @param url url
     * @param localPath 本地路径
     * @return Git
     * @throws Exception 异常
     */
    public static Git clone(CredentialsProvider credentialsProvider,String url,String localPath)
            throws Exception {
        CloneCommand cloneCommand = Git.cloneRepository();
        return cloneCommand.setURI(url) //设置远程URI
                .setBranch("master") //设置clone下来的分支
                .setDirectory(new File(localPath)) //设置下载存放路径
                .setCredentialsProvider(credentialsProvider) //设置权限验证
                .call();
    }

    public static PullResult pull(Git git, CredentialsProvider credentialsProvider) throws Exception {
        return git.pull().setRemote("origin").setCredentialsProvider(credentialsProvider).call();
    }

    public static void push(Git git, CredentialsProvider credentialsProvider, String filepattern, String message)
            throws Exception {

        git.add().addFilepattern(filepattern).call();
        git.add().setUpdate(true);
        git.commit().setMessage(message).call();
        git.push().setCredentialsProvider(credentialsProvider).call();

    }

    /**
     *
     * @param git git对象
     * @param credentialsProvider 认证
     * @return 下载后的结果
     * @throws Exception 异常
     */
    public static PullResult download(Git git, CredentialsProvider credentialsProvider) throws Exception {
        //git fetch --all && git reset --hard origin/master && git pull
        git.fetch().setForceUpdate(true).setRemote("origin").call();
        git.reset().setMode(ResetCommand.ResetType.HARD).call();
        return git.pull().setRebase(true).setRemote("origin").setCredentialsProvider(credentialsProvider).call();
    }

    /**
     *
     * @param git git对象
     * @param credentialsProvider 认证
     * @param message 提交描述
     * @return 推送列表
     * @throws Exception 异常
     */
    public static Iterable<PushResult> upload(Git git, CredentialsProvider credentialsProvider, String message) throws Exception
    {
        //添加文件
        DirCache dirCache = git.add().addFilepattern(".").call();
        if (dirCache.lock())
        {
            dirCache.unlock();
        }
        git.commit().setMessage(message).call();
        return git.push().setRemote("origin").setCredentialsProvider(credentialsProvider).call();
    }
/*

    public static void main(String[] args) throws Exception {
        String uri = "http://127.0.0.1:3000/XXX/git_test.git";
        String username = "XXX";
        String password = "123456";
        CredentialsProvider credentialsProvider = getCredentialsProvider(username, password);
        String localDir = "D:/tmp/git_test";
        Git git = getGit(credentialsProvider,uri, localDir);
        pull(git, credentialsProvider);
        push(git, credentialsProvider, ".", "提交文件");

    }
*/

}