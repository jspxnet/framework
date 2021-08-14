package com.github.jspxnet.utils;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/7/10 22:23
 * description: jspbox
 **/
public final class ThrowableUtil {
    private ThrowableUtil()
    {

    }
    /**
     * 查询得到异常信息
     * @param e 异常
     * @return 信息
     */
    public static String getThrowableMessage(Throwable e)
    {
        if (e==null)
        {
            return StringUtil.empty;
        }
        if (e instanceof  NullPointerException)
        {
            return "null 空异常";
        }
        String result = e.getMessage();
        if (StringUtil.isNull(result)&&e.getCause()!=null)
        {
            result = e.getCause().getMessage();
        }

        return result;
    }
}
