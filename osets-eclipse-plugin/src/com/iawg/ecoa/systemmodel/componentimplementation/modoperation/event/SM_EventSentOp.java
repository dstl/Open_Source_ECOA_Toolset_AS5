/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event;

import java.util.List;

import com.iawg.ecoa.systemmodel.SM_OperationParameter;

public class SM_EventSentOp extends SM_EventOp {

	public SM_EventSentOp(String name, List<SM_OperationParameter> inList) {
		super(name, inList);
	}

}
