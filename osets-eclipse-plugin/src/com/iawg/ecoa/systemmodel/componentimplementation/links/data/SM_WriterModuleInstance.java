/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.data;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataWrittenOp;

public class SM_WriterModuleInstance implements SM_WriterInterface {

	private SM_ModuleInstance writer;
	private SM_DataWrittenOp operation;

	public SM_WriterModuleInstance(SM_ModuleInstance cl, SM_DataWrittenOp op) {
		writer = cl;
		operation = op;
	}

	public SM_ModuleInstance getWriterInst() {
		return writer;
	}

	public SM_DataWrittenOp getWriterOp() {
		return operation;
	}

}
