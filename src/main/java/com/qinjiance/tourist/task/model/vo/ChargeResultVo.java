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
	private ChargeResultEnum chargeResultEnum;

	/**
	 * 
	 */
	public ChargeResultVo() {
	}

	public ChargeResultVo(int code, String message, ChargeResultEnum chargeResultEnum) {
		super();
		this.code = code;
		this.message = message;
		this.chargeResultEnum = chargeResultEnum;
	}

	public ChargeResultVo(ChargeResultEnum chargeResultEnum) {
		super();
		this.chargeResultEnum = chargeResultEnum;
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
	 * @return the chargeResultEnum
	 */
	public ChargeResultEnum getChargeResultEnum() {
		return chargeResultEnum;
	}

	/**
	 * @param chargeResultEnum
	 *            the chargeResultEnum to set
	 */
	public void setChargeResultEnum(ChargeResultEnum chargeResultEnum) {
		this.chargeResultEnum = chargeResultEnum;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
