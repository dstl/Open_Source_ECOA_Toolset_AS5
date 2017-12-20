/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.servicedefinition;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_DataServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_EventServiceOp;
import com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation.SM_RRServiceOp;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

public class SM_ServiceInterface extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_ServiceInterface.class);
	private static final String SEP_PATTERN_11 = "Operation - ";

	protected List<SM_EventServiceOp> eventOps = new ArrayList<SM_EventServiceOp>();
	protected List<SM_DataServiceOp> dataOps = new ArrayList<SM_DataServiceOp>();
	protected List<SM_RRServiceOp> rrOps = new ArrayList<SM_RRServiceOp>();
	private List<SM_Namespace> usesList = new ArrayList<SM_Namespace>();

	public SM_ServiceInterface(String name, List<SM_EventServiceOp> eventOps, List<SM_DataServiceOp> dataOps, List<SM_RRServiceOp> rrOps) {
		super(name);
		this.eventOps = eventOps;
		this.dataOps = dataOps;
		this.rrOps = rrOps;
	}

	public SM_ServiceInterface(String name) {
		super(name);
	}

	public void addUse(SM_Namespace namespace) {
		usesList.add(namespace);
	}

	public List<SM_EventServiceOp> getEventOps() {
		return eventOps;
	}

	public List<SM_DataServiceOp> getDataOps() {
		return dataOps;
	}

	public List<SM_RRServiceOp> getRROps() {
		return rrOps;
	}

	public SM_EventServiceOp getEventOperation(String operationName) {
		for (SM_EventServiceOp eventOp : eventOps) {
			if (eventOp.getName().equals(operationName)) {
				return eventOp;
			}
		}

		LOGGER.info(SEP_PATTERN_11 + operationName + " does not exist in service interface " + this.name);
		
		return null;
	}

	public SM_DataServiceOp getDataOperation(String operationName) {
		for (SM_DataServiceOp dataOp : dataOps) {
			if (dataOp.getName().equals(operationName)) {
				return dataOp;
			}
		}

		LOGGER.info(SEP_PATTERN_11 + operationName + " does not exist in service interface " + this.name);
		
		return null;
	}

	public SM_RRServiceOp getRROperation(String operationName) {
		for (SM_RRServiceOp rrOp : rrOps) {
			if (rrOp.getName().equals(operationName)) {
				return rrOp;
			}
		}

		LOGGER.info(SEP_PATTERN_11 + operationName + " does not exist in service interface " + this.name);
		
		return null;
	}

	public List<SM_Object> getOps() {
		List<SM_Object> operations = new ArrayList<SM_Object>();

		for (SM_EventServiceOp op : this.eventOps) {
			operations.add(op);
		}
		for (SM_RRServiceOp op : this.rrOps) {
			operations.add(op);
		}
		for (SM_DataServiceOp op : this.dataOps) {
			operations.add(op);
		}

		return operations;
	}

}
