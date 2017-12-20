/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.eliin;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class ELIInGenerator {

	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public ELIInGenerator(PlatformManagerGenerator platformGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = platformGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		ELIInWriterC ELIInHeaderWriter = new ELIInWriterC(true, pfManagerGenerator, lcp, pfManagerGenerator.getOutputDir().resolve("inc-gen/"));
		ELIInWriterC ELIInBodyWriter = new ELIInWriterC(false, pfManagerGenerator, lcp, pfManagerGenerator.getOutputDir().resolve("src-gen/"));

		// Open the file
		ELIInHeaderWriter.open();
		ELIInBodyWriter.open();

		// Write Preamble
		ELIInHeaderWriter.writePreamble();
		ELIInBodyWriter.writePreamble();

		// Write process platform ELI
		ELIInHeaderWriter.writeProcessPlatformELI();
		ELIInBodyWriter.writeProcessPlatformELI();

		// Write process service op ELI
		ELIInHeaderWriter.writeProcessServiceOpELI();
		ELIInBodyWriter.writeProcessServiceOpELI();

		// Write process protection domain ELI
		ELIInHeaderWriter.writeProcessPDELI();
		ELIInBodyWriter.writeProcessPDELI();

		// Write process ELI
		ELIInHeaderWriter.writeProcessELI();
		ELIInBodyWriter.writeProcessELI();

		// Write process ELI
		ELIInHeaderWriter.writeIncludes();
		ELIInBodyWriter.writeIncludes();

		// Close the file.
		ELIInHeaderWriter.close();
		ELIInBodyWriter.close();

	}

}
