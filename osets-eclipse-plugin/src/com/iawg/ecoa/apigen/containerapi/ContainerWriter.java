/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.containerapi;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleTypeProperty;

/**
 * This class is an abstract class that declares the methods that must be
 * implemented by source code language-specific classes that extend it.
 * 
 */
public abstract class ContainerWriter extends SourceFileWriter {
	protected String isType;
	protected SM_ModuleImpl moduleImpl;
	protected String moduleImplName;
	protected SM_ComponentImplementation componentImplementation;
	protected SystemModel systemModel = null;
	protected ToolConfig toolConfig = null;

	ContainerWriter(SystemModel systemModel, ToolConfig toolConfig, String isType, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(outputDir);
		this.isType = isType;
		this.systemModel = systemModel;
		this.toolConfig = toolConfig;
		this.componentImplementation = compImpl;
		this.moduleImpl = moduleImpl;
		this.moduleImplName = moduleImpl.getName();
	}

	public abstract void writeCancelWriteAccess(String verData);

	public abstract void writeConstParameter(SM_OperationParameter opParam);

	public abstract void writeEndParameters();

	public abstract void writeEndParametersNoStatusReturn();

	public abstract void writeEventSend(String eventName);

	public abstract void writeGetProperty(SM_ModuleTypeProperty property);

	public abstract void writeGetReadAccess(String verData);

	public abstract void writeGetRequiredAvailability();

	public abstract void writeGetWriteAccess(String verData);

	public abstract void writeLifecycleServices();

	public abstract void writeLoggingServices();

	public abstract void writeParameter(SM_OperationParameter opParam);

	public abstract void writePInfo(String PInfoName, boolean readOnly);

	public abstract void writePreamble();

	public abstract void writePublishWriteAccess(String verData);

	public abstract void writeRecoveryAction();

	public abstract void writeReleaseReadAccess(String verData);

	public abstract void writeRequestAsynchronous(String reqName);

	public abstract void writeRequestSynchronous(String reqName);

	public abstract void writeResponseSend(String reqName);

	public abstract void writeSaveNonVolatileContext();

	public abstract void writeSetProvidedAvailability();

	public abstract void writeTimeResolutionServices();

	public abstract void writeTimeServices();

}
