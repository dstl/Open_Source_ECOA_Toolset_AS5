/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage;

import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;

public class DataILIMessage extends ILIMessage {
	private SM_DataLink dataLink;

	public DataILIMessage(int messageID, SM_DataLink dataLink) {
		super(messageID);
		this.dataLink = dataLink;
	}

	public SM_DataLink getDataLink() {
		return dataLink;
	}

	public void setDataLink(SM_DataLink dataLink) {
		this.dataLink = dataLink;
	}
}
