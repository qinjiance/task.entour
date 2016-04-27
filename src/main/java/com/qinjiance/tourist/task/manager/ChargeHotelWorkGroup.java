/**
 * 
 */
package com.qinjiance.tourist.task.manager;

import java.util.concurrent.ExecutorService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinjiance.tourist.task.mapper.BillingHotelMapper;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月15日
 * 
 * @time 上午2:44:27
 * 
 * @desc 每个类别有一个工作组：包括一个生产线程，一个主线消费线程，一个工作队列
 * 
 */
public class ChargeHotelWorkGroup {
	private Logger logger = LoggerFactory.getLogger(ChargeHotelWorkGroup.class);

	private ExecutorService execs;
	private ChargeHotelProducer chargeHotelProducer;
	private ChargeHotelConsumer chargeHotelConsumer;
	private ChargeHotelQueue chargeHotelQueue;

	public ChargeHotelWorkGroup(BillingHotelMapper billingHotelMapper, ChargeHotelManager chargeManager,
			ExecutorService execs) {

		this.execs = execs;
		chargeHotelQueue = new ChargeHotelQueue();

		// 生产者
		chargeHotelProducer = new ChargeHotelProducer(billingHotelMapper, chargeHotelQueue);

		// 主线消费者
		chargeHotelConsumer = new ChargeHotelConsumer(billingHotelMapper, chargeManager, chargeHotelQueue);
	}

	public void start() {
		logger.info("start the hotel work group");
		execs.execute(chargeHotelProducer);
		execs.execute(chargeHotelConsumer);
	}

	public void stop() {
		logger.info("stop the hotel work group");
		chargeHotelProducer.stop();
		chargeHotelConsumer.stop();
	}
}
