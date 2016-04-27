/**
 * 
 */
package com.qinjiance.tourist.task.constants;

/**
 * @author Aaron
 * @Date Mar 20, 2014
 * @Time 2:58:01 PM Mar 20, 2014
 */
public enum ChargeResultEnum {

	SUCCESS(0, "成功"),

	CONSUME_FAIL(-1, "通知发送失败"),

	CONSUME_TIME_OUT(-2, "通知超时"),

	ORDER_IS_CHARGED(-94, "订单已完成"),

	ORDER_NOT_PAY(-95, "订单未支付"),

	ORDER_NOT_FOUND(-96, "订单不存在"),

	MOBILE_CHARGE_URL_NOT_EXISTS(-97, "通知地址未设置"),

	USER_NOEXIS(-98, "用户不存在！"),

	SYSTEM_ERROR(-99, "系统错误");

	private int status;
	private String errMsg;

	ChargeResultEnum(int status, String errMsg) {
		this.status = status;
		this.errMsg = errMsg;
	}

	public int getResultStatus() {
		return status;
	}

	public String getErrMsg() {
		return errMsg;
	}
}
