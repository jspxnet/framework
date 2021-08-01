package com.github.jspxnet.sober.enums;

import com.github.jspxnet.enums.EnumType;

public class DatabaseEnumType   implements EnumType {
/*

    static public final String ORACLE = "Oracle";

    static public final String DM = "Dm";

    static public final String POSTGRESQL = "PostgreSQL";

    static public final String INTERBASE = "Interbase";

    static public final String MSSQL = "MsSql";

    static public final String MYSQL = "MySQL";

    static public final String HSQL = "HSQL";

    static public final String DB2 = "DB2";

    static public final String Firebird = "Firebird";

    static public final String Sqlite = "Sqlite";

    static public final String Smalldb = "SmallDB";

    static public final String General = "General";

 */
    //查询
    ORACLE(1, "QUERY"),
    //更新
    UPDATE(1, "UPDATE"),
    //执行
    EXECUTE(2, "EXECUTE");


    final private int value;
    final private String name;

    DatabaseEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public DatabaseEnumType find(int value) {
        DatabaseEnumType            if (c.value == value) {
                return c;
            }
        }
        return ExecuteEnumType.QUERY;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }

}
