/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.pfcontrol;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PFControllerGenerator {

	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public PFControllerGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		PFControllerWriterC pfControlHeaderWriter = new PFControllerWriterC(pfManagerGenerator, true, pfManagerGenerator.getOutputDir().resolve("inc-gen/"), lcp);
		PFControllerWriterC pfControlBodyWriter = new PFControllerWriterC(pfManagerGenerator, false, pfManagerGenerator.getOutputDir().resolve("src-gen/"), lcp);

		// Open the file
		pfControlHeaderWriter.open();
		pfControlBodyWriter.open();

		// Write the start of file
		pfControlHeaderWriter.writePreamble();
		pfControlBodyWriter.writePreamble();

		// Write the initialisation function
		pfControlHeaderWriter.writeInitialise(pfManagerGenerator.getUnderlyingPlatformInstantiation());
		pfControlBodyWriter.writeInitialise(pfManagerGenerator.getUnderlyingPlatformInstantiation());

		// Write all the includes
		pfControlHeaderWriter.writeIncludes();
		pfControlBodyWriter.writeIncludes();

		// Close the file.
		pfControlHeaderWriter.close();
		pfControlBodyWriter.close();
	}
}
