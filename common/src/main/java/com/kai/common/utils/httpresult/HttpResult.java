package com.kai.common.utils.httpresult;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * 请求返回结果对象
 * @author bill.lin
 *
 */
@Data
public class HttpResult<T> implements Serializable{
	@ApiModelProperty(value ="返回状态编码 200 操作成功；400 操作失败；401 权限不足；404 接口不存在；500 服务器内部错误；600 请重新登录")
	protected Integer status = ResultType.SUCCESS.getCode();

	@ApiModelProperty(value ="返回提示信息")
	protected String message = ResultType.SUCCESS.getName();

	@ApiModelProperty(value ="返回结果数据")
	protected T data = null;

	@ApiModelProperty(value ="是否成功 true false")
	protected Boolean success = true;

	public void success(String message) {
		this.success = true;
		this.message = message;
		this.status = ResultType.SUCCESS.getCode();
		if(this.message == null || "".equals(this.message)){
			this.message = ResultType.SUCCESS.getName();
		}
	}

	public void fail(String message) {
		this.success = false;
		this.message = message;
		this.status = ResultType.FAIL.getCode();
		if(this.message == null || "".equals(this.message)){
			this.message = ResultType.FAIL.getName();
		}
	}

	public void fail(Integer status, String message) {
		this.success = false;
		this.status = status;
		this.message = message;
		if(this.message == null || "".equals(this.message)){
			this.message = ResultType.FAIL.getName();
		}
	}
}
