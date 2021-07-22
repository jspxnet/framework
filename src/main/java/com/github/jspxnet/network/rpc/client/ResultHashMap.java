package com.github.jspxnet.network.rpc.client;

import com.github.jspxnet.network.rpc.model.cmd.SendCmd;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/21 15:18
 * description: 请求返回的结构队列
 **/
public class ResultHashMap extends ConcurrentHashMap<String, ArrayBlockingQueue<SendCmd>>
{
    final private static ResultHashMap INSTANCE = new ResultHashMap();
    public static ResultHashMap getInstance(){
        return INSTANCE;
    }

    private ResultHashMap()
    {

    }
}
