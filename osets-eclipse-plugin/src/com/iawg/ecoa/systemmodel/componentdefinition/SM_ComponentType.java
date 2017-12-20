/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentdefinition;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public class SM_ComponentType extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_ComponentType.class);
	private static final String SEP_PATTERN_01 = " does not exist in component type ";

	private Map<String, SM_ServiceInstance> serviceInstances = new LinkedHashMap<String, SM_ServiceInstance>();
	private Map<String, SM_ServiceInstance> referenceInstances = new LinkedHashMap<String, SM_ServiceInstance>();
	private List<SM_ComponentTypeProperty> properties = new ArrayList<SM_ComponentTypeProperty>();
	private List<SM_ComponentImplementation> componentImplementations = new ArrayList<SM_ComponentImplementation>();

	public SM_ComponentType(String name) {
		super(name);
	}

	public void addServiceInstance(String name, SM_ServiceInstance serviceInstance) {
		serviceInstances.put(name, serviceInstance);
	}

	public void addReferenceInstance(String name, SM_ServiceInstance serviceInstance) {
		referenceInstances.put(name, serviceInstance);
	}

	public void addProperty(SM_ComponentTypeProperty p) {
		properties.add(p);
	}

	public SM_ServiceInstance getReferenceInstanceByName(String name) {
		if (referenceInstances.get(name) != null) {
			return referenceInstances.get(name);
		} else {
			LOGGER.info("Reference - " + name + SEP_PATTERN_01 + this.name);
			
			return null;
		}
	}

	public SM_ServiceInstance getServiceInstanceByName(String name) {
		if (serviceInstances.get(name) != null) {
			return serviceInstances.get(name);
		} else {
			LOGGER.info("Service - " + name + SEP_PATTERN_01 + this.name);
			
			return null;
		}
	}

	public Set<String> getServiceInstanceNames() {
		return serviceInstances.keySet();
	}

	public Set<String> getReferenceInstanceNames() {
		return referenceInstances.keySet();
	}

	public List<SM_ComponentTypeProperty> getProperties() {
		return properties;
	}

	public Map<String, SM_ServiceInstance> getServiceInstances() {
		return serviceInstances;
	}

	public Map<String, SM_ServiceInstance> getReferenceInstances() {
		return referenceInstances;
	}

	public Map<String, SM_ServiceInstance> getServiceAndReferenceInstances() {
		// Concatenate both the services and references into a single map
		// NOTE - could this overwrite some data if duplicate key?!
		LinkedHashMap<String, SM_ServiceInstance> serviceReferences = new LinkedHashMap<String, SM_ServiceInstance>();

		serviceReferences.putAll(serviceInstances);
		serviceReferences.putAll(referenceInstances);

		return serviceReferences;
	}

	public List<SM_ServiceInstance> getServiceAndReferenceInstancesList() {
		ArrayList<SM_ServiceInstance> serviceRefList = new ArrayList<SM_ServiceInstance>();
		Iterator<Entry<String, SM_ServiceInstance>> it = getServiceAndReferenceInstances().entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, SM_ServiceInstance> mapEntry = (Map.Entry<String, SM_ServiceInstance>) it.next();
			serviceRefList.add(mapEntry.getValue());
		}

		return serviceRefList;
	}

	public List<SM_ServiceInstance> getReferenceInstancesList() {
		ArrayList<SM_ServiceInstance> refList = new ArrayList<SM_ServiceInstance>();
		Iterator<Entry<String, SM_ServiceInstance>> it = referenceInstances.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, SM_ServiceInstance> mapEntry = (Map.Entry<String, SM_ServiceInstance>) it.next();
			refList.add(mapEntry.getValue());
		}

		return refList;
	}

	public List<SM_ServiceInstance> getServiceInstancesList() {
		ArrayList<SM_ServiceInstance> serviceList = new ArrayList<SM_ServiceInstance>();
		Iterator<Entry<String, SM_ServiceInstance>> it = serviceInstances.entrySet().iterator();

		while (it.hasNext()) {
			Map.Entry<String, SM_ServiceInstance> mapEntry = (Map.Entry<String, SM_ServiceInstance>) it.next();
			serviceList.add(mapEntry.getValue());
		}

		return serviceList;
	}

	public void addImplementation(SM_ComponentImplementation compImpl) {
		this.componentImplementations.add(compImpl);
	}

	public List<SM_ComponentImplementation> getComponentImplementations() {
		return componentImplementations;
	}

	public SM_ComponentTypeProperty getPropertyByName(String name) {
		for (SM_ComponentTypeProperty property : properties) {
			if (property.getName().equals(name)) {
				return property;
			}
		}

		LOGGER.info("Property - " + name + SEP_PATTERN_01 + this.name);
		
		return null;
	}

}
