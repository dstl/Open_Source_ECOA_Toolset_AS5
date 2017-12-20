/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel;

/*
 * Base System Model Class
 */
public abstract class SM_Object {
	protected String LF = System.lineSeparator();
	protected String name;

	public SM_Object(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

}
