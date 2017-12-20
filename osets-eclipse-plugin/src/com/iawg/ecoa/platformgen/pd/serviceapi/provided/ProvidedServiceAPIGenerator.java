/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.serviceapi.provided;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ProvidedServiceAPIGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ComponentInstance compInst;
	private SM_ServiceInstance serviceInst;
	private SM_ProtectionDomain protectionDomain;

	public ProvidedServiceAPIGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd, SM_ComponentInstance compInst, SM_ServiceInstance serviceInst) {
		this.platformGenerator = platformGenerator;
		this.compInst = compInst;
		this.serviceInst = serviceInst;
		this.protectionDomain = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + compInst.getName());

		ProvidedServiceAPIWriterC provServiceAPIHeaderWriter = new ProvidedServiceAPIWriterC(platformGenerator, true, directory, compInst, protectionDomain, serviceInst);
		ProvidedServiceAPIWriterC provServiceAPIBodyWriter = new ProvidedServiceAPIWriterC(platformGenerator, false, directory, compInst, protectionDomain, serviceInst);

		// Open the file
		provServiceAPIHeaderWriter.open();
		provServiceAPIBodyWriter.open();

		// Write the start of file
		provServiceAPIHeaderWriter.writePreamble();
		provServiceAPIBodyWriter.writePreamble();

		// Write the set service availability operation
		provServiceAPIHeaderWriter.writeSetServiceAvailability();
		provServiceAPIBodyWriter.writeSetServiceAvailability();

		// Write the Container Interface side operations
		provServiceAPIHeaderWriter.writeModuleToContainerCalls();
		provServiceAPIBodyWriter.writeModuleToContainerCalls();

		// Write the Module Interface side operations
		provServiceAPIHeaderWriter.writeContainerToModuleCalls();
		provServiceAPIBodyWriter.writeContainerToModuleCalls();

		// Write the Initialise function
		provServiceAPIHeaderWriter.writeInitialise();
		provServiceAPIBodyWriter.writeInitialise();

		// Write the includes
		provServiceAPIHeaderWriter.writeIncludes();
		provServiceAPIBodyWriter.writeIncludes();

		// Close the file.
		provServiceAPIHeaderWriter.close();
		provServiceAPIBodyWriter.close();
	}

}
