支付宝程序使用说明
1  index页面是创建支付url,使用ItemUrl方法拼凑一个url。
   index页面的参数已经是必要参数，可以不用修改。可以稍许微调整。
   把自己网站的相对应的变量，赋值给对应参数后面即可，比如：
   String out_trade_no		= Now_Date.toString();	
   这个客户订单号，取的系统时间，Now_date这边变量。
   
2  alipay_notify.jsp为对支付宝返回通知处理，服务器post消息到这个页面。
   所以对应给notify_url这个参数设置。
   
3  alipay_return.jsp为对支付宝返回通知处理，ie页面跳转通知，只要支付成功，
   支付宝通过get方式跳转到这个地址，并且带有参数给这个页面。
   
4 java程序要注意的中文乱码问题，一定要配置上去中文filter,
  注意：一定要在web.xml中配置过滤器。每个项目中都配置了这个过滤器，具体可以直接打开
webcontent文件夹下，web-inf文件夹下的web.xml文件。
  可以参考下面文章：
   http://blog.csdn.net/lixinye0123/archive/2006/03/26/639402.aspx
  例如：
  <filter>
		<filter-name>Set Character Encoding</filter-name>
		<filter-class>filters.SetCharacterEncodingFilter</filter-class>
		<init-param>
			<param-name>encoding</param-name>
			<param-value>GBK</param-value>
		</init-param>
	</filter>
	<filter-mapping>
		<filter-name>Set Character Encoding</filter-name>
		<url-pattern>/*</url-pattern>
	</filter-mapping>
在Tomcat5.5服务器上测试则要在server.xml中 
<!-- Define a non-SSL HTTP/1.1 Connector on port 8080 -->
<Connector acceptCount="100" connectionTimeout="20000" disableUploadTimeout="true" enableLookups="false" maxHttpHeaderSize="8192" maxSpareThreads="75" maxThreads="150" minSpareThreads="25" port="8080" redirectPort="8443" URIEncoding="GBK"  useBodyEncodingForURI="true"/>加入URIEncoding="GBK"  useBodyEncodingForURI="true"