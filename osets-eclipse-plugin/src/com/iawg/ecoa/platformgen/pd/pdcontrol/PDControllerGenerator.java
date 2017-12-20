/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.pdcontrol;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class PDControllerGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain protectionDomain;

	public PDControllerGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.protectionDomain = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();

		PDControllerWriterC pdControlHeaderWriter = new PDControllerWriterC(platformGenerator, true, directory.resolve("inc-gen/"), protectionDomain);
		PDControllerWriterC pdControlBodyWriter = new PDControllerWriterC(platformGenerator, false, directory.resolve("src-gen/"), protectionDomain);

		// Open the file
		pdControlHeaderWriter.open();
		pdControlBodyWriter.open();

		// Write the start of file
		pdControlHeaderWriter.writePreamble();
		pdControlBodyWriter.writePreamble();

		// Write the async request Id declaration
		pdControlBodyWriter.writeSequenceNumDecl();

		// Write the allocate async request Id function
		pdControlHeaderWriter.writeAllocateSequenceNum();
		pdControlBodyWriter.writeAllocateSequenceNum();

		// Write the initialisation function
		pdControlHeaderWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());
		pdControlBodyWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());

		// Write all the includes
		pdControlHeaderWriter.writeIncludes();
		pdControlBodyWriter.writeIncludes();

		// Close the file.
		pdControlHeaderWriter.close();
		pdControlBodyWriter.close();

	}

}
