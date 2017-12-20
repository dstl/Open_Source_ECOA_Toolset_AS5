/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.moduleid;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public class CompImplModInstIDGenerator {
	private PlatformGenerator platformGenerator;
	private SM_ComponentImplementation compImpl;

	public CompImplModInstIDGenerator(PlatformGenerator platformGenerator, SM_ComponentImplementation compImpl) {
		this.platformGenerator = platformGenerator;
		this.compImpl = compImpl;
	}

	public void generate() {
		CompImplModInstIDWriterC compImplModInstIDHeaderWriter = new CompImplModInstIDWriterC(platformGenerator.getPdOutputDir().resolve("src-gen/" + compImpl.getName()), platformGenerator.getSystemModel(), compImpl);

		// Open the file
		compImplModInstIDHeaderWriter.open();

		// Write the start of file
		compImplModInstIDHeaderWriter.writePreamble();

		// Write the module instance id definitions
		compImplModInstIDHeaderWriter.writeModInstIDDefs();

		// Close the file.
		compImplModInstIDHeaderWriter.close();
	}

}
