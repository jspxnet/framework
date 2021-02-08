package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.env.TXWeb;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.lang.reflect.Method;

/**
 * 目录拦截器，多企业模式
 */
@Slf4j
public class CompanyInterceptor extends InterceptorSupport {
    //自动模式,将会 从1-3都来一次，一直到有数据
    public static final int AUTO_MODE = 0;
    //session中得到
    public static final int SESSION_MODE = 1;
    //二级域名方式
    public static final int SUBDOMAIN_MODE = 2;
    //目录窃取方式
    public static final int URL_MODE = 3;

    //当前使用那种模式获取会员ID
    private int mode = 0;

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private int pathLevel = 2;

    public void setPathLevel(int pathLevel) {
        this.pathLevel = pathLevel;
    }

    /**
     * 需要拦截的目录
     */
    private String[] folders = new String[]{"company"};

    public String[] getFolders() {
        return folders;
    }

    public void setFolders(String[] folders) {
        this.folders = folders;
    }

    /**
     * 支持多种方式, 1.session 中取， 2.organizeId.domin.com (二级域名方式),和目录窃取方式
     *
     * @param action action
     * @return 支持多种方式
     */
    private String getOrganizeId(ActionSupport action) {
        HttpSession httpSession = action.getSession();
        String organizeId = null;
        HttpServletRequest request = action.getRequest();
        switch (mode) {
            default: {
                if (httpSession != null && httpSession.getAttribute(TXWeb.organizeId) != null) {
                    organizeId = ObjectUtil.toString(httpSession.getAttribute(TXWeb.organizeId));
                }
                if (request!=null&&StringUtil.isEmpty(organizeId)) {
                    String hostName = URLUtil.getHostName(action.getRequest().getRequestURL().toString());
                    if (!IpUtil.isIpv4(hostName) && !IpUtil.isIpv6(hostName)) {
                        String subdomain = URLUtil.getSubdomainPrefix(action.getRequest().getRequestURL().toString());
                        if (!StringUtil.isNull(subdomain) && subdomain.length() > 30) {
                            organizeId = subdomain;
                        }
                    }
                }
               /*
               后边根据需求在配置
               if (StringUtil.isEmpty(organizeId))
                    organizeId = action.getPathLevel(pathLevel);*/
                break;
            }
            case SESSION_MODE: {
                if (httpSession != null && httpSession.getAttribute(TXWeb.organizeId) != null) {
                    organizeId = ObjectUtil.toString(httpSession.getAttribute(TXWeb.organizeId));
                }
                break;
            }
            case SUBDOMAIN_MODE: {
                String hostName = URLUtil.getHostName(action.getRequest().getRequestURL().toString());
                if (!IpUtil.isIpv4(hostName) && !IpUtil.isIpv6(hostName)) {
                    String subdomain = URLUtil.getSubdomainPrefix(action.getRequest().getRequestURL().toString());
                    if (!StringUtil.isNull(subdomain) && subdomain.length() > 30) {
                        organizeId = subdomain;
                    }
                }
                break;
            }
            case URL_MODE: {
                return action.getPathLevel(pathLevel);
            }
        }
        return organizeId;
    }

    /**
     * organizeId,接收采用
     * 支持多种方式, 1.session 中取， 2.organizeId.domin.com (二级域名方式),和目录窃取方式
     *
     * @param actionInvocation action上下文
     * @return 是否成功
     */
    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {

        //配置为空，不拦截任何目录
        if (ArrayUtil.isEmpty(folders)) {
            return actionInvocation.invoke();
        }
        ActionSupport action = actionInvocation.getActionProxy().getAction();
        String organizeId = getOrganizeId(action);
       /*
        if (organizeId <= 0) {
            return ActionEnv.LOGIN;
        }
        */
        String namespace = action.getEnv(ActionEnv.Key_Namespace);
        for (String folder : folders) {
            if (folder.equalsIgnoreCase(namespace)) {
                //提供给日志记录使用
                action.put(ActionEnv.KEY_organizeId, organizeId);
                Method method = ClassUtil.getSetMethod(action.getClass(), "setOrganizeId");
                if (method != null) {
                    BeanUtil.setSimpleProperty(action, method.getName(), organizeId);
                }
                //执行下一个动作,可能是下一个拦截器,也可能是action取决你的配置
            }
        }
        return actionInvocation.invoke();
    }
}
