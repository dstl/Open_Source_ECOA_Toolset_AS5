/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.uid;

import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;

public class SM_UIDServiceInst {
	private Integer uid;
	private SM_ServiceInstance serviceInstance;

	public SM_UIDServiceInst(Integer uid, SM_ServiceInstance si) {
		this.uid = uid;
		this.serviceInstance = si;
	}

	public Integer getID() {
		return uid;
	}

	public SM_ServiceInstance getServiceInstance() {
		return serviceInstance;
	}

}
