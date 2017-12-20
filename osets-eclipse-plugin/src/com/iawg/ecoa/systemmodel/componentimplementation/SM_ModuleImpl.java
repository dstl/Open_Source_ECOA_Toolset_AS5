/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.systemmodel.componentimplementation;

import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.ECOA_System_Model.ImplLanguage;
import com.iawg.ecoa.systemmodel.SM_Object;

public class SM_ModuleImpl extends SM_Object {

	private ImplLanguage type;
	private SM_ModuleType moduleType;
	private SM_ComponentImplementation componentImplementation;
	private List<SM_ModuleInstance> moduleInstances = new ArrayList<SM_ModuleInstance>();
	private boolean instrument;
	private boolean isPrebuilt = false;
	private String prebuiltObjLocation;
	private ArrayList<String> objectFileList = new ArrayList<String>();

	public SM_ModuleImpl(String name, ImplLanguage impl, SM_ModuleType mtype, SM_ComponentImplementation compImpl) {
		super(name);
		type = impl;
		moduleType = mtype;
		componentImplementation = compImpl;
	}

	public SM_ModuleType getModuleType() {
		return moduleType;

	}

	public ImplLanguage getLanguageType() {
		return type;
	}

	public SM_ComponentImplementation getComponentImplementation() {
		return componentImplementation;
	}

	public void addModuleInstance(SM_ModuleInstance modInst) {
		moduleInstances.add(modInst);
	}

	public List<SM_ModuleInstance> getModuleInstances() {
		return moduleInstances;
	}

	public void setLanguage(ImplLanguage ltype) {
		type = ltype;
	}

	public void setInstrument(boolean instrument) {
		this.instrument = instrument;
	}

	public boolean isInstrument() {
		return instrument;
	}

	public boolean isPrebuilt() {
		return isPrebuilt;
	}

	public void setPrebuilt(boolean isPrebuilt) {
		this.isPrebuilt = isPrebuilt;
	}

	public String getPrebuiltObjLocation() {
		return prebuiltObjLocation;
	}

	public void setPrebuiltObjLocation(String prebuiltObjLocation) {
		this.prebuiltObjLocation = prebuiltObjLocation;
	}

	public void addObjectFile(String object) {
		this.objectFileList.add(object);
	}

	public List<String> getObjectFileList() {
		return objectFileList;
	}

}
