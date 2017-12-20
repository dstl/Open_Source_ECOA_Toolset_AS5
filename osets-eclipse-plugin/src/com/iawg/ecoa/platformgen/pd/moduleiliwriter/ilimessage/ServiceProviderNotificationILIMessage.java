/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage;

import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;

public class ServiceProviderNotificationILIMessage extends ILIMessage {
	private SM_ServiceInstance serviceInstance;

	public ServiceProviderNotificationILIMessage(int messageID, SM_ServiceInstance serviceInstance) {
		super(messageID);
		this.serviceInstance = serviceInstance;
	}

	public SM_ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

	public void setServiceInstance(SM_ServiceInstance serviceInstance) {
		this.serviceInstance = serviceInstance;
	}
}
