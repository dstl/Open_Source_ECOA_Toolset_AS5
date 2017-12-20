/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.moduleapi;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.systemmodel.SM_Object;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleType;
import com.iawg.ecoa.systemmodel.componentimplementation.modoperation.event.SM_EventReceivedOp;

/**
 * This class is an abstract class that declares the methods that must be
 * implemented by source code language-specific classes that extend it.
 * 
 */
public abstract class ModuleWriter extends SourceFileWriter {
	protected SM_ModuleImpl moduleImpl;
	protected SM_ModuleType moduleType;
	protected String moduleImplName;
	protected SM_ComponentImplementation componentImplementation;
	protected String isType;
	protected SystemModel systemModel = null;
	protected ToolConfig toolConfig = null;

	ModuleWriter(SystemModel systemModel, ToolConfig toolConfig, String isType, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(outputDir);
		this.isType = isType;
		this.systemModel = systemModel;
		this.toolConfig = toolConfig;
		this.componentImplementation = compImpl;
		this.moduleImpl = moduleImpl;
		this.moduleImplName = moduleImpl.getName();
		this.moduleType = moduleImpl.getModuleType();
	}

	public abstract void writeConstParameter(SM_OperationParameter opParam);

	public abstract void writeEndParameters();

	public abstract void writeErrorNotification(SM_Object instance);

	public abstract void writeEventReceived(SM_EventReceivedOp eventReceiveOp);

	public abstract void writeFaultHandlerNotification();

	public abstract void writeLifecycleNotification(SM_Object instance);

	public abstract void writeLifecycleServices();

	public abstract void writeParameter(SM_OperationParameter opParam);

	public abstract void writePreamble();

	public abstract void writeRequestReceived(String name);

	public abstract void writeResponseReceivedAsynchonous(String name);

	public abstract void writeServiceAvailabilityNotifications();

	// public abstract void writeLifecycleNotification(SM_ModuleInstance
	// moduleInstance);

	public abstract void writeVDUpdated(String vdOpName);

	// public abstract void writeErrorNotification(SM_TriggerInstance
	// triggerInstance);

}
