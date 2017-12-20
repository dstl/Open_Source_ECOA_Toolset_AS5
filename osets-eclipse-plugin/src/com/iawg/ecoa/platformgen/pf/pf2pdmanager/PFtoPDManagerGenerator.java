/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.pf2pdmanager;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PFtoPDManagerGenerator {

	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public PFtoPDManagerGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		PFtoPDManagerWriterC pf2pdManagerHeaderWriter = new PFtoPDManagerWriterC(pfManagerGenerator, true, pfManagerGenerator.getOutputDir().resolve("inc-gen/"), lcp);
		PFtoPDManagerWriterC pf2pdManagerBodyWriter = new PFtoPDManagerWriterC(pfManagerGenerator, false, pfManagerGenerator.getOutputDir().resolve("src-gen/"), lcp);

		// Open the file
		pf2pdManagerHeaderWriter.open();
		pf2pdManagerBodyWriter.open();

		// Write the start of file
		pf2pdManagerHeaderWriter.writePreamble();
		pf2pdManagerBodyWriter.writePreamble();

		// Write the definition of the protection domain availability structure
		pf2pdManagerHeaderWriter.writePDAvailabilityStruct();

		// Write the declaration of the protection domain availability list
		pf2pdManagerBodyWriter.writePDAvailablityDecl();

		// Write the number of PDs in UP state declaration
		pf2pdManagerBodyWriter.writeNumOfPDsUpDecl();

		// Write set protection domain state function
		pf2pdManagerHeaderWriter.writeSetPDStatus();
		pf2pdManagerBodyWriter.writeSetPDStatus();

		// Write get protection domain state function
		pf2pdManagerHeaderWriter.writeGetPDStatus();
		pf2pdManagerBodyWriter.writeGetPDStatus();

		// Write send protection domain state function
		pf2pdManagerHeaderWriter.writeSendPDStatus();
		pf2pdManagerBodyWriter.writeSendPDStatus();

		// Write send service availability request function
		pf2pdManagerHeaderWriter.writeSendServiceAvailabilityRequest();
		pf2pdManagerBodyWriter.writeSendServiceAvailabilityRequest();

		// Write send single service availability function
		pf2pdManagerHeaderWriter.writeSendSingleServiceAvailability();
		pf2pdManagerBodyWriter.writeSendSingleServiceAvailability();

		// Write send versioned data (all repositories) function
		pf2pdManagerHeaderWriter.writeSendVersionedData();
		pf2pdManagerBodyWriter.writeSendVersionedData();

		// Write send versioned data (single service) function
		pf2pdManagerHeaderWriter.writeSendSingleVersionedData();
		pf2pdManagerBodyWriter.writeSendSingleVersionedData();

		// Write the initialisation function
		pf2pdManagerHeaderWriter.writeInitialise();
		pf2pdManagerBodyWriter.writeInitialise();

		// Write all the includes
		pf2pdManagerHeaderWriter.writeIncludes();
		pf2pdManagerBodyWriter.writeIncludes();

		// Write the end of file
		pf2pdManagerHeaderWriter.writePostscript();
		pf2pdManagerBodyWriter.writePostscript();

		// Close the file.
		pf2pdManagerHeaderWriter.close();
		pf2pdManagerBodyWriter.close();

	}

}
