/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;

public class SM_TriggerInstance extends SM_Object {
	private List<SM_EventLink> eventLinks = new ArrayList<SM_EventLink>();
	private SM_ComponentImplementation componentImplementation;

	public SM_TriggerInstance(String name, SM_ComponentImplementation componentImplementation) {
		super(name);
		this.componentImplementation = componentImplementation;
	}

	public void addEventLink(SM_EventLink eventLink) {
		eventLinks.add(eventLink);
	}

	public List<SM_EventLink> getEventLinks() {
		return eventLinks;
	}

	public void setComponentImplementation(SM_ComponentImplementation componentImplementation) {
		this.componentImplementation = componentImplementation;
	}

	public SM_ComponentImplementation getComponentImplementation() {
		return componentImplementation;
	}
}
