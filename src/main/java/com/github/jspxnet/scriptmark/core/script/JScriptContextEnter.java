package com.github.jspxnet.scriptmark.core.script;

import org.mozilla.javascript.Context;

/**
 * Created by yuan on 2014/6/16 0016.
 * js 上下文
 *  先去除安卓支持,如果需要支持,参考
 *  https://blog.csdn.net/aqiscu06240/article/details/101106293
 *  重新封装一下rhino包
 * */
public class JScriptContextEnter {
    private JScriptContextEnter() {

    }

    static public Context getContext() {
        return Context.enter();
    }

    static public void exit() {
        Context.exit();
    }
}
