/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.pdid;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PDIDGenerator {
	private PlatformGenerator platformGenerator;
	private SM_LogicalComputingPlatform lcp;

	public PDIDGenerator(PlatformGenerator platformGenerator, SM_LogicalComputingPlatform lcp) {
		this.platformGenerator = platformGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		PDIDWriterC pdIDHeaderWriter = new PDIDWriterC(platformGenerator.getOutputDir().resolve(lcp.getName() + "/include/"), lcp);

		// Open the file
		pdIDHeaderWriter.open();

		// Write the start of file
		pdIDHeaderWriter.writePreamble();

		// Write the PD ID definitions
		pdIDHeaderWriter.writePDIDs();

		// Close the file.
		pdIDHeaderWriter.close();
	}

}
