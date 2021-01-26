package com.github.jspxnet.network.rpc.service;

import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.utils.ArrayUtil;
import com.github.jspxnet.utils.FileUtil;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/9 22:19
 * description: jspbox
 **/
@Slf4j
public class NettyServerMain {

    public static void main(String[] args) {

        if (ArrayUtil.isEmpty(args)) {
            JspxNetApplication.autoRun();
            start();
        } else
        {
            if (FileUtil.isDirectory(args[0]))
            {
                JspxNetApplication.autoRun(FileUtil.mendPath(args[0]));
            }
            if ("start".equalsIgnoreCase(args[0])||args.length>1&&"start".equalsIgnoreCase(args[1])) {
                start();
                return;
            }
            if ("stop".equalsIgnoreCase(args[0])||args.length>1&&"stop".equalsIgnoreCase(args[1])) {
                stop();
            }
        }
    }

    public static void start()  {
        NettyRpcServiceGroup.getInstance().start();
    }

    public static void stop() {
        NettyRpcServiceGroup.getInstance().stop();
    }

    public static void restart() {
        stop();
        start();
    }


}
