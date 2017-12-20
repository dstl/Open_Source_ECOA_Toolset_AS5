/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.gprfile.pf;

import java.nio.file.Path;

import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PlatformManagerGPRFileGenerator {
	private SM_LogicalComputingPlatform lcp;
	private Path containerOutputDir;

	public PlatformManagerGPRFileGenerator(Path containerOutputDir, SM_LogicalComputingPlatform lcp) {
		this.lcp = lcp;
		this.containerOutputDir = containerOutputDir;
	}

	public void generate() {
		// TODO - need to specify which node the PF_Manager is deployed on (not
		// just get the first!)
		PlatformManagerGPRFileWriter gprWriter = new PlatformManagerGPRFileWriter(containerOutputDir.resolve(lcp.getName() + "/" + lcp.getLogicalcomputingNodes().get(0).getName() + "/" + lcp.getName() + "_PF_Manager"), lcp);

		// Open the file
		gprWriter.open();

		gprWriter.writePreamble();
		gprWriter.writeSourceDirs();
		gprWriter.writeGenericContent();

		// Close the file.
		gprWriter.close();
	}
}
