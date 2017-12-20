/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation.links.request;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public class SM_RequestLink {

	private List<SM_ClientInterface> clients = new ArrayList<SM_ClientInterface>();
	private SM_ServerInterface server;
	private SM_ComponentImplementation componentImpl;

	public SM_RequestLink(SM_ComponentImplementation compImpl) {
		this.componentImpl = compImpl;
		// Link this object to the component impl object
		compImpl.addRequestLink(this);
	}

	public SM_ServerInterface getServer() {
		return server;
	}

	public void addServer(SM_ServerInterface smServer) {
		this.server = smServer;
	}

	public List<SM_ClientInterface> getClients() {
		return clients;
	}

	public void addClient(SM_ClientInterface smClient) {
		this.clients.add(smClient);
	}

	public SM_ComponentImplementation getComponentImpl() {
		return componentImpl;
	}
}
