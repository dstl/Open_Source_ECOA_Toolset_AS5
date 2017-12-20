/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.vcid;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class VCIDGenerator {
	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain pd;

	public VCIDGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.pd = pd;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir();
		VCIDWriterC vcIDHeaderWriter = new VCIDWriterC(directory.resolve("inc-gen/"), pd, true);
		VCIDWriterC vcIDBodyWriter = new VCIDWriterC(directory.resolve("src-gen/"), pd, false);

		// Open the file
		vcIDHeaderWriter.open();
		vcIDBodyWriter.open();

		// Write the start of file
		vcIDHeaderWriter.writePreamble();
		vcIDBodyWriter.writePreamble();

		// Write the VC ID definitions
		vcIDHeaderWriter.writeVCIDs();

		// Write the Get Send VC ID function
		vcIDHeaderWriter.writeGetSendVCID();
		vcIDBodyWriter.writeGetSendVCID();

		// Write the Get Receive VC ID function
		vcIDHeaderWriter.writeGetReceiveVCID();
		vcIDBodyWriter.writeGetReceiveVCID();

		// Write the includes
		vcIDHeaderWriter.writeIncludes();
		vcIDBodyWriter.writeIncludes();

		// Close the file.
		vcIDHeaderWriter.close();
		vcIDBodyWriter.close();
	}

}
