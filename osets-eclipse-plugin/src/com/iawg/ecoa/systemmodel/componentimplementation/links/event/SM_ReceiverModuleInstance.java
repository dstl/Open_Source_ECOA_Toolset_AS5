/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import java.math.BigInteger;
import java.util.List;

import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.SM_ActivatingFIFO;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;

public class SM_ReceiverModuleInstance extends SM_ActivatingFIFO implements SM_ReceiverInterface {

	private SM_ModuleInstance receiver;
	private SM_EventReceivedOp operation;

	public SM_ReceiverModuleInstance(SM_ModuleInstance receiver, SM_EventReceivedOp op, BigInteger bigInteger) {
		super(bigInteger);
		this.receiver = receiver;
		this.operation = op;
	}

	@Override
	public SM_ModuleInstance getReceiverInst() {
		return receiver;
	}

	@Override
	public SM_EventReceivedOp getReceiverOp() {
		return operation;
	}

	@Override
	public List<SM_OperationParameter> getInputs() {
		return operation.getInputs();
	}

}
