package com.qinjiance.tourist.task.manager;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.qinjiance.tourist.task.model.BillingHotel;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月15日
 * 
 * @time 上午2:53:35
 * 
 * @desc 工作队列：一个一次通知队列，一个10次通知队列（每分钟），一个24次通知队列（每小时）
 * 
 */
public class ChargeHotelQueue {

	/**
	 * 每次从数据库取得的未处理订单的数量
	 */
	public static int PRODUCE_LIMIT = 100;
	/**
	 * 主线消费队列
	 */
	public ConcurrentLinkedQueue<BillingHotel> majorQueue = new ConcurrentLinkedQueue<BillingHotel>();
	/**
	 * 发送小于10次的订单，每分钟通知一次的订单消费队列
	 */
	public static ConcurrentLinkedQueue<BillingHotel> minorQueue = new ConcurrentLinkedQueue<BillingHotel>();
	/**
	 * 发送24次，每小时通知一次
	 */
	public static ConcurrentLinkedQueue<BillingHotel> thirdLevelQueue = new ConcurrentLinkedQueue<BillingHotel>();

	/**
	 * 主线消费
	 * 
	 * @return
	 */
	public BillingHotel pollMajorObject() {
		return majorQueue.poll();
	}

	/**
	 * 辅线消费
	 * 
	 * @return
	 */
	public static BillingHotel pollMinorObject() {
		return minorQueue.poll();
	}

	/**
	 * 3级消费队列
	 * 
	 * @return
	 */
	public static BillingHotel pollThirdLevelObject() {
		return thirdLevelQueue.poll();
	}

	/**
	 * 辅线是否为空
	 * 
	 * @return
	 */
	public static boolean minorIsEmpty() {
		return minorQueue.isEmpty();
	}

	/**
	 * 3级队列是否为空
	 * 
	 * @return
	 */
	public static boolean ThirdLevelIsEmpty() {
		return thirdLevelQueue.isEmpty();
	}

	/**
	 * 主线队列生成，单个添加
	 * 
	 * @param BillingHotel
	 */
	public void addMajorObject(BillingHotel BillingHotel) {
		majorQueue.add(BillingHotel);
	}

	/**
	 * 辅线队列生产，单个添加
	 * 
	 * @param BillingHotel
	 */
	public static void addMinorObject(BillingHotel BillingHotel) {
		minorQueue.add(BillingHotel);
	}

	/**
	 * 3级队列生产，单个添加
	 * 
	 * @param BillingHotel
	 */
	public static void addThirdLevelObject(BillingHotel BillingHotel) {
		thirdLevelQueue.add(BillingHotel);
	}

	/**
	 * 主线队列生成,批量添加
	 * 
	 * @param BillingHotels
	 */
	public void addMajorObjects(List<BillingHotel> BillingHotels) {
		majorQueue.addAll(BillingHotels);
	}

	/**
	 * 辅线队列生产,批量添加
	 * 
	 * @param BillingHotels
	 */
	public static void addMinorObjects(List<BillingHotel> BillingHotels) {
		minorQueue.addAll(BillingHotels);
	}

	/**
	 * 3级队列生产,批量添加
	 * 
	 * @param BillingHotels
	 */
	public static void addThirdLevelObjects(List<BillingHotel> BillingHotels) {
		thirdLevelQueue.addAll(BillingHotels);
	}
}
