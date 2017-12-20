/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.pinfo.pd;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;

public class PDPinfoGenerator {
	private PlatformGenerator platformGenerator;
	private SM_ProtectionDomain protectionDomain;

	public PDPinfoGenerator(PlatformGenerator platformGenerator, SM_ProtectionDomain pd) {
		this.platformGenerator = platformGenerator;
		this.protectionDomain = pd;
	}

	public void generate() {
		PDPinfoWriterC pdPinfoHeaderWriter = new PDPinfoWriterC(platformGenerator.getPdOutputDir().resolve("inc-gen/"), true, protectionDomain);
		PDPinfoWriterC pdPinfoBodyWriter = new PDPinfoWriterC(platformGenerator.getPdOutputDir().resolve("src-gen/"), false, protectionDomain);

		// Open the file
		pdPinfoHeaderWriter.open();
		pdPinfoBodyWriter.open();

		// Write the start of file
		pdPinfoHeaderWriter.writePreamble();
		pdPinfoBodyWriter.writePreamble();

		// Write the current size definitions
		pdPinfoHeaderWriter.writeCurrentSize();
		pdPinfoBodyWriter.writeCurrentSize();

		// Write the includes
		pdPinfoHeaderWriter.writeIncludes();
		pdPinfoBodyWriter.writeIncludes();

		// Close the file.
		pdPinfoHeaderWriter.close();
		pdPinfoBodyWriter.close();
	}

}
