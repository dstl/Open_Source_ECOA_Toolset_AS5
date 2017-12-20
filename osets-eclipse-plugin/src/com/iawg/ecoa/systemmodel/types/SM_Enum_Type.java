/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

import java.util.ArrayList;
import java.util.List;

public class SM_Enum_Type extends SM_Type {

	private SM_Type referencedType;
	private List<SM_Enum_Value> enumValues = new ArrayList<SM_Enum_Value>();

	public SM_Enum_Type(String name, SM_Type rType, SM_Namespace namespace) {
		super(name, true, namespace);
		referencedType = rType;
	}

	public SM_Type getType() {
		return referencedType;
	}

	public void addEnumValue(String name, String valnum) {
		enumValues.add(new SM_Enum_Value(name, valnum));
	}

	public List<SM_Enum_Value> getEnumValues() {
		return enumValues;
	}

	public class SM_Enum_Value {
		private String name;
		private String valnum;

		public SM_Enum_Value(String name, String valnum) {
			this.name = name;
			this.valnum = valnum;
		}

		public String getName() {
			return name;
		}

		public String getValnum() {
			return valnum;
		}
	}

}
