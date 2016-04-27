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
 * @time 下午1:09:08
 * 
 * @desc 次消费队列：每1分钟跑一次，每个订单跑10次
 * 
 */
public class ChargeHotelConsumer1 implements Runnable {

	private Logger logger = LoggerFactory.getLogger(ChargeHotelConsumer1.class);

	private BillingHotelMapper billingHotelMapper;

	private ChargeHotelManager chargeHotelManager;

	// 计数器
	public static ConcurrentHashMap<Long, AtomicInteger> countMap = new ConcurrentHashMap<Long, AtomicInteger>();

	public ChargeHotelConsumer1(BillingHotelMapper billingOrderMapper, ChargeHotelManager chargeManager) {
		super();
		this.billingHotelMapper = billingOrderMapper;
		this.chargeHotelManager = chargeManager;
	}

	@Override
	public void run() {
		while (true) {
			try {
				List<BillingHotel> orderList = new ArrayList<BillingHotel>();
				while (!ChargeHotelQueue.minorIsEmpty()) {
					BillingHotel bo = ChargeHotelQueue.pollMinorObject();
					if (bo != null) {
						long orderId = bo.getId();
						ChargeResultVo chargeResult = chargeHotelManager.commonCharge(bo);
						if (chargeResult.getChargeResultEnum() == ChargeResultEnum.ORDER_IS_CHARGED) {
							StringBuilder sb = new StringBuilder();
							sb.append("charge hotel ").append(bo.getId()).append(" failed, order is already charged.");
							logger.info(sb.toString());
						} else if (chargeResult.getChargeResultEnum() == ChargeResultEnum.SUCCESS) {
							// 充值已成功
							int result = billingHotelMapper.updateChargeOrder(orderId,
									ChargeStatus.CHARGED.getStatus(), null);

							StringBuilder sb = new StringBuilder();
							sb.append("charge hotel ").append(bo.getId())
									.append(" success, update charge status, result=").append(result).append(".");
							logger.info(sb.toString());
						} else {
							AtomicInteger times = countMap.get(orderId);

							String notifyTimes = String.valueOf(times == null ? Constants.NOTIFY_TIMES_COMSUMER + 1
									: Constants.NOTIFY_TIMES_COMSUMER + times.get() + 1);

							// 更新失败原因
							billingHotelMapper.updateChargeOrder(bo.getId(), ChargeStatus.PROCESSING.getStatus(), "通知"
									+ notifyTimes + "次-" + chargeResult.getMessage());

							if (times == null) {
								countMap.put(orderId, new AtomicInteger(1));
								orderList.add(bo);
							} else if (times.getAndIncrement() >= Constants.NOTIFY_TIMES_COMSUMER1) {
								// remove from the second level queue,add to
								// third level queue
								ChargeHotelQueue.addThirdLevelObject(bo);
								countMap.remove(orderId);
							} else {
								orderList.add(bo);
							}
						}
					}
					TimeUnit.MICROSECONDS.sleep(100);
				}
				if (!orderList.isEmpty()) {
					ChargeHotelQueue.addMinorObjects(orderList);
				}
				// 进入循环充值业务中的数据，每分钟跑一次业务
				TimeUnit.MINUTES.sleep(1);
			} catch (InterruptedException e) {
				logger.info("Interrupted exit from ChargeOrderConsumer1-second leve consumer!");
				break;
			} catch (Throwable t) {
				logger.error("ChargeHotelConsumer1 exception!", t);
			}
		}
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

}
