/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.iawg.ecoa.buildgen.componentimplmakefile.ComponentImplMakefileGenerator;
import com.iawg.ecoa.buildgen.componentsmakefile.ComponentsMakefileGenerator;
import com.iawg.ecoa.buildgen.moduleimplgpr.ModuleImplGPRGenerator;
import com.iawg.ecoa.buildgen.moduleimplmakefile.ModuleImplMakefileGenerator;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

public class BuildGeneratorAPI {

	private SystemModel systemModel;
	private Path stepsDir;
	private Path containerOutputDir;

	public BuildGeneratorAPI(SystemModel systemModel, Path stepsDir, Path containerOutputDir) {
		this.systemModel = systemModel;
		this.stepsDir = stepsDir;
		this.containerOutputDir = containerOutputDir;
	}

	public void generateBuildFiles() {
		List<SM_ComponentImplementation> compImpls = new ArrayList<SM_ComponentImplementation>(systemModel.getComponentImplementations().getImplementations().values());

		ComponentsMakefileGenerator componentsMakefileGen = new ComponentsMakefileGenerator(stepsDir, compImpls);

		componentsMakefileGen.generate();

		for (SM_ComponentImplementation compImpl : compImpls) {
			ComponentImplMakefileGenerator componentImplMakefileGen = new ComponentImplMakefileGenerator(compImpl);

			componentImplMakefileGen.generate();

			for (SM_ModuleImpl modImpl : compImpl.getModuleImplementations().values()) {
				ModuleImplMakefileGenerator moduleImplMakefileGen = new ModuleImplMakefileGenerator(containerOutputDir, compImpl, modImpl);

				moduleImplMakefileGen.generate();

				// Generate a GPR library project file.
				ModuleImplGPRGenerator moduleImplGPRGen = new ModuleImplGPRGenerator(containerOutputDir, compImpl, modImpl);
				moduleImplGPRGen.generate();
			}
		}
	}
}