/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.moduleimplgpr;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.WriterSupport;
import com.iawg.ecoa.systemmodel.componentimplementation.SM_ModuleImpl;

public class ModuleImplGPRWriter extends SourceFileWriter {
	private String containerOutputDirString;
	private SM_ModuleImpl modImpl;

	public ModuleImplGPRWriter(Path outputDir, SM_ModuleImpl modImpl, Path containerOutputDir) {
		super(outputDir);
		this.modImpl = modImpl;

		this.containerOutputDirString = outputDir.relativize(containerOutputDir).toString().replace("\\", "/");
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(modImpl.getName() + ".gpr"));

		// Create directories for the build (OBJ_DIR and LIB_DIR)
		super.createDirectory(outputDir.resolve("obj"));
		super.createDirectory(outputDir.resolve("lib"));

		setFileStructure();
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#SOURCE_DIRS#" + LF + "#GENERIC_CONTENT#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writePreamble() {
		String preambleText = "-- Generated GPR Build file for " + modImpl.getName() + LF + LF + "library project " + modImpl.getName() + " is" + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);

	}

	public void writeGenericContent() {
		String genericText = "   for Library_Name use \"" + modImpl.getName() + "\";" + LF + "   for Library_Dir use \"lib\";" + LF + "   for Object_Dir use \"obj\";" + LF + "   for Library_Kind use \"static\";" + LF + LF +

		// TODO - probably need to switch on Module Implementation language?!
				"   for Languages use (\"C\");" + LF + LF +

				"   package Compiler is" + LF + "      for Default_Switches (\"c\") use (\"-g\", \"-DECOA_64BIT_SUPPORT\");" + LF + "   end Compiler;" + LF + LF +

				"end " + modImpl.getName() + ";" + LF;

		// Replace the #GENERIC_CONTENT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GENERIC_CONTENT#", genericText);
	}

	public void writeSourceDirs() {
		String sourceDirsText = "   for Source_Dirs use (\"./**\"," + LF + "                        \"" + containerOutputDirString + "/include\");" + LF + LF +

				"   for Excluded_Source_Dirs use (\"./lib\", \"./obj\");" + LF;

		// Replace the #SOURCE_DIRS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SOURCE_DIRS#", sourceDirsText);
	}

}
