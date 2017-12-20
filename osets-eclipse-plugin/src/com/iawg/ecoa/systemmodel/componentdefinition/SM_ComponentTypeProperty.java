/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentdefinition;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.types.SM_Type;

public class SM_ComponentTypeProperty extends SM_Object {
	private SM_Type type;
	private SM_ComponentType compType;

	public SM_ComponentTypeProperty(String name, SM_Type type, SM_ComponentType compType) {
		super(name);
		this.type = type;
		this.compType = compType;
	}

	public SM_Type getType() {
		return type;
	}

	public SM_ComponentType getCompType() {
		return compType;
	}

}
