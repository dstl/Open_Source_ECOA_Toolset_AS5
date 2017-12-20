/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentTypeProperty;

public class SM_ModuleInstanceProperty {
	private String value;
	private SM_ModuleTypeProperty moduleTypeProperty;
	private SM_ModuleInstance modInstance;
	private SM_ComponentTypeProperty relatedCompTypeProperty = null;

	public SM_ModuleInstanceProperty(SM_ModuleTypeProperty moduleTypeProperty, String value, SM_ModuleInstance modInstance) {
		this.moduleTypeProperty = moduleTypeProperty;
		this.value = value;
		this.modInstance = modInstance;

		// relate this to a component property if required
		if (value.startsWith("$")) {
			this.relatedCompTypeProperty = modInstance.getComponentImplementation().getCompType().getPropertyByName(value.substring(1, value.length()));
		}

	}

	public String getValue() {
		return value;
	}

	public SM_ModuleInstance getModInstance() {
		return modInstance;
	}

	public SM_ModuleTypeProperty getModuleTypeProperty() {
		return moduleTypeProperty;
	}

	public SM_ComponentTypeProperty getRelatedCompTypeProperty() {
		return relatedCompTypeProperty;
	}

}
