/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage;

import java.util.ArrayList;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;

public class DynamicTriggerInstanceILI {
	private static final Logger LOGGER = LogManager.getLogger(DynamicTriggerInstanceILI.class);

	private SM_DynamicTriggerInstance trigInst;
	private ArrayList<ILIMessage> iliMessageList = new ArrayList<ILIMessage>();

	public DynamicTriggerInstanceILI(SM_DynamicTriggerInstance trigInst) {
		this.trigInst = trigInst;
	}

	public ILIMessage getILIForEventLink(SM_EventLink eventLink) {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof EventILIMessage) {
				if (((EventILIMessage) ili).getEventLink() == eventLink) {
					return ili;
				}
			}
		}
		LOGGER.info("Failed to find ILI message for event in : " + trigInst.getName());
		
		return null;
	}

	public ArrayList<ILIMessage> getILIMessageList() {
		return this.iliMessageList;
	}

	public SM_DynamicTriggerInstance getTrigInst() {
		return trigInst;
	}

	public void setILIMessageList(ArrayList<ILIMessage> iliMessageList) {
		this.iliMessageList = iliMessageList;
	}
}
