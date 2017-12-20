/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.serviceopuid;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ServiceOpUIDGenerator {
	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain pd;

	public ServiceOpUIDGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain protectionDomain) {
		this.platformGenerator = platformGenerator;
		this.pd = protectionDomain;
	}

	public void generate() {
		ServiceOpUIDWriterC serviceOpUIDHeaderWriter = new ServiceOpUIDWriterC(platformGenerator.getPdOutputDir().resolve("inc-gen/"), pd);

		// Open the file
		serviceOpUIDHeaderWriter.open();

		// Write the start of file
		serviceOpUIDHeaderWriter.writePreamble();

		// Write the service UID definitions
		serviceOpUIDHeaderWriter.writeServiceUIDDefs();

		// Close the file.
		serviceOpUIDHeaderWriter.close();
	}

}
