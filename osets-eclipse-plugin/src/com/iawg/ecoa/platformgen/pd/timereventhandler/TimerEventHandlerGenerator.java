/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.timereventhandler;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class TimerEventHandlerGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain protectionDomain;

	public TimerEventHandlerGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.protectionDomain = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();

		TimerEventHandlerWriterC tehHeaderWriter = new TimerEventHandlerWriterC(platformGenerator, true, directory.resolve("inc-gen/"), protectionDomain);
		TimerEventHandlerWriterC tehBodyWriter = new TimerEventHandlerWriterC(platformGenerator, false, directory.resolve("src-gen/"), protectionDomain);

		// Open the file
		tehHeaderWriter.open();
		tehBodyWriter.open();

		// Write the start of file
		tehHeaderWriter.writePreamble();
		tehBodyWriter.writePreamble();

		// Write the event handler function
		tehHeaderWriter.writeEventHandler();
		tehBodyWriter.writeEventHandler();

		// Write the initialisation function
		tehHeaderWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());
		tehBodyWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());

		// Write all the includes
		tehHeaderWriter.writeIncludes();
		tehBodyWriter.writeIncludes();

		// Close the file.
		tehHeaderWriter.close();
		tehBodyWriter.close();

	}

}
