package com.github.jspxnet.sioc.annotation;

import com.github.jspxnet.sioc.Sioc;
import com.github.jspxnet.sioc.util.Empty;
import com.github.jspxnet.txweb.enums.RpcProtocolEnumType;
import com.github.jspxnet.utils.StringUtil;
import java.lang.annotation.*;

/**
 * Created by jspx.net
 *
 * author: chenYuan
 * date: 2020/6/21 23:59
 * description: 远程RPC调用
 * 默认服务器简数据传输处理推荐是用TCP的ioc方式调用
 *
 **/
@Documented
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface RpcClient {
    //默认为类名,  这里为ioc方式调用,属于直接连接调用,不会有拦截器,权限相关控制
    Class<?> bind() default Empty.class;

    //命名空间,ioc 注入是用
    String namespace() default Sioc.global;

   //tcp方式更好,但需要单独的端口,在低配置环境，和端口限制的地方是用http方式
    RpcProtocolEnumType protocol() default RpcProtocolEnumType.HTTP;

    //HTTP 方式必须有远程URL地址
    //TCP方式这里是对方的ioc命名空间
    String url() default StringUtil.empty;

    //服务名称.TCP分组服务
    String groupName() default StringUtil.empty;


}
