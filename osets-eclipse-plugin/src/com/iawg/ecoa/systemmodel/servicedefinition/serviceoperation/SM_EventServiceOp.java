/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.servicedefinition.serviceoperation;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.types.SM_Type;

public class SM_EventServiceOp extends SM_Object {

	public enum EventDirection {
		SENT_BY_PROVIDER, RECEIVED_BY_PROVIDER;
		public String value() {
			return name();
		}

		public static EventDirection fromValue(String v) {
			return valueOf(v);
		}
	}

	private EventDirection direction;
	protected List<SM_OperationParameter> inputParameterTypes = new ArrayList<SM_OperationParameter>();

	public SM_EventServiceOp(String name, EventDirection direction, List<SM_OperationParameter> in) {
		super(name);
		this.direction = direction;
		this.inputParameterTypes = in;
	}

	public SM_EventServiceOp(SM_EventServiceOp eventServiceOp) {
		super(eventServiceOp.getName());
		this.direction = eventServiceOp.getDirection();
		this.inputParameterTypes = eventServiceOp.getInputs();
	}

	public List<SM_OperationParameter> getInputs() {
		return inputParameterTypes;
	}

	public List<String> getInputNames() {
		List<String> inputNames = new ArrayList<String>();
		for (SM_OperationParameter o : inputParameterTypes) {
			String name = o.getName();
			inputNames.add(name);
		}
		return inputNames;
	}

	public List<SM_Type> getInputTypes() {
		List<SM_Type> inputTypes = new ArrayList<SM_Type>();
		for (SM_OperationParameter o : inputParameterTypes) {
			SM_Type type = o.getType();
			inputTypes.add(type);
		}
		return inputTypes;
	}

	public SM_Type getInputTypeByName(String name) {
		SM_Type type = null;
		search: for (SM_OperationParameter o : inputParameterTypes) {
			if (o.getName().equals(name)) {
				type = o.getType();
				break search;
			}
		}
		return type;
	}

	public EventDirection getDirection() {
		return direction;
	}

}
