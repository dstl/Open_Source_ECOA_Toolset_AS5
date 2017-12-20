/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;

public class SM_SenderModuleInstance implements SM_SenderInterface {

	private SM_ModuleInstance sender;
	private SM_EventSentOp operation = null;

	public SM_SenderModuleInstance(SM_ModuleInstance sender, SM_EventSentOp op) {
		this.sender = sender;
		this.operation = op;
	}

	public SM_ModuleInstance getSenderInst() {
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
