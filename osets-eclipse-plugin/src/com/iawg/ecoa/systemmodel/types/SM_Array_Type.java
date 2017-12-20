/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

public class SM_Array_Type extends SM_Type {
	private SM_Type referencedType;
	private Integer maxNumber;

	public SM_Array_Type(String name, SM_Type rType, Integer maxNum, SM_Namespace namespace) {
		super(name, false, namespace);
		referencedType = rType;
		maxNumber = maxNum;
	}

	public SM_Type getType() {
		return referencedType;
	}

	public Integer getMaxNumber() {
		return maxNumber;
	}
}
