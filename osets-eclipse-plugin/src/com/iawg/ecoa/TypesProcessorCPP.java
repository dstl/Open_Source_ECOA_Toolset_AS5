/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa;

import com.iawg.ecoa.systemmodel.types.SM_Type;

/**
 * This class contains any static methods used for the conversion of
 * language-agnostic types specified in the XML to C language-specific types.
 * 
 */
public class TypesProcessorCPP {
	public static String convertParameterToCPP(SM_Type smType) {
		return smType.getNamespace().getName().replaceAll("\\.", "::") + "::" + smType.getName();
	}
}
