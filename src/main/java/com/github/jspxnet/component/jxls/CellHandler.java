package com.github.jspxnet.component.jxls;

import com.github.jspxnet.utils.ObjectUtil;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class CellHandler implements InvocationHandler {
    private int mergerRows = 0;
    private Object target;

    private CellHandler() {

    }


    public CellHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("getMergerRows".equals(method.getName())) {
            return mergerRows;
        }
        if ("setMergerRows".equals(method.getName())) {
            mergerRows = ObjectUtil.toInt(args[0]);
            return null;
        }
        return method.invoke(target, args);
    }

}
