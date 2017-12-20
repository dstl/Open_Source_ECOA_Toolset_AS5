/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.util.HashMap;
import java.util.Map;

public class SM_ComponentImplementations {

	private Map<String, SM_ComponentImplementation> implementations = new HashMap<String, SM_ComponentImplementation>();

	public SM_ComponentImplementation getImplementationByName(String name) {
		return implementations.get(name);
	}

	public void addImplementation(SM_ComponentImplementation cii) {
		implementations.put(cii.getName(), cii);
	}

	public Boolean implementationExists(String name) {
		return implementations.get(name) != null;
	}

	public Map<String, SM_ComponentImplementation> getImplementations() {
		return implementations;
	}
}
