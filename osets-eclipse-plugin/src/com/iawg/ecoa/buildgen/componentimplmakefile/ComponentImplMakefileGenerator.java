/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.componentimplmakefile;

import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;

public class ComponentImplMakefileGenerator {
	private SM_ComponentImplementation compImpl;

	public ComponentImplMakefileGenerator(SM_ComponentImplementation compImpl) {
		this.compImpl = compImpl;
	}

	public void generate() {
		ComponentImplMakefileWriter componentimplMakefileWriter = null;

		componentimplMakefileWriter = new ComponentImplMakefileWriterC(compImpl);

		// Open the file
		componentimplMakefileWriter.open();

		componentimplMakefileWriter.writePreamble();
		componentimplMakefileWriter.writeTargets();
		componentimplMakefileWriter.writeSubBuilds();
		componentimplMakefileWriter.writeCleans();

		// Close the file.
		componentimplMakefileWriter.close();
	}
}
