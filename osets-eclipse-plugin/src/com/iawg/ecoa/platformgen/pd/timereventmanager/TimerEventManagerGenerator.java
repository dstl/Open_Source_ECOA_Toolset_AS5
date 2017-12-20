/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.timereventmanager;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class TimerEventManagerGenerator {

	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain protectionDomain;

	public TimerEventManagerGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.protectionDomain = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();

		TimerEventManagerWriterC tehHeaderWriter = new TimerEventManagerWriterC(platformGenerator, true, directory.resolve("inc-gen/"), protectionDomain);
		TimerEventManagerWriterC tehBodyWriter = new TimerEventManagerWriterC(platformGenerator, false, directory.resolve("src-gen/"), protectionDomain);

		// Open the file
		tehHeaderWriter.open();
		tehBodyWriter.open();

		// Write the start of file
		tehHeaderWriter.writePreamble();
		tehBodyWriter.writePreamble();

		// Define the Timer_Type type
		tehHeaderWriter.writeTypeDefs();
		tehBodyWriter.writeTypeDefs();

		// Write the setup timer function
		tehHeaderWriter.writeSetupRequestTimer();
		tehBodyWriter.writeSetupRequestTimer();

		// TODO - could we make this more generic and have a single get_timer /
		// set_timer / delete_timer ?
		// Write the setup request QoS timer function
		tehHeaderWriter.writeSetupRequestQoSTimer();
		tehBodyWriter.writeSetupRequestQoSTimer();

		// Write the (private) delete timer function
		tehHeaderWriter.writeDeleteTimer();
		tehBodyWriter.writeDeleteTimer();

		// Write the (public) delete timer function
		tehHeaderWriter.writeDeleteTimerWithLock();
		tehBodyWriter.writeDeleteTimerWithLock();

		// Write the get timer ID function
		tehHeaderWriter.writeGetTimer();
		tehBodyWriter.writeGetTimer();

		// Write the delete timer id function
		tehHeaderWriter.writeDeleteTimerID();
		tehBodyWriter.writeDeleteTimerID();

		// Write the delete request QoS timer id function
		tehHeaderWriter.writeDeleteRequestQoSTimerID();
		tehBodyWriter.writeDeleteRequestQoSTimerID();

		// Write the initialisation function
		tehHeaderWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());
		tehBodyWriter.writeInitialise(platformGenerator.getunderlyingPlatformInstantiation());

		// Write all the includes
		tehHeaderWriter.writeIncludes();
		tehBodyWriter.writeIncludes();

		// Close the file.
		tehHeaderWriter.close();
		tehBodyWriter.close();

	}

}
