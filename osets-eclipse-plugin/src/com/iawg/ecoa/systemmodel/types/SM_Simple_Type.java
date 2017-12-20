/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

public class SM_Simple_Type extends SM_Type {
	private SM_Type referencedType;
	private String minRange;
	private String maxRange;
	private String unit;

	public SM_Simple_Type(String name, Boolean simple, SM_Type rType, SM_Namespace namespace) {
		super(name, simple, namespace);
		referencedType = rType;
	}

	public SM_Type getType() {
		return referencedType;
	}

	public String getMinRange() {
		return minRange;
	}

	public void setMinRange(String minRange) {
		this.minRange = minRange;
	}

	public String getMaxRange() {
		return maxRange;
	}

	public void setMaxRange(String maxRange) {
		this.maxRange = maxRange;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}
}
