/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.uid;

import com.iawg.ecoa.systemmodel.SM_Object;

public class SM_UIDServiceOp {
	private Integer uid;
	private SM_Object operation;
	private String uidString;

	public SM_UIDServiceOp(Integer uid, SM_Object operation, String uidString) {
		this.uid = uid;
		this.operation = operation;
		this.uidString = uidString;
	}

	public Integer getID() {
		return uid;
	}

	public SM_Object getOperation() {
		return operation;
	}

	public String getUIDString() {
		return uidString;
	}

	public String getUIDDefString() {
		// Return the definition of the service operation UID which can be used
		// in code.

		String uidDefString = uidString;

		uidDefString = uidDefString.replaceAll("/", "_");
		uidDefString = uidDefString.replaceAll(":", "__");

		uidDefString += "__UID";

		return uidDefString;
	}

}
