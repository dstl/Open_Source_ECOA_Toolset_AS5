/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.moduleimplmakefile;

import java.nio.file.Path;

import com.iawg.ecoa.ECOA_System_Model.ImplLanguage;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

public class ModuleImplMakefileGenerator {
	private SM_ModuleImpl modImpl;
	private SM_ComponentImplementation compImpl;
	private Path containerOutputDir;

	public ModuleImplMakefileGenerator(Path containerOutputDir, SM_ComponentImplementation compImpl, SM_ModuleImpl modImpl) {
		this.modImpl = modImpl;
		this.compImpl = compImpl;

		// Get the relative directory between the containerOuputDir and the
		// projectLocation (outputDir)
		this.containerOutputDir = containerOutputDir;
	}

	public void generate() {
		ModuleImplMakefileWriter moduleImplMakefileWriter = null;

		Path directory = compImpl.getContainingDir().resolve(modImpl.getName());
		if (modImpl.getLanguageType() == ImplLanguage.C) {
			moduleImplMakefileWriter = new ModuleImplMakefileWriterC(directory, modImpl, containerOutputDir);
		} else if (modImpl.getLanguageType() == ImplLanguage.ADA) {
			moduleImplMakefileWriter = new ModuleImplMakefileWriterAda(directory, modImpl, containerOutputDir);
		} else if (modImpl.getLanguageType() == ImplLanguage.CPP) {
			moduleImplMakefileWriter = new ModuleImplMakefileWriterCPP(directory, modImpl, containerOutputDir);
		}

		// Open the file
		moduleImplMakefileWriter.open();

		moduleImplMakefileWriter.writePreamble();
		moduleImplMakefileWriter.writeSources();
		moduleImplMakefileWriter.writeHeaders();
		moduleImplMakefileWriter.writeObjects();
		moduleImplMakefileWriter.writeTarget();

		// Close the file.
		moduleImplMakefileWriter.close();
	}
}
