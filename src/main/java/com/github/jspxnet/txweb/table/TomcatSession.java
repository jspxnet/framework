package com.github.jspxnet.txweb.table;

import com.github.jspxnet.sober.annotation.Column;
import com.github.jspxnet.sober.annotation.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * Created by ChenYuan on 2017/6/11.
 * 让tomcat 的session数据保存在数据库中
 * <p>
 * CREATE TABLE `tomcat_session` (
 * `sessionId` varchar(128) DEFAULT NULL,
 * `sessionData` mediumblob,
 * `maxInactive` int(128) DEFAULT NULL,
 * `lastAccessed` bigint(20) DEFAULT NULL,
 * `sessionApp` varchar(128) DEFAULT NULL,
 * `sessionValid` char(2) DEFAULT NULL
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8;
 *
 * <p>
 * [Manager className="org.apache.catalina.session.PersistentManager"
 * saveOnRestart="true"
 * maxActiveSession="-1"
 * minIdleSwap="0"
 * maxIdleSwap="30"
 * maxIdleBackup="0"
 * ]
 * <p>
 * [IStore className="org.apache.catalina.session.JDBCStore"
 * driverName="com.mysql.jdbc.Driver"
 * connectionURL="jdbc:mysql://localhost:3306/demo?user=root&amp;password=123456"
 * sessionTable="tomcat_session"
 * sessionIdCol="sessionId"
 * sessionDataCol="sessionData"
 * sessionValidCol="sessionValid"
 * sessionMaxInactiveCol="maxInactive"
 * sessionLastAccessedCol="lastAccessed"
 * sessionAppCol="sessionApp"
 * checkInterval="60"
 * debug="99" /]
 * [/Manager]
 */

@Data
@Table(name = "tomcat_session", caption = "在线信息", cache = true)
public class TomcatSession implements Serializable {

    @Column(caption = "sessionId", dataType = "isLengthBetween(1,250)", length = 200)
    private String sessionId;

    @Column(caption = "data", length = 50000, defaultValue = "", notNull = false)
    private String sessionData;

    @Column(caption = "最大有效期", notNull = true)
    private long maxInactive;

    @Column(caption = "最后访问时间", defaultValue = "0", notNull = true)
    private long lastAccessed;

    @Column(caption = "sessionApp", length = 200, notNull = false)
    private String sessionApp;

    @Column(caption = "sessionValid", length = 4, notNull = false)
    private String sessionValid;
}
