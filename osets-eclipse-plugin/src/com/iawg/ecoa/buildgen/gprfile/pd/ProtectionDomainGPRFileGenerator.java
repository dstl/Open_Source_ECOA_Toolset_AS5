/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.gprfile.pd;

import java.nio.file.Path;

import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ProtectionDomainGPRFileGenerator {
	private SM_ProtectionDomain pd;
	private Path containerOutputDir;
	private Path stepsDir;

	public ProtectionDomainGPRFileGenerator(Path containerOutputDir, SM_ProtectionDomain pd, Path stepsDir) {
		this.pd = pd;
		this.containerOutputDir = containerOutputDir;
		this.stepsDir = stepsDir;
	}

	public void generate() {
		ProtectionDomainGPRFileWriter gprWriter = new ProtectionDomainGPRFileWriter(containerOutputDir.resolve(pd.getLogicalComputingNode().getLogicalComputingPlatform().getName() + "/" + pd.getLogicalComputingNode().getName() + "/" + pd.getName()), pd, stepsDir);

		// Open the file
		gprWriter.open();

		gprWriter.writeModuleLibraryWiths();
		gprWriter.writePreamble();
		gprWriter.writeSourceDirs();
		gprWriter.writeGenericContent();

		// Close the file.
		gprWriter.close();
	}
}
