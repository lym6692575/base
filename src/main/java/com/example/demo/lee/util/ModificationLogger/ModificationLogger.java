package com.example.demo.lee.util.ModificationLogger;

import com.dqjq.base.common.lee.annotation.LogField;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.StringJoiner;

/**
 * 工具类，用于记录实体字段的修改日志。
 * 该类提供了根据实体和 DTO 对象的比较，生成修改日志的方法。
 */
public final class ModificationLogger {

    // 防止实例化工具类
    private ModificationLogger() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * 比较原始实体与更新后的实体，生成修改日志。
     * 该方法会检查两个实体中标注了 @LogField 注解的字段是否有值的改变，并生成修改日志。
     *
     * @param originalEntity 原始实体对象
     * @param updatedEntity  更新后的实体对象
     * @param request        HttpServletRequest 对象，用于获取客户端 IP (可选，未使用)
     * @return 修改日志对象，包含修改前和修改后的字段及值
     */
    public static ModificationLog generateModificationLog(Object originalEntity, Object updatedEntity, HttpServletRequest request) {
        // 参数验证
        if (originalEntity == null || updatedEntity == null) {
            throw new IllegalArgumentException("实体对象不能为空");
        }

        // 确保两个实体类型一致
        Class<?> clazz = originalEntity.getClass();
        if (!clazz.equals(updatedEntity.getClass())) {
            throw new IllegalArgumentException("实体对象类型不一致");
        }

        // 用于存储修改前和修改后的字段值
        StringJoiner beforeJoiner = new StringJoiner("，");
        StringJoiner afterJoiner = new StringJoiner("，");

        // 获取类的所有字段
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            // 只处理标注了 @LogField 注解的字段
            if (field.isAnnotationPresent(LogField.class)) {
                field.setAccessible(true);
                try {
                    // 获取字段的原始值和更新后的值
                    Object originalValue = field.get(originalEntity);
                    Object updatedValue = field.get(updatedEntity);

                    // 判断字段是否发生了变化
                    if (hasFieldChanged(field.getType(), originalValue, updatedValue)) {
                        // 获取 @LogField 注解中的 displayName
                        LogField logField = field.getAnnotation(LogField.class);
                        String displayName = logField.displayName();
                        String originalValStr = originalValue != null ? originalValue.toString() : "null";
                        String updatedValStr = updatedValue != null ? updatedValue.toString() : "null";

                        // 将字段值加入到修改前和修改后的记录中
                        beforeJoiner.add(String.format("%s：%s", displayName, originalValStr));
                        afterJoiner.add(String.format("%s：%s", displayName, updatedValStr));
                    }
                } catch (IllegalAccessException e) {
                    System.err.println("无法访问字段：" + field.getName());
                }
            }
        }

        // 返回修改日志对象，封装修改前和修改后的日志
        return new ModificationLog(beforeJoiner.toString(), afterJoiner.toString());
    }

    /**
     * 根据字段类型和原始值、更新值判断字段是否发生了变化。
     *
     * @param fieldType     字段的类型
     * @param originalValue 原始值
     * @param updatedValue  更新后的值
     * @return 如果字段值发生变化则返回 true，否则返回 false
     */
    private static boolean hasFieldChanged(Class<?> fieldType, Object originalValue, Object updatedValue) {
        // 如果两者都为 null，认为没有变化
        if (originalValue == null && updatedValue == null) {
            return false;
        }

        // 如果其中一个为 null，另一个不为 null，则认为有变化
        if (originalValue == null || updatedValue == null) {
            return true;
        }

        // 针对不同类型采用不同的比较方法
        if (BigDecimal.class.isAssignableFrom(fieldType)) {
            // 对于 BigDecimal，使用 compareTo 来比较数值大小，忽略 scale
            BigDecimal originalDecimal = (BigDecimal) originalValue;
            BigDecimal updatedDecimal = (BigDecimal) updatedValue;
            return originalDecimal.compareTo(updatedDecimal) != 0;
        }

        // 如果需要针对其他类型进行特殊比较，可以在这里添加
        // 例如，对于日期类型，可以只比较日期部分而忽略时间部分

        // 默认使用 Objects.equals 进行比较
        return !Objects.equals(originalValue, updatedValue);
    }
}

