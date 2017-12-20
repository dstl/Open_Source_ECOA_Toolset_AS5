/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.componentimplmakefile;

import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ComponentImplementation;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

public class ComponentImplMakefileWriterC extends ComponentImplMakefileWriter {

	private SM_ComponentImplementation compImpl;

	public ComponentImplMakefileWriterC(SM_ComponentImplementation compImpl) {
		super(compImpl.getContainingDir());
		this.compImpl = compImpl;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("Makefile"));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure;
		fileStructure = "#PREAMBLE#" + LF + LF +

				"TARGETS=#TARGETS#" + LF +

				"default: $(TARGETS)" + LF + LF + "clean:" + LF + "#CLEANS#" + LF + "#SUBBUILDS#" + "dummy:" + LF + "\ttrue" + LF;

		codeStringBuilder.append(fileStructure);
	}

	@Override
	public void writeCleans() {
		String targetText = "";

		for (SM_ModuleImpl modImpl : compImpl.getModuleImplementations().values()) {
			targetText += "\tcd " + modImpl.getName() + " ; make -f Makefile clean" + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#CLEANS#", targetText);
	}

	@Override
	public void writePreamble() {
		String preambleText = "";

		preambleText += "# Generated Makefile for " + compImpl.getName();

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);

	}

	@Override
	public void writeSubBuilds() {
		String targetText = "";

		for (SM_ModuleImpl modImpl : compImpl.getModuleImplementations().values()) {
			targetText += modImpl.getName() + ": dummy" + LF + "\tcd " + modImpl.getName() + " ; make -f Makefile -$(MAKEFLAGS)" + LF + LF;
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SUBBUILDS#", targetText);
	}

	@Override
	public void writeTargets() {
		String targetText = "";

		for (SM_ModuleImpl modImpl : compImpl.getModuleImplementations().values()) {
			targetText += modImpl.getName() + " ";
		}

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TARGETS#", targetText);

	}

}
