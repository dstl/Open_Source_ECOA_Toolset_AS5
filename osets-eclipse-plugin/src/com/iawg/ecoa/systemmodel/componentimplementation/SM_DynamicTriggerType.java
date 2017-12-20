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
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventSentOp;

public class SM_DynamicTriggerType extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_DynamicTriggerType.class);
	private List<SM_EventReceivedOp> eventReceivedOps = new ArrayList<SM_EventReceivedOp>();
	private List<SM_EventSentOp> eventSentOps = new ArrayList<SM_EventSentOp>();
	private List<SM_OperationParameter> paramList = new ArrayList<SM_OperationParameter>();

	private SM_ComponentImplementation compImpl;

	public SM_DynamicTriggerType(String name, List<SM_OperationParameter> paramList, SystemModel systemModel) {
		super(name);

		List<SM_OperationParameter> inOpParms = new ArrayList<SM_OperationParameter>();
		inOpParms.add(new SM_OperationParameter("delay", systemModel.getTypes().getType("duration")));
		inOpParms.addAll(paramList);

		// Add the operations, and their parameters, to categorised lists.
		eventReceivedOps.add(new SM_EventReceivedOp("in", inOpParms));
		eventReceivedOps.add(new SM_EventReceivedOp("reset", null));
		eventSentOps.add(new SM_EventSentOp("out", paramList));
	}

	public List<SM_EventReceivedOp> getEventReceivedOps() {
		return eventReceivedOps;
	}

	public List<SM_EventSentOp> getEventSentOps() {
		return eventSentOps;
	}

	public SM_EventSentOp getEventSentOpByName(String operationName) {
		for (SM_EventSentOp eventOp : eventSentOps) {
			if (eventOp.getName().equals(operationName)) {
				return eventOp;
			}
		}
		LOGGER.info(operationName + " not found in module - " + this.name);
		
		return null;
	}

	public SM_EventReceivedOp getEventReceivedOpByName(String operationName) {
		for (SM_EventReceivedOp eventOp : eventReceivedOps) {
			if (eventOp.getName().equals(operationName)) {
				return eventOp;
			}
		}
		LOGGER.info(operationName + " not found in module - " + this.name);
		
		return null;
	}

	public void addEventReceivedOp(SM_EventReceivedOp eventReceivedOp) {
		this.eventReceivedOps.add(eventReceivedOp);
	}

	public void addEventSentOp(SM_EventSentOp eventSentOp) {
		this.eventSentOps.add(eventSentOp);
	}

	public List<SM_OperationParameter> getParamList() {
		return paramList;
	}

	public SM_ComponentImplementation getCompImpl() {
		return compImpl;
	}

	public void setCompImpl(SM_ComponentImplementation compImpl) {
		this.compImpl = compImpl;
	}
}
