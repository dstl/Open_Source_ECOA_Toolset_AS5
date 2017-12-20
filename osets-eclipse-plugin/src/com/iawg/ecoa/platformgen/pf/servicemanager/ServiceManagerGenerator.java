/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.servicemanager;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class ServiceManagerGenerator {

	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public ServiceManagerGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		ServiceManagerWriterC servManagerHeaderWriter = new ServiceManagerWriterC(pfManagerGenerator, true, pfManagerGenerator.getOutputDir().resolve("inc-gen/"), lcp);
		ServiceManagerWriterC servManagerBodyWriter = new ServiceManagerWriterC(pfManagerGenerator, false, pfManagerGenerator.getOutputDir().resolve("src-gen/"), lcp);

		// Open the file
		servManagerHeaderWriter.open();
		servManagerBodyWriter.open();

		// Write the start of file
		servManagerHeaderWriter.writePreamble();
		servManagerBodyWriter.writePreamble();

		// Write the definition of the service availability structure
		servManagerHeaderWriter.writeServiceAvailabilityStruct();

		// Write the declaration of the service availability list
		servManagerBodyWriter.writeServiceAvailablityDecl();

		// Write the get service availability function
		servManagerHeaderWriter.writeGetServiceAvail();
		servManagerBodyWriter.writeGetServiceAvail();

		// Write the set service availability function
		servManagerHeaderWriter.writeSetServiceAvail();
		servManagerBodyWriter.writeSetServiceAvail();

		// Write the set service availability function
		servManagerHeaderWriter.writeSetProviderUnavailable();
		servManagerBodyWriter.writeSetProviderUnavailable();

		// Write the initialisation function
		servManagerHeaderWriter.writeInitialise();
		servManagerBodyWriter.writeInitialise();

		// Write all the includes
		servManagerHeaderWriter.writeIncludes();
		servManagerBodyWriter.writeIncludes();

		// Close the file.
		servManagerHeaderWriter.close();
		servManagerBodyWriter.close();
	}
}
