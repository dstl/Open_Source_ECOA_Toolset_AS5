/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage;

import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;

public class LifecycleILIMessage extends ILIMessage {

	public enum ILIMessageType {
		START_MODULE, STOP_MODULE, INITIALIZE_MODULE, SHUTDOWN_MODULE, LIFECYCLE_NOTIFICATION;
	}

	private ILIMessageType messageType;

	private SM_EventLink eventLink;

	public LifecycleILIMessage(ILIMessageType messageType, int messageID) {
		super(messageID);
		this.setMessageType(messageType);
	}

	public SM_EventLink getEventLink() {
		return eventLink;
	}

	public ILIMessageType getMessageType() {
		return messageType;
	}

	public void setEventLink(SM_EventLink eventLink) {
		this.eventLink = eventLink;
	}

	public void setMessageType(ILIMessageType messageType) {
		this.messageType = messageType;
	}

}
