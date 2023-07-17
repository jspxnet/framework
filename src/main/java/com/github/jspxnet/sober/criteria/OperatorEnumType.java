package com.github.jspxnet.sober.criteria;

import com.github.jspxnet.enums.EnumType;
import com.github.jspxnet.sober.criteria.expression.*;

public enum OperatorEnumType implements EnumType {
    /**
     * 未知
     */
    UNKNOWN(0, "UNKNOWN","未知" ,"",null),
    /**
     * sql 逻辑 表达式
     */
    LOGIC(100, "LOGIC","逻辑表达式","",null),

    EQ(1, "EQ","等于","=", SimpleExpression.class.getName()),
    GT(2, "GT","大于",">", SimpleExpression.class.getName()),
    LT(3, "LT","小于","<", SimpleExpression.class.getName()),
    GE(4, "GE","大于等于","=>", SimpleExpression.class.getName()),
    LE(5, "LE","小于等于","<=", SimpleExpression.class.getName()),

    NE(6, "NE","不等于","<=", SimpleExpression.class.getName()),

    BETWEEN(7, "BETWEEN","至","BETWEEN", BetweenExpression.class.getName()),

    IN(8, "IN","IN","IN", InExpression.class.getName()),

    INSQL(9, "IN_SQL","IN SQL","IN" , InSqlExpression.class.getName()),

    NOT_IN(10, "NOT IN","NOT IN","NOT IN", NotInExpression.class.getName()),

    NINSQL(11, "NOT_IN_SQL","NOT IN SQL","NOT IN",NotInSqlExpression.class.getName()),

    LIKE(12, "LIKE","包含","LIKE",LikeExpression.class.getName()),
    NOT_LIKE(13, "NOT LIKE","不包含","NOT LIKE",NotLikeExpression.class.getName()),

    NOT_NULL(14, "IS NOT NULL","非空","IS NOT NULL",NotNullExpression.class.getName()),

    ISNULL(15, "IS NULL","为空","IS NULL", IsNullExpression.class.getName()),

    FIND(16, "FIND","搜索","LIKE",FindExpression.class.getName()),

    //这里主要未了兼容
    NOT(17, "NOT","非","NOT",NotExpression.class.getName());

    private final int value;
    private final String key;
    private final String name;
    private final String sql;

    private final String className;

    OperatorEnumType(int value,String key, String name,String sql,String className) {
        this.value = value;
        this.key = key;
        this.name = name;
        this.sql = sql;
        this.className = className;
    }

    static public OperatorEnumType find(int value) {
        for (OperatorEnumType c : OperatorEnumType.values()) {
            if (c.value == value) {
                return c;
            }
        }
        return OperatorEnumType.UNKNOWN;
    }

    /**
     *
     * @param key sql关键字
     * @return 匹配操作方式
     */
    static public OperatorEnumType find(String key) {
        for (OperatorEnumType c : OperatorEnumType.values()) {
            if (c.key.equalsIgnoreCase(key)) {
                return c;
            }
        }
        return OperatorEnumType.UNKNOWN;
    }

    @Override
    public int getValue() {
        return this.value;
    }

    @Override
    public String getName() {
        return this.name;
    }


    public String getKey() {
        return this.key;
    }

    public String getSql() {
        return sql;
    }

    public String getClassName() {
        return className;
    }


    static public String getDesc() {
        StringBuilder sb = new StringBuilder();
        for (OperatorEnumType c : OperatorEnumType.values()) {
            sb.append(c.key).append("\t").append(c.name).append("\t").append(c.sql).append("\n");
        }
        return sb.toString();
    }
    public static void main(String[] args) {
        System.out.println(getDesc());
    }
}
