/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.pd.pinfo.compinst;

import java.nio.file.Path;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.assembly.SM_ComponentInstance;

public class CompInstPinfoGenerator {
	private PlatformGenerator platformGenerator;
	private SM_ComponentInstance compInst;

	public CompInstPinfoGenerator(PlatformGenerator platformGenerator, SM_ComponentInstance compInst) {
		this.platformGenerator = platformGenerator;
		this.compInst = compInst;
	}

	public void generate() {
		Path directory = platformGenerator.getPdOutputDir().resolve("src-gen/" + compInst.getName());

		CompInstPinfoWriterC compInstPinfoHeaderWriter = new CompInstPinfoWriterC(directory, true, compInst);
		CompInstPinfoWriterC compInstPinfoBodyWriter = new CompInstPinfoWriterC(directory, false, compInst);

		// Open the file
		compInstPinfoHeaderWriter.open();
		compInstPinfoBodyWriter.open();

		// Write the start of file
		compInstPinfoHeaderWriter.writePreamble();
		compInstPinfoBodyWriter.writePreamble();

		// Write the current size definitions
		compInstPinfoHeaderWriter.writeCurrentSize();
		compInstPinfoBodyWriter.writeCurrentSize();

		// Write the includes
		compInstPinfoHeaderWriter.writeIncludes();
		compInstPinfoBodyWriter.writeIncludes();

		// Close the file.
		compInstPinfoHeaderWriter.close();
		compInstPinfoBodyWriter.close();
	}

}
