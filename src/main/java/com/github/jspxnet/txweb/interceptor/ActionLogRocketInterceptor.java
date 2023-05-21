package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.mq.RocketMqProducer;
import com.github.jspxnet.mq.env.MqIoc;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.Action;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.context.ActionContext;
import com.github.jspxnet.txweb.context.ThreadContextHolder;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;


/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/12/20 21:58
 * description: 消息服务器方式发送保存日志
 *  将迁移出去，减少依赖
 **/
@Slf4j
//@Bean
@Deprecated
public class ActionLogRocketInterceptor extends InterceptorSupport {

    /**
     * 载入在线管理
     */
    @Ref
    private OnlineManager onlineManager;

    @Ref(name = MqIoc.actionLogMqProducer,test = true)
    private RocketMqProducer rocketMqProducer;

    private String topic;

    private String tags;


    public void setTopic(String topic) {
        this.topic = topic;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    private boolean guestLog = false;

    /**
     * @param guestLog 是否记录游客日志
     */
    public void setGuestLog(boolean guestLog) {
        this.guestLog = guestLog;
    }

    @Override
    public void init() {

    }

    @Override
    public void destroy() {

    }

    @Override
    public String intercept(ActionInvocation actionInvocation) throws Exception {

        ActionProxy actionProxy = actionInvocation.getActionProxy();
        Action action = actionProxy.getAction();

        if (RequestUtil.isMultipart(action.getRequest()))
        {
            return actionInvocation.invoke();
        }
        //游客就不记录了
        ActionContext actionContext = ThreadContextHolder.getContext();
        if (guestLog && action.isGuest() || !actionContext.isExecuted()) {
            return actionContext.getActionResult();
        }

        //也可以 return Action.ERROR; 终止action的运行
        //保存历史记录 begin
        //@method
        String operation = actionContext.getMethod().getName();
        if (ActionEnv.DEFAULT_EXECUTE.equalsIgnoreCase(operation) && !RequestUtil.isMultipart(action.getRequest()) || StringUtil.isEmpty(operation)) {
            return actionContext.getActionResult();
        }
        ActionLog actionLog = action.getActionLog();
        if (actionLog != null && !StringUtil.isNull(actionLog.getContent())) {

            if (StringUtil.isNull(actionLog.getTitle())) {
                actionLog.setTitle(actionProxy.getCaption());
            }
            if (StringUtil.isNull(actionLog.getTitle())) {
                actionLog.setTitle(operation);
            }
            actionLog.setCaption(actionProxy.getCaption());
            actionLog.setClassMethod(operation);
            actionLog.setMethodCaption(actionProxy.getMethodCaption());
            actionLog.setActionResult(actionContext.getActionResult());

            /*if (rocketMqProducer != null && rocketMqProducer.getDefaultMQProducer()!=null) {
                DefaultMQProducer mqProducer = (DefaultMQProducer)rocketMqProducer.getDefaultMQProducer();
                Message message = new Message(topic, tags,  new JSONObject(actionLog).toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
                mqProducer.send(message, new SendCallback() {
                    @Override
                    public void onSuccess(SendResult sendResult) {
                        log.debug("日志保存成功,{}", sendResult.getMsgId());
                    }

                    @Override
                    public void onException(Throwable e) {
                        e.printStackTrace();
                        log.error("日志记录保存发生错误", e);
                    }
                });
            }*/
            //删除3年前的记录数据
        }
        //执行下一个动作,可能是下一个拦截器,也可能是action取决你的配置
        return actionContext.getActionResult();
        //也可以 return Action.ERROR; 终止action的运行
    }
}