/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.externalapi;

import java.nio.file.Path;
import java.util.List;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.systemmodel.SM_OperationParameter;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public abstract class ExternalWriter extends SourceFileWriter {
	protected SM_ComponentImplementation componentImplementation;
	protected String compImplName;

	public ExternalWriter(Path outputDir, SM_ComponentImplementation compImpl) {
		super(outputDir);
		this.componentImplementation = compImpl;
		this.compImplName = compImpl.getName();
	}

	public abstract void writePreamble();

	public abstract void writeExternalInterface(String senderOpName, List<SM_OperationParameter> params);

}
