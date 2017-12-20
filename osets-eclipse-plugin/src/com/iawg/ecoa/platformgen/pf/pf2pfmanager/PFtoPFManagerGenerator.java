/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.pf2pfmanager;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PFtoPFManagerGenerator {

	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public PFtoPFManagerGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		PFtoPFManagerWriterC pf2pfManagerHeaderWriter = new PFtoPFManagerWriterC(pfManagerGenerator, true, pfManagerGenerator.getOutputDir().resolve("inc-gen/"), lcp);
		PFtoPFManagerWriterC pf2pfManagerBodyWriter = new PFtoPFManagerWriterC(pfManagerGenerator, false, pfManagerGenerator.getOutputDir().resolve("src-gen/"), lcp);

		// Open the file
		pf2pfManagerHeaderWriter.open();
		pf2pfManagerBodyWriter.open();

		// Write the start of file
		pf2pfManagerHeaderWriter.writePreamble();
		pf2pfManagerBodyWriter.writePreamble();

		// Write the platform ID enum
		pf2pfManagerHeaderWriter.writePlatformIDEnum();

		// Write the definition of the protection domain availability structure
		pf2pfManagerHeaderWriter.writePFAvailabilityStruct();

		// Write the declaration of the protection domain availability list
		pf2pfManagerBodyWriter.writePFAvailablityDecl();

		// Write send service availability request function
		pf2pfManagerHeaderWriter.writeSendServiceAvailabilityRequest();
		pf2pfManagerBodyWriter.writeSendServiceAvailabilityRequest();

		// Write send versioned data request function
		pf2pfManagerHeaderWriter.writeSendVersionedDataRequest();
		pf2pfManagerBodyWriter.writeSendVersionedDataRequest();

		// Write set protection domain state function
		pf2pfManagerHeaderWriter.writeSetPFStatus();
		pf2pfManagerBodyWriter.writeSetPFStatus();

		// Write get protection domain state function
		pf2pfManagerHeaderWriter.writeGetPFStatus();
		pf2pfManagerBodyWriter.writeGetPFStatus();

		// Write send protection domain state function
		pf2pfManagerHeaderWriter.writeSendPFStatus();
		pf2pfManagerBodyWriter.writeSendPFStatus();

		// Write send service availability (all services) function
		pf2pfManagerHeaderWriter.writeSendServiceAvailability();
		pf2pfManagerBodyWriter.writeSendServiceAvailability();

		// Write send service availability (single service) function
		pf2pfManagerHeaderWriter.writeSendSingleServiceAvailability();
		pf2pfManagerBodyWriter.writeSendSingleServiceAvailability();

		// Write the initialisation function
		pf2pfManagerHeaderWriter.writeInitialise();
		pf2pfManagerBodyWriter.writeInitialise();

		// Write all the includes
		pf2pfManagerHeaderWriter.writeIncludes();
		pf2pfManagerBodyWriter.writeIncludes();

		// Write the end of file
		pf2pfManagerHeaderWriter.writePostscript();
		pf2pfManagerBodyWriter.writePostscript();

		// Close the file.
		pf2pfManagerHeaderWriter.close();
		pf2pfManagerBodyWriter.close();

	}

}
