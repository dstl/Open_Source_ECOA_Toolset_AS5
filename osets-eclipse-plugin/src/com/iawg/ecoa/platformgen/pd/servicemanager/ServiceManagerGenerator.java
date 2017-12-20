/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.servicemanager;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ServiceManagerGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain protectionDomain;

	public ServiceManagerGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.protectionDomain = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();

		ServiceManagerWriterC servManagerHeaderWriter = new ServiceManagerWriterC(platformGenerator, true, directory.resolve("inc-gen/"), protectionDomain);
		ServiceManagerWriterC servManagerBodyWriter = new ServiceManagerWriterC(platformGenerator, false, directory.resolve("src-gen/"), protectionDomain);

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
