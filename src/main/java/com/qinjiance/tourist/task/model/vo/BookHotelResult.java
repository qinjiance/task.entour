/**
 * 
 */
package com.qinjiance.tourist.task.model.vo;

import module.laohu.commons.model.BaseObject;

/**
 * @author "Jiance Qin"
 * 
 * @date 2016年5月3日
 * 
 * @time 下午6:13:47
 * 
 * @desc
 * 
 */
public class BookHotelResult extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1917389308642569590L;

	private Integer transNum;
	private Integer rgid;

	/**
	 * 
	 */
	public BookHotelResult() {
	}

	/**
	 * @param transNum
	 * @param rgid
	 */
	public BookHotelResult(Integer transNum, Integer rgid) {
		super();
		this.transNum = transNum;
		this.rgid = rgid;
	}

	/**
	 * @return the transNum
	 */
	public Integer getTransNum() {
		return transNum;
	}

	/**
	 * @param transNum
	 *            the transNum to set
	 */
	public void setTransNum(Integer transNum) {
		this.transNum = transNum;
	}

	/**
	 * @return the rgid
	 */
	public Integer getRgid() {
		return rgid;
	}

	/**
	 * @param rgid
	 *            the rgid to set
	 */
	public void setRgid(Integer rgid) {
		this.rgid = rgid;
	}

	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}

}
