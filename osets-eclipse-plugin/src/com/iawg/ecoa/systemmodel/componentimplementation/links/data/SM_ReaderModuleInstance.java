/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.data;

import java.math.BigInteger;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.SM_ActivatingFIFO;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataOp;

public class SM_ReaderModuleInstance extends SM_ActivatingFIFO implements SM_ReaderInterface {
	private SM_ModuleInstance reader;
	private SM_DataOp operation;

	public SM_ReaderModuleInstance(SM_ModuleInstance reader, SM_DataOp op, BigInteger bigInteger) {
		super(bigInteger);
		this.reader = reader;
		this.operation = op;
	}

	@Override
	public SM_ModuleInstance getReaderInst() {
		return reader;
	}

	@Override
	public SM_DataOp getReaderOp() {
		return operation;
	}

}
