/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage;

import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;

public class ResponseILIMessage extends ILIMessage {

	private SM_RequestLink requestLink;

	public ResponseILIMessage(int messageID, SM_RequestLink requestLink) {
		super(messageID);
		this.setRequestLink(requestLink);
	}

	public SM_RequestLink getRequestLink() {
		return requestLink;
	}

	public void setRequestLink(SM_RequestLink requestLink) {
		this.requestLink = requestLink;
	}

}
