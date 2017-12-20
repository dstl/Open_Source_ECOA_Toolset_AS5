/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.defaulter;

import com.iawg.ecoa.platformgen.PlatformGenerator;
import com.iawg.ecoa.systemmodel.SystemModel;

public class DefaulterGenerator {

	private PlatformGenerator platformGenerator;

	public DefaulterGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;

	}

	public void generate(SystemModel systemModel) {

		DefaulterWriterC defaulterHeaderWriter = new DefaulterWriterC(systemModel, true, platformGenerator.getOutputDir().resolve("include/"));
		;
		DefaulterWriterC defaulterBodyWriter = new DefaulterWriterC(systemModel, false, platformGenerator.getOutputDir().resolve("src/"));
		;

		// Open the file
		defaulterHeaderWriter.open();
		defaulterBodyWriter.open();

		defaulterHeaderWriter.writePreamble();
		defaulterBodyWriter.writePreamble();

		defaulterHeaderWriter.writeDefaulterFunctions();
		defaulterBodyWriter.writeDefaulterFunctions();

		defaulterHeaderWriter.writeIncludes();
		defaulterBodyWriter.writeIncludes();

		// Close the file.
		defaulterHeaderWriter.close();
		defaulterBodyWriter.close();

	}

}
