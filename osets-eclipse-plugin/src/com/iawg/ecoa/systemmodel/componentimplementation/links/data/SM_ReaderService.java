/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.data;

import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;

public class SM_ReaderService implements SM_ReaderInterface {

	private SM_ServiceInstance reader;
	private SM_DataServiceOp operation;

	public SM_ReaderService(SM_ServiceInstance reader, SM_DataServiceOp op) {
		this.reader = reader;
		this.operation = op;
	}

	@Override
	public SM_ServiceInstance getReaderInst() {
		return reader;
	}

	@Override
	public SM_DataServiceOp getReaderOp() {
		return operation;
	}

}
