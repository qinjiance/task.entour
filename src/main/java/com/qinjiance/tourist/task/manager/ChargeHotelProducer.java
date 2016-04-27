package com.qinjiance.tourist.task.manager;

import java.util.List;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinjiance.tourist.task.constants.ChargeStatus;
import com.qinjiance.tourist.task.mapper.BillingHotelMapper;
import com.qinjiance.tourist.task.model.BillingHotel;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月15日
 * 
 * @time 上午3:06:21
 * 
 * @desc 订单生产者：从数据库中加入订单
 * 
 */
public class ChargeHotelProducer implements Runnable {

	private Logger logger = LoggerFactory.getLogger(ChargeHotelProducer.class);

	private boolean stop = false;

	private BillingHotelMapper billingHotelMapper;
	// 工作队列的引用
	private ChargeHotelQueue chargeOrderQueue;

	public ChargeHotelProducer(BillingHotelMapper billingOrderMapper, ChargeHotelQueue chargeHotelQueue) {
		this.billingHotelMapper = billingOrderMapper;
		this.chargeOrderQueue = chargeHotelQueue;

		// 初始化时先把数据库中上次退出时订单状态为正在处理的订单装载到MobileChargeOrderData的数据队列中去
		List<BillingHotel> list = this.billingHotelMapper.getProcessingList();
		if (list != null && !list.isEmpty()) {
			// 加入主队列
			chargeHotelQueue.addMajorObjects(list);
		}
	}

	@Override
	public void run() {
		while (!stop) {
			try {
				// 取得最近100笔已完成支付的没有兑换的业务订单
				List<BillingHotel> list = billingHotelMapper.getUntreatedList(ChargeHotelQueue.PRODUCE_LIMIT);
				logger.info("scan billing hotels, result=" + (list == null ? 0 : list.size()));
				if (list == null || list.size() == 0) {
					TimeUnit.SECONDS.sleep(1);
				}
				for (BillingHotel order : list) {
					// 装载入MobileChargeOrderQueue的数据队列之前也更新其状态为处理中
					int ret = billingHotelMapper.updateChargeOrder(order.getId(), ChargeStatus.PROCESSING.getStatus(),
							null);
					// 状态更新成功，添加到队列中
					if (ret > 0) {
						chargeOrderQueue.addMajorObject(order);
					}
				}
			} catch (InterruptedException e) {
				logger.error("Interrupted exit from ChargeOrderProducer!");
				break;
			} catch (Throwable t) {
				logger.error("ChargeHotelProducer exception!", t);
			}
		}
	}

	public void stop() {
		this.stop = true;
	}
}
