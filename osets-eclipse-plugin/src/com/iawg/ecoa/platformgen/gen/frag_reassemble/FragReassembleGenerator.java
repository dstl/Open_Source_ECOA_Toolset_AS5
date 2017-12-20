/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.platformgen.gen.frag_reassemble;

import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.platformgen.PlatformGenerator;

public class FragReassembleGenerator {
	private PlatformGenerator platformGenerator;

	public FragReassembleGenerator(PlatformGenerator platformGenerator) {
		this.platformGenerator = platformGenerator;
	}

	public void generate() {
		// Fragment
		// Copy the header
		WriterSupport.copyResource(platformGenerator.getOutputDir().resolve("include/fragment.h"), "fragment.h");

		// Copy the body
		WriterSupport.copyResource(platformGenerator.getOutputDir().resolve("src/fragment.c"), "fragment.c");

		// Reassemble
		// Copy the header
		WriterSupport.copyResource(platformGenerator.getOutputDir().resolve("include/reassemble.h"), "reassemble.h");

		// Copy the body
		WriterSupport.copyResource(platformGenerator.getOutputDir().resolve("src/reassemble.c"), "reassemble.c");
	}

}
