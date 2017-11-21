package com.ingkee.plugin.entitys;

public class ParamEntity {
    public String paramName;
    public String paramType;
    public String paramDiscribe;

    @Override
    public String toString() {
        return "ParamEntity{" +
                "paramName='" + paramName + '\'' +
                ", paramType='" + paramType + '\'' +
                ", paramDiscribe='" + paramDiscribe + '\'' +
                '}';
    }
}
