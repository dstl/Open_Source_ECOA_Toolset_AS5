/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.posixaposbind.portno;

import com.iawg.ecoa.platformgen.PlatformGenerator;

public class PortNoGenerator {
	private PlatformGenerator platformGenerator;

	public PortNoGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;
	}

	public void generate() {
		PortNoWriterC portNoHeaderWriter = new PortNoWriterC(platformGenerator.getOutputDir().resolve("include/"), platformGenerator.getSystemModel());

		// Open the file
		portNoHeaderWriter.open();

		// Write the start of file
		portNoHeaderWriter.writePreamble();

		// Write the port definitions
		portNoHeaderWriter.writePorts();

		// Close the file.
		portNoHeaderWriter.close();
	}

}
