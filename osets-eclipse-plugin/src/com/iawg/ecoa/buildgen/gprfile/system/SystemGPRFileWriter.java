/*
 * Copyright 2017, BAE Systems Limited and GE Aviation Limited.
 *  
 * This software and its outputs are not claimed to be fit or safe for any purpose. Any user should
 * satisfy themselves that this software or its outputs are appropriate for its intended purpose.
*/
package com.iawg.ecoa.buildgen.gprfile.system;

import java.nio.file.Path;

import com.iawg.ecoa.SourceFileWriter;
import com.iawg.ecoa.platformgen.common.WriterSupport;
import com.iawg.ecoa.systemmodel.SystemModel;
import com.iawg.ecoa.systemmodel.deployment.SM_ProtectionDomain;
import com.iawg.ecoa.systemmodel.deployment.logicalsystem.SM_LogicalComputingPlatform;

public class SystemGPRFileWriter extends SourceFileWriter {
	private SystemModel sysModel;

	public SystemGPRFileWriter(Path outputDir, SystemModel sysModel) {
		super(outputDir);
		this.sysModel = sysModel;

		setFileStructure();
	}

	@Override
	public void open() {
		super.openFile(outputDir.resolve("ECOA_System.gpr"));
	}

	@Override
	protected void setFileStructure() {
		// Set the file structure
		String fileStructure = "#PREAMBLE#" + LF + "#GENERIC_CONTENT#" + LF;

		codeStringBuilder.append(fileStructure);
	}

	public void writeGenericContent() {
		String genericText = "   for Project_Files use (" + LF;

		for (SM_LogicalComputingPlatform lcp : sysModel.getLogicalSystem().getLogicalcomputingPlatforms()) {
			// Add PF_Manager GPR
			genericText += "      \"" + lcp.getName() + "/" + lcp.getLogicalcomputingNodes().get(0).getName() + "/" + lcp.getName() + "_PF_Manager/" + lcp.getName() + ".gpr\"," + LF;

			// Add each PD GPR
			for (SM_ProtectionDomain pd : lcp.getAllProtectionDomains()) {
				genericText += "      \"" + lcp.getName() + "/" + pd.getLogicalComputingNode().getName() + "/" + pd.getName() + "/" + pd.getName() + ".gpr\"," + LF;
			}
		}

		// Remove the last ","
		genericText = genericText.substring(0, genericText.lastIndexOf(","));

		genericText += ");" + LF + LF +

				"end ECOA_System;" + LF;

		// Replace the #GENERIC_CONTENT# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#GENERIC_CONTENT#", genericText);
	}

	public void writePreamble() {
		String preambleText = "-- Generated GPR Build file for the ECOA System" + LF + LF +

				"aggregate project ECOA_System is" + LF + LF +

				"   for External (\"Build_Type\") use \"Aggregate\";" + LF;

		// Replace the #PREAMBLE# tag in string builder
		WriterSupport.replaceText(codeStringBuilder, "#PREAMBLE#", preambleText);
	}

}
