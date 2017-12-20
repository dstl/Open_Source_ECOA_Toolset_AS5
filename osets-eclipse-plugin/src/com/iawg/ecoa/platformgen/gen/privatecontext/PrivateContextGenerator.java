/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.privatecontext;

import com.iawg.ecoa.platformgen.PlatformGenerator;

public class PrivateContextGenerator {

	private PlatformGenerator platformGenerator;

	public PrivateContextGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;
	}

	public void generate() {
		PrivateContextWriterC privateContextHeaderWriter = new PrivateContextWriterC(platformGenerator.getOutputDir().resolve("include/"));

		// Open the file
		privateContextHeaderWriter.open();

		// Write the start of file
		privateContextHeaderWriter.writePreamble();

		privateContextHeaderWriter.writeIncludes();

		// Write the module id definitions
		privateContextHeaderWriter.writePrivateContext();

		// Close the file.
		privateContextHeaderWriter.close();
	}

}
