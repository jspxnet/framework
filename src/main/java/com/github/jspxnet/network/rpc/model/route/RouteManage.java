package com.github.jspxnet.network.rpc.model.route;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * Created by jspx.net
 * author: chenYuan
 * date: 2021/9/2 22:13
 * description: thermo-model
 **/
public interface RouteManage {

    boolean isLocalAddress(InetSocketAddress socketAddress);

    String getSendRouteTable();

    void joinCheckRoute(List<RouteSession> list);

    void joinCheckRoute(RouteSession routeSession);

    void joinRoute(List<RouteSession> list);

    List<RouteSession> getRouteSessionList();

    void clearCheckRouteSocketMap();

    List<RouteSession> getNeedCheckRouteSessionList();

    void routeOff(InetSocketAddress address);

    void routeOn(InetSocketAddress address);

    void cleanOffRoute();
}
