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
 * @desc 订单兑换状态
 * 
 */
public enum ChargeStatus {

	NOT_CHARGE(0), CHARGED(1), PROCESSING(2), FAILED(99);

	private final int status;

	private ChargeStatus(int status) {
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
	public static ChargeStatus getEnum(int status) {
		for (ChargeStatus enumItem : ChargeStatus.values()) {
			if (enumItem.getStatus() == status) {
				return enumItem;
			}
		}
		return null;
	}
}
