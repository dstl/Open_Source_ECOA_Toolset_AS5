/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.io.File;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentTypeProperty;

public class SM_PinfoValue extends SM_Object {

	private SM_Pinfo moduleTypePinfo;
	private File pinfoFile;
	private SM_ComponentTypeProperty relatedCompTypeProperty;

	public SM_PinfoValue(SM_Pinfo moduleTypePinfo, String value, SM_ModuleInstance modInstance) {
		super(moduleTypePinfo.getName());
		this.moduleTypePinfo = moduleTypePinfo;

		// relate this to a component property if required
		if (value.startsWith("$")) {
			this.relatedCompTypeProperty = modInstance.getComponentImplementation().getCompType().getPropertyByName(value.substring(1, value.length()));
		} else {
			this.pinfoFile = new File(value);
		}
	}

	public SM_Pinfo getModuleTypePinfo() {
		return moduleTypePinfo;
	}

	public File getPinfoFile() {
		return pinfoFile;
	}

	public SM_ComponentTypeProperty getRelatedCompTypeProperty() {
		return relatedCompTypeProperty;
	}

}
