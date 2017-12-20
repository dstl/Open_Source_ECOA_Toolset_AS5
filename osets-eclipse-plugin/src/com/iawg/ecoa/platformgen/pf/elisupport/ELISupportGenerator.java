/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.elisupport;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class ELISupportGenerator {

	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public ELISupportGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		ELISupportWriterC eliSupportHeaderWriter = new ELISupportWriterC(pfManagerGenerator, true, pfManagerGenerator.getOutputDir().resolve("inc-gen/"), lcp);
		ELISupportWriterC eliSupportBodyWriter = new ELISupportWriterC(pfManagerGenerator, false, pfManagerGenerator.getOutputDir().resolve("src-gen/"), lcp);

		// Open the file
		eliSupportHeaderWriter.open();
		eliSupportBodyWriter.open();

		// Write the start of file
		eliSupportHeaderWriter.writePreamble();
		eliSupportBodyWriter.writePreamble();

		eliSupportHeaderWriter.writeVCInfoStructDecl();

		// Write ELI send semaphore variable declaration
		eliSupportBodyWriter.writeSemaphoreIDDecl();

		// Write channel counter variable declaration
		eliSupportBodyWriter.writeChannelCounterDecl();

		// Write the UID to VC function
		eliSupportBodyWriter.writeUIDtoVC();

		// Write the send ELI message function
		eliSupportHeaderWriter.writeSendELI();
		eliSupportBodyWriter.writeSendELI();

		// Write the send PD ELI message function
		eliSupportHeaderWriter.writeSendPDELI();
		eliSupportBodyWriter.writeSendPDELI();

		// Write the send platform ELI message function
		eliSupportHeaderWriter.writeSendPlatformELI();
		eliSupportBodyWriter.writeSendPlatformELI();

		// Write the receive ELI message function
		eliSupportHeaderWriter.writeReceiveELI();
		eliSupportBodyWriter.writeReceiveELI();

		// Write the initialise function
		eliSupportHeaderWriter.writeInitialise();
		eliSupportBodyWriter.writeInitialise();

		// Write the includes
		eliSupportHeaderWriter.writeIncludes();
		eliSupportBodyWriter.writeIncludes();

		// Write the end of the file
		eliSupportHeaderWriter.writePostamble();
		eliSupportHeaderWriter.writePostamble();

		// Close the file.
		eliSupportHeaderWriter.close();
		eliSupportBodyWriter.close();
	}

}
