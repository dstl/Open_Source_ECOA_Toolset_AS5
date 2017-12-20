/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.qos;

import java.util.HashMap;
import java.util.Map;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

public class SM_QualityOfService extends SM_Object {

	private Map<SM_RRServiceOp, SM_RequestResponseQoS> requestResponseQoSMap = new HashMap<SM_RRServiceOp, SM_RequestResponseQoS>();
	private SM_ServiceInstance referencedServiceInstance;

	public SM_QualityOfService(String name, SM_ServiceInstance serviceInst) {
		super(name);

		this.referencedServiceInstance = serviceInst;
	}

	public Map<SM_RRServiceOp, SM_RequestResponseQoS> getRequestResponseQoSMap() {
		return requestResponseQoSMap;
	}

	public SM_ServiceInstance getReferencedServiceInstance() {
		return referencedServiceInstance;
	}

}
