/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.SM_ComponentType;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.componentimplementation.links.data.SM_DataLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_EventLink;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderExternal;
import com.iawg.ecoa.systemmodel.componentimplementation.links.event.SM_SenderInterface;
import com.iawg.ecoa.systemmodel.componentimplementation.links.request.SM_RequestLink;
import com.iawg.ecoa.systemmodel.qos.SM_QualityOfService;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

public class SM_ComponentImplementation extends SM_Object {
	private static final Logger LOGGER = LogManager.getLogger(SM_ComponentImplementation.class);

	private SM_ComponentType compType;
	private List<SM_ComponentInstance> componentInstances = new ArrayList<SM_ComponentInstance>();
	private Map<String, SM_ModuleInstance> moduleInstances = new HashMap<String, SM_ModuleInstance>();
	private Map<String, SM_ModuleImpl> moduleImplementations = new HashMap<String, SM_ModuleImpl>();
	private Map<String, SM_ModuleType> moduleTypes = new HashMap<String, SM_ModuleType>();
	private List<SM_RequestLink> requestLinks = new ArrayList<SM_RequestLink>();
	private List<SM_DataLink> dataLinks = new ArrayList<SM_DataLink>();
	private List<SM_EventLink> eventLinks = new ArrayList<SM_EventLink>();
	private Map<String, SM_TriggerInstance> triggerInstances = new HashMap<String, SM_TriggerInstance>();
	private Map<String, SM_DynamicTriggerInstance> dynamicTriggerInstances = new HashMap<String, SM_DynamicTriggerInstance>();
	private List<SM_Namespace> usesList = new ArrayList<SM_Namespace>();
	private List<SM_VDRepository> vdRepositories = new ArrayList<SM_VDRepository>();
	private Map<SM_ServiceInstance, SM_QualityOfService> requiredQOSMap = new HashMap<SM_ServiceInstance, SM_QualityOfService>();
	private Path containingDir;
	private boolean isPrebuilt = false;

	public SM_ComponentImplementation(String name, SM_ComponentType compType, List<SM_ModuleType> types) {
		super(name);
		this.compType = compType;
		for (SM_ModuleType mtd : types) {
			// Link the module type to this implementation.
			mtd.setCompImpl(this);
			moduleTypes.put(mtd.getName(), mtd);
		}

		// Relate this implementation to the component type.
		compType.addImplementation(this);

	}

	public SM_ModuleInstance getModuleInstanceByName(String name) {
		return moduleInstances.get(name);
	}

	public SM_ModuleImpl getModuleImplementationByName(String name) {
		return moduleImplementations.get(name);
	}

	public Boolean moduleTypeExists(String name) {
		return moduleTypes.get(name) != null;
	}

	public SM_ModuleType getModuleType(String name) {
		return moduleTypes.get(name);
	}

	public void addEventLink(SM_EventLink eventLink) {
		eventLinks.add(eventLink);
	}

	public List<SM_EventLink> getEventLinks() {
		return eventLinks;
	}

	public void addDataLink(SM_DataLink dataLink) {
		dataLinks.add(dataLink);
	}

	public List<SM_DataLink> getDataLinks() {
		return dataLinks;
	}

	public void addRequestLink(SM_RequestLink requestLink) {
		requestLinks.add(requestLink);
	}

	public List<SM_RequestLink> getRequestLinks() {
		return requestLinks;
	}

	public SM_TriggerInstance getTriggerInstanceByName(String name) {
		return triggerInstances.get(name);
	}

	public SM_DynamicTriggerInstance getDynamicTriggerInstanceByName(String name) {
		return dynamicTriggerInstances.get(name);
	}

	public SM_ComponentType getCompType() {
		return compType;
	}

	public void addModuleInstance(SM_ModuleInstance mid) {
		moduleInstances.put(mid.getName(), mid);
	}

	public void addModuleImplementation(SM_ModuleImpl mim) {
		moduleImplementations.put(mim.getName(), mim);
	}

	public SM_ModuleInstance getModuleInstance(String name) {
		if (moduleInstances.get(name) != null) {
			return moduleInstances.get(name);
		} else {
			LOGGER.info("Module instance - " + name + " does not exist in comp impl - " + this.name);
			
			return null;
		}
	}

	public void addTriggerInstance(SM_TriggerInstance triggerInst) {
		triggerInstances.put(triggerInst.getName(), triggerInst);
	}

	public void addDynamicTriggerInstance(SM_DynamicTriggerInstance dynamicTriggerInstance) {
		dynamicTriggerInstances.put(dynamicTriggerInstance.getName(), dynamicTriggerInstance);
	}

	public Map<String, SM_ModuleInstance> getModuleInstances() {
		return moduleInstances;
	}

	public Map<String, SM_TriggerInstance> getTriggerInstances() {
		return triggerInstances;
	}

	public Map<String, SM_DynamicTriggerInstance> getDynamicTriggerInstances() {
		return dynamicTriggerInstances;
	}

	public Map<String, SM_ModuleImpl> getModuleImplementations() {
		return moduleImplementations;
	}

	public void addUse(SM_Namespace namespace) {
		usesList.add(namespace);
	}

	public List<SM_Namespace> getUses() {
		return usesList;
	}

	public void addComponentInstance(SM_ComponentInstance componentInstance) {
		componentInstances.add(componentInstance);
	}

	public List<SM_ComponentInstance> getComponentInstances() {
		return componentInstances;
	}

	public List<SM_VDRepository> getVdRepositories() {
		return vdRepositories;
	}

	public void setVdRepositories(ArrayList<SM_VDRepository> vdRepositories) {
		this.vdRepositories = vdRepositories;
	}

	public void addVDRepository(SM_VDRepository vdRepo) {
		this.vdRepositories.add(vdRepo);
	}

	public SM_ModuleInstance getSupervisorModule() {
		// TODO - not sure if we should be able to handle more than one
		// supervisor module instance.
		// For now, cannot handle this!
		for (SM_ModuleInstance modInst : moduleInstances.values()) {
			if (modInst.getModuleType().getIsSupervisor()) {
				return modInst;
			}
		}
		LOGGER.info("Failed to find supervisor module instance in : " + name);
		
		return null;
	}

	public List<SM_Object> getNonSupervisionModuleTriggerInstances() {
		List<SM_Object> modTrigList = new ArrayList<SM_Object>();

		// Add non-supervision module instances
		for (SM_ModuleInstance modInst : moduleInstances.values()) {
			if (!modInst.getModuleType().getIsSupervisor()) {
				modTrigList.add(modInst);
			}
		}

		// Add trigger instances
		// modTrigList.addAll(triggerInstances.values());
		Iterator<Entry<String, SM_TriggerInstance>> itTI = triggerInstances.entrySet().iterator();
		while (itTI.hasNext()) {
			Map.Entry<String, SM_TriggerInstance> mapEntry = (Map.Entry<String, SM_TriggerInstance>) itTI.next();
			modTrigList.add(mapEntry.getValue());
		}

		// Add dynamic trigger instances
		Iterator<Entry<String, SM_DynamicTriggerInstance>> itDTI = dynamicTriggerInstances.entrySet().iterator();
		while (itDTI.hasNext()) {
			Map.Entry<String, SM_DynamicTriggerInstance> mapEntry = (Map.Entry<String, SM_DynamicTriggerInstance>) itDTI.next();
			modTrigList.add(mapEntry.getValue());
		}

		return modTrigList;
	}

	public List<SM_Object> getModuleTriggerInstances() {
		List<SM_Object> modTrigList = new ArrayList<SM_Object>();

		modTrigList.addAll(moduleInstances.values());
		modTrigList.addAll(triggerInstances.values());
		modTrigList.addAll(dynamicTriggerInstances.values());

		return modTrigList;
	}

	public Map<SM_ServiceInstance, SM_QualityOfService> getRequiredQOSMap() {
		return requiredQOSMap;
	}

	public List<SM_SenderExternal> getExternalSenders() {
		List<SM_SenderExternal> externalSenders = new ArrayList<SM_SenderExternal>();

		for (SM_EventLink eventLink : eventLinks) {
			for (SM_SenderInterface sender : eventLink.getSenders()) {
				if (sender instanceof SM_SenderExternal) {
					externalSenders.add((SM_SenderExternal) sender);
				}
			}
		}
		return externalSenders;
	}

	public Path getContainingDir() {
		return containingDir;
	}

	public void setContainingDir(Path containingDir) {
		this.containingDir = containingDir;
	}

	public boolean isPrebuilt() {
		return isPrebuilt;
	}

	public void setPrebuilt(boolean isPrebuilt) {
		this.isPrebuilt = isPrebuilt;
	}
}
