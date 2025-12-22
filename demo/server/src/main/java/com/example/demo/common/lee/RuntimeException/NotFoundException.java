package com.example.demo.common.lee.RuntimeException;

public class NotFoundException extends ApplicationException {
    // 构造函数1：只有消息
    public NotFoundException(String message) {
        super(message);
    }

    // 构造函数2：包含消息和原因
    public NotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    // 构造函数3：只有原因
    public NotFoundException(Throwable cause) {
        super(cause);
    }

    // 构造函数4：包含消息、原因、抑制标志和可写栈追踪标志
    public NotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}