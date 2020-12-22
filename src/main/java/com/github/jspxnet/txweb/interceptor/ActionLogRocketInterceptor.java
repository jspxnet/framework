package com.github.jspxnet.txweb.interceptor;

import com.github.jspxnet.json.JSONObject;
import com.github.jspxnet.mq.RocketMqProducer;
import com.github.jspxnet.mq.env.MqIoc;
import com.github.jspxnet.sioc.annotation.Bean;
import com.github.jspxnet.sioc.annotation.Ref;
import com.github.jspxnet.txweb.ActionInvocation;
import com.github.jspxnet.txweb.ActionProxy;
import com.github.jspxnet.txweb.IUserSession;
import com.github.jspxnet.txweb.env.ActionEnv;
import com.github.jspxnet.txweb.online.OnlineManager;
import com.github.jspxnet.txweb.support.ActionSupport;
import com.github.jspxnet.txweb.table.ActionLog;
import com.github.jspxnet.txweb.util.RequestUtil;
import com.github.jspxnet.txweb.util.TXWebUtil;
import com.github.jspxnet.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.rocketmq.client.producer.SendCallback;
import org.apache.rocketmq.client.producer.SendResult;
import org.apache.rocketmq.common.message.Message;
import org.apache.rocketmq.remoting.common.RemotingHelper;

/**
 * Created by jspx.net
 * <p>
 * author: chenYuan
 * date: 2020/12/20 21:58
 * description: 消息服务器方式发送保存日志
 **/
@Slf4j
@Bean
public class ActionLogRocketInterceptor extends InterceptorSupport {

    /**
     * 载入在线管理
     */
    @Ref
    private OnlineManager onlineManager;

    @Ref(name = MqIoc.actionLogMqProducer)
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
        String result = actionInvocation.invoke();
        ActionProxy actionProxy = actionInvocation.getActionProxy();
        ActionSupport action = actionProxy.getAction();
        IUserSession userSession = onlineManager.getUserSession(action);
        //游客就不记录了
        if (guestLog && action.isGuest() || !actionInvocation.isExecuted()) {
            return result;
        }

        //也可以 return Action.ERROR; 终止action的运行
        //保存历史记录 begin
        //@method
        String operation = actionProxy.getMethod().getName();
        if (TXWebUtil.defaultExecute.equalsIgnoreCase(operation) && !RequestUtil.isMultipart(action.getRequest()) || StringUtil.isEmpty(operation)) {
            return result;
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
            actionLog.setNamespace(action.getRootNamespace());
            actionLog.setActionResult(result);
            String id;
            if (ClassUtil.isDeclaredMethod(action.getClass(), "getId")) {
                id = ObjectUtil.toString(BeanUtil.getProperty(action, "getId"));
            } else {
                id = action.getString("id");
            }
            if (StringUtil.isEmpty(id)) {
                String[] ids = action.getArray("id", true);
                id = ArrayUtil.toString(ids, ";");
            }

            actionLog.setObjectId(id);
            actionLog.setUrl(action.getRequest().getRequestURI());
            actionLog.setPutName(userSession.getName());
            actionLog.setPutUid(userSession.getUid());
            actionLog.setIp(action.getRemoteAddr());
            String organizeId = action.getEnv(ActionEnv.KEY_organizeId);
            if (StringUtil.isEmpty(organizeId)) {
                organizeId = ObjectUtil.toString(action.getSession().getAttribute(ActionEnv.KEY_organizeId));
            }
            actionLog.setOrganizeId(organizeId);

            if (rocketMqProducer != null) {
                JSONObject json = new JSONObject(actionLog);
                Message message = new Message(topic, tags, json.toString().getBytes(RemotingHelper.DEFAULT_CHARSET));
                rocketMqProducer.send(message, new SendCallback() {
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
            }
            //删除3年前的记录数据
        }
        //执行下一个动作,可能是下一个拦截器,也可能是action取决你的配置
        return result;
        //也可以 return Action.ERROR; 终止action的运行
    }
}