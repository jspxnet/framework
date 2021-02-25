配置方式支持说明:
本配置载入方式为3种
1.传统普通web方式,jspx.properties放在classes目录读取,或打包压缩作为应用是用的时候可以放在 conf这个目录
3.vcs下载方式,配置好git或者svn 后启动会自动下载来是用这个配置
4.appollo配置中心方式,appollo的配置直接写在jspx.properties
appollo方式不修改jspx.properties里边的配置,直接载入内存使用

对应关系:
1:default;2:vcs;3:appollo
configMode
