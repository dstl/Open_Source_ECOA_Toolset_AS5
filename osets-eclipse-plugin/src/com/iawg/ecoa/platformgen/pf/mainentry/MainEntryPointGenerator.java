/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.mainentry;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class MainEntryPointGenerator {
	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public MainEntryPointGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		MainEntryPointWriterC mainEntryPointWriter = new MainEntryPointWriterC(pfManagerGenerator.getOutputDir().resolve("src-gen/"), lcp);

		// Open the file
		mainEntryPointWriter.open();

		// Write the start of file
		mainEntryPointWriter.writePreamble();

		// Write main function
		mainEntryPointWriter.writeMain();

		// Close the file.
		mainEntryPointWriter.close();

	}

}
