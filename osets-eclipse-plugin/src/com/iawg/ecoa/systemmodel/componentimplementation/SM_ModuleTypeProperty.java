/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.types.SM_Type;

public class SM_ModuleTypeProperty extends SM_Object {
	SM_Type type;
	SM_ModuleType moduleType;

	public SM_ModuleTypeProperty(String name, SM_Type type, SM_ModuleType moduleType) {
		super(name);
		this.type = type;
		this.moduleType = moduleType;
	}

	public SM_Type getType() {
		return type;
	}

	public SM_ModuleType getModuleType() {
		return moduleType;
	}
}
