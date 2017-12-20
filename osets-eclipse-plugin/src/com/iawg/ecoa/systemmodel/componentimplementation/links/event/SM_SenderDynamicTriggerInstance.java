/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;

public class SM_SenderDynamicTriggerInstance implements SM_SenderInterface {

	private SM_DynamicTriggerInstance sender;
	private SM_EventSentOp operation = null;

	public SM_SenderDynamicTriggerInstance(SM_DynamicTriggerInstance trigger, SM_EventSentOp op) {
		this.sender = trigger;
		this.operation = op;

	}

	public SM_DynamicTriggerInstance getSenderInst() {
		return sender;
	}

	public SM_EventSentOp getSenderOp() {
		return operation;
	}

	@Override
	public String getSenderOpName() {
		return operation.getName();
	}
}
