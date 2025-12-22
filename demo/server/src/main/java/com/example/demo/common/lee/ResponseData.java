package com.example.demo.common.lee;

import lombok.Data;

@Data
public class ResponseData<T extends Object> {
	
	/** HTTP状态码 */
	protected int httpCode;
	
	/** 操作结果 */
	protected boolean result;
	
	/** 提示信息 */
	protected String msg;
	
	/** 附加信息 */
	protected T data;
	protected ResponseData(){}
	
	protected ResponseData(int httpCode, boolean result, String msg, T data) {
		this.httpCode = httpCode;
		this.result = result;
		this.msg = msg;
		this.data = data;
	}
	
	public static<T> ResponseData<T> getSuccess(String msg) {
		return new ResponseData<T>(200, true, msg, null);
	}
	
	public static<T> ResponseData<T> getSuccess(String msg, T data) {
		return new ResponseData<T>(200, true, msg, data);
	}
	
	public static<T> ResponseData<T> getFail( String msg) {
		return new ResponseData<T>(200, false, msg, null);
	}
	
	public static<T> ResponseData<T> getFail(String msg, T data) {
		return new ResponseData<T>(200, false, msg, data);
	}
	
	public static<T> ResponseData<T> getFail(int httpCode, String msg, T data) {
		return new ResponseData<T>(httpCode, false, msg, data);
	}

	/**
	 * 创建一个包含错误信息的响应数据对象。
	 *
	 * @param message 错误信息
	 * @param e 异常对象
	 * @param <T> 泛型类型参数
	 * @return 包含错误信息的响应数据对象
	 */
	public static <T> ResponseData<T> getError(String message, Exception e) {
		return new ResponseData<T>(500, false, message + ": " + e.getMessage(), null);
	}

	public static <T> ResponseData<T> getError(String message) {
		return new ResponseData<T>(500, false, message + ": ", null);
	}
}
