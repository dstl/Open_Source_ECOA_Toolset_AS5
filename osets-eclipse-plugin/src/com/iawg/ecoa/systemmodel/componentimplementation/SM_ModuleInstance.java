/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_ReaderModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_ReceiverModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ClientModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_ServerModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataWrittenOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;

public class SM_ModuleInstance extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_ModuleInstance.class);

	private SM_ModuleImpl implementation;
	private SM_ModuleType moduleType;
	private List<SM_EventLink> eventLinks = new ArrayList<SM_EventLink>();
	private List<SM_RequestLink> requestLinks = new ArrayList<SM_RequestLink>();
	private List<SM_DataLink> dataLinks = new ArrayList<SM_DataLink>();
	private SM_ComponentImplementation componentImplementation;
	private List<SM_ModuleInstanceProperty> modInstProperties = new ArrayList<SM_ModuleInstanceProperty>();
	private List<SM_PinfoValue> publicPinfos = new ArrayList<SM_PinfoValue>();
	private List<SM_PinfoValue> privatePinfos = new ArrayList<SM_PinfoValue>();

	public SM_ModuleInstance(String name, SM_ModuleType type, SM_ModuleImpl imp, SM_ComponentImplementation compImpl) {
		super(name);
		moduleType = type;
		implementation = imp;
		componentImplementation = compImpl;
	}

	public SM_ModuleType getModuleType() {
		return moduleType;
	}

	public SM_ModuleImpl getImplementation() {
		return implementation;
	}

	public SM_ComponentImplementation getComponentImplementation() {
		return componentImplementation;
	}

	public void addEventLink(SM_EventLink evLink) {
		// add if not already added.
		if (!eventLinks.contains(evLink)) {
			eventLinks.add(evLink);
		}
	}

	public List<SM_EventLink> getEventLinks() {
		return eventLinks;
	}

	public void addRequestLink(SM_RequestLink rqLink) {
		// add if not already added.
		if (!requestLinks.contains(rqLink)) {
			requestLinks.add(rqLink);
		}
	}

	public List<SM_RequestLink> getRequestLinks() {
		return requestLinks;
	}

	public void addDataLink(SM_DataLink dataLink) {
		// add if not already added.
		if (!dataLinks.contains(dataLink)) {
			dataLinks.add(dataLink);
		}
	}

	public List<SM_DataLink> getDataLinks() {
		return dataLinks;
	}

	public List<SM_EventLink> getEventLinksForReceiverOp(SM_EventReceivedOp evRx) {
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

	public List<SM_EventLink> getEventLinksForSenderOp(SM_EventSentOp evSent) {
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

	public SM_RequestLink getRequestLinkForClientOp(SM_RequestSentOp rqSent) {
		SM_RequestLink requestLinkForRequest = null;

		for (SM_RequestLink rqLink : requestLinks) {
			// Check if we are a client
			for (SM_ClientInterface clientInterface : rqLink.getClients()) {
				if (clientInterface.getClientInst() == this && clientInterface.getClientOp() == rqSent) {
					requestLinkForRequest = rqLink;
				}
			}
		}

		return requestLinkForRequest;
	}

	public List<SM_RequestLink> getRequestLinksForServerOp(SM_RequestReceivedOp requestReceivedOp) {
		List<SM_RequestLink> requestLinksForRequest = new ArrayList<SM_RequestLink>();

		for (SM_RequestLink rqLink : requestLinks) {
			// Check if we are the server
			if (rqLink.getServer().getServerInst() == this && rqLink.getServer().getServerOp() == requestReceivedOp) {
				requestLinksForRequest.add(rqLink);
			}
		}

		return requestLinksForRequest;
	}

	public List<SM_DataLink> getDataLinksForReaderOp(SM_DataReadOp dataReadOp) {
		List<SM_DataLink> dataLinksForData = new ArrayList<SM_DataLink>();

		for (SM_DataLink dataLink : dataLinks) {
			// Check if we are a reader
			for (SM_ReaderInterface readerInterface : dataLink.getReaders()) {
				if (readerInterface.getReaderInst() == this && readerInterface.getReaderOp() == dataReadOp) {
					dataLinksForData.add(dataLink);
				}
			}
		}

		return dataLinksForData;
	}

	public List<SM_DataLink> getDataLinksForWriterOp(SM_DataWrittenOp dataWrittenOp) {
		List<SM_DataLink> dataLinksForData = new ArrayList<SM_DataLink>();

		for (SM_DataLink dataLink : dataLinks) {
			// Check if we are a writer
			if (dataLink.getWriter().getWriterInst() == this && dataLink.getWriter().getWriterOp() == dataWrittenOp) {
				dataLinksForData.add(dataLink);
			}
		}

		return dataLinksForData;
	}

	public void addModInstPropertyValue(SM_ModuleInstanceProperty modInstanceProperty) {
		modInstProperties.add(modInstanceProperty);
	}

	public List<SM_ModuleInstanceProperty> getModInstPropertyValues() {
		return modInstProperties;
	}

	public SM_ModuleInstanceProperty getModInstPropertyByName(String name) {
		for (SM_ModuleInstanceProperty modInstProperty : modInstProperties) {
			if (modInstProperty.getModuleTypeProperty().getName().equals(name)) {
				return modInstProperty;
			}
		}

		LOGGER.info(name + " module instance property not found in module instance - " + this.name);
		
		return null;

	}

	public List<SM_ReceiverModuleInstance> getEventLinksAsReceiver() {
		List<SM_ReceiverModuleInstance> receiverOps = new ArrayList<SM_ReceiverModuleInstance>();

		for (SM_EventLink evLink : eventLinks) {
			// Check if we are a receiver
			for (SM_ReceiverInterface receiverInterface : evLink.getReceivers()) {
				if (receiverInterface.getReceiverInst() == this) {
					receiverOps.add((SM_ReceiverModuleInstance) receiverInterface);
				}
			}
		}

		return receiverOps;
	}

	public List<SM_ReaderModuleInstance> getDataLinksAsReader() {
		List<SM_ReaderModuleInstance> readerOps = new ArrayList<SM_ReaderModuleInstance>();

		for (SM_DataLink dataLink : dataLinks) {
			// Check if we are a reader
			for (SM_ReaderInterface readerInterface : dataLink.getReaders()) {
				if (readerInterface.getReaderInst() == this) {
					readerOps.add((SM_ReaderModuleInstance) readerInterface);
				}
			}
		}

		return readerOps;
	}

	public List<SM_ServerModuleInstance> getRequestLinksAsServer() {
		List<SM_ServerModuleInstance> serverOps = new ArrayList<SM_ServerModuleInstance>();

		for (SM_RequestLink rqLink : requestLinks) {
			// Check if we are the server
			if (rqLink.getServer().getServerInst() == this) {
				serverOps.add((SM_ServerModuleInstance) rqLink.getServer());
			}
		}

		return serverOps;
	}

	public List<SM_ClientModuleInstance> getRequestLinksAsClient() {
		ArrayList<SM_ClientModuleInstance> clientOps = new ArrayList<SM_ClientModuleInstance>();

		for (SM_RequestLink rqLink : requestLinks) {
			// Check if we are a client
			for (SM_ClientInterface clientInterface : rqLink.getClients()) {
				if (clientInterface.getClientInst() == this) {
					clientOps.add((SM_ClientModuleInstance) clientInterface);
				}
			}
		}

		return clientOps;
	}

	public void addPublicPinfoValue(SM_PinfoValue pinfoValue) {
		publicPinfos.add(pinfoValue);
	}

	public void addPrivatePinfoValue(SM_PinfoValue pinfoValue) {
		privatePinfos.add(pinfoValue);
	}

	public List<SM_PinfoValue> getPublicPinfoValues() {
		return publicPinfos;
	}

	public List<SM_PinfoValue> getPrivatePinfoValues() {
		return privatePinfos;
	}

	public List<SM_PinfoValue> getAllPinfos() {
		List<SM_PinfoValue> allPinfos = new ArrayList<SM_PinfoValue>();
		allPinfos.addAll(privatePinfos);
		allPinfos.addAll(publicPinfos);

		return allPinfos;
	}

}
