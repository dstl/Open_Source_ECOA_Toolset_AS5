/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.triggerinstance;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_DeployedTrigInst;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class TrigInstControllerGenerator {

	private PlatformGenerator platformGenerator;
	private SM_DeployedTrigInst deployedTrigInst;
	private SM_ProtectionDomain protectionDomain;

	public TrigInstControllerGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd, SM_DeployedTrigInst deployedTrigInst) {
		this.platformGenerator = platformGenerator;
		this.deployedTrigInst = deployedTrigInst;
		this.protectionDomain = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + deployedTrigInst.getCompInstance().getName());

		TrigInstContWriterC trigInstContHeaderWriter = new TrigInstContWriterC(platformGenerator, true, directory, deployedTrigInst, protectionDomain);
		;
		TrigInstContWriterC trigInstContBodyWriter = new TrigInstContWriterC(platformGenerator, false, directory, deployedTrigInst, protectionDomain);
		;

		// Open the file
		trigInstContHeaderWriter.open();
		trigInstContBodyWriter.open();

		// Write the start of file
		trigInstContHeaderWriter.writePreamble();
		trigInstContBodyWriter.writePreamble();

		// Write the trigger function(s) - one per each time period
		trigInstContHeaderWriter.writeTriggerOps(platformGenerator.getunderlyingPlatformInstantiation());
		trigInstContBodyWriter.writeTriggerOps(platformGenerator.getunderlyingPlatformInstantiation());

		// Write the trigger queue id declaration
		trigInstContBodyWriter.writeTriggerQueueIDDecl();

		// Write the trigger module state declaration
		trigInstContBodyWriter.writeModuleStateDecl();

		// Write the ILI message declarations (one for the queue entry and one
		// for the lifecycle responses).
		trigInstContBodyWriter.writeILIMessageDecls();

		// Write the process queue function
		trigInstContHeaderWriter.writeProcessQueue();
		trigInstContBodyWriter.writeProcessQueue();

		// Write the queue message function
		trigInstContHeaderWriter.writeQueueMessage();
		trigInstContBodyWriter.writeQueueMessage();

		// Write the get state function
		trigInstContHeaderWriter.writeGetState();
		trigInstContBodyWriter.writeGetState();

		// Write the get lifecycle operations
		trigInstContHeaderWriter.WriteLifecycleOps();
		trigInstContBodyWriter.WriteLifecycleOps();

		// Write the initialise function
		trigInstContHeaderWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());
		trigInstContBodyWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());

		// Write the reinitialise function
		trigInstContHeaderWriter.writeReinitialise();
		trigInstContBodyWriter.writeReinitialise();

		// Write the includes
		trigInstContHeaderWriter.writeIncludes();
		trigInstContBodyWriter.writeIncludes();

		// Close the file.
		trigInstContHeaderWriter.close();
		trigInstContBodyWriter.close();
	}

}
