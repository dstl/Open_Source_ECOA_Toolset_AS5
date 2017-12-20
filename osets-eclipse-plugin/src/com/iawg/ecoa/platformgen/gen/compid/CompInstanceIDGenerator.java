/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.compid;

import com.iawg.ecoa.platformgen.PlatformGenerator;

public class CompInstanceIDGenerator {
	private PlatformGenerator platformGenerator;

	public CompInstanceIDGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;
	}

	public void generate() {
		CompInstanceIDWriterC compModIDHeaderWriter = new CompInstanceIDWriterC(platformGenerator.getOutputDir().resolve("include/"), platformGenerator.getSystemModel());

		// Open the file
		compModIDHeaderWriter.open();

		// Write the start of file
		compModIDHeaderWriter.writePreamble();

		// Write the component id definitions
		compModIDHeaderWriter.writeCompInstIDDefs();

		// Close the file.
		compModIDHeaderWriter.close();
	}

}
