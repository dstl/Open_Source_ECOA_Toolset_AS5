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
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataReadOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.data.SM_DataWrittenOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.request.SM_RequestSentOp;

public class SM_ModuleType extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_ModuleType.class);
	private static final String SEP_PATTERN_01 = " not found in module - ";
	private boolean isSupervisor;
	private boolean isFaultHandler;

	private boolean isActivatingErrorNotifs;
	private boolean isActivatingSvcAvailNotifs;
	private boolean isEnableModuleLifeCycleNotifs;

	private List<SM_EventReceivedOp> eventReceivedOps = new ArrayList<SM_EventReceivedOp>();
	private List<SM_EventSentOp> eventSentOps = new ArrayList<SM_EventSentOp>();
	private List<SM_RequestReceivedOp> requestReceivedOps = new ArrayList<SM_RequestReceivedOp>();
	private List<SM_RequestSentOp> requestSentOps = new ArrayList<SM_RequestSentOp>();
	private List<SM_DataWrittenOp> dataWrittenOps = new ArrayList<SM_DataWrittenOp>();
	private List<SM_DataReadOp> dataReadOps = new ArrayList<SM_DataReadOp>();
	private List<SM_ModuleTypeProperty> moduleProperties = new ArrayList<SM_ModuleTypeProperty>();
	private List<SM_Object> operationList = new ArrayList<SM_Object>();
	private List<SM_Pinfo> privatePinfos = new ArrayList<SM_Pinfo>();
	private List<SM_Pinfo> publicPinfos = new ArrayList<SM_Pinfo>();

	private SM_ComponentImplementation compImpl;

	public SM_ModuleType(String name, List<SM_Object> operationsList, boolean isSupervisor, boolean isFaultHandler) {
		super(name);
		this.isSupervisor = isSupervisor;
		this.isFaultHandler = isFaultHandler;

		operationList = operationsList;

		// Add the operations to categorised lists.
		for (SM_Object operation : operationsList) {
			if (operation instanceof SM_EventReceivedOp) {
				eventReceivedOps.add((SM_EventReceivedOp) operation);
			} else if (operation instanceof SM_EventSentOp) {
				eventSentOps.add((SM_EventSentOp) operation);
			} else if (operation instanceof SM_RequestReceivedOp) {
				requestReceivedOps.add((SM_RequestReceivedOp) operation);
			} else if (operation instanceof SM_RequestSentOp) {
				requestSentOps.add((SM_RequestSentOp) operation);
			} else if (operation instanceof SM_DataWrittenOp) {
				dataWrittenOps.add((SM_DataWrittenOp) operation);
			} else if (operation instanceof SM_DataReadOp) {
				dataReadOps.add((SM_DataReadOp) operation);
			}
		}

	}

	public List<SM_EventReceivedOp> getEventReceivedOps() {
		return eventReceivedOps;
	}

	public List<SM_EventSentOp> getEventSentOps() {
		return eventSentOps;
	}

	public List<SM_RequestReceivedOp> getRequestReceivedOps() {
		return requestReceivedOps;
	}

	public List<SM_RequestSentOp> getRequestSentOps() {
		return requestSentOps;
	}

	public List<SM_DataWrittenOp> getDataWrittenOps() {
		return dataWrittenOps;
	}

	public List<SM_DataReadOp> getDataReadOps() {
		return dataReadOps;
	}

	public boolean getIsSupervisor() {
		return isSupervisor;
	}

	public boolean getIsFaultHandler() {
		return isFaultHandler;
	}

	public void setIsSupervisor(boolean isSupervisor) {
		this.isSupervisor = isSupervisor;
	}

	public SM_EventSentOp getEventSentOpByName(String operationName) {
		for (SM_EventSentOp eventOp : eventSentOps) {
			if (eventOp.getName().equals(operationName)) {
				return eventOp;
			}
		}
		LOGGER.info(operationName + SEP_PATTERN_01 + this.name);
		
		return null;
	}

	public SM_EventReceivedOp getEventReceivedOpByName(String operationName) {
		for (SM_EventReceivedOp eventOp : eventReceivedOps) {
			if (eventOp.getName().equals(operationName)) {
				return eventOp;
			}
		}
		LOGGER.info(operationName + SEP_PATTERN_01 + this.name);
		
		return null;
	}

	public SM_DataWrittenOp getDataWrittenOpByName(String operationName) {
		for (SM_DataWrittenOp dataOp : dataWrittenOps) {
			if (dataOp.getName().equals(operationName)) {
				return dataOp;
			}
		}
		LOGGER.info(operationName + SEP_PATTERN_01 + this.name);
		
		return null;
	}

	public SM_DataReadOp getDataReaderOpByName(String operationName) {
		for (SM_DataReadOp dataOp : dataReadOps) {
			if (dataOp.getName().equals(operationName)) {
				return dataOp;
			}
		}
		LOGGER.info(operationName + SEP_PATTERN_01 + this.name);
		
		return null;
	}

	public SM_RequestSentOp getRequestSentOpByName(String operationName) {
		for (SM_RequestSentOp requestOp : requestSentOps) {
			if (requestOp.getName().equals(operationName)) {
				return requestOp;
			}
		}
		LOGGER.info(operationName + SEP_PATTERN_01 + this.name);
		
		return null;
	}

	public SM_RequestReceivedOp getRequestReceivedOpByName(String operationName) {
		for (SM_RequestReceivedOp requestOp : requestReceivedOps) {
			if (requestOp.getName().equals(operationName)) {
				return requestOp;
			}
		}
		LOGGER.info(operationName + SEP_PATTERN_01 + this.name);
		
		return null;
	}

	public void addEventReceivedOp(SM_EventReceivedOp eventReceivedOp) {
		this.eventReceivedOps.add(eventReceivedOp);
	}

	public void addEventSentOp(SM_EventSentOp eventSentOp) {
		this.eventSentOps.add(eventSentOp);
	}

	public void addVDReadOp(SM_DataReadOp dataReadOp) {
		this.dataReadOps.add(dataReadOp);
	}

	public List<SM_RequestSentOp> getSyncRequestSentOps() {
		List<SM_RequestSentOp> syncRequestSentOps = new ArrayList<SM_RequestSentOp>();

		for (SM_RequestSentOp requestSent : requestSentOps) {
			if (requestSent.getIsSynchronous()) {
				syncRequestSentOps.add(requestSent);
			}
		}

		return syncRequestSentOps;
	}

	public List<SM_RequestSentOp> getAsyncRequestSentOps() {
		List<SM_RequestSentOp> asyncRequestSentOps = new ArrayList<SM_RequestSentOp>();

		for (SM_RequestSentOp requestSent : requestSentOps) {
			if (!requestSent.getIsSynchronous()) {
				asyncRequestSentOps.add(requestSent);
			}
		}

		return asyncRequestSentOps;
	}

	public void addModuleProperty(SM_ModuleTypeProperty property) {
		moduleProperties.add(property);
	}

	public List<SM_ModuleTypeProperty> getModuleProperties() {
		return moduleProperties;
	}

	public SM_ModuleTypeProperty getModulePropertyByName(String name) {
		for (SM_ModuleTypeProperty property : moduleProperties) {
			if (property.getName().equals(name)) {
				return property;
			}
		}

		LOGGER.info(name + " module type property not found in module - " + this.name);
		
		return null;

	}

	public List<SM_Object> getOperationList() {
		return operationList;
	}

	public SM_ComponentImplementation getCompImpl() {
		return compImpl;
	}

	public void setCompImpl(SM_ComponentImplementation compImpl) {
		this.compImpl = compImpl;
	}

	public void addPublicPinfo(SM_Pinfo sm_PInfo) {
		publicPinfos.add(sm_PInfo);
	}

	public List<SM_Pinfo> getPublicPinfos() {
		return publicPinfos;
	}

	public void addPrivatePinfo(SM_Pinfo sm_PInfo) {
		privatePinfos.add(sm_PInfo);
	}

	public List<SM_Pinfo> getPrivatePinfos() {
		return privatePinfos;
	}

	public SM_Pinfo getPublicPinfo(String pinfoValueName) {
		for (SM_Pinfo pinfo : publicPinfos) {
			if (pinfo.getName().equalsIgnoreCase(pinfoValueName)) {
				return pinfo;
			}
		}

		LOGGER.info(pinfoValueName + " public pinfo value cannot be associated to a module type pinfo in module type - " + this.name);
		
		return null;

	}

	public SM_Pinfo getPrivatePinfo(String pinfoValueName) {
		for (SM_Pinfo pinfo : privatePinfos) {
			if (pinfo.getName().equalsIgnoreCase(pinfoValueName)) {
				return pinfo;
			}
		}

		LOGGER.info(pinfoValueName + " private pinfo value cannot be associated to a module type pinfo in module type - " + this.name);
		
		return null;
	}

	public List<SM_Pinfo> getAllPinfos() {
		List<SM_Pinfo> allPinfos = new ArrayList<SM_Pinfo>();

		allPinfos.addAll(publicPinfos);
		allPinfos.addAll(privatePinfos);

		return allPinfos;
	}

	public List<SM_Pinfo> getPrivateReadPinfos() {
		List<SM_Pinfo> readonlyPinfos = new ArrayList<SM_Pinfo>();
		for (SM_Pinfo pinfo : privatePinfos) {
			if (!pinfo.isWriteable()) {
				readonlyPinfos.add(pinfo);
			}
		}

		return readonlyPinfos;
	}

	public List<SM_Pinfo> getPrivateWritePinfos() {
		List<SM_Pinfo> writePinfos = new ArrayList<SM_Pinfo>();
		for (SM_Pinfo pinfo : privatePinfos) {
			if (pinfo.isWriteable()) {
				writePinfos.add(pinfo);
			}
		}

		return writePinfos;
	}

	public boolean isActivatingErrorNotifs() {
		return isActivatingErrorNotifs;
	}

	public void setActivatingErrorNotifs(boolean isActivatingErrorNotifs) {
		this.isActivatingErrorNotifs = isActivatingErrorNotifs;
	}

	public boolean isActivatingSvcAvailNotifs() {
		return isActivatingSvcAvailNotifs;
	}

	public void setActivatingSvcAvailNotifs(boolean isActivatingSvcAvailNotifs) {
		this.isActivatingSvcAvailNotifs = isActivatingSvcAvailNotifs;
	}

	public boolean isEnableModuleLifeCycleNotifs() {
		return isEnableModuleLifeCycleNotifs;
	}

	public void setEnableModuleLifeCycleNotifs(boolean isEnableModuleLifeCycleNotifs) {
		this.isEnableModuleLifeCycleNotifs = isEnableModuleLifeCycleNotifs;
	}

}
