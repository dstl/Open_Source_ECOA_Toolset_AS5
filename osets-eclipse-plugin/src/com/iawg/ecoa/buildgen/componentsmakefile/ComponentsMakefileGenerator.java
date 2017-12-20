/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.componentsmakefile;

import java.nio.file.Path;
import java.util.List;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public class ComponentsMakefileGenerator {
	private List<SM_ComponentImplementation> compImpls;
	private Path outputDir;

	public ComponentsMakefileGenerator(Path outputDir, List<SM_ComponentImplementation> compImpls) {
		this.compImpls = compImpls;
		this.outputDir = outputDir;
	}

	public void generate() {
		ComponentsMakefileWriter componentMakefileWriter = null;

		componentMakefileWriter = new ComponentsMakefileWriterC(outputDir.resolve("4-ComponentImplementations/"), compImpls);

		// Open the file
		componentMakefileWriter.open();

		componentMakefileWriter.writePreamble();
		componentMakefileWriter.writeTargets();
		componentMakefileWriter.writeSubBuilds();
		componentMakefileWriter.writeCleans();

		// Close the file.
		componentMakefileWriter.close();
	}
}
