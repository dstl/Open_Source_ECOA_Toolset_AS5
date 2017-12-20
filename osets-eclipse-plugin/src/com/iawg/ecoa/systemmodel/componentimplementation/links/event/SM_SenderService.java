/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;

public class SM_SenderService implements SM_SenderInterface {

	private SM_ServiceInstance sender;
	private SM_EventServiceOp operation = null;

	public SM_SenderService(SM_ServiceInstance sender, SM_EventServiceOp op) {
		this.sender = sender;
		this.operation = op;
	}

	public SM_ServiceInstance getSenderInst() {
		return sender;
	}

	public SM_EventServiceOp getSenderOp() {
		return operation;
	}

	@Override
	public String getSenderOpName() {
		return operation.getName();
	}
}
