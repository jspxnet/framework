package com.github.jspxnet.sober.enums;

import com.github.jspxnet.enums.EnumType;
import com.github.jspxnet.utils.ArrayUtil;

/**
 * 数据库类型
 */
public enum  DatabaseEnumType implements EnumType {

    //枚举
    General(0,"General"),

    MYSQL(1,"MySQL"),

    POSTGRESQL(2,"PostgreSQL"),

    ORACLE(3,"Oracle"),

    DM(4,"Dm"),

    INTERBASE(5,"Interbase"),

    INFORMIX(5,"Informix"),

    MSSQL(6,"MsSql"),

    HSQL(7,"HSQL"),

    DB2(8,"DB2"),

    FIREBIRD(9,"Firebird"),

    SQLITE(10,"Sqlite"),

    SMALLDB(11,"Smalldb");

    final private int value;
    final private String name;

    DatabaseEnumType(int value, String name) {
        this.value = value;
        this.name = name;
    }

    static public DatabaseEnumType find(int value) {
        for (DatabaseEnumType c : DatabaseEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return DatabaseEnumType.General;
    }

    static public DatabaseEnumType find(String name) {
        for (DatabaseEnumType c : DatabaseEnumType.values()) {
            if (c.name.equalsIgnoreCase(name)) {
                return c;
            }
        }
        return DatabaseEnumType.General;
    }

    static public boolean inArray(DatabaseEnumType[] enumTypes,String name) {
        if (name==null)
        {
            return false;
        }
        for (DatabaseEnumType c : enumTypes) {
            if (c.name.equalsIgnoreCase(name)) {
                return true;
            }
        }
        return false;
    }

    static public String[] getNameList()
    {
        String[] array = null;
        for (DatabaseEnumType c : DatabaseEnumType.values()) {
            array = ArrayUtil.add(array,c.name);
        }
        return array;
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
