package com.github.jspxnet.datasource;

import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.utils.ArrayUtil;

/**
 * Created by ChenYuan on 2017/5/12.
 * 连接池密码创建工具
 */
public class CreateDatabasePassword extends JspxDataSource {

    public static void main(String[] args) throws Exception {

        JspxNetApplication.autoRun();
        if (ArrayUtil.isEmpty(args)) {
            args = new String[1];
            args[0] = "";
        }

        CreateDatabasePassword createDatabasePassword = new CreateDatabasePassword();
        String ps = createDatabasePassword.makePassword(args[0]);
        System.out.println(ps);
    }

}
