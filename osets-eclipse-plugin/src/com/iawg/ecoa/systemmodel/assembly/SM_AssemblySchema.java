/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.assembly;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;

public class SM_AssemblySchema extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_AssemblySchema.class);

	private List<SM_ComponentInstance> componentInstances = new ArrayList<SM_ComponentInstance>();
	private List<SM_Wire> wires = new ArrayList<SM_Wire>();
	private Integer uid;
	private List<SM_AssemblyPropertyValue> propertyValues = new ArrayList<SM_AssemblyPropertyValue>();

	public SM_AssemblySchema(String name) {
		super(name);
	}

	public void addInstance(SM_ComponentInstance ci) {
		componentInstances.add(ci);
	}

	public void addWire(SM_Wire wi) {
		wires.add(wi);
	}

	public SM_ComponentInstance getInstanceByName(String name) {
		SM_ComponentInstance comp = null;
		search: for (SM_ComponentInstance ci : componentInstances) {
			if (name.equals(ci.getName())) {
				comp = ci;
				break search;
			}
		}
		return comp;
	}

	public List<SM_ComponentInstance> getComponentInstances() {
		return componentInstances;
	}

	public List<SM_Wire> getWires() {
		return wires;
	}

	public boolean exists(String assemblyName) {
		return name.equals(assemblyName);
	}

	public void setUID(Integer uid) {
		this.uid = uid;
	}

	public Integer getUID() {
		if (uid != null) {
			return uid;
		} else {
			LOGGER.info("Failed to get UID for composite");
			
			return null;
		}
	}

	public void addPropertyValue(SM_AssemblyPropertyValue pinfo) {
		this.propertyValues.add(pinfo);
	}

	public List<SM_AssemblyPropertyValue> getPropertyValues() {
		return propertyValues;
	}

	public SM_AssemblyPropertyValue getPropertyValueByName(String name) {
		for (SM_AssemblyPropertyValue propValue : propertyValues) {
			if (propValue.getName().equals(name)) {
				return propValue;
			}
		}

		LOGGER.info("Property value - " + name + " does not exist in assembly " + this.name);
		
		return null;
	}

}
