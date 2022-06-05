#Jspx Framework 6.6x

##简介

一套开源的 Jspx Framework (简实构架)是一套开源的java一体化构架, 完整的架构体系,ioc、cache、DAO、连接池、验证、模板、sqlmap持久层，b/s,c/s ,分布式集群调用支持, 语言上只使用了标准的 java 和javascript,模板语言呈现页面和API接口方式无缝结合,支持读写分。支持resetFull和传统模板呈现页面。自动API文档生成, 其中融入了很多作者开发软件碰到的问题解决方法。可替代sping cloud构架的的一套解决方案,同时也可以嵌入到spring中是用持久层。 主体结构和spring类似,但功能细节点的设计上不同,更多的添加易性,简化和实用性。 这套框架不但提供了技术实现,同时也提供了一套开发规范,让系统的结构分层,代码规范,文档一体化的实现。 

##仓库地址

```xml
<dependency>
    <groupId>com.github.jspxnet</groupId>
    <artifactId>jspx-framework</artifactId>
    <version>6.64</version>
</dependency>
```
    
##参考手册

    技术网站:http://www.jspx.net

    参考手册:http://www.jspx.net/help
    
    例子: https://github.com/jspxnet/demo

    https://github.com.cnpmjs.org/jspxnet/framework
    
    https://github.com.cnpmjs.org/jspxnet/framework.git

##名称说明
	
*	请大家不要以为是jspx，jspx是一个来自于埃及的java web快速开发框架。本构架和它没一点关系，而且本框架在它之前就已经开发了。
*	后边有个.net也和C\#没一点关系,本构架开发之初.net才开始发布。
*	名称里边的x表示：jsp的扩展性,并且劲量遵循xml标准。
*	名称里边的.net表示：以web和网络研究为主的一套框架。
*	本框架不是一个傻瓜式的构架，在本构架的开发过程中一直在寻求，简化和效率的结合点，故而考虑得比较灵活。
*   sping cloud 能实现的功能,本构架基本都能实现,各有侧重


##要解决的问题
6.x主要微服务,接口化和分布式支持.

1.	高并发及高稳定性。本构在7x24小时，365天不停机环境下稳定运行。 
2.	去除重复的功能，相同功能组件中只选择最好的。例如上传，支持一个完美的就好，没必要支持一堆的上传控件。
3.	模板语言生成界面。丢掉JSP和JSTL标签，避免重复太多，难记又难用。统一使用scriptmark模版语言。
4.	数据库连接池，高并发性能，媲美c3p0，dbcp连接池，解决8小时问题和断线从连问题。
5.	JRWPoolDataSource + Sober支持分布式负载均衡和读写分离。
6.	默认UTF-8为编码，这样实现多语言支持也较方便。同时可以自定义支持所需编码。
7.	上传部分使用升级版的cos组件。内置这个版本上传组件支持缓存，多编码，限制大小，是否覆盖，拼音自动从命名，ajax上传状态返回，并且相当稳定。
9.	持久层简单化hibernate的使用方式，及屏蔽了应用陷阱。采用hibernate，iBATIS和jdbc三种方式结合，架构中为sqlMap并且提供了自动识别查询。
10.	标签方式定义bean，自动创建数据库表结构，这样避免使用sql，达到数据库建表无需SQL脚本。
11.	连接池部分内置一个高性能连接池，处理mysql的8小时问题,并且能够支持高并发，并且支持读写分离和负载均衡。
12.	配置分两部分ioc和web转向，配置支持通配符和命名空间。支持继承访问，拦截器等功能,注释化已经实现。
13.	框架不但能使用在B/S结构,也能够使用在安卓平台。
14.	国标化和配置全局化支持。
15.	高伸缩性。能够方便的使用中租用空间，也能分布式部署使用在多台集群服务器上。
16.	参数进入自动安全过滤,内置安全过滤验证功能，能够有效避免SQL注入漏洞。
18.	整合常用接口应用。例如单点登录，用户信息，可以不是用，只是方便快速是用。功能性组件迁移出来。
20.	后缀识别默认三种 jhtml模版页面输出,jwc API接口调用后缀,md 内置md格式解析器,cmd 命令模式。
21.	提供在线管理，权限配置，验证等常用功能，提高开发速度。
22. API文档自动化,减少写文档的书写麻烦.
23. 分布式调用集群支持,内置Netty的Tcp长连接RPC调用服务自带注册发现功能,当不想用dubbo,zookpeer这些分布式构件,一个开关打开就实现了,调用方式比dubbo友好点.
24. 常用整合分布式注册发现整合consul支持。整合appollo 配置中心.安装好consul,appollo后填配置地址就可以是用
25. 微服务化,嵌入tomcat, com.github.jspxnet.boot.TomcatApplication 提供命令方式直接启动

   
##主要特点

*    快速：配置灵活,高可伸缩性
*    开源：开放源代码，高质量，高品质
*    底层：使用jdk1.8原生库。
*    扩展：基于插件的设计，所有功能都是插件，可根据需求增减功能,多应用可同服务.
*    文档：文档自动生成API.
*    系统: 支持window，linux， unix等多种平台，支持32位,64位系统
*    性能: 高速可靠,无需consul,eureka,zuul这些东西,就能支持分布式调用和聚群


**本平台主要针对以下开发对者：**

1.	已有一定的基本开发能力,需要提高开发能力的开发人员
2.	对构架要求较高性能，同时又追求灵活轻便的开发人员
3.	本构架都使用标准的开发体现，并不会干扰和限制你的技术
4.	被java 的null异常，乱码，连接数老卡死这些繁琐问题搞得头大的程序员
5.  目标宗旨就是让你脱离繁琐的技术,专注业务逻辑,简单快速的完成工作

##硬件部署

6.x 版本,只要满足租用空间可用内存512M内存以上,JDK版本1.8版本以上。
 

##发展历程 

* 2007年09月：Jspx.net Framework 1.0 发布
* 2008年07月：Jspx.net Framework 2.0 发布
* 2010年06月：Jspx.net Framework 3.5 发布
* 2011年05月：Jspx.net Framework 4.0 发布
* 2012年11月：Jspx.net Framework 4.9 发布，基本达到设计目标
* 2013年07月：Jspx.net Framework 5.0 发布，正式发布开发文档
* 2014年08月：Jspx.net Framework 5.4 发布，TXWeb正式支持ROC协议,同时简版兼容安卓
* 2015年04月：Jspx.net Framework 5.5 发布，配置统一化，界面组件化，开发规范化
* 2016年11月：Jspx.net Framework 5.6 发布，更新jdk1.8支持，调整在线管理部分，优化
* 2018年11月：Jspx.net Framework 5.8 发布，分布式接口调整
* 2019年11月：Jspx.net Framework 6.0 发布，精简,转向maven工程方式.resetFull,roc,redis 支持增强
* 2020年3月：Jspx.net Framework 6.x 发布，泛型支持升级,标签标准化,API自动文档完善,服务间调用签名标准化,id生成算法优化
* 2020年5月：Jspx.net Framework 6.5 发布，修复多个bug,优化权限管理方式支持动态配置,稳定版本
* 2020年7月：Jspx.net Framework 6.6-6.11 发布，添加tcp和http RPC调用支持,可以使用注释方式,tcp方式支持多服务器集群调用,http方式可以使用代理服务器集群,bug修复
* 2020年8月：Jspx.net Framework 6.11-6.16 性能,内存占用优化,修复多线程返回bug问题,bug版本
* 2020年9月：Jspx.net Framework 6.17 规范代码
* 2020年9月：Jspx.net Framework 6.18-22 稳定版本,调整上传支持云盘
* 2020年10月：Jspx.net Framework 6.23 添加SqlMap注释,支持查询,调整用户支持来源
* 2020年11月：Jspx.net Framework 6.24-26 微调
* 2020年12月：Jspx.net Framework 6.27 稳定版本,迁移出部分功能,修复bug,做了部分调整避免事务踩坑
* 2021年01月：Jspx.net Framework 6.30 分布式通讯支持完善,RpcClient标签,支持Http和TCP方式,TCP方式可以实现gossip整合集群
* 2021年02月：Jspx.net Framework 6.34 修复bug,完整API文档自动生成功能,线程优化,资源优化,稳定版本
* 2021年03月：Jspx.net Framework 6.35 嵌入tomcat,整合consul,appollo
* 2021年03月：Jspx.net Framework 6.39 bug修复版本稳定版本,支持嵌入spring使用
* 2021年04月：Jspx.net Framework 6.40 支持嵌入java二开平台
* 2021年04月：Jspx.net Framework 6.41-6.42 嵌入兼容性调整，oracle兼容性调整
* 2021年05月：Jspx.net Framework 6.43 嵌入兼容性调整，json兼容致远OA的PO解析.优化调整线程关闭,里程碑版本
* 2021年07月：Jspx.net Framework 6.43-6.45 定时认为支持到秒，为了兼容嵌入到老系统，调整部分日子的输出方式，测试长时间高并发优化
* 2021年07月：Jspx.net Framework 6.46 日志统一slf4j,rpc分布式调用默认不进行权限判断,细节调整优化,修复类型转换bug和UTC日期转换bug
* 2021年07月：Jspx.net Framework 6.47 添加对达梦数据库支持,优化多数据库支持
* 2021年08月：Jspx.net Framework 6.48 json兼容致远OA调整.sql功能升级支持变量直接映射到xml,sql配置.
* 2021年09月：Jspx.net Framework 6.49-6.51 sqlmap简化,修复单一返回模式的bug,路由算法优化.oos上传优化,手机图片单独保存功删除.* 
* 2021年10月：Jspx.net Framework 6.52 json兼容性调整,上传调整,注释标签简化，细节调整标准化.
* 2021年12月：Jspx.net Framework 6.53 修复sqlmap配置bug,扩展两个大数据是用方便的功能,http连接提供关闭,中文路径识别错误修复.
* 2022年03月：Jspx.net Framework 6.54 分布式调用稳定性完善，金蝶星空字段映射功能(后期重新归类)，json功能扩展支持不区分大小写方式。异步action支持.
* 2022年04月：Jspx.net Framework 6.55 修复单利模式下不释放bug,加强安全过滤功能,提供后缀和配置密码,嵌入运行模式配置优化,添加tomcat上传支持.
* 2022年05月：Jspx.net Framework 6.56 最后一个非本地线程方式版本.
* 2022年05月：Jspx.net Framework 6.60-64 提供本地线程方式版本,并发会更好,内核优化性能提升.交大的调整变动64后基本稳定.