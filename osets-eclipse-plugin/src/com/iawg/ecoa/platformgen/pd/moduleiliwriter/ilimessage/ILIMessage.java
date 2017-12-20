/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage;

import java.util.ArrayList;

import com.iawg.ecoa.systemmodel.SM_OperationParameter;

public abstract class ILIMessage {
	private int messageID;
	private ArrayList<SM_OperationParameter> params = new ArrayList<SM_OperationParameter>();

	public ILIMessage(int messageID) {
		this.messageID = messageID;
	}

	public void addParam(SM_OperationParameter param) {
		params.add(param);
	}

	public int getMessageID() {
		return messageID;
	}

	public ArrayList<SM_OperationParameter> getParams() {
		return params;
	}

	public void setMessageID(int messageID) {
		this.messageID = messageID;
	}

}
