/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import java.util.List;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;

public interface SM_ReceiverInterface {
	public SM_Object getReceiverInst();

	public SM_Object getReceiverOp();

	public List<SM_OperationParameter> getInputs();
}
