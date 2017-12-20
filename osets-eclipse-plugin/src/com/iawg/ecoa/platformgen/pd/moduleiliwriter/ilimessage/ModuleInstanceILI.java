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

import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;

public class ModuleInstanceILI {
	private static final Logger LOGGER = LogManager.getLogger(ModuleInstanceILI.class);

	private SM_ModuleInstance modInst;
	private ArrayList<ILIMessage> iliMessageList = new ArrayList<ILIMessage>();

	public ModuleInstanceILI(SM_ModuleInstance modInst) {
		this.modInst = modInst;
	}

	public ILIMessage getILIForDataLink(SM_DataLink dataLink) {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof DataILIMessage) {
				if (((DataILIMessage) ili).getDataLink() == dataLink) {
					return ili;
				}
			}
		}
		LOGGER.info("Failed to find ILI message for data in : " + modInst.getName());
		
		return null;
	}

	public ILIMessage getILIForErrorNotification(SM_ModuleInstance modInst) {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof ErrorNotificationILIMessage) {
				if (((ErrorNotificationILIMessage) ili).getModuleInstance() == modInst) {
					return ili;
				}
			}
		}

		LOGGER.info("Failed to find ILI message for module instance error notification : " + modInst.getName());
		
		return null;
	}

	public ILIMessage getILIForEventLink(SM_EventLink eventLink) {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof EventILIMessage) {
				if (((EventILIMessage) ili).getEventLink() == eventLink) {
					return ili;
				}
			}
		}
		LOGGER.info("Failed to find ILI message for event in : " + modInst.getName());
		
		return null;
	}

	public ILIMessage getILIForFaultNotification() {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof FaultNotificationILIMessage) {
				return ili;
			}
		}

		LOGGER.info("Failed to find ILI message for module instance fault notification : " + modInst.getName());
		
		return null;
	}

	public ILIMessage getILIForRequestLink(SM_RequestLink requestLink) {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof RequestILIMessage) {
				if (((RequestILIMessage) ili).getRequestLink() == requestLink) {
					return ili;
				}
			}
		}
		LOGGER.info("Failed to find ILI message for request in : " + modInst.getName());
		
		return null;
	}

	public ILIMessage getILIForResponseLink(SM_RequestLink requestLink) {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof ResponseILIMessage) {
				if (((ResponseILIMessage) ili).getRequestLink() == requestLink) {
					return ili;
				}
			}
		}

		LOGGER.info("Failed to find ILI message for response in : " + modInst.getName());
		
		return null;
	}

	public ILIMessage getILIForServiceAvailNotification(SM_ServiceInstance serviceInst) {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof ServiceAvailNotificationILIMessage) {
				if (((ServiceAvailNotificationILIMessage) ili).getServiceInstance() == serviceInst) {
					return ili;
				}
			}
		}

		LOGGER.info("Failed to find ILI message for service instance : " + serviceInst.getName());
		
		return null;
	}

	public ILIMessage getILIForServiceProviderNotification(SM_ServiceInstance serviceInst) {
		for (ILIMessage ili : iliMessageList) {
			if (ili instanceof ServiceProviderNotificationILIMessage) {
				if (((ServiceProviderNotificationILIMessage) ili).getServiceInstance() == serviceInst) {
					return ili;
				}
			}
		}

		LOGGER.info("Failed to find ILI message for service instance : " + serviceInst.getName());
		
		return null;
	}

	public ArrayList<ILIMessage> getILIMessageList() {
		return this.iliMessageList;
	}

	public SM_ModuleInstance getModInst() {
		return modInst;
	}

	public void setILIMessageList(ArrayList<ILIMessage> iliMessageList) {
		this.iliMessageList = iliMessageList;
	}

}
