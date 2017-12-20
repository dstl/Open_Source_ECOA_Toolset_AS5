/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.request;

import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

public class SM_ClientService implements SM_ClientInterface {

	private SM_ServiceInstance client;
	private SM_RRServiceOp operation;

	public SM_ClientService(SM_ServiceInstance cl, SM_RRServiceOp op) {
		this.client = cl;
		this.operation = op;
	}

	@Override
	public SM_ServiceInstance getClientInst() {
		return client;
	}

	@Override
	public SM_RRServiceOp getClientOp() {
		return operation;
	}

}
