package com.qinjiance.tourist.task.util;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	/** 
	 * 获取今天凌晨 
	 * @return 
	 */
	public static Date getMorning() {
		return getMorning(new Date());
	}
	
	public static Date getPreDayMorning() {
		Calendar c = Calendar.getInstance();
		c.roll(Calendar.DAY_OF_YEAR, -1);
		return getMorning(c.getTime());
	}

	/**
	 * 获取指定时间深夜
	 * @return
	 */
	public static Date getEvening(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 24);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	/** 
	 * 获取指定时间当天的凌晨 
	 * @param date 给定时间当天的凌晨 
	 * @return 
	 */
	public static Date getMorning(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		return c.getTime();
	}

	public static void main(String args[]) {
		System.out.println(getEvening(new Date()));
	}
}
