/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

import java.util.List;

public class SM_Record_Type extends SM_Type {
	private List<nameAndType> referencedTypes;

	public SM_Record_Type(String name, List<nameAndType> rTypes, SM_Namespace namespace) {
		super(name, false, namespace);
		referencedTypes = rTypes;
	}

	public List<nameAndType> getType() {
		return referencedTypes;
	}
}
