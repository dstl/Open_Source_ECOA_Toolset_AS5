/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;

public class SM_EventOp extends SM_Object {
	protected List<SM_OperationParameter> inputParameterTypes = new ArrayList<SM_OperationParameter>();

	public SM_EventOp(String name, List<SM_OperationParameter> inList) {
		super(name);
		inputParameterTypes = inList;
	}

	public List<SM_OperationParameter> getInputs() {
		return inputParameterTypes;
	}

}
