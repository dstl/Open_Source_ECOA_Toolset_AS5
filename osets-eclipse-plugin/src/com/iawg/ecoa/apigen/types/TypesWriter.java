/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.apigen.types;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.systemmodel.types.SM_Namespace;

/**
 * This class is an abstract class that declares the methods that must be
 * implemented by source code language-specific classes that extend it.
 * 
 */
public abstract class TypesWriter extends SourceFileWriter {
	protected SM_Namespace namespace;
	protected String namespaceName;

	public TypesWriter(Path outputDir) {
		super(outputDir);
	}

	public abstract void generate();

}
