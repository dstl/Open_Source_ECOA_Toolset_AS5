/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pf.vcid;

import com.iawg.ecoa.platformgen.pf.PlatformManagerGenerator;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class VCIDGenerator {
	private PlatformManagerGenerator pfManagerGenerator;
	private SM_LogicalComputingPlatform lcp;

	public VCIDGenerator(PlatformManagerGenerator pfManagerGenerator, SM_LogicalComputingPlatform lcp) {
		this.pfManagerGenerator = pfManagerGenerator;
		this.lcp = lcp;
	}

	public void generate() {
		VCIDWriterC vcIDHeaderWriter = new VCIDWriterC(pfManagerGenerator.getOutputDir().resolve("inc-gen/"), lcp, true, pfManagerGenerator);
		VCIDWriterC vcIDBodyWriter = new VCIDWriterC(pfManagerGenerator.getOutputDir().resolve("src-gen/"), lcp, false, pfManagerGenerator);

		// Open the file
		vcIDHeaderWriter.open();
		vcIDBodyWriter.open();

		// Write the start of file
		vcIDHeaderWriter.writePreamble();
		vcIDBodyWriter.writePreamble();

		// Write the VC ID definitions
		vcIDHeaderWriter.writeVCIDs();

		// Write the Get Send PD VC ID function
		vcIDHeaderWriter.writeGetSendPDVCID();
		vcIDBodyWriter.writeGetSendPDVCID();

		// Write the Get Receive PD VC ID function
		vcIDHeaderWriter.writeGetReceivePDVCID();
		vcIDBodyWriter.writeGetReceivePDVCID();

		// Write the Get Send Platform VC ID function
		vcIDHeaderWriter.writeGetSendPlatformVCID();
		vcIDBodyWriter.writeGetSendPlatformVCID();

		// Write the includes
		vcIDHeaderWriter.writeIncludes();
		vcIDBodyWriter.writeIncludes();

		// Close the file.
		vcIDHeaderWriter.close();
		vcIDBodyWriter.close();
	}

}
