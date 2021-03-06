package com.github.jspxnet.network.vcs.svn;

import lombok.extern.slf4j.Slf4j;
import org.tmatesoft.svn.core.*;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.internal.wc.DefaultSVNOptions;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.*;
import java.io.File;

/**
 * Created by jspx.net
 *
  * author: chenYuan
 * date: 2020/2/9 23:36
 * description: svn 调用单元
 **/
@Slf4j
public class SvnUtil {

    private SvnUtil()
    {

    }
    /**
     * 通过不同的协议初始化版本库
     */
    public static void setupLibrary() {
        DAVRepositoryFactory.setup();
        SVNRepositoryFactoryImpl.setup();
        FSRepositoryFactory.setup();
    }


    /**
     * 验证登录svn
     *
     * @param svnRoot  本地根目录
     * @param username 用户名
     * @param password 密码
     * @return 认证信息
     */
    public static SVNClientManager authSvn(String svnRoot, String username, String password) {
        // 初始化版本库
        setupLibrary();

        // 创建库连接
        SVNRepository repository;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(svnRoot));
        } catch (SVNException e) {
            log.error("svn", e);
            return null;
        }
        // 身份验证
        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password.toCharArray());
        // 创建身份验证管理器
        repository.setAuthenticationManager(authManager);

        DefaultSVNOptions options = SVNWCUtil.createDefaultOptions(true);
        return SVNClientManager.newInstance(options, authManager);
    }

    /**
     * Make directory in svn repository
     *
     * @param clientManager 认证
     * @param url           eg: http://svn.ambow.com/wlpt/bsp/trunk
     * @param commitMessage 说明
     * @return 信息
     */
    public static SVNCommitInfo makeDirectory(SVNClientManager clientManager,
                                              SVNURL url, String commitMessage) {
        try {
            return clientManager.getCommitClient().doMkDir(new SVNURL[]{url}, commitMessage);
        } catch (SVNException e) {
            log.error("svn 创建目录", e);
        }
        return null;
    }

    /**
     * Imports an unversioned directory into a repository location denoted by a
     * destination URL
     *
     * @param clientManager 认证
     * @param localPath     a local unversioned directory or singal file that will be imported into a
     *                      repository;
     * @param svnUrl        a repository location where the local unversioned directory/file will be
     *                      imported into
     * @param commitMessage 秒杀
     * @param isRecursive   递归
     * @return 提交信息
     * @throws SVNException 异常
     */
    public static SVNCommitInfo importDirectory(SVNClientManager clientManager,
                                                File localPath, SVNURL svnUrl, String commitMessage,
                                                boolean isRecursive) throws SVNException {
        return clientManager.getCommitClient().doImport(localPath, svnUrl,
                commitMessage, null, true, true,
                SVNDepth.fromRecurse(isRecursive));

    }

    /**
     * Puts directories and files under version control
     *
     * @param clientManager SVNClientManager
     * @param wcPath        work copy path
     * @throws SVNException 异常
     */
    public static void addEntry(SVNClientManager clientManager, File wcPath) throws SVNException {
        clientManager.getWCClient().doAdd(new File[]{wcPath}, true,
                false, false, SVNDepth.INFINITY, false, false,
                true);
    }

    /**
     * Collects status information on a single Working Copy item
     *
     * @param clientManager 认证
     * @param wcPath        local item's path
     * @param remote        true to check up the status of the item in the repository,
     *                      that will tell if the local item is out-of-date (like '-u' option in the SVN client's
     *                      'svn status' command), otherwise false
     * @return 状态
     * @throws SVNException 异常信息
     */
    public static SVNStatus showStatus(SVNClientManager clientManager,
                                       File wcPath, boolean remote) throws SVNException {

        return clientManager.getStatusClient().doStatus(wcPath, remote);
    }

    /**
     * Commit work copy's change to svn
     *
     * @param clientManager 认证
     * @param wcPath        working copy paths which changes are to be committed
     * @param keepLocks     whether to unlock or not files in the repository
     * @param commitMessage commit log message
     * @return 提交信息
     * @throws SVNException 异常信息
     */
    public static SVNCommitInfo commit(SVNClientManager clientManager,
                                       File wcPath, boolean keepLocks, String commitMessage) throws SVNException {
        return clientManager.getCommitClient().doCommit(
                new File[]{wcPath}, keepLocks, commitMessage, null,
                null, false, false, SVNDepth.INFINITY);
    }

    /**
     * Updates a working copy (brings changes from the repository into the working copy).
     * 是从SVN服务器上把最新版本下载到本地来
     * 第一次是checkout
     * 以后才是update
     *
     * @param clientManager    认证信息
     * @param wcPath           working copy path
     * @param updateToRevision revision to update to
     * @param depth            update的深度：目录、子目录、文件
     * @return 版本
     * @throws SVNException 异常
     */
    public static long update(SVNClientManager clientManager, File wcPath,
                              SVNRevision updateToRevision, SVNDepth depth) throws SVNException {

        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        /*
         * sets externals not to be ignored during the update
         */
        updateClient.setIgnoreExternals(false);
        /*
         * returns the number of the revision wcPath was updated to
         */
        return updateClient.doUpdate(wcPath, updateToRevision, depth, false, false);

    }

    /**
     * recursively checks out a working copy from url into wcDir
     *
     * @param clientManager 认证信息
     * @param url           a repository location from where a Working Copy will be checked out
     * @param revision      the desired revision of the Working Copy to be checked out
     * @param destPath      the local path where the Working Copy will be placed
     * @param depth         checkout的深度，目录、子目录、文件
     * @return 执行check out操作，返回工作副本的版本号。
     * @throws SVNException 异常
     */
    public static long checkout(SVNClientManager clientManager, SVNURL url,
                                SVNRevision revision, File destPath, SVNDepth depth) throws SVNException {

        SVNUpdateClient updateClient = clientManager.getUpdateClient();
        /*
         * sets externals not to be ignored during the checkout
         */
        updateClient.setIgnoreExternals(false);
        /*
         * returns the number of the revision at which the working copy is
         */
        return updateClient.doCheckout(url, destPath, revision, revision, depth, false);

    }

    /**
     * 确定path是否是一个工作空间
     *
     * @param path 路径
     * @return 确定path是否是一个工作空间
     * @throws SVNException 异常
     */
    public static File isWorkingCopy(File path) throws SVNException {
        if (!path.exists()) {

            return null;
        }
        return SVNWCUtil.getWorkingCopyRoot(path, false);
    }


    /**
     * 确定一个URL在SVN上是否存在
     * @param url url地址
     * @param username 用户名
     * @param password 密码
     * @return  确定一个URL在SVN上是否存在
     */
    public static boolean isUrlExist(SVNURL url, String username, char[] password) {
        try {
            SVNRepository svnRepository = SVNRepositoryFactory.create(url);
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            svnRepository.setAuthenticationManager(authManager);
            SVNNodeKind nodeKind = svnRepository.checkPath("", -1);
            return nodeKind != SVNNodeKind.NONE;
        } catch (SVNException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * @param url      url
     * @param username 用户名
     * @param password 密码
     * @return 得到仓库
     */
    public static SVNRepository getRepository(String url, String username, char[] password) {
        SVNRepository repository ;
        SVNNodeKind nodeKind;
        try {
            repository = SVNRepositoryFactory.create(SVNURL.parseURIEncoded(url));
            ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);
            repository.setAuthenticationManager(authManager);
            nodeKind = repository.checkPath("", -1);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if (nodeKind == SVNNodeKind.NONE) {
            throw new RuntimeException("There is no entry at '" + url + "'.");
        } else if (nodeKind == SVNNodeKind.FILE) {
            throw new RuntimeException("The entry at '" + url + "' is a file while a directory was expected.");
        }
        return repository;
    }


    /**
     * @param clientManager 认证信息
     * @param localPath     本地目录
     * @param commitMessage 提交描述
     * @return 提交后的信息
     * @throws SVNException 异常
     */
    public static SVNCommitInfo upload(SVNClientManager clientManager, File localPath, String commitMessage) throws SVNException {


        clientManager.getWCClient().doRevert(new File[]{localPath},SVNDepth.fromRecurse(true),null);

        //如果此文件是新增加的则先把此文件添加到版本库，然后提交。
        clientManager.getWCClient().doAdd(new File[]{localPath}, true,
                true, false, SVNDepth.INFINITY, false, false, true);
        //如果此文件不是新增加的，直接提交。
        return clientManager.getCommitClient().doCommit(
                new File[]{localPath}, false, commitMessage, null,
                null, false, true, SVNDepth.INFINITY);
    }


    /**
     *
     * @param clientManager 认证信息
     * @param svnUrl 远程目录
     * @param localPath 本地目录
     * @return 更新的版本
     * @throws SVNException 异常
     */

    public static long download(SVNClientManager clientManager, String svnUrl, File localPath) throws SVNException
    {
        return download( clientManager,  svnUrl,  localPath,SVNRevision.HEAD);
    }
    /**
     *
     * @param clientManager 认证信息
     * @param svnUrl 远程目录
     * @param localPath 本地目录
     * @param revision 版本
     * @return 更新的版本
     * @throws SVNException 异常
     */
    public static long download(SVNClientManager clientManager, String svnUrl, File localPath,SVNRevision revision) throws SVNException
    {
        if (revision==null)
        {
            revision = SVNRevision.HEAD;
        }
        File file = new File(localPath,".svn");
        if (file.exists() || file.exists()&&file.getPath().contains(".svn"))
        {
            //要把版本库的内容check out到的目录
            return SvnUtil.update(clientManager, localPath, revision, SVNDepth.INFINITY);
        }
        else
        {
            SVNURL repositoryUrl = SVNURL.parseURIEncoded(svnUrl);
            return checkout(clientManager, repositoryUrl, revision, localPath, SVNDepth.INFINITY);
        }
    }


}