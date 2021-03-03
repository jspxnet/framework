package com.github;

import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.boot.annotation.JspxNetBootApplication;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2021/3/2 12:17
 * description: jspbox
 **/
@JspxNetBootApplication(port = 999,webPath = "D:\\website\\webapps\\root\\")
public class TestApplication {
    public static void main(String[] args) {
        JspxNetApplication.run(TestApplication.class,args);
    }

}
