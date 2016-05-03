/**
 * 
 */
package com.qinjiance.tourist.task.manager;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qinjiance.tourist.task.mapper.BillingHotelMapper;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月15日
 * 
 * @time 上午2:03:53
 * 
 * @desc 充值工作组，负责根据数据库中的订单列表，添加和删除工作组
 * 
 */
public class ChargeThreadManager implements Runnable {

	private Logger logger = LoggerFactory.getLogger(ChargeThreadManager.class);

	private ExecutorService execs;

	private ChargeHotelManager chargeHotelManager;

	private static ConcurrentHashMap<Integer, ChargeHotelWorkGroup> typeWorkGroup = new ConcurrentHashMap<Integer, ChargeHotelWorkGroup>();

	private BillingHotelMapper billingHotelMapper;

	public ChargeThreadManager(BillingHotelMapper billingHotelMapper, ChargeHotelManager chargeHotelManager,
			ExecutorService execs) {
		this.billingHotelMapper = billingHotelMapper;
		this.chargeHotelManager = chargeHotelManager;
		this.execs = execs;
	}

	/**
	 * scan the general_app table, compare the apps with thread container. close
	 * the thread which does not exist in table; create new thread for apps that
	 * insert into table recently.
	 */
	@Override
	public void run() {
		logger.info("start init all the threads for types");
		while (true) {
			try {
				createNewWorkGroup();

				TimeUnit.MINUTES.sleep(20);
			} catch (InterruptedException e) {
				logger.error("the thread is interrupted ", e);
			}
		}
	}

	/**
	 * create work group for new apps
	 * 
	 * @param appList
	 */
	public void createNewWorkGroup() {
		ChargeHotelWorkGroup workGroup = null;
		for (int i = 1; i <= 3; i++) {
			if (!typeWorkGroup.containsKey(i)) {
				if (i == 1) {
					workGroup = new ChargeHotelWorkGroup(billingHotelMapper, chargeHotelManager, execs);
				} else if (i == 2) {

				} else {

				}
				typeWorkGroup.put(i, workGroup);
				workGroup.start();
				logger.info("create work group for type: " + i);
			}
		}
	}
}
