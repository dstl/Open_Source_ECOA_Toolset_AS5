/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.containertypes;

import java.nio.file.Path;
import java.util.ArrayList;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.types.SM_Type;

/**
 * This class is an abstract class that declares the methods that must be
 * implemented by source code language-specific classes that extend it.
 * 
 */
public abstract class ContainerTypesWriter extends SourceFileWriter {
	protected ArrayList<String> includeList = new ArrayList<String>();

	protected SM_ModuleImpl moduleImpl;
	protected String moduleImplName;
	protected SM_ComponentImplementation componentImplementation;
	protected SystemModel systemModel;

	ContainerTypesWriter(SystemModel systemModel, Path outputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl moduleImpl) {
		super(outputDir);
		this.systemModel = systemModel;
		this.componentImplementation = compImpl;
		this.moduleImpl = moduleImpl;
		this.moduleImplName = moduleImpl.getName();
	}

	public abstract void writePreamble();

	public abstract void writeVDHandle(String opName, SM_Type opType);

	public abstract void writeSupervisionTypes();

	public abstract void writeIncludes();

}
