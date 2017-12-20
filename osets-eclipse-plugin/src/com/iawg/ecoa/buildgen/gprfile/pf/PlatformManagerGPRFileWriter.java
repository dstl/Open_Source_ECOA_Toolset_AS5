/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.gprfile.pf;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class PlatformManagerGPRFileWriter extends SourceFileWriter {
	private SM_LogicalComputingPlatform lcp;

	public PlatformManagerGPRFileWriter(Path outputDir, SM_LogicalComputingPlatform lcp) {
		super(outputDir);
		this.lcp = lcp;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve(lcp.getName() + ".gpr"));

		// Create a directory for the build (OBJ_DIR)
		super.createDirectory(outputDir.resolve("build_" + lcp.getName()));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#SOURCE_DIRS#" + LF + "#GENERIC_CONTENT#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeGenericContent() {
		String genericText = "   for Languages use (\"C\");" + LF + "   for Object_Dir use \"build_" + lcp.getName() + "\";" + LF + "   for Main use (\"main.c\");" + LF + LF +

				"   package Naming is" + LF + "      for Specification_Suffix (\"c\") use \".h\";" + LF + "      for Implementation_Suffix (\"c\") use \".c\";" + LF + "   end Naming;" + LF + LF +

				"   package Compiler is" + LF;

		// TODO - need a way to determine which node we are running on!
		if (lcp.getLogicalcomputingNodes().get(0).isLittleEndian()) {
			genericText += "      for Default_Switches (\"c\") use (\"-g\", \"-DECOA_64BIT_SUPPORT\", \"-DLITTLE_ENDIAN\");" + LF;
		} else {
			genericText += "      for Default_Switches (\"c\") use (\"-g\", \"-DECOA_64BIT_SUPPORT\");" + LF;
		}

		genericText += "   end Compiler;" + LF + LF +

				"   package Linker is" + LF + "      for Default_Switches (\"c\") use (\"-lpthread\", \"-lrt\", \"-lm\");" + LF + "   end Linker;" + LF + LF +

				"end " + lcp.getName() + ";" + LF;

		// Replace the #GENERIC_CONTENT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GENERIC_CONTENT#", genericText);
	}

	public void writePreamble() {
		String preambleText = "-- Generated GPR Build file for " + lcp.getName() + LF + LF +

				"project " + lcp.getName() + " is" + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

	public void writeSourceDirs() {
		String sourceDirsText = "   for Source_Dirs use (";

		// Add source directory for PD source code
		sourceDirsText += "\"./**\"," + LF + "      \"../inc-gen/**\"," + LF + "      \"../src-gen/**\"," + LF + "      \"../../include/**\"," + LF + "      \"../../../include/**\"," + LF + "      \"../../../src/**\");" + LF;

		// Replace the #SOURCE_DIRS# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#SOURCE_DIRS#", sourceDirsText);
	}

}
