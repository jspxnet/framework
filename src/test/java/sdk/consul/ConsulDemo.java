package sdk.consul;

/**
 * Created by jspx.net
 *
 * @author: chenYuan
 * @date: 2020/12/13 23:44
 * @description: jspbox
 **/
import com.google.common.net.HostAndPort;
import com.orbitz.consul.AgentClient;
import com.orbitz.consul.Consul;
import com.orbitz.consul.HealthClient;
import com.orbitz.consul.model.agent.ImmutableRegCheck;
import com.orbitz.consul.model.agent.ImmutableRegistration;

public class ConsulDemo {

    static Consul consul = Consul.builder().withHostAndPort(HostAndPort.fromString("127.0.0.1:8500")).build();

    /**
     * 服务注册
     */
    public static void serviceRegister() {
        AgentClient agent = consul.agentClient();

        //健康检测
        ImmutableRegCheck check = ImmutableRegCheck.builder().http("http://127.0.0.1:9020/health").interval("5s").build();

        ImmutableRegistration.Builder builder = ImmutableRegistration.builder();
        builder.id("tomcat1").name("tomcat").addTags("v1").address("127.0.0.1").port(8080).addChecks(check);

        agent.register(builder.build());
    }

    /**
     * 服务获取
     */
    public static void serviceGet() {
        HealthClient client = consul.healthClient();
        String name = "tomcat";
        //获取所有服务
        System.out.println(client.getAllServiceInstances(name).getResponse().size());

        //获取所有正常的服务（健康检测通过的）
        client.getHealthyServiceInstances(name).getResponse().forEach((resp) -> {
            System.out.println(resp);
        });
    }

    public static void main(String[] args) {
        serviceRegister();
     //   serviceGet();
    }
}