/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.posixaposbind;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PosixAPOSBindGenerator {

	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public PosixAPOSBindGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		PosixAPOSBindWriterC posix2APOSHeaderWriter = new PosixAPOSBindWriterC(pfManagerGenerator, true, pfManagerGenerator.getOutputDir().resolve("inc-gen/"), lcp);
		PosixAPOSBindWriterC posix2APOSBodyWriter = new PosixAPOSBindWriterC(pfManagerGenerator, false, pfManagerGenerator.getOutputDir().resolve("src-gen/"), lcp);

		// Open the file
		posix2APOSHeaderWriter.open();
		posix2APOSBodyWriter.open();

		// Write the start of file
		posix2APOSHeaderWriter.writePreamble();
		posix2APOSBodyWriter.writePreamble();

		// Write the APOS Types
		posix2APOSHeaderWriter.writeAPOSTypes();

		// Write Time Type Decls
		posix2APOSBodyWriter.writerTimerTypesDecls();

		// Write the Semaphore API (Generic functions)
		posix2APOSHeaderWriter.writeSemaphoreAPIs();
		posix2APOSBodyWriter.writeSemaphoreAPIs();

		// Write the Socket Declaration
		posix2APOSBodyWriter.writeSocketDecls();

		// Write the Send Message Non Blocking function
		posix2APOSHeaderWriter.writeSendMessageNonBlock();
		posix2APOSBodyWriter.writeSendMessageNonBlock();

		// Write the Receive Message function
		posix2APOSHeaderWriter.writeReceiveMessage();
		posix2APOSBodyWriter.writeReceiveMessage();

		// Write the Scale Thread Priorities function
		posix2APOSHeaderWriter.writeScalePriorities();
		posix2APOSBodyWriter.writeScalePriorities();

		// Write the Create Thread function
		posix2APOSHeaderWriter.writeCreateThread();
		posix2APOSBodyWriter.writeCreateThread();

		// Write the Exit Thread function
		posix2APOSHeaderWriter.writeExitThread();
		posix2APOSBodyWriter.writeExitThread();

		// Write the Timer API functions
		posix2APOSHeaderWriter.writeTimerAPIs();
		posix2APOSBodyWriter.writeTimerAPIs();

		// Write the Wait For Event function
		posix2APOSHeaderWriter.writeWaitForEventAPI();
		posix2APOSBodyWriter.writeWaitForEventAPI();

		// Write the Signal Handler function
		posix2APOSBodyWriter.writeSigHandlerAPI();

		// Write the initialisation function
		posix2APOSHeaderWriter.writeInitialise();
		posix2APOSBodyWriter.writeInitialise();

		// Write all the includes
		posix2APOSHeaderWriter.writeIncludes();
		posix2APOSBodyWriter.writeIncludes();

		// Close the file.
		posix2APOSHeaderWriter.close();
		posix2APOSBodyWriter.close();

	}

}
