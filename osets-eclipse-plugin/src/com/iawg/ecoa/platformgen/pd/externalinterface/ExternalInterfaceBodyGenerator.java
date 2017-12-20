/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.externalinterface;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;

public class ExternalInterfaceBodyGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ComponentImplementation compImpl;
	private SM_DeployedModInst depModInst;

	public ExternalInterfaceBodyGenerator(PlatformGenerator platformGenerator, SM_ComponentImplementation compImpl, SM_DeployedModInst depModInst) {
		this.platformGenerator = platformGenerator;
		this.compImpl = compImpl;
		this.depModInst = depModInst;
	}

	public void generate() {
		ExternalInterfaceBodyWriterC extBodyWriter = new ExternalInterfaceBodyWriterC(platformGenerator, compImpl, depModInst);

		// Open the file
		extBodyWriter.open();

		// Write the start of file
		extBodyWriter.writePreamble();

		// Write the event sends
		extBodyWriter.writeExternalInterfaces();

		// Write includes
		extBodyWriter.writeIncludes();

		// Close the file.
		extBodyWriter.close();
	}

}
