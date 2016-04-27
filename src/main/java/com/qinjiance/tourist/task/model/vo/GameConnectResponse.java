/**
 * 
 */
package com.qinjiance.tourist.task.model.vo;

import module.laohu.commons.model.BaseObject;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年7月28日
 * 
 * @time 上午10:58:28
 * 
 * @desc
 * 
 */
public class GameConnectResponse<E> extends BaseObject {

	private static final long serialVersionUID = -968662622056751643L;
	private Integer code;
	private String message;
	private E result;

	public GameConnectResponse() {

	}

	public GameConnectResponse(int code) {
		this.code = code;
	}

	public GameConnectResponse(int code, String message) {
		this.code = code;
		this.message = message;
	}

	public GameConnectResponse(int code, E result) {
		this.code = code;
		this.result = result;
	}

	public GameConnectResponse(int code, String message, E result) {
		this.code = code;
		this.message = message;
		this.result = result;
	}

	/**
	 * @return the code
	 */
	public Integer getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(Integer code) {
		this.code = code;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public E getResult() {
		return result;
	}

	public void setResult(E result) {
		this.result = result;
	}

}
