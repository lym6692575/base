package com.dqjq.codegen;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class CodegenConfig {
    public String packageBase;
    public String module;
    public String entityName;
    public String idType;
    public String mapping;
    public String tableName;
    public String subselect;
    public String outputDir;
    public String templatesDir;
    public String entityBaseClass;
    public String dtoBaseClass;
    public List<CodegenFieldDef> fields = new ArrayList<>();

    public static CodegenConfig fromProperties(Properties p) {
        CodegenConfig c = new CodegenConfig();
        c.packageBase = p.getProperty("packageBase", "com.dqjq.myapp.oracle");
        c.module = p.getProperty("module", "demo");
        c.entityName = p.getProperty("entityName", "DemoGen");
        c.idType = p.getProperty("idType", "Long");
        c.mapping = p.getProperty("mapping", "SUBSELECT");
        c.tableName = p.getProperty("tableName", "");
        c.subselect = p.getProperty("subselect", "SELECT 1 AS ID, 'DEMO' AS NAME FROM DUAL");
        c.outputDir = p.getProperty("outputDir", "src/main/java");
        c.templatesDir = p.getProperty("templatesDir", "");
        c.entityBaseClass = p.getProperty("entityBaseClass", "com.dqjq.base.lee.entity.BaseEntity");
        c.dtoBaseClass = p.getProperty("dtoBaseClass", "com.dqjq.base.lee.dto.BaseDto");
        String fieldsStr = p.getProperty("fields", "id:Long:ID:true:ID;name:String:NAME:false:名称");
        for (String part : fieldsStr.split(";")) {
            String[] seg = part.trim().split(":");
            if (seg.length >= 4) {
                CodegenFieldDef f = new CodegenFieldDef();
                f.name = seg[0];
                f.type = seg[1];
                f.column = seg[2];
                f.id = Boolean.parseBoolean(seg[3]);
                f.label = seg.length >= 5 ? seg[4] : seg[0];
                c.fields.add(f);
            }
        }
        return c;
    }
}
