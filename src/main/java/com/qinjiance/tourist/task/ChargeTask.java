package com.qinjiance.tourist.task;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.qos.logback.core.joran.spi.JoranException;

import com.qinjiance.tourist.task.manager.ChargeHotelConsumer1;
import com.qinjiance.tourist.task.manager.ChargeHotelConsumer2;
import com.qinjiance.tourist.task.manager.ChargeHotelManager;
import com.qinjiance.tourist.task.manager.ChargeThreadManager;
import com.qinjiance.tourist.task.mapper.BillingHotelMapper;
import com.qinjiance.tourist.task.util.LogBackConfigLoader;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月15日
 * 
 * @time 上午2:03:43
 * 
 * @desc
 * 
 */
public class ChargeTask {

	private static final Logger logger = LoggerFactory.getLogger(ChargeTask.class);

	private static ExecutorService execs = Executors.newCachedThreadPool();

	static {
		try {
			LogBackConfigLoader.load("logback.xml");
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JoranException e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {

		BillingHotelMapper billingHotelMapper = (BillingHotelMapper) BeanContainer.getBean("billingHotelMapper");
		ChargeHotelManager chargeHotelManager = (ChargeHotelManager) BeanContainer.getBean("chargeHotelManager");
		
		logger.info("init bean success...");

		// start the scan app thread
		ChargeThreadManager threadManager = new ChargeThreadManager(billingHotelMapper, chargeHotelManager, execs);
		execs.execute(threadManager);

		// second level queue
		ChargeHotelConsumer1 mcoConsumer1 = new ChargeHotelConsumer1(billingHotelMapper, chargeHotelManager);

		ChargeHotelConsumer2 mcoConsumer2 = new ChargeHotelConsumer2(billingHotelMapper, chargeHotelManager);

		execs.execute(mcoConsumer1);
		execs.execute(mcoConsumer2);

		// RemoteServiceThread remoteService = new
		// RemoteServiceThread(billingOrderMapper, chargeManager);
		// execs.execute(remoteService);

		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
			public void run() {
				if (execs != null && !execs.isShutdown()) {
					execs.shutdownNow();
					logger.info("shutown the execute");
				}
				logger.info("shutdown the running pool");
			}
		}));

	}
}
