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

public class SM_RRServiceOp extends SM_Object {

	protected List<SM_OperationParameter> inputParameterTypes = new ArrayList<SM_OperationParameter>();
	protected List<SM_OperationParameter> outputParameterTypes = new ArrayList<SM_OperationParameter>();

	public SM_RRServiceOp(String name, List<SM_OperationParameter> in, List<SM_OperationParameter> out) {
		super(name);
		this.inputParameterTypes = in;
		this.outputParameterTypes = out;
	}

	public SM_RRServiceOp(SM_RRServiceOp rrServiceOp) {
		super(rrServiceOp.getName());
		this.inputParameterTypes = rrServiceOp.getInputs();
		this.outputParameterTypes = rrServiceOp.getOutputs();
	}

	public List<SM_OperationParameter> getInputs() {
		return inputParameterTypes;
	}

	public List<SM_OperationParameter> getOutputs() {
		return outputParameterTypes;
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

	public List<String> getOutputNames() {
		List<String> outputNames = new ArrayList<String>();
		for (SM_OperationParameter o : outputParameterTypes) {
			String name = o.getName();
			outputNames.add(name);
		}
		return outputNames;
	}

	public List<SM_Type> getOutputTypes() {
		List<SM_Type> outputTypes = new ArrayList<SM_Type>();
		for (SM_OperationParameter o : outputParameterTypes) {
			SM_Type type = o.getType();
			outputTypes.add(type);
		}
		return outputTypes;
	}

	public SM_Type getOutputTypeByName(String name) {
		SM_Type type = null;
		search: for (SM_OperationParameter o : outputParameterTypes) {
			if (o.getName().equals(name)) {
				type = o.getType();
				break search;
			}
		}
		return type;
	}

}
