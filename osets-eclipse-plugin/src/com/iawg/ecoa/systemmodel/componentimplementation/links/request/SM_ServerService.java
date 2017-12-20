/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.request;

import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;

public class SM_ServerService implements SM_ServerInterface {

	private SM_ServiceInstance serverInst;
	private SM_RRServiceOp serverOperation;

	public SM_ServerService(SM_ServiceInstance serverInst, SM_RRServiceOp op) {
		this.serverInst = serverInst;
		this.serverOperation = op;
	}

	public SM_ServiceInstance getServerInst() {
		return serverInst;
	}

	public SM_RRServiceOp getServerOp() {
		return serverOperation;
	}

}
