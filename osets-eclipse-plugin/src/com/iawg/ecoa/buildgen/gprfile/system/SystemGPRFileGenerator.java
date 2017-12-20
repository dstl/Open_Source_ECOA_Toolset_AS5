/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.gprfile.system;

import java.nio.file.Path;

import com.iawg.ecoa.systemmodel.SystemModel;

public class SystemGPRFileGenerator {
	private Path containerOutputDir;
	private SystemModel sysModel;

	public SystemGPRFileGenerator(Path containerOutputDir, SystemModel systemModel) {
		this.containerOutputDir = containerOutputDir;
		this.sysModel = systemModel;
	}

	public void generate() {
		SystemGPRFileWriter gprWriter = new SystemGPRFileWriter(containerOutputDir, sysModel);

		// Open the file
		gprWriter.open();

		gprWriter.writePreamble();
		gprWriter.writeGenericContent();

		// Close the file.
		gprWriter.close();
	}
}
