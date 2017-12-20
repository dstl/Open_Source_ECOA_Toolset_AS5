/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen;

import java.nio.file.Path;

import com.iawg.ecoa.buildgen.gprfile.pd.ProtectionDomainGPRFileGenerator;
import com.iawg.ecoa.buildgen.gprfile.pf.PlatformManagerGPRFileGenerator;
import com.iawg.ecoa.buildgen.gprfile.system.SystemGPRFileGenerator;
import com.iawg.ecoa.buildgen.protectiondomainmakefile.pd.ProtectionDomainMakefileGenerator;
import com.iawg.ecoa.buildgen.protectiondomainmakefile.pf.PlatformManagerMakefileGenerator;
import com.iawg.ecoa.buildgen.runsystem.RunSystemFileGenerator;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class BuildGeneratorPlatform {
	private SystemModel systemModel;
	private Path containerOutputDir;
	private Path stepsDir;

	public BuildGeneratorPlatform(SystemModel systemModel, Path stepsDir, Path containerOutputDir) {
		this.systemModel = systemModel;
		this.containerOutputDir = containerOutputDir;
		this.stepsDir = stepsDir;
	}

	public void generateBuildFiles() {
		for (SM_ProtectionDomain pd : systemModel.getDeployment().getProtectionDomains()) {
			ProtectionDomainMakefileGenerator protDomsMakefileGen = new ProtectionDomainMakefileGenerator(containerOutputDir, pd, stepsDir);
			protDomsMakefileGen.generate();

			// Generate a GPR file
			// This should point at all source areas and build everything
			ProtectionDomainGPRFileGenerator pdGPRFileGen = new ProtectionDomainGPRFileGenerator(containerOutputDir, pd, stepsDir);
			pdGPRFileGen.generate();
		}

		// Generate a GPR file for the platform manager.
		for (SM_LogicalComputingPlatform lcp : systemModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			PlatformManagerMakefileGenerator pfMakefileGen = new PlatformManagerMakefileGenerator(containerOutputDir, lcp);
			pfMakefileGen.generate();

			// This should point at all source areas and build everything
			PlatformManagerGPRFileGenerator pfManagerGPRFileGen = new PlatformManagerGPRFileGenerator(containerOutputDir, lcp);
			pfManagerGPRFileGen.generate();
		}

		// Generate an "aggregate" GPR build file to manage the building of all
		// PDs/PF_Managers in the system.
		SystemGPRFileGenerator sysGPRFileGen = new SystemGPRFileGenerator(containerOutputDir, systemModel);
		sysGPRFileGen.generate();

		// Generate a "run" script to run all PDs/PF_Managers in the system.
		RunSystemFileGenerator runSystemFileGen = new RunSystemFileGenerator(containerOutputDir, systemModel);
		runSystemFileGen.generate();

	}
}