package com.qinjiance.tourist.task.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinjiance.tourist.task.constants.ChargeResultEnum;
import com.qinjiance.tourist.task.constants.ChargeStatus;
import com.qinjiance.tourist.task.constants.Constants;
import com.qinjiance.tourist.task.mapper.BillingHotelMapper;
import com.qinjiance.tourist.task.model.BillingHotel;
import com.qinjiance.tourist.task.model.vo.ChargeResultVo;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月15日
 * 
 * @time 下午1:09:52
 * 
 * @desc 次消费队列：每60分钟跑一次，每个订单跑10次
 * 
 */
public class ChargeHotelConsumer2 implements Runnable {

	private Logger logger = LoggerFactory.getLogger(ChargeHotelConsumer2.class);

	private BillingHotelMapper billingOrderMapper;

	private ChargeHotelManager mobileChargeManager;

	// 计数器
	public static ConcurrentHashMap<Long, AtomicInteger> countMap = new ConcurrentHashMap<Long, AtomicInteger>();

	public ChargeHotelConsumer2(BillingHotelMapper billingOrderMapper, ChargeHotelManager mobileChargeManager) {
		super();
		this.billingOrderMapper = billingOrderMapper;
		this.mobileChargeManager = mobileChargeManager;
	}

	@Override
	public void run() {
		while (true) {
			try {
				List<BillingHotel> orderList = new ArrayList<BillingHotel>();
				while (!ChargeHotelQueue.ThirdLevelIsEmpty()) {
					BillingHotel bo = ChargeHotelQueue.pollThirdLevelObject();
					if (bo != null) {
						long orderId = bo.getId();
						ChargeResultVo chargeResult = mobileChargeManager.commonCharge(bo);
						if (chargeResult.getChargeResultEnum() == ChargeResultEnum.ORDER_IS_CHARGED) {
							StringBuilder sb = new StringBuilder();
							sb.append("charge order ").append(bo.getId()).append(" failed, order is already charged.");
							logger.info(sb.toString());
						} else if (chargeResult.getChargeResultEnum() == ChargeResultEnum.SUCCESS) {
							// 充值已成功
							int result = billingOrderMapper.updateChargeOrder(orderId,
									ChargeStatus.CHARGED.getStatus(), null);

							StringBuilder sb = new StringBuilder();
							sb.append("charge order ").append(bo.getId())
									.append(" success, update charge status, result=").append(result).append(".");
							logger.info(sb.toString());
						} else {

							AtomicInteger times = countMap.get(orderId);

							String notifyTimes = String.valueOf(times == null ? Constants.NOTIFY_TIMES_COMSUMER
									+ Constants.NOTIFY_TIMES_COMSUMER1 + 1 : Constants.NOTIFY_TIMES_COMSUMER
									+ Constants.NOTIFY_TIMES_COMSUMER1 + times.get() + 1);

							if (times == null) {
								// 更新失败原因
								billingOrderMapper.updateChargeOrder(bo.getId(), ChargeStatus.PROCESSING.getStatus(),
										"通知" + notifyTimes + "次-" + chargeResult.getMessage());
								countMap.put(orderId, new AtomicInteger(1));
								orderList.add(bo);
							} else if (times.getAndIncrement() >= Constants.NOTIFY_TIMES_COMSUMER2) {
								// 充值失败
								billingOrderMapper.updateChargeOrder(bo.getId(), ChargeStatus.FAILED.getStatus(), "通知"
										+ notifyTimes + "次-" + chargeResult.getMessage());
								countMap.remove(orderId);
							} else {
								// 更新失败原因
								billingOrderMapper.updateChargeOrder(bo.getId(), ChargeStatus.PROCESSING.getStatus(),
										"通知" + notifyTimes + "次-" + chargeResult.getMessage());
								orderList.add(bo);
							}
						}
					}
					TimeUnit.MICROSECONDS.sleep(100);
				}
				if (!orderList.isEmpty()) {
					ChargeHotelQueue.addThirdLevelObjects(orderList);
				}
				// 进入循环充值业务中的数据，每60分钟跑一次业务
				TimeUnit.MINUTES.sleep(60);
			} catch (InterruptedException e) {
				logger.info("Interrupted exit from ChargeOrderConsumer2-third level consumer!");
				break;
			} catch (Throwable t) {
				logger.error("ChargeHotelConsumer2 exception!", t);
			}
		}
	}

	public BillingHotelMapper getBillingOrderMapper() {
		return billingOrderMapper;
	}

	public void setBillingOrderMapper(BillingHotelMapper billingOrderMapper) {
		this.billingOrderMapper = billingOrderMapper;
	}

	public ChargeHotelManager getMobileChargeManager() {
		return mobileChargeManager;
	}

	public void setMobileChargeManager(ChargeHotelManager mobileChargeManager) {
		this.mobileChargeManager = mobileChargeManager;
	}

}
