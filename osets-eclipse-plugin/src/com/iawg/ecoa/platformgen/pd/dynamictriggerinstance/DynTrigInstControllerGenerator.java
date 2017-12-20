/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.dynamictriggerinstance;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.platformgen.pd.moduleiliwriter.ilimessage.DynamicTriggerInstanceILI;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class DynTrigInstControllerGenerator {

	private PlatformGenerator platformGenerator;
	private SM_DeployedTrigInst deployedTrigInst;
	private SM_ProtectionDomain protectionDomain;
	private DynamicTriggerInstanceILI trigInstILI;

	public DynTrigInstControllerGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd, SM_DeployedTrigInst deployedTrigInst, DynamicTriggerInstanceILI trigInstILI) {
		this.platformGenerator = platformGenerator;
		this.deployedTrigInst = deployedTrigInst;
		this.protectionDomain = pd;
		this.trigInstILI = trigInstILI;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + deployedTrigInst.getCompInstance().getName());

		// *********************************************************************************************************************
		// Write the dynamic trigger controller

		DynTrigInstContWriterC dynTrigInstContHeaderWriter = new DynTrigInstContWriterC(platformGenerator, true, directory, deployedTrigInst, protectionDomain, trigInstILI);
		DynTrigInstContWriterC dynTrigInstContBodyWriter = new DynTrigInstContWriterC(platformGenerator, false, directory, deployedTrigInst, protectionDomain, trigInstILI);

		// Open the file
		dynTrigInstContHeaderWriter.open();
		dynTrigInstContBodyWriter.open();

		// Write the start of file
		dynTrigInstContHeaderWriter.writePreamble();
		dynTrigInstContBodyWriter.writePreamble();

		// Write the fifo queue size
		dynTrigInstContBodyWriter.writeFifoListDefinition();

		// Write the dynamic trigger function(s)
		dynTrigInstContHeaderWriter.writeTriggerOps(platformGenerator.getunderlyingPlatformInstantiation());
		dynTrigInstContBodyWriter.writeTriggerOps(platformGenerator.getunderlyingPlatformInstantiation());

		// Write the dynamic trigger queue id declaration
		dynTrigInstContBodyWriter.writeTriggerQueueIDDecl();

		// Write the dynamic trigger module state declaration
		dynTrigInstContBodyWriter.writeModuleStateDecl();

		// Write the dynamic trigger operation timestamp declaration
		dynTrigInstContBodyWriter.writeModuleOpTimestampDecl();

		// Write the dynamic trigger module context declaration
		dynTrigInstContBodyWriter.writeModuleOpContextDecl();

		// Write the ILI message declarations (one for the queue entry and one
		// for the lifecycle responses).
		dynTrigInstContBodyWriter.writeILIMessageDecls();

		// Write the "is queue full" function
		dynTrigInstContBodyWriter.writeIsQueueFull();

		// Write the "timeLeft" utility function
		dynTrigInstContBodyWriter.writeTimeLeft();

		// Write the process queue function
		dynTrigInstContHeaderWriter.writeProcessQueue();
		dynTrigInstContBodyWriter.writeProcessQueue();

		// Write the queue message function
		dynTrigInstContHeaderWriter.writeQueueMessage();
		dynTrigInstContBodyWriter.writeQueueMessage();

		// Write the FIFO Accessor functions
		dynTrigInstContBodyWriter.writeFIFOAccessors();

		// Write the get state function
		dynTrigInstContHeaderWriter.writeGetState();
		dynTrigInstContBodyWriter.writeGetState();

		// Write the get lifecycle operations
		dynTrigInstContHeaderWriter.WriteLifecycleOps();
		dynTrigInstContBodyWriter.WriteLifecycleOps();

		// Write the initialise function
		dynTrigInstContHeaderWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());
		dynTrigInstContBodyWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());

		// Write the reinitialise function
		dynTrigInstContHeaderWriter.writeReinitialise();
		dynTrigInstContBodyWriter.writeReinitialise();

		// Write the includes
		dynTrigInstContHeaderWriter.writeIncludes();
		dynTrigInstContBodyWriter.writeIncludes();

		// Close the file.
		dynTrigInstContHeaderWriter.close();
		dynTrigInstContBodyWriter.close();

		// *********************************************************************************************************************
		// Now write the dynamic trigger "module"

		DynTrigInstModuleWriterC dynTrigInstModHeaderWriter = new DynTrigInstModuleWriterC(platformGenerator, true, directory, deployedTrigInst, protectionDomain, trigInstILI);
		DynTrigInstModuleWriterC dynTrigInstModBodyWriter = new DynTrigInstModuleWriterC(platformGenerator, false, directory, deployedTrigInst, protectionDomain, trigInstILI);

		// Open the file
		dynTrigInstModHeaderWriter.open();
		dynTrigInstModBodyWriter.open();

		// Write the start of file
		dynTrigInstModHeaderWriter.writePreamble();
		dynTrigInstModBodyWriter.writePreamble();

		// Write the private operations & co.
		dynTrigInstModHeaderWriter.writePrivateDecls();
		dynTrigInstModBodyWriter.writePrivateDecls();

		// Write the lifecycle operations
		dynTrigInstModHeaderWriter.WriteLifecycleOps();
		dynTrigInstModBodyWriter.WriteLifecycleOps();

		// Write the lifecycle operations
		dynTrigInstModHeaderWriter.WriteContextDecl();
		dynTrigInstModBodyWriter.WriteContextDecl();

		// Write the dynamic trigger function(s)
		dynTrigInstModHeaderWriter.writeTriggerOps(platformGenerator.getunderlyingPlatformInstantiation());
		dynTrigInstModBodyWriter.writeTriggerOps(platformGenerator.getunderlyingPlatformInstantiation());

		// Write the includes
		dynTrigInstModHeaderWriter.writeIncludes();
		dynTrigInstModBodyWriter.writeIncludes();

		// Write the end of the file
		dynTrigInstModHeaderWriter.writePostamble();
		dynTrigInstModBodyWriter.writePostamble();

		// Close the file.
		dynTrigInstModHeaderWriter.close();
		dynTrigInstModBodyWriter.close();

	}

}
