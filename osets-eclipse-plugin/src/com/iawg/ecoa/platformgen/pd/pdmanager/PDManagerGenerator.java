/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.pdmanager;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class PDManagerGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain pd;

	public PDManagerGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.pd = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();

		PDManagerWriterC pdManagerHeaderWriter = new PDManagerWriterC(platformGenerator, true, directory.resolve("inc-gen/"), pd);
		PDManagerWriterC pdManagerBodyWriter = new PDManagerWriterC(platformGenerator, false, directory.resolve("src-gen/"), pd);

		// Open the file
		pdManagerHeaderWriter.open();
		pdManagerBodyWriter.open();

		// Write the start of file
		pdManagerHeaderWriter.writePreamble();
		pdManagerBodyWriter.writePreamble();

		// Write the definition of the protection domain availability structure
		pdManagerHeaderWriter.writePDAvailabilityStruct();

		// Write the declaration of the protection domain availability list
		pdManagerBodyWriter.writePDAvailablityDecl();

		// Write send service availability request function
		pdManagerHeaderWriter.writeSendServiceAvailabilityRequest();
		pdManagerBodyWriter.writeSendServiceAvailabilityRequest();

		// Write send versioned data request function
		pdManagerHeaderWriter.writeSendVersionedDataRequest();
		pdManagerBodyWriter.writeSendVersionedDataRequest();

		// Write set protection domain state function
		pdManagerHeaderWriter.writeSetPDStatus();
		pdManagerBodyWriter.writeSetPDStatus();

		// Write get protection domain state function
		pdManagerHeaderWriter.writeGetPDStatus();
		pdManagerBodyWriter.writeGetPDStatus();

		// Write send protection domain state function
		pdManagerHeaderWriter.writeSendPDStatus();
		pdManagerBodyWriter.writeSendPDStatus();

		// Write send service availability (all services) function
		pdManagerHeaderWriter.writeSendServiceAvailability();
		pdManagerBodyWriter.writeSendServiceAvailability();

		// Write send service availability (single service) function
		pdManagerHeaderWriter.writeSendSingleServiceAvailability();
		pdManagerBodyWriter.writeSendSingleServiceAvailability();

		// Write send versioned data (all repositories) function
		pdManagerHeaderWriter.writeSendVersionedData();
		pdManagerBodyWriter.writeSendVersionedData();

		// Write send versioned data (single service) function
		pdManagerHeaderWriter.writeSendSingleVersionedData();
		pdManagerBodyWriter.writeSendSingleVersionedData();

		// Write the initialisation function
		pdManagerHeaderWriter.writeInitialise();
		pdManagerBodyWriter.writeInitialise();

		// Write all the includes
		pdManagerHeaderWriter.writeIncludes();
		pdManagerBodyWriter.writeIncludes();

		// Write the end of file
		pdManagerHeaderWriter.writePostscript();
		pdManagerBodyWriter.writePostscript();

		// Close the file.
		pdManagerHeaderWriter.close();
		pdManagerBodyWriter.close();

	}

}
