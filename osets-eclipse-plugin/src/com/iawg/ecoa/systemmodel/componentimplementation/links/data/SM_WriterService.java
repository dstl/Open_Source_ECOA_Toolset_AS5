/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.data;

import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;

public class SM_WriterService implements SM_WriterInterface {

	private SM_ServiceInstance writer;
	private SM_DataServiceOp operation;

	public SM_WriterService(SM_ServiceInstance cl, SM_DataServiceOp op) {
		writer = cl;
		operation = op;
	}

	public SM_ServiceInstance getWriterInst() {
		return writer;
	}

	public SM_DataServiceOp getWriterOp() {
		return operation;
	}

}
