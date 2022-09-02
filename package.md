##依赖打包说明


+- org.apache.maven:maven-artifact:jar:3.0:provided
[INFO] |  +- org.apache.maven:maven-core:jar:3.0:provided
[INFO] |  |  +- org.apache.maven:maven-settings:jar:3.0:provided
[INFO] |  |  +- org.apache.maven:maven-settings-builder:jar:3.0:provided
[INFO] |  |  +- org.apache.maven:maven-repository-metadata:jar:3.0:provided
[INFO] |  |  +- org.apache.maven:maven-model-builder:jar:3.0:provided
[INFO] |  |  +- org.apache.maven:maven-aether-provider:jar:3.0:provided
[INFO] |  |  +- org.sonatype.aether:aether-impl:jar:1.7:provided
[INFO] |  |  |  \- org.sonatype.aether:aether-spi:jar:1.7:provided
[INFO] |  |  +- org.sonatype.aether:aether-api:jar:1.7:provided
[INFO] |  |  +- org.sonatype.aether:aether-util:jar:1.7:provided
[INFO] |  |  +- org.sonatype.sisu:sisu-inject-plexus:jar:1.4.2:provided
[INFO] |  |  |  \- org.sonatype.sisu:sisu-inject-bean:jar:1.4.2:provided
[INFO] |  |  |     \- org.sonatype.sisu:sisu-guice:jar:noaop:2.1.7:provided
[INFO] |  |  +- org.codehaus.plexus:plexus-classworlds:jar:2.2.3:provided
[INFO] |  |  \- org.sonatype.plexus:plexus-sec-dispatcher:jar:1.3:provided
[INFO] |  |     \- org.sonatype.plexus:plexus-cipher:jar:1.4:provided

##这几个在正式环境里边必须删除,不然有兼容问题,让apollo 初始化失败
sisu-inject-plexus
sisu-inject-bean
sisu-guice