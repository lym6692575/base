package com.example.demo.lee.util.ModificationLogger;

import lombok.Getter;
import lombok.Setter; /**
 * 用于封装修改日志，包括修改前和修改后的字段及值。
 */
@Setter
@Getter
public class ModificationLog {
    private String beforeChange;  // 修改前的字段及值
    private String afterChange;   // 修改后的字段及值

    public ModificationLog(String beforeChange, String afterChange) {
        this.beforeChange = beforeChange;
        this.afterChange = afterChange;
    }

    @Override
    public String toString() {
        return "修改前：[" + beforeChange + "]，修改后：[" + afterChange + "]";
    }
}
