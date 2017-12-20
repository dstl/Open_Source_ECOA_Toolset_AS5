/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data;

import com.iawg.ecoa.systemmodel.types.SM_Type;

public class SM_DataReadOp extends SM_DataOp {

	private boolean isNotifying = false;

	public SM_DataReadOp(String name, SM_Type dataType, boolean isNotifying) {
		super(name, dataType);
		this.isNotifying = isNotifying;
	}

	public boolean getIsNotifying() {
		return isNotifying;
	}

}
