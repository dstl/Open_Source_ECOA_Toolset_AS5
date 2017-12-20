/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SM_Constant_Type extends SM_Type {
	private static final Logger LOGGER = LogManager.getLogger(SM_Constant_Type.class);

	private double value;
	private SM_Type referencedType;

	public SM_Constant_Type(String name, SM_Type referencedType, String value, SM_Namespace namespace) {
		super(name, true, namespace);
		this.referencedType = referencedType;

		try {
			this.value = Double.parseDouble(value);
		} catch (NumberFormatException e) {
			LOGGER.info("System Model does not currently handle constant values which are defined using a constant reference - constant type name: " + name);
			
		}
	}

	public SM_Constant_Type(String name, SM_Type referencedType, double value, SM_Namespace namespace) {
		super(name, true, namespace);
		this.referencedType = referencedType;
		this.value = value;
	}

	public SM_Type getType() {
		return referencedType;
	}

	public double getValue() {
		return value;
	}

}
