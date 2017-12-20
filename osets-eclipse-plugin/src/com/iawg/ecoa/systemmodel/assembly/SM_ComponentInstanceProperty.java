/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.assembly;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentTypeProperty;

public class SM_ComponentInstanceProperty extends SM_Object {
	private String value;
	private SM_AssemblyPropertyValue referencedAssemblyPropertyVal;
	private SM_ComponentTypeProperty componentTypeProperty;
	private SM_ComponentInstance compInst;

	public SM_ComponentInstanceProperty(SM_ComponentTypeProperty componentTypeProperty, String value, SM_ComponentInstance compInst) {
		super(componentTypeProperty.getName());
		this.componentTypeProperty = componentTypeProperty;
		this.value = value;
		this.compInst = compInst;
	}

	public SM_ComponentInstanceProperty(SM_ComponentTypeProperty componentTypeProperty, String assemblyPropName, SM_ComponentInstance compInst, SM_AssemblySchema assembly) {
		super(componentTypeProperty.getName());
		this.componentTypeProperty = componentTypeProperty;
		this.compInst = compInst;

		// Set the reference to assembly level PINFO (Note: need to take off the
		// leading $).
		referencedAssemblyPropertyVal = assembly.getPropertyValueByName(assemblyPropName.substring(1, assemblyPropName.length()));
	}

	public SM_AssemblyPropertyValue getReferencedAssemblyPropertyVal() {
		return referencedAssemblyPropertyVal;
	}

	public String getValue() {
		// Return the referenced assembly property value if it has been
		// assigned, otherwise return the literal.
		if (referencedAssemblyPropertyVal != null) {
			return referencedAssemblyPropertyVal.getValue();
		} else {
			return value;
		}
	}

	public SM_ComponentInstance getCompInst() {
		return compInst;
	}

	public SM_ComponentTypeProperty getComponentTypeProperty() {
		return componentTypeProperty;
	}

}
