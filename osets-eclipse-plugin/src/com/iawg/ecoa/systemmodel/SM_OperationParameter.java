/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel;

import com.iawg.ecoa.systemmodel.types.SM_Type;

public class SM_OperationParameter extends SM_Object {

	protected SM_Type type;

	public SM_OperationParameter(String name, SM_Type smType) {
		super(name);
		type = smType;
	}

	public SM_Type getType() {
		return type;
	}

}
