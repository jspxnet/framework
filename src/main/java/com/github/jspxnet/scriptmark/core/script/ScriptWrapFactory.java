/*
 * Copyright © 2004-2014 chenYuan. All rights reserved.
 * @Website:wwww.jspx.net
 * @Mail:39793751@qq.com
  * author: chenYuan , 陈原
 * @License: Jspx.net Framework Code is open source (LGPL)，Jspx.net Framework 使用LGPL 开源授权协议发布。
 * @jvm:jdk1.6+  x86/amd64
 *
 */
package com.github.jspxnet.scriptmark.core.script;

import com.github.jspxnet.sober.model.container.PropertyContainer;
import com.github.jspxnet.utils.ObjectUtil;
import com.github.jspxnet.utils.ReflectUtil;
import org.mozilla.javascript.*;
import java.util.Date;

/**
 * Created by IntelliJ IDEA.
 * User: chenYuan
 * date: 2008-12-11
 * Time: 16:01:30
 * 转换的动作发生在页面数据已经传递到库文件的时候才发生，比较晚
 */
public class ScriptWrapFactory extends WrapFactory {

    final private static WrapFactory INSTANCE = new ScriptWrapFactory();

    public static WrapFactory getInstance() {
        return INSTANCE;
    }

    private ScriptWrapFactory() {
<<<<<<< HEAD
        super.setJavaPrimitiveWrap(false);

=======
        super.setJavaPrimitiveWrap(true);
>>>>>>> dev
    }

    /**
     *
     * @param cx 上下文
     * @param scope 变量空间
     * @param obj java对象
     * @param staticType 类型
     * @return 返回
     */
    @Override
    public Object wrap(Context cx, Scriptable scope, Object obj, Class staticType) {
        if (obj instanceof java.util.Date) {
            long time = ((Date) obj).getTime();
            return cx.evaluateString(scope, "new Date(" + time + ")", "<wrap-Date>", 0, null);
        } else if (obj instanceof Character) {
            char[] a = {(Character) obj};
            return new String(a);
        }

        return super.wrap(cx, scope, obj, staticType);
    }

    /**
     * 处理java对象
     * @param cx 上下文
     * @param scope 变量空间
     * @param obj java对象
     * @param staticType 类型
     * @return 返回
     */
    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, Object obj, Class<?> staticType) {
        if (PropertyContainer.class.isAssignableFrom(obj.getClass()))
        {
            PropertyContainer propertyContainer = (PropertyContainer)obj;
            if (!ObjectUtil.isEmpty(propertyContainer.getValues()))
            {
                Object newObj = ReflectUtil.createDynamicBean(obj,propertyContainer.getValues());
                return new NativeJavaObject(scope,newObj,staticType);
            }
        }
        return super.wrapAsJavaObject(cx,scope,obj,staticType);
    }
}