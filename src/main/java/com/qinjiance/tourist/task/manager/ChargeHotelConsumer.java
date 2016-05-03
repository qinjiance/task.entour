package com.qinjiance.tourist.task.manager;

import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinjiance.tourist.task.constants.ChargeResultEnum;
import com.qinjiance.tourist.task.constants.ChargeStatus;
import com.qinjiance.tourist.task.mapper.BillingHotelMapper;
import com.qinjiance.tourist.task.model.BillingHotel;
import com.qinjiance.tourist.task.model.vo.BookHotelResult;
import com.qinjiance.tourist.task.model.vo.ChargeResultVo;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月15日
 * 
 * @time 上午3:27:32
 * 
 * @desc 主消费线程
 * 
 */
public class ChargeHotelConsumer implements Runnable {

	private Logger logger = LoggerFactory.getLogger(ChargeHotelConsumer.class);

	private boolean stop = false;

	private BillingHotelMapper billingHotelMapper;

	private ChargeHotelManager chargeHotelManager;

	private ChargeHotelQueue chargeHotelQueue;

	public ChargeHotelConsumer(BillingHotelMapper billingOrderMapper, ChargeHotelManager chargeManager,
			ChargeHotelQueue chargeOrderQueue) {
		super();
		this.billingHotelMapper = billingOrderMapper;
		this.chargeHotelManager = chargeManager;
		this.chargeHotelQueue = chargeOrderQueue;
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				BillingHotel bo = chargeHotelQueue.pollMajorObject();
				if (bo == null) {
					TimeUnit.SECONDS.sleep(1);
				} else {
					// 发送充值通知
					ChargeResultVo chargeResult = chargeHotelManager.commonCharge(bo);
					if (chargeResult.getCode() == ChargeResultEnum.ORDER_IS_CHARGED.getResultStatus()) {
						StringBuilder sb = new StringBuilder();
						sb.append("charge hotel ").append(bo.getId()).append(" failed, order is already charged.");
						logger.info(sb.toString());
					} else if (chargeResult.getCode() == ChargeResultEnum.SUCCESS.getResultStatus()) {
						// 充值已成功
						BookHotelResult bookResult = (BookHotelResult) chargeResult.getResult();
						int result = billingHotelMapper.updateChargeOk(bo.getId(),
								bookResult.getTransNum().longValue(), bookResult.getRgid().longValue());

						StringBuilder sb = new StringBuilder();
						sb.append("charge hotel ").append(bo.getId()).append(" success, update charge status, result=")
								.append(result).append(".");
						logger.info(sb.toString());
					} else {
						// 更新失败原因
						billingHotelMapper.updateChargeOrder(bo.getId(), ChargeStatus.PROCESSING.getStatus(), "通知1次-"
								+ chargeResult.getMessage());
						// 主线充值失败，订单进入辅线队列
						ChargeHotelQueue.addMinorObject(bo);
					}
				}
			} catch (InterruptedException e) {
				logger.error("Interrupted exit from ChargeOrderConsumer!");
				break;
			} catch (Throwable t) {
				logger.error("ChargeHotelConsumer exception!", t);
			}
		}
	}

	public void stop() {
		stop = true;
	}

	public BillingHotelMapper getBillingOrderMapper() {
		return billingHotelMapper;
	}

	public void setBillingOrderMapper(BillingHotelMapper billingOrderMapper) {
		this.billingHotelMapper = billingOrderMapper;
	}

	public ChargeHotelManager getMobileChargeManager() {
		return chargeHotelManager;
	}

	public void setMobileChargeManager(ChargeHotelManager chargeManager) {
		this.chargeHotelManager = chargeManager;
	}

	public ChargeHotelQueue getMobileChargeOrderQueue() {
		return chargeHotelQueue;
	}

	public void setMobileChargeOrderQueue(ChargeHotelQueue chargeOrderQueue) {
		this.chargeHotelQueue = chargeOrderQueue;
	}
}
