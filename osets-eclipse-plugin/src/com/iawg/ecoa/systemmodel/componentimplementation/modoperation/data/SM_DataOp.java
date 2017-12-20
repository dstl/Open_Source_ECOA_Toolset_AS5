/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.types.SM_Type;

public abstract class SM_DataOp extends SM_Object {

	private SM_Type dataType = null;

	public SM_DataOp(String name, SM_Type dataType) {
		super(name);
		this.dataType = dataType;
	}

	public SM_Type getData() {
		return dataType;
	}

}
