/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentdefinition;

import java.util.HashMap;
import java.util.Map;

public class SM_ComponentTypes {
	private Map<String, SM_ComponentType> componentTypes = new HashMap<String, SM_ComponentType>();

	public void addComponentType(SM_ComponentType comp) {
		componentTypes.put(comp.getName(), comp);
	}

	public SM_ComponentType getComponentType(String name) {
		return componentTypes.get(name);
	}

	public Boolean componentTypeExists(String name) {
		return componentTypes.get(name) != null;
	}

	public Boolean serviceExists(SM_ComponentType comp) {
		return componentTypes.get(comp.getName()) != null;
	}

}
