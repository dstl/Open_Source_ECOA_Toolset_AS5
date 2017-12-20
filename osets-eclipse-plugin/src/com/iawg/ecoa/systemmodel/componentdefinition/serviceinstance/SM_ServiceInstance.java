/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.servicedefinition.SM_ServiceInterface;

public abstract class SM_ServiceInstance extends SM_Object {
	private SM_ServiceInterface serviceInterface;

	public SM_ServiceInstance(String name, SM_ServiceInterface serviceInterface) {
		super(name);
		this.serviceInterface = serviceInterface;
	}

	public SM_ServiceInterface getServiceInterface() {
		return serviceInterface;
	}

}
