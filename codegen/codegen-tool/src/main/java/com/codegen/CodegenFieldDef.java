package com.codegen;

import lombok.Data;

@Data
public class CodegenFieldDef {
    public String name;
    public String type;
    public String column;
    public boolean id;
    public String label;
}
