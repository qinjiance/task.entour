/**
 * 
 */
package com.qinjiance.tourist.task;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月14日
 * 
 * @time 下午2:19:14
 * 
 * @desc
 * 
 */
public class BeanContainer {

	private static final Logger logger = LoggerFactory.getLogger(BeanContainer.class);
	/**
	 * single instance
	 */
	private static ApplicationContext context = null;

	// load spring configuration
	public static ApplicationContext getAppContext() {
		if (null == context) {
			synchronized (BeanContainer.class) {
				if (null == context) {
					context = new FileSystemXmlApplicationContext(new String[] { "config/applicationContext-db.xml",
							"config/applicationContext-remote.xml" });
					logger.info("Initialize FileSystemXmlApplicationContext with config file: applicationContext-db.xml,applicationContext-remote.xml");
				}
			}
		}
		return context;
	}

	public static Object getBean(String beanName) {
		return getAppContext().getBean(beanName);
	}

}
