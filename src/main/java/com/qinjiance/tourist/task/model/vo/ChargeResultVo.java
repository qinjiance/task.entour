/**
 * 
 */
package com.qinjiance.tourist.task.model.vo;

import java.io.Serializable;

import com.qinjiance.tourist.task.constants.ChargeResultEnum;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月15日
 * 
 * @time 下午7:40:39
 * 
 * @desc
 * 
 */
public class ChargeResultVo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2756141678444865668L;

	private int code;
	private String message;
	private Object result;

	/**
	 * 
	 */
	public ChargeResultVo() {
	}

	public ChargeResultVo(int code, String message, ChargeResultEnum chargeResultEnum, Object result) {
		super();
		this.code = code;
		this.message = message;
		this.result = result;
	}

	public ChargeResultVo(ChargeResultEnum chargeResultEnum, Object result) {
		super();
		this.result = result;
		this.code = chargeResultEnum.getResultStatus();
		this.message = chargeResultEnum.getErrMsg();
	}

	/**
	 * @return the code
	 */
	public int getCode() {
		return code;
	}

	/**
	 * @param code
	 *            the code to set
	 */
	public void setCode(int code) {
		this.code = code;
	}

	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}

	/**
	 * @param message
	 *            the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * @return the result
	 */
	public Object getResult() {
		return result;
	}

	/**
	 * @param result
	 *            the result to set
	 */
	public void setResult(Object result) {
		this.result = result;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
