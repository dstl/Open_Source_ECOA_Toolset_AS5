/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.serviceuid;

import com.iawg.ecoa.platformgen.PlatformGenerator;

public class ServiceUIDGenerator {
	private PlatformGenerator platformGenerator;

	public ServiceUIDGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;
	}

	public void generate() {
		ServiceUIDWriterC serviceUIDHeaderWriter = new ServiceUIDWriterC(platformGenerator.getOutputDir().resolve("include/"), platformGenerator.getSystemModel());

		// Open the file
		serviceUIDHeaderWriter.open();

		// Write the start of file
		serviceUIDHeaderWriter.writePreamble();

		// Write the service UID definitions
		serviceUIDHeaderWriter.writeServiceUIDDefs();

		// Close the file.
		serviceUIDHeaderWriter.close();
	}

}
