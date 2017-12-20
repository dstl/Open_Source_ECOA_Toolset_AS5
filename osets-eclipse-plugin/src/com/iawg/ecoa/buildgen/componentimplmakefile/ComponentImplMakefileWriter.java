/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.componentimplmakefile;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;

public abstract class ComponentImplMakefileWriter extends SourceFileWriter {

	public ComponentImplMakefileWriter(Path outputDir) {
		super(outputDir);
	}

	public abstract void writeCleans();

	public abstract void writePreamble();

	public abstract void writeSubBuilds();

	public abstract void writeTargets();

}
