/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.math.BigInteger;

import com.iawg.ecoa.systemmodel.SM_Object;

public class SM_Pinfo extends SM_Object {

	private boolean isWriteable;
	private boolean isPrivate;
	private BigInteger capacity;

	public SM_Pinfo(String name, boolean isWriteable, BigInteger capacity, boolean isPrivate) {
		super(name);

		this.isWriteable = isWriteable;
		this.isPrivate = isPrivate;
		this.capacity = capacity;
	}

	public boolean isWriteable() {
		return isWriteable;
	}

	public BigInteger getCapacity() {
		return capacity;
	}

	public boolean isPrivate() {
		return isPrivate;
	}
}
