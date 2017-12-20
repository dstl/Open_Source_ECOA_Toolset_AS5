/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.elisupport;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class ELISupportGenerator {

	private PlatformGenerator platformGenerator;
	private SM_LogicalComputingPlatform lcp;
	private SM_ProtectionDomain pd;

	public ELISupportGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.pd = pd;
		this.lcp = pd.getLogicalComputingNode().getLogicalComputingPlatform();
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();

		ELISupportWriterC eliSupportHeaderWriter = new ELISupportWriterC(platformGenerator, true, directory.resolve("inc-gen/"), lcp, pd);
		ELISupportWriterC eliSupportBodyWriter = new ELISupportWriterC(platformGenerator, false, directory.resolve("src-gen/"), lcp, pd);

		// Open the file
		eliSupportHeaderWriter.open();
		eliSupportBodyWriter.open();

		// Write the start of file
		eliSupportHeaderWriter.writePreamble();
		eliSupportBodyWriter.writePreamble();

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

		// Write the receive ELI message function
		eliSupportHeaderWriter.writeReceiveELI();
		eliSupportBodyWriter.writeReceiveELI();

		// Write the initialise function
		eliSupportHeaderWriter.writeInitialise();
		eliSupportBodyWriter.writeInitialise();

		// Write the includes
		eliSupportHeaderWriter.writeIncludes();
		eliSupportBodyWriter.writeIncludes();

		// Close the file.
		eliSupportHeaderWriter.close();
		eliSupportBodyWriter.close();
	}

}
