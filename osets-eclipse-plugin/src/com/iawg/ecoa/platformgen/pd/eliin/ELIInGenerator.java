/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.eliin;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ELIInGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain protectionDomain;

	public ELIInGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.protectionDomain = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();

		ELIInWriterC ELIInHeaderWriter = new ELIInWriterC(true, platformGenerator, protectionDomain, directory.resolve("inc-gen/"));
		ELIInWriterC ELIInBodyWriter = new ELIInWriterC(false, platformGenerator, protectionDomain, directory.resolve("src-gen/"));

		// Open the file
		ELIInHeaderWriter.open();
		ELIInBodyWriter.open();

		// Write Preamble
		ELIInHeaderWriter.writePreamble();
		ELIInBodyWriter.writePreamble();

		// Write process platform ELI
		ELIInHeaderWriter.writeProcessPDELI();
		ELIInBodyWriter.writeProcessPDELI();

		// Write process service op ELI
		ELIInHeaderWriter.writeProcessServiceOpELI();
		ELIInBodyWriter.writeProcessServiceOpELI();

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
