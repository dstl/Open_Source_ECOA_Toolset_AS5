/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;

public class SM_DynamicTriggerInstance extends SM_Object {
	private List<SM_EventLink> eventLinks = new ArrayList<SM_EventLink>();
	private SM_ComponentImplementation componentImplementation;
	private BigInteger size;
	private double minDelay;
	private double maxDelay;
	private SM_DynamicTriggerType moduleType;

	public SM_DynamicTriggerInstance(String name, SM_DynamicTriggerType type, BigInteger size, double delayMin, double delayMax, SM_ComponentImplementation compImpl) {
		super(name);
		this.moduleType = type;
		this.componentImplementation = compImpl;
		this.size = size;
		this.minDelay = delayMin;
		this.maxDelay = delayMax;
	}

	public SM_DynamicTriggerType getModuleType() {
		return moduleType;
	}

	public BigInteger getSize() {
		return size;
	}

	public double getDelayMax() {
		return maxDelay;
	}

	public double getDelayMin() {
		return minDelay;
	}

	public void addLink(SM_EventLink eventLink) {
		eventLinks.add(eventLink);
	}

	public List<SM_EventLink> getLinks() {
		return eventLinks;
	}

	public List<SM_EventLink> getLinksForReceiverOp(SM_EventReceivedOp evRx) {
		List<SM_EventLink> eventLinksForEvent = new ArrayList<SM_EventLink>();

		for (SM_EventLink evLink : eventLinks) {
			// Check if we are a receiver
			for (SM_ReceiverInterface receiverInterface : evLink.getReceivers()) {
				if (receiverInterface.getReceiverInst() == this && receiverInterface.getReceiverOp() == evRx) {
					eventLinksForEvent.add(evLink);
				}
			}
		}
		return eventLinksForEvent;
	}

	public List<SM_EventLink> getLinksForSenderOp(SM_EventSentOp evSent) {
		List<SM_EventLink> eventLinksForEvent = new ArrayList<SM_EventLink>();

		for (SM_EventLink evLink : eventLinks) {
			// Check if we are a sender
			for (SM_SenderInterface senderInterface : evLink.getSenders()) {
				if (senderInterface.getSenderInst() == this && senderInterface.getSenderOpName().equals(evSent.getName())) {
					eventLinksForEvent.add(evLink);
				}
			}
		}

		return eventLinksForEvent;
	}

	public void setComponentImplementation(SM_ComponentImplementation componentImplementation) {
		this.componentImplementation = componentImplementation;
	}

	public SM_ComponentImplementation getComponentImplementation() {
		return componentImplementation;
	}

}
