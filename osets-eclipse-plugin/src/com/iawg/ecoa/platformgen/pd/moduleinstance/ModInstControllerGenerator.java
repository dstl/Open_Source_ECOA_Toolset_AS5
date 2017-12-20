/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleinstance;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.ModuleInstanceILI;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedModInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class ModInstControllerGenerator {

	private PlatformGenerator platformGenerator;
	private SM_DeployedModInst deployedModInst;
	private SM_ProtectionDomain protectionDomain;
	private ModuleInstanceILI modInstILI;

	public ModInstControllerGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd, SM_DeployedModInst deployedModInst, ModuleInstanceILI modInstILI) {
		this.platformGenerator = platformGenerator;
		this.deployedModInst = deployedModInst;
		this.protectionDomain = pd;
		this.modInstILI = modInstILI;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + deployedModInst.getCompInstance().getName() + "/" + deployedModInst.getModInstance().getName());

		ModInstContWriterC modInstContHeaderWriter = new ModInstContWriterC(platformGenerator, true, directory, deployedModInst, protectionDomain, modInstILI);
		ModInstContWriterC modInstContBodyWriter = new ModInstContWriterC(platformGenerator, false, directory, deployedModInst, protectionDomain, modInstILI);

		// Open the file
		modInstContHeaderWriter.open();
		modInstContBodyWriter.open();

		// Write the start of file
		modInstContHeaderWriter.writePreamble();
		modInstContBodyWriter.writePreamble();

		// Write the fifo queue size
		modInstContBodyWriter.writeFifoListDefinition();

		// Write the module queue id declaration
		modInstContBodyWriter.writeModuleQueueIDDecl();

		// Write the synchronous request queue id declarations
		modInstContHeaderWriter.writeSyncRequestQueueIDDecls();
		modInstContBodyWriter.writeSyncRequestQueueIDDecls();

		// Write the ILI message declarations (one for the queue entry and one
		// for the response).
		modInstContBodyWriter.writeILIMessageDecls();

		// Write the Pinfo file declarations
		modInstContBodyWriter.writePinfoFileDecls();

		// Write the private context
		modInstContBodyWriter.writePrivateContext();

		// Write the "is queue full" function
		modInstContBodyWriter.writeIsQueueFull();

		// Write the process queue function
		modInstContHeaderWriter.writeProcessQueue();
		modInstContBodyWriter.writeProcessQueue();

		// Write the queue message function
		modInstContHeaderWriter.writeQueueMessage();
		modInstContBodyWriter.writeQueueMessage();

		// Write the FIFO Accessor functions
		modInstContBodyWriter.writeFIFOAccessors();

		// Write the get state function
		modInstContHeaderWriter.writeGetState();
		modInstContBodyWriter.writeGetState();

		// Write the get client lookup functions
		modInstContBodyWriter.writeClientLookupDecl();
		modInstContBodyWriter.writeSetClientLookup();
		// Get needs to be visible on spec for container body to use...
		modInstContHeaderWriter.writeGetClientLookup();
		modInstContBodyWriter.writeGetClientLookup();

		// Write the lifecycle operations
		modInstContHeaderWriter.WriteLifecycleOps();
		modInstContBodyWriter.WriteLifecycleOps();

		// Write the supervision operations (if supervision module...)
		modInstContHeaderWriter.writeSupervisionOps();
		modInstContBodyWriter.writeSupervisionOps();

		// Write the event received operations
		modInstContHeaderWriter.WriteEventReceives();
		modInstContBodyWriter.WriteEventReceives();

		// Write the request received operations
		modInstContHeaderWriter.writeRequestReceives();
		modInstContBodyWriter.writeRequestReceives();

		// Write the response received operations
		modInstContHeaderWriter.writeResponseReceives();
		modInstContBodyWriter.writeResponseReceives();

		// Write the updated (VD) operations
		modInstContHeaderWriter.writeUpdates();
		modInstContBodyWriter.writeUpdates();

		// Write the supervision notification function (only for supervision
		// modules, but need to always call to remove "file_structure" tag)
		modInstContHeaderWriter.writeSupervisionNotificationFunction();
		modInstContBodyWriter.writeSupervisionNotificationFunction();

		// Write the PINFO functions for read/write/seek
		modInstContHeaderWriter.writePINFO();
		modInstContBodyWriter.writePINFO();

		// Write the initialise function
		modInstContHeaderWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());
		modInstContBodyWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());

		// Write the reinitialise function
		modInstContHeaderWriter.writeReinitialise();
		modInstContBodyWriter.writeReinitialise();

		modInstContHeaderWriter.writeZeroWarmContext();
		modInstContBodyWriter.writeZeroWarmContext();

		// Write the includes
		modInstContHeaderWriter.writeIncludes();
		modInstContBodyWriter.writeIncludes();

		// Close the file.
		modInstContHeaderWriter.close();
		modInstContBodyWriter.close();
	}

}
