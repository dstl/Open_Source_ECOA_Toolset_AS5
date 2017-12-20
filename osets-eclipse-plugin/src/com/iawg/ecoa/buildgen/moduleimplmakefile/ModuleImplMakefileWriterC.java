/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.moduleimplmakefile;

import java.nio.file.Path;

import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

public class ModuleImplMakefileWriterC extends ModuleImplMakefileWriter {
	private SM_ModuleImpl modImpl;
	private String containerOutputDirString;

	public ModuleImplMakefileWriterC(Path outputDir, SM_ModuleImpl modImpl, Path containerOutputDir) {
		super(outputDir);
		this.modImpl = modImpl;
		this.containerOutputDirString = outputDir.relativize(containerOutputDir).toString().replace("\\", "/");
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
		fileStructure = "#PREAMBLE#" + LF + LF + "OBJS=#OBJS#" + LF + "TARGET=#TARGET#" + LF + "CC=gcc $(CPPFLAGS)" + LF + "CFLAGS=-I" + containerOutputDirString + "/include -DECOA_64BIT_SUPPORT" + LF + "AR=ar" + LF + LF + "default: $(TARGET)" + LF + LF + "clean:" + LF + "\trm -f obj/*.o" + LF + "\trm -f *.a" + LF + LF + "#OBJECTS#" + "dirCheck:" + LF + "\tmkdir -p obj" + LF + LF + "$(TARGET) : dirCheck $(OBJS)" + LF + "\t$(AR) -cr $@ $(OBJS)" + LF;

		codeStringBuilder.append(fileStructure);
	}

	@Override
	public void writeHeaders() {
		String headerText = "";

		headerText += modImpl.getName() + ".h " + modImpl.getName() + "_user_context.h " + modImpl.getName() + "_container.h";

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#HEADERS#", headerText);
	}

	@Override
	public void writeObjects() {
		String objectText = "";

		objectText += "obj/" + modImpl.getName() + ".o : " + modImpl.getName() + ".c " + modImpl.getName() + ".h " + modImpl.getName() + "_container.h " + LF + "\t$(CC) $(CFLAGS) -c $< -o $@" + LF + LF;

		String objsText = "obj/" + modImpl.getName() + ".o ";

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#OBJECTS#", objectText);
		WriterSupport.replaceText(codeStringBuilder, "#OBJS#", objsText);

	}

	@Override
	public void writePreamble() {
		String preambleText = "";

		preambleText += "# Generated Makefile for " + modImpl.getName();

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);

	}

	@Override
	public void writeSources() {
		String sourceText = "";

		sourceText += modImpl.getName() + ".c ";

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SOURCES#", sourceText);
	}

	@Override
	public void writeTarget() {
		String targetText = "";

		targetText += modImpl.getName() + ".a";

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#TARGET#", targetText);

	}

}
