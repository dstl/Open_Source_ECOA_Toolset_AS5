/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.request;

import java.math.BigInteger;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.SM_ActivatingFIFO;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestReceivedOp;

public class SM_ServerModuleInstance extends SM_ActivatingFIFO implements SM_ServerInterface {

	private SM_ModuleInstance serverInst;
	private SM_RequestReceivedOp serverOperation;

	public SM_ServerModuleInstance(SM_ModuleInstance serverInst, SM_RequestReceivedOp op, BigInteger bigInteger) {
		super(bigInteger);
		this.serverInst = serverInst;
		this.serverOperation = op;
	}

	@Override
	public SM_ModuleInstance getServerInst() {
		return serverInst;
	}

	@Override
	public SM_RequestReceivedOp getServerOp() {
		return serverOperation;
	}
}
