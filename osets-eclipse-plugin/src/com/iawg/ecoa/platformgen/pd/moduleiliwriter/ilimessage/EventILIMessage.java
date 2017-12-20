/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage;

import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;

public class EventILIMessage extends ILIMessage {

	private SM_EventLink eventLink;

	public EventILIMessage(int messageID, SM_EventLink eventLink) {
		super(messageID);
		this.eventLink = eventLink;
	}

	public SM_EventLink getEventLink() {
		return eventLink;
	}

	public void setEventLink(SM_EventLink eventLink) {
		this.eventLink = eventLink;
	}

}
