/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.serviceopuid;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class ServiceOpUIDGenerator {
	private PlatformManagerGenerator platformGenerator;
	private SM_LogicalComputingPlatform lcp;

	public ServiceOpUIDGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.platformGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		ServiceOpUIDWriterC serviceOpUIDHeaderWriter = new ServiceOpUIDWriterC(platformGenerator.getOutputDir().resolve("inc-gen/"), lcp);

		// Open the file
		serviceOpUIDHeaderWriter.open();

		// Write the start of file
		serviceOpUIDHeaderWriter.writePreamble();

		// Write the service UID definitions
		serviceOpUIDHeaderWriter.writeServiceUIDDefs();

		// Close the file.
		serviceOpUIDHeaderWriter.close();
	}

}
