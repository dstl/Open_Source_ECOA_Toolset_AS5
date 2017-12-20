/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.request;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;

public class SM_ClientModuleInstance implements SM_ClientInterface {

	private SM_ModuleInstance client;
	private SM_RequestSentOp operation;

	public SM_ClientModuleInstance(SM_ModuleInstance cl, SM_RequestSentOp op) {
		this.client = cl;
		this.operation = op;
	}

	@Override
	public SM_ModuleInstance getClientInst() {
		return client;
	}

	@Override
	public SM_RequestSentOp getClientOp() {
		return operation;
	}

}
