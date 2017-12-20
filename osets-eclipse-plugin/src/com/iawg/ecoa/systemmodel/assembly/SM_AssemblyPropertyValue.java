/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.assembly;

import com.iawg.ecoa.systemmodel.SM_Object;

public class SM_AssemblyPropertyValue extends SM_Object {
	private String value;

	public SM_AssemblyPropertyValue(String name, String value) {
		super(name);

		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
