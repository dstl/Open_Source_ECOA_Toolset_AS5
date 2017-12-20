/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.runsystem;

import java.nio.file.Path;

import com.iawg.ecoa.systemmodel.SystemModel;

public class RunSystemFileGenerator {
	private Path containerOutputDir;
	private SystemModel sysModel;

	public RunSystemFileGenerator(Path containerOutputDir, SystemModel systemModel) {
		this.containerOutputDir = containerOutputDir;
		this.sysModel = systemModel;
	}

	public void generate() {
		RunSystemFileWriter runSystemWriter = new RunSystemFileWriter(containerOutputDir, sysModel);

		// Open the file
		runSystemWriter.open();

		runSystemWriter.writePreamble();
		runSystemWriter.writeGenericContent();

		// Close the file.
		runSystemWriter.close();
	}
}
