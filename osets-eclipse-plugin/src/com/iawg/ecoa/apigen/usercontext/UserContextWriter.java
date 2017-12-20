/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.usercontext;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.ToolConfig;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

public abstract class UserContextWriter extends SourceFileWriter {
	protected SM_ModuleImpl moduleImpl;
	protected String moduleImplName;
	protected SystemModel systemModel = null;
	protected ToolConfig toolConfig = null;

	public UserContextWriter(SystemModel systemModel, ToolConfig toolConfig, Path outputDir, SM_ModuleImpl moduleImpl) {
		super(outputDir);
		this.systemModel = systemModel;
		this.toolConfig = toolConfig;
		this.moduleImpl = moduleImpl;
		this.moduleImplName = moduleImpl.getName();
	}

	public abstract void writePreamble();

	public abstract void writeUserContext();

}
