/**
 * 
 */
package com.qinjiance.tourist.task.constants;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年7月8日
 * 
 * @time 下午2:24:03
 * 
 * @desc 订单支付状态
 * 
 */
public enum PayStatus {

	NOT_PAY(0), PAYED(1), FAILED(99);

	private final int status;

	private PayStatus(int status) {
		this.status = status;
	}

	/**
	 * @return the status
	 */
	public int getStatus() {
		return status;
	}

	/**
	 * @param status
	 * @return
	 */
	public static PayStatus getEnum(int status) {
		for (PayStatus enumItem : PayStatus.values()) {
			if (enumItem.getStatus() == status) {
				return enumItem;
			}
		}
		return null;
	}
}
