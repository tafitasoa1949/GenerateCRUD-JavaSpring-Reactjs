package database_details;

import java.util.HashMap;
import java.util.Map;

public class DataTypeCsGenerator {
    private static final Map<String, String> TYPE_MAP = new HashMap<>();

    static {
        TYPE_MAP.put("int4", "int");
        TYPE_MAP.put("varchar", "string");
        TYPE_MAP.put("timestamp", "DateTime");
        TYPE_MAP.put("bool", "bool");
        TYPE_MAP.put("float8", "float");
        TYPE_MAP.put("int8", "long");
        TYPE_MAP.put("int2", "int");
        TYPE_MAP.put("serial", "int");
        TYPE_MAP.put("int1", "int");
        TYPE_MAP.put("numeric", "BigInt");
        TYPE_MAP.put("date", "DateTime");
        TYPE_MAP.put("time", "TimeSpan");
        TYPE_MAP.put("bytea", "byte[]");
        TYPE_MAP.put("text", "string");
        TYPE_MAP.put("float4", "float");
    }

    private String columnType;

    public DataTypeCsGenerator(String columnType) {
        this.columnType = columnType;
    }

    public String getCsTypeOfColumn() {
        String csType = TYPE_MAP.get(columnType.toLowerCase());
        return (csType != null) ? csType : "";
    }

    public static void main(String[] args) {
        DataTypeCsGenerator generator = new DataTypeCsGenerator("int4");
        System.out.println(generator.getCsTypeOfColumn());
    }
}
