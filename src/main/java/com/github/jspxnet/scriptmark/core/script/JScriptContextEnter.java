package com.github.jspxnet.scriptmark.core.script;

import com.github.jspxnet.utils.SystemUtil;
import org.mozilla.javascript.Context;

/**
 * Created by yuan on 2014/6/16 0016.
 * js 上下文
 */
public class JScriptContextEnter {

    static private Context context = null;

    static {
        if (SystemUtil.isAndroid()) {
            context = Context.enter();
        }
    }

    private JScriptContextEnter() {
    }

    static public Context getContext() {
        if (context != null) {
            return context;
        }
        return Context.enter();
    }

    static public void exit() {
        if (context != null) {
            return;
        }
        Context.exit();
    }

}
