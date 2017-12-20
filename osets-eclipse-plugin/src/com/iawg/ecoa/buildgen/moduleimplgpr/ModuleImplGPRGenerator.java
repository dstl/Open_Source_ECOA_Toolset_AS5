/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.moduleimplgpr;

import java.nio.file.Path;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

public class ModuleImplGPRGenerator {
	private SM_ModuleImpl modImpl;
	private SM_ComponentImplementation compImpl;
	private Path containerOutputDir;

	public ModuleImplGPRGenerator(Path containerOutputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl modImpl) {
		this.modImpl = modImpl;
		this.compImpl = compImpl;
		this.containerOutputDir = containerOutputDir;
	}

	public void generate() {
		Path directory = compImpl.getContainingDir().resolve(modImpl.getName());
		ModuleImplGPRWriter moduleImplGPRWriter = new ModuleImplGPRWriter(directory, modImpl, containerOutputDir);

		// Open the file
		moduleImplGPRWriter.open();

		moduleImplGPRWriter.writePreamble();

		// Write content
		moduleImplGPRWriter.writeGenericContent();

		// Write source directories.
		moduleImplGPRWriter.writeSourceDirs();

		// Close the file.
		moduleImplGPRWriter.close();
	}
}
