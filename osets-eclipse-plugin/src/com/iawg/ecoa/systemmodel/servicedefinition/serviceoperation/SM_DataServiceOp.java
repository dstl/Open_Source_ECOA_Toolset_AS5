/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;

public class SM_DataServiceOp extends SM_Object {
	private SM_OperationParameter versionedData;

	public SM_DataServiceOp(String name, SM_OperationParameter param) {
		super(name);
		this.versionedData = param;
	}

	public SM_DataServiceOp(SM_DataServiceOp dataServiceOp) {
		super(dataServiceOp.getName());
		this.versionedData = dataServiceOp.getData();
	}

	public SM_OperationParameter getData() {
		return versionedData;
	}

}
