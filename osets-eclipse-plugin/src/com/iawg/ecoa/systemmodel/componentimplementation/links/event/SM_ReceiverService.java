/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import java.util.List;

import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;

public class SM_ReceiverService implements SM_ReceiverInterface {

	private SM_ServiceInstance receiver;
	private SM_EventServiceOp operation;

	public SM_ReceiverService(SM_ServiceInstance receiver, SM_EventServiceOp eventServiceOp) {
		this.receiver = receiver;
		this.operation = eventServiceOp;
	}

	public SM_ServiceInstance getReceiverInst() {
		return receiver;
	}

	public SM_EventServiceOp getReceiverOp() {
		return operation;
	}

	@Override
	public List<SM_OperationParameter> getInputs() {
		return operation.getInputs();
	}
}
