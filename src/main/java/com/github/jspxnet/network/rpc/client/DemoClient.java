package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.boot.JspxNetApplication;
import com.github.jspxnet.network.rpc.HelloService;
import com.github.jspxnet.network.rpc.client.proxy.NettyRpcProxy;
import lombok.extern.slf4j.Slf4j;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/10 23:08
 * description: jspbox
 **/
@Slf4j
public class DemoClient {

    static void testPools()
    {
        long startTimes = System.currentTimeMillis();
        for (int i=1;i<Integer.MAX_VALUE;i++)
        {
            HelloService helloService = NettyRpcProxy.create(HelloService.class);
            System.out.println(helloService.hello("call " +i));
        }
        System.out.println("testPools-------------:" + (System.currentTimeMillis()-startTimes));

    }

    static void testClient()
    {
        long startTimes = System.currentTimeMillis();
        for (int i=1;i<100;i++)
        {

        }
        System.out.println("testPools-------------:" + (System.currentTimeMillis()-startTimes));
    }

    public static void main(String[] args) throws Exception {
        JspxNetApplication.autoRun();
        testPools();

/*
        List<InetSocketAddress> inetSocketAddress = MasterSocketAddress.getInstance().getDefaultSocketAddressList();
        for (InetSocketAddress address:inetSocketAddress)
        {

            HelloService helloService = (HelloService)RpcProxy.create(HelloService.class,address);
            System.out.println(helloService.hello("call " + "--" + address.getPort()));
        }*/


    }



}
