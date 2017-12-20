/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.xmlprocessor;

public class XMLProc35bGenericAssembly {
	protected String getComponentName(String fn) {
		String sa[] = fn.split("/");

		if (sa.length == 2) {
			return sa[0];
		} else {
			return null;
		}
	}

	protected String getServiceName(String fn) {
		String sa[] = fn.split("/");

		// The last sub-string
		String name = sa[sa.length - 1];
		return name;
	}

}
