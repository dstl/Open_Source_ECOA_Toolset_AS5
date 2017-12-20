/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.mainentry;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class MainEntryPointGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain pd;

	public MainEntryPointGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.pd = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();

		MainEntryPointWriterC mainEntryPointWriter = new MainEntryPointWriterC(platformGenerator, directory.resolve("src-gen/"), pd);

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
