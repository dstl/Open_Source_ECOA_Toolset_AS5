/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.containerbody;

import java.util.HashMap;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ModuleInstanceILI;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleInstance;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ContainerBodyGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ModuleImpl moduleImpl;
	private SM_ProtectionDomain pd;
	private HashMap<SM_ModuleInstance, ModuleInstanceILI> modInstILIMap;

	public ContainerBodyGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd, SM_ModuleImpl moduleImpl, HashMap<SM_ModuleInstance, ModuleInstanceILI> modInstILIMap) {
		this.platformGenerator = platformGenerator;
		this.moduleImpl = moduleImpl;
		this.pd = pd;
		this.modInstILIMap = modInstILIMap;
	}

	public void generate() {
		ContainerBodyWriterC contBodyWriter = new ContainerBodyWriterC(platformGenerator, pd, moduleImpl, modInstILIMap);

		// Open the file
		contBodyWriter.open();

		// Write the start of file
		contBodyWriter.writePreamble();

		// Write the event sends
		contBodyWriter.writeEventSents();

		// Write the deferred replies
		contBodyWriter.writeResponseSents();

		// Write the sync requests
		contBodyWriter.writeSyncRequests();

		// Write the async requests
		contBodyWriter.writeAsyncRequests();

		// Write the versioned data reads
		contBodyWriter.writeVDReads();

		// Write the versioned data writes
		contBodyWriter.writeVDWrites();

		// Write properties
		contBodyWriter.writeGetProperties();

		// Write Service Availability operations
		contBodyWriter.writeSetProvidedAvailability();
		contBodyWriter.writeGetRequiredAvailability();

		// Write lifecycle operations (only if supervisor)
		contBodyWriter.writeLifecycle();

		// Write time services operations
		contBodyWriter.writeTimeServices();

		// Write time resolution services operations
		contBodyWriter.writeTimeResolutionServices();

		// Write Fault/Logging management services
		contBodyWriter.writeFaultLoggingManagment();

		// Write Recovery Action
		contBodyWriter.writeRecoveryAction();

		// Write Save Non Volatile Context (warm start context)
		contBodyWriter.writeSaveNonVolatileContext();

		// Write PINFO
		contBodyWriter.writePINFO();

		// Write includes
		contBodyWriter.writeIncludes();

		// Close the file.
		contBodyWriter.close();
	}
}
