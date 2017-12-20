/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.event;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_DynamicTriggerInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_TriggerInstance;

public class SM_EventLink {
	private static final Logger LOGGER = LogManager.getLogger(SM_EventLink.class);

	private List<SM_SenderInterface> senders = new ArrayList<SM_SenderInterface>();
	private List<SM_ReceiverInterface> receivers = new ArrayList<SM_ReceiverInterface>();
	private SM_ComponentImplementation componentImpl;

	public SM_EventLink(SM_ComponentImplementation compImpl) {
		componentImpl = compImpl;
		// Link this object to the component impl object
		compImpl.addEventLink(this);
	}

	public List<SM_SenderInterface> getSenders() {
		return senders;
	}

	public List<SM_ReceiverInterface> getReceivers() {
		return receivers;
	}

	public void addReceivers(ArrayList<SM_ReceiverInterface> receivers) {
		this.receivers = receivers;
	}

	public void addSenders(ArrayList<SM_SenderInterface> senders) {
		this.senders = senders;
	}

	public void addReceiver(SM_ReceiverInterface receiver) {
		this.receivers.add(receiver);
	}

	public void addSender(SM_SenderInterface sender) {
		this.senders.add(sender);
	}

	public List<SM_ModuleInstance> getReceiverModInstances() {
		List<SM_ModuleInstance> receiverModInsts = new ArrayList<SM_ModuleInstance>();

		for (SM_ReceiverInterface receiver : receivers) {
			if (receiver instanceof SM_ReceiverModuleInstance) {
				receiverModInsts.add((SM_ModuleInstance) receiver.getReceiverInst());
			}
		}

		return receiverModInsts;
	}

	public List<SM_DynamicTriggerInstance> getReceiverDynTrigInstances() {
		List<SM_DynamicTriggerInstance> receiverModInsts = new ArrayList<SM_DynamicTriggerInstance>();

		for (SM_ReceiverInterface receiver : receivers) {
			if (receiver instanceof SM_ReceiverDynamicTriggerInstance) {
				receiverModInsts.add((SM_DynamicTriggerInstance) receiver.getReceiverInst());
			}
		}

		return receiverModInsts;
	}

	public SM_SenderTriggerInstance getSenderTriggerInstance(SM_TriggerInstance triggerInstance) {
		// Get the trigger instance sender
		for (SM_SenderInterface senderInterface : senders) {
			if (senderInterface.getSenderInst() == triggerInstance) {
				return (SM_SenderTriggerInstance) senderInterface;
			}
		}
		LOGGER.info("Failed to find (sender) trigger instance " + triggerInstance.getName() + " in event link");
		
		return null;
	}

	public SM_SenderDynamicTriggerInstance getSenderTriggerInstance(SM_DynamicTriggerInstance dynamicTriggerInstance) {
		// Get the dynamic trigger instance sender
		for (SM_SenderInterface senderInterface : senders) {
			if (senderInterface.getSenderInst() == dynamicTriggerInstance) {
				return (SM_SenderDynamicTriggerInstance) senderInterface;
			}
		}
		// LOGGER.info("Failed to find (sender) dynamic trigger instance
		// " + dynamicTriggerInstance.getName() + " in event link");
		// 
		return null;
	}

	public SM_ComponentImplementation getComponentImpl() {
		return componentImpl;
	}

	public SM_ReceiverModuleInstance getReceiver(SM_ModuleInstance moduleInstance) {
		for (SM_ReceiverInterface receiver : receivers) {
			if (receiver.getReceiverInst() == moduleInstance) {
				return (SM_ReceiverModuleInstance) receiver;
			}
		}

		return null;
	}

	public SM_ReceiverDynamicTriggerInstance getReceiver(SM_DynamicTriggerInstance dynTrigInstance) {
		for (SM_ReceiverInterface receiver : receivers) {
			if (receiver.getReceiverInst() == dynTrigInstance) {
				return (SM_ReceiverDynamicTriggerInstance) receiver;
			}
		}

		return null;
	}

}
