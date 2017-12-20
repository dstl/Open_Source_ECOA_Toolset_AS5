/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.serviceapi.required;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;
import com.iawg.ecoa.systemmodel.componentdefinition.serviceinstance.SM_ServiceInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class RequiredServiceAPIGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ComponentInstance compInst;
	private SM_ServiceInstance serviceInst;
	private SM_ProtectionDomain protectionDomain;

	public RequiredServiceAPIGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd, SM_ComponentInstance compInst, SM_ServiceInstance serviceInst) {
		this.platformGenerator = platformGenerator;
		this.compInst = compInst;
		this.serviceInst = serviceInst;
		this.protectionDomain = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + compInst.getName());

		RequiredServiceAPIWriterC serviceAPIHeaderWriter = new RequiredServiceAPIWriterC(platformGenerator, true, directory, compInst, protectionDomain, serviceInst);
		RequiredServiceAPIWriterC serviceAPIBodyWriter = new RequiredServiceAPIWriterC(platformGenerator, false, directory, compInst, protectionDomain, serviceInst);

		// Open the file
		serviceAPIHeaderWriter.open();
		serviceAPIBodyWriter.open();

		// Write the start of file
		serviceAPIHeaderWriter.writePreamble();
		serviceAPIBodyWriter.writePreamble();

		// Write the active service provider declaration
		serviceAPIBodyWriter.writeActiveServiceProviderDecl();

		// Write the client lookup declaration and operations
		serviceAPIBodyWriter.WriteClientLookupDecl();
		serviceAPIBodyWriter.WriteSetClientLookup();
		;
		serviceAPIBodyWriter.WriteGetClientLookup();
		serviceAPIHeaderWriter.WriteRemoveClientLookup();
		serviceAPIBodyWriter.WriteRemoveClientLookup();

		// Write the service availability operations
		serviceAPIHeaderWriter.writeGetServiceAvailability();
		serviceAPIBodyWriter.writeGetServiceAvailability();

		// Write the service availability notification operations
		serviceAPIHeaderWriter.writeServiceAvailabilityNotificationCalls();
		serviceAPIBodyWriter.writeServiceAvailabilityNotificationCalls();

		// Write the Container Interface side operations
		serviceAPIHeaderWriter.writeModuleToContainerCalls();
		serviceAPIBodyWriter.writeModuleToContainerCalls();

		// Write the Module Interface side operations
		serviceAPIHeaderWriter.writeContainerToModuleCalls();
		serviceAPIBodyWriter.writeContainerToModuleCalls();

		// Write the Initialise function
		serviceAPIHeaderWriter.writeInitialise();
		serviceAPIBodyWriter.writeInitialise();

		// Write the includes
		serviceAPIHeaderWriter.writeIncludes();
		serviceAPIBodyWriter.writeIncludes();

		// Close the file.
		serviceAPIHeaderWriter.close();
		serviceAPIBodyWriter.close();
	}

}
