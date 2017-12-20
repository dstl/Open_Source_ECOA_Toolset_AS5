/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

public class SM_Base_Type extends SM_Type {
	private Integer size;

	public SM_Base_Type(String typeName, Integer bitSize, SM_Namespace namespace) {
		super(typeName, true, namespace);
		size = bitSize;
	}

	public Integer getSize() {
		return size;
	}
}
