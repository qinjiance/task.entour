/**
 * IActivityBookFlow_ActivityPreBook_WSFault_FaultMessage.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis2 version: 1.6.3  Built on : Jun 27, 2015 (11:17:49 BST)
 */
package com.qinjiance.tourist.task.support.tourico.activity;

public class IActivityBookFlow_ActivityPreBook_WSFault_FaultMessage extends java.lang.Exception {
	private static final long serialVersionUID = 1450024186414L;
	private com.qinjiance.tourist.task.support.tourico.activity.ActivityBookFlowStub.WSFaultE faultMessage;

	public IActivityBookFlow_ActivityPreBook_WSFault_FaultMessage() {
		super("IActivityBookFlow_ActivityPreBook_WSFault_FaultMessage");
	}

	public IActivityBookFlow_ActivityPreBook_WSFault_FaultMessage(java.lang.String s) {
		super(s);
	}

	public IActivityBookFlow_ActivityPreBook_WSFault_FaultMessage(java.lang.String s, java.lang.Throwable ex) {
		super(s, ex);
	}

	public IActivityBookFlow_ActivityPreBook_WSFault_FaultMessage(java.lang.Throwable cause) {
		super(cause);
	}

	public void setFaultMessage(com.qinjiance.tourist.task.support.tourico.activity.ActivityBookFlowStub.WSFaultE msg) {
		faultMessage = msg;
	}

	public com.qinjiance.tourist.task.support.tourico.activity.ActivityBookFlowStub.WSFaultE getFaultMessage() {
		return faultMessage;
	}
}
