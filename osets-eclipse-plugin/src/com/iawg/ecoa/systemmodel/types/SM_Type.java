/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.types;

import com.iawg.ecoa.systemmodel.SM_Object;

public class SM_Type extends SM_Object {
	private Boolean isSimple;
	private SM_Namespace namespace;

	public SM_Type(String typeName, Boolean simple, SM_Namespace namespace) {
		super(typeName);
		isSimple = simple;
		this.namespace = namespace;
	}

	public Boolean isSimple() {
		return isSimple;
	}

	public SM_Namespace getNamespace() {
		return namespace;
	}

	public static class nameAndType extends SM_Object {
		private SM_Type referencedType;

		public nameAndType(String name) {
			super(name);
		}

		public SM_Type getReferencedType() {
			return referencedType;
		}

		public void setReferencedType(SM_Type referencedType) {
			this.referencedType = referencedType;
		}
	}

}
