/**
 * 
 */
package com.qinjiance.tourist.task.manager.exception;

/**
 * @author "Jiance Qin"
 * 
 * @date 2014年9月14日
 * 
 * @time 下午7:25:16
 * 
 * @desc
 * 
 */
public class TaskManagerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8453886717107985073L;

	/**
	 * 
	 */
	public TaskManagerException() {
	}

	/**
	 * @param message
	 */
	public TaskManagerException(String message) {
		super(message);
	}

	/**
	 * @param cause
	 */
	public TaskManagerException(Throwable cause) {
		super(cause);
	}

	/**
	 * @param message
	 * @param cause
	 */
	public TaskManagerException(String message, Throwable cause) {
		super(message, cause);
	}

}
